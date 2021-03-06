package com.lukdut.monitoring.backend.integration;

import com.lukdut.monitoring.backend.model.SensorMessage;
import com.lukdut.monitoring.backend.repository.CommandRepository;
import com.lukdut.monitoring.backend.repository.DataRepository;
import com.lukdut.monitoring.backend.repository.SensorRepository;
import com.lukdut.monitoring.gateway.dto.IncomingSensorMessage;
import com.lukdut.monitoring.gateway.dto.IntermodularSensorCommand;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.messaging.MessageChannel;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.integration.dsl.Transformers.fromJson;

@Configuration
public class SensorMessageIntegrationConfig {
    private static final String MESSAGES_FROM_KAFKA_CHANNEL = "messagesFromKafka";

    @Bean
    public ConsumerFactory<?, ?> stringConsumerFactory(@Value("${gateway.bootstrap}") String bootstrapServer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "backend");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }


    @Bean(MESSAGES_FROM_KAFKA_CHANNEL)
    public MessageChannel commandsChannel() {
        return MessageChannels.direct().get();
    }

    //Flows
    @Bean
    public IntegrationFlow messagesListenerFromKafkaFlow(
            @Qualifier("stringConsumerFactory") ConsumerFactory<?, ?> consumerFactory,
            @Value("${gateway.topics.messages}") String messagesTopic) {
        return IntegrationFlows
                .from(Kafka.messageDrivenChannelAdapter(
                        consumerFactory,
                        KafkaMessageDrivenChannelAdapter.ListenerMode.record,
                        messagesTopic))
                .channel(MESSAGES_FROM_KAFKA_CHANNEL)
                .get();
    }

    @Bean
    IntegrationFlow commandsFlow(DataRepository repository, SensorRepository sensorRepository) {
        return f -> f.channel(MESSAGES_FROM_KAFKA_CHANNEL)
                .transform(fromJson(IncomingSensorMessage.class))
                .log()
                .transform(new IncomingMessageTransformer())
                //Only registered devices
                .filter(SensorMessage.class, sensorMessage -> sensorRepository.existsByImei(sensorMessage.getImei()))
                .handle(message -> {
                    SensorMessage sensorMessage = (SensorMessage) message.getPayload();
                    repository.save(sensorMessage);
                    if (sensorMessage.getState() != null) {
                        sensorRepository.findByImei(sensorMessage.getImei()).ifPresent(sensor -> {
                            sensor.setState(sensorMessage.getState());
                            sensorRepository.save(sensor);
                        });
                    }
                });
    }

    @Bean
    public IntegrationFlow commandsResponseFlow(
            @Qualifier("stringConsumerFactory") ConsumerFactory<?, ?> consumerFactory,
            @Value("${gateway.topics.commands.response}") String commandResponseTopic,
            CommandRepository commandRepository) {
        return IntegrationFlows
                .from(Kafka.messageDrivenChannelAdapter(
                        consumerFactory,
                        KafkaMessageDrivenChannelAdapter.ListenerMode.record,
                        commandResponseTopic))
                .transform(fromJson(IntermodularSensorCommand.class))
                .handle(message -> {
                    IntermodularSensorCommand receivedCommand = (IntermodularSensorCommand) message.getPayload();
                    commandRepository.findById(receivedCommand.getId()).ifPresent(command -> {
                        command.setState(receivedCommand.getState());
                        commandRepository.save(command);
                    });
                })
                .get();
    }
}

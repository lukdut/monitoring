package com.lukdut.monitoring.backend.integration;

import com.lukdut.monitoring.backend.model.Command;
import com.lukdut.monitoring.gateway.dto.OutcomingSensorCommand;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CommandIntegrationConfig {
    public static final String COMMANDS_TO_KAFKA_CHANNEL = "commandsToKafka";
    public static final String SERIALIZED_COMMANDS_CHANNEL = "stringCommands";

    @Bean
    public ProducerFactory<String, String> producerFactory(@Value("${gateway.bootstrap}") String bootstrapServer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean(COMMANDS_TO_KAFKA_CHANNEL)
    public MessageChannel commandsChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    IntegrationFlow inputFlow() {
        return f -> f.channel(COMMANDS_TO_KAFKA_CHANNEL)
                .transform(new SensorCommandTransformer())
                .transform(Transformers.toJson())
                .channel(SERIALIZED_COMMANDS_CHANNEL);
    }

    @Bean
    @ServiceActivator(inputChannel = SERIALIZED_COMMANDS_CHANNEL)
    public MessageHandler kafkaSaver(KafkaTemplate<String, String> kafkaTemplate,
                                     @Value("${gateway.topics.commands}") String topic) {
        KafkaProducerMessageHandler<String, String> handler = new KafkaProducerMessageHandler<>(kafkaTemplate);
        handler.setTopicExpression(new LiteralExpression(topic));
        return handler;
    }

    private class SensorCommandTransformer implements GenericTransformer<Command, OutcomingSensorCommand> {
        @Override
        public OutcomingSensorCommand transform(Command command) {
            return new OutcomingSensorCommand(command.getImei(), command.getCommand());
        }
    }
}

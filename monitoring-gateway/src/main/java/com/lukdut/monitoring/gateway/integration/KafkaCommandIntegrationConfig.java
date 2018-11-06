package com.lukdut.monitoring.gateway.integration;

import com.lukdut.monitoring.gateway.command.CommandManager;
import com.lukdut.monitoring.gateway.dto.OutcomingSensorCommand;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.integration.kafka.inbound.KafkaMessageDrivenChannelAdapter;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.messaging.MessageChannel;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaCommandIntegrationConfig {
    private static final String COMMANDS_FROM_KAFKA_CHANNEL = "commandsFromKafka";

    @Bean
    public ConsumerFactory<?, ?> consumerFactory(@Value("${gateway.bootstrap}") String bootstrapServer) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "gateway");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }


    @Bean(COMMANDS_FROM_KAFKA_CHANNEL)
    public MessageChannel commandsChannel() {
        return MessageChannels.direct().get();
    }

    //Flows
    @Bean
    public IntegrationFlow topic1ListenerFromKafkaFlow(
            ConsumerFactory<?, ?> consumerFactory,
            @Value("${gateway.topics.commands}") String commandsTopic) {
        return IntegrationFlows
                .from(Kafka.messageDrivenChannelAdapter(
                        consumerFactory,
                        KafkaMessageDrivenChannelAdapter.ListenerMode.record,
                        commandsTopic))
                .channel(COMMANDS_FROM_KAFKA_CHANNEL)
                .get();
    }

    @Bean
    IntegrationFlow commandsFlow(CommandManager commandManager) {
        return f -> f.channel(COMMANDS_FROM_KAFKA_CHANNEL)
                .transform(Transformers.fromJson(OutcomingSensorCommand.class))
                .log()
                .filter(o -> ((OutcomingSensorCommand) o).getCommand() != null)
                .handle(message -> {
                    commandManager.addCommand((OutcomingSensorCommand) message.getPayload());
                });
    }
}

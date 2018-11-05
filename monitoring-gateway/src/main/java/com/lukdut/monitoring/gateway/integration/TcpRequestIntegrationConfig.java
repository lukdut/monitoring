package com.lukdut.monitoring.gateway.integration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.integration.transformer.ObjectToStringTransformer;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TcpRequestIntegrationConfig {
    private static final String RAW_INPUT_ERROR_CHANNEL = "rawInputError";
    static final String REQUESTS_CHANNEL = "requests";
    public static final String RAW_INPUT_CHANNEL = "rawInput";

    @Bean
    public AbstractServerConnectionFactory serverFactory(@Value("${gateway.port}") Integer port) {
        return new TcpNetServerConnectionFactory(port);
    }

    @Bean
    public TcpReceivingChannelAdapter inboundAdapter(AbstractServerConnectionFactory connectionFactory) {
        TcpReceivingChannelAdapter inbound = new TcpReceivingChannelAdapter();
        inbound.setConnectionFactory(connectionFactory);
        inbound.setOutputChannelName(RAW_INPUT_CHANNEL);
        inbound.setErrorChannelName(RAW_INPUT_ERROR_CHANNEL);
        return inbound;
    }

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

    @Bean(REQUESTS_CHANNEL)
    public MessageChannel sensorMessagesChannel() {
        return MessageChannels.publishSubscribe().get();
    }

    @Bean(RAW_INPUT_ERROR_CHANNEL)
    public MessageChannel wrongMessagesChannel() {
        return MessageChannels.direct().get();
    }

    //Flow
    @Bean
    IntegrationFlow inputFlow() {
        return f -> f.channel(RAW_INPUT_CHANNEL)
                .transform(new ObjectToStringTransformer())
                .filter(String.class, s -> !s.isEmpty())
                .filter(String.class, s -> s.length() > 2)
                //only json allowed
                .filter(String.class, s -> s.getBytes()[0] == '{' && s.getBytes()[s.length() - 1] == '}')
                .channel(REQUESTS_CHANNEL);
    }


    @Bean
    @ServiceActivator(inputChannel = REQUESTS_CHANNEL)
    public MessageHandler kafkaSaver(KafkaTemplate<String, String> kafkaTemplate,
                                     @Value("${gateway.topics.messages}") String topic) {
        KafkaProducerMessageHandler<String, String> handler = new KafkaProducerMessageHandler<>(kafkaTemplate);
        handler.setTopicExpression(new LiteralExpression(topic));
        return handler;
    }

    @Bean
    IntegrationFlow errorMessagesFlow() {
        return f -> f.channel(RAW_INPUT_ERROR_CHANNEL)
                .log(LoggingHandler.Level.WARN);
    }
}

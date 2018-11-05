package com.lukdut.monitoring.gateway.integration;

import com.lukdut.monitoring.gateway.command.CommandManager;
import com.lukdut.monitoring.gateway.dto.IncomingSensorMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;

import static com.lukdut.monitoring.gateway.integration.TcpRequestIntegrationConfig.REQUESTS_CHANNEL;

@Configuration
public class TcpResponseIntegrationConfig {
    private static final String RESPONSES_CHANNEL = "responses";

    @Bean
    IntegrationFlow responseFlow(CommandManager commandManager) {
        return f -> f.channel(REQUESTS_CHANNEL)
                .log()
                .transform(Transformers.fromJson(IncomingSensorMessage.class))
                .filter(IncomingSensorMessage.class, incomingSensorMessage -> incomingSensorMessage.getImei() != 0)
                .transform(IncomingSensorMessage.class, incomingSensorMessage ->
                        commandManager.getCommand(incomingSensorMessage.getImei()).orElse(""))
                .filter(String.class, sensorMessage -> !sensorMessage.isEmpty())
                .channel(RESPONSES_CHANNEL);
    }

    @Bean
    @ServiceActivator(inputChannel = RESPONSES_CHANNEL)
    public TcpSendingMessageHandler outboundAdapter(AbstractServerConnectionFactory connectionFactory) {
        TcpSendingMessageHandler outbound = new TcpSendingMessageHandler();
        outbound.setConnectionFactory(connectionFactory);
        return outbound;
    }
}

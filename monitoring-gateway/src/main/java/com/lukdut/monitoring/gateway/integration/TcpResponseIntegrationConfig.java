package com.lukdut.monitoring.gateway.integration;

import com.lukdut.monitoring.gateway.command.CommandManager;
import com.lukdut.monitoring.gateway.dto.CommandState;
import com.lukdut.monitoring.gateway.dto.IncomingSensorMessage;
import com.lukdut.monitoring.gateway.dto.IntermodularSensorCommand;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.ip.tcp.TcpSendingMessageHandler;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

import java.util.concurrent.Executors;

import static com.lukdut.monitoring.gateway.integration.KafkaCommandIntegrationConfig.COMMANDS_REPLY_CHANNEL;
import static com.lukdut.monitoring.gateway.integration.TcpRequestIntegrationConfig.REQUESTS_CHANNEL;

@Configuration
public class TcpResponseIntegrationConfig {
    private static final String RESPONSES_CHANNEL = "responses";
    private static final String COMMANDS_CHANNEL = "commands";
    private static final IntermodularSensorCommand NO_MESSAGE = new IntermodularSensorCommand();

    @Bean
    IntegrationFlow responseFlow(CommandManager commandManager) {
        return f -> f.channel(REQUESTS_CHANNEL)
                .log()
                .transform(Transformers.fromJson(IncomingSensorMessage.class))
                .filter(IncomingSensorMessage.class, incomingSensorMessage -> incomingSensorMessage.getImei() != 0)
                .transform(IncomingSensorMessage.class, incomingSensorMessage ->
                        commandManager.getCommand(incomingSensorMessage.getImei()).orElse(NO_MESSAGE))
                //TODO:check for stale commands
                .filter(IntermodularSensorCommand.class, command -> command != NO_MESSAGE)
                .transform(IntermodularSensorCommand.class, command -> {
                    command.setState(CommandState.EXECUTED);
                    return command;
                })
                .log()
                .channel(COMMANDS_CHANNEL);
    }

    @Bean(COMMANDS_CHANNEL)
    public SubscribableChannel commands() {
        return MessageChannels.publishSubscribe(Executors.newFixedThreadPool(2)).get();
    }

    @Bean
    public IntegrationFlow commandsResponse(@Qualifier(COMMANDS_REPLY_CHANNEL) MessageChannel commandsReply) {
        return IntegrationFlows.from(COMMANDS_CHANNEL)
                .transform(Transformers.toJson())
                .channel(commandsReply)
                .get();
    }

    @Bean
    public IntegrationFlow commandsSend() {
        return IntegrationFlows.from(COMMANDS_CHANNEL)
                .transform(IntermodularSensorCommand.class, IntermodularSensorCommand::getCommand)
                .channel(RESPONSES_CHANNEL)
                .get();
    }

    @Bean
    @ServiceActivator(inputChannel = RESPONSES_CHANNEL)
    public TcpSendingMessageHandler outboundAdapter(AbstractServerConnectionFactory connectionFactory) {
        TcpSendingMessageHandler outbound = new TcpSendingMessageHandler();
        outbound.setConnectionFactory(connectionFactory);
        return outbound;
    }
}

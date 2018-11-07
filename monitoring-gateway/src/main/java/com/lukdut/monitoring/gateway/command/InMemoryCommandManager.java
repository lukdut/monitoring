package com.lukdut.monitoring.gateway.command;

import com.lukdut.monitoring.gateway.dto.CommandState;
import com.lukdut.monitoring.gateway.dto.IntermodularSensorCommand;
import com.lukdut.monitoring.gateway.metrics.Benchmark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.dsl.Transformers;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import static com.lukdut.monitoring.gateway.integration.KafkaCommandIntegrationConfig.COMMANDS_REPLY_CHANNEL;

@Service
public class InMemoryCommandManager implements CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCommandManager.class);

    private final Map<Long, Queue<IntermodularSensorCommand>> commands = new ConcurrentHashMap<>();
    private final int ttlSec;
    private final MessageChannel commandsReplyChannel;

    public InMemoryCommandManager(@Value("${gateway.command.ttl}") int ttlSec,
                                  @Qualifier(COMMANDS_REPLY_CHANNEL) MessageChannel commandsReplyChannel) {
        this.ttlSec = ttlSec;
        this.commandsReplyChannel = commandsReplyChannel;
    }

    @Override
    public void addCommand(IntermodularSensorCommand command) {
        Queue<IntermodularSensorCommand> sensorCommandQueue =
                commands.computeIfAbsent(command.getImei(), imei ->
                        new PriorityBlockingQueue<>(10, (o1, o2) -> o1.getCreationDate().compareTo(o2.getCreationDate())));
        sensorCommandQueue.add(command);
        LOGGER.debug("Added new command: {}", command);
    }

    @Override
    public Optional<IntermodularSensorCommand> getCommand(long imei) {
        final Queue<IntermodularSensorCommand> sensorCommands = commands.get(imei);
        if (sensorCommands == null) {
            return Optional.empty();
        }
        IntermodularSensorCommand command;
        synchronized (sensorCommands) {
            command = sensorCommands.poll();
        }
        return Optional.ofNullable(command);
    }

    @Scheduled(fixedRate = 10000)
    @Benchmark
    public void check() {
        LOGGER.trace("Stale commands check fired");
        commands.forEach((imei, queue) -> {
            synchronized (queue) {
                while (true) {
                    IntermodularSensorCommand command = queue.peek();
                    if (command != null && isStale(command)) {
                        command = queue.poll();
                        if (command != null) {
                            LOGGER.warn("Stale command found for device with imei {}: {}", imei, command);
                            command.setState(CommandState.REJECTED);
                            commandsReplyChannel.send(Transformers.toJson().transform(MessageBuilder.withPayload(command).build()));
                        } else {
                            LOGGER.error("Concurrent modification occurred during stale commands scan! (imei = {})", imei);
                        }
                    } else {
                        break;
                    }
                }
            }
        });
    }

    private boolean isStale(IntermodularSensorCommand command) {
        return System.currentTimeMillis() - command.getCreationDate().getTime() > ttlSec * 1000;
    }
}

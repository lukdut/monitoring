package com.lukdut.monitoring.gateway.command;

import com.lukdut.monitoring.gateway.dto.IntermodularSensorCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class InMemoryCommandManager implements CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCommandManager.class);

    private final Map<Long, Queue<IntermodularSensorCommand>> commands = new ConcurrentHashMap<>();
    private final int ttlSec;

    public InMemoryCommandManager(@Value("${gateway.command.ttl}") int ttlSec) {
        this.ttlSec = ttlSec;
    }

    @Override
    public void addCommand(IntermodularSensorCommand command) {
        Queue<IntermodularSensorCommand> sensorCommandQueue =
                commands.computeIfAbsent(command.getImei(), imei -> new ConcurrentLinkedQueue<>());
        sensorCommandQueue.add(command);
        LOGGER.debug("Added new command: {}", command);
    }

    @Override
    public Optional<IntermodularSensorCommand> getCommand(long imei) {
        Queue<IntermodularSensorCommand> sensorCommands = commands.get(imei);
        if (sensorCommands == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(sensorCommands.poll());
    }

    @Scheduled(fixedRate = 1000)
    public void check() {
        LOGGER.info("Stale commands check fired");
        //TODO
    }
}

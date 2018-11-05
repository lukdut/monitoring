package com.lukdut.monitoring.gateway.command;

import com.lukdut.monitoring.gateway.dto.SensorCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class InMemoryCommandManager implements CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCommandManager.class);

    private final Map<Long, Queue<String>> commands = new ConcurrentHashMap<>();

    @Override
    public void addCommand(SensorCommand command) {
        Queue<String> sensorCommandQueue = commands.computeIfAbsent(command.getImei(), imei -> new ConcurrentLinkedQueue<>());
        sensorCommandQueue.add(command.getCommand());
        LOGGER.debug("Added new command: {}", command);
    }

    @Override
    public Optional<String> getCommand(long imei) {
        Queue<String> sensorCommands = commands.get(imei);
        if (sensorCommands == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(sensorCommands.poll());
    }
}

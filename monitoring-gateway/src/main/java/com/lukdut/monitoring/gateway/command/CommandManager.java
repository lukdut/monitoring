package com.lukdut.monitoring.gateway.command;

import com.lukdut.monitoring.gateway.dto.SensorCommand;

import java.util.Optional;

public interface CommandManager {
    void addCommand(SensorCommand command);

    Optional<String> getCommand(long imei);
}

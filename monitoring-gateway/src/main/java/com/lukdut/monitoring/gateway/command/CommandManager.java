package com.lukdut.monitoring.gateway.command;

import com.lukdut.monitoring.gateway.dto.IntermodularSensorCommand;

import java.util.Optional;

public interface CommandManager {
    void addCommand(IntermodularSensorCommand command);

    Optional<IntermodularSensorCommand> getCommand(long imei);
}

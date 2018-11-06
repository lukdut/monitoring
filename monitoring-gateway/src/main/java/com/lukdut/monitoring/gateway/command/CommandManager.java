package com.lukdut.monitoring.gateway.command;

import com.lukdut.monitoring.gateway.dto.OutcomingSensorCommand;

import java.util.Optional;

public interface CommandManager {
    void addCommand(OutcomingSensorCommand command);

    Optional<String> getCommand(long imei);
}

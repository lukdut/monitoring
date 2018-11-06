package com.lukdut.monitoring.backend.integration;

import com.lukdut.monitoring.backend.rest.resources.CommandDto;
import com.lukdut.monitoring.gateway.dto.OutcomingSensorCommand;
import org.springframework.integration.transformer.GenericTransformer;

public class SensorCommandTransformer implements GenericTransformer<CommandDto, OutcomingSensorCommand> {
    @Override
    public OutcomingSensorCommand transform(CommandDto command) {
        return new OutcomingSensorCommand(command.getImei(), command.getCommand());
    }
}
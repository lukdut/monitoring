package com.lukdut.monitoring.backend.integration;

import com.lukdut.monitoring.backend.model.Command;
import com.lukdut.monitoring.gateway.dto.IntermodularSensorCommand;
import org.springframework.integration.transformer.GenericTransformer;

public class SensorCommandTransformer implements GenericTransformer<Command, IntermodularSensorCommand> {
    @Override
    public IntermodularSensorCommand transform(Command c) {
        return new IntermodularSensorCommand(c.getId(), c.getImei(), c.getCommand(), c.getTimestamp());
    }
}
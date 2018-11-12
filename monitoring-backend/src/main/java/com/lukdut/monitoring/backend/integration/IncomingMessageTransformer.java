package com.lukdut.monitoring.backend.integration;

import com.lukdut.monitoring.backend.model.SensorMessage;
import com.lukdut.monitoring.gateway.dto.IncomingSensorMessage;
import org.springframework.integration.transformer.GenericTransformer;

public class IncomingMessageTransformer implements GenericTransformer<IncomingSensorMessage, SensorMessage> {
    @Override
    public SensorMessage transform(IncomingSensorMessage dto) {
        SensorMessage sensorMessage = new SensorMessage();
        sensorMessage.setImei(dto.getImei());
        sensorMessage.setSms(dto.getSms());
        sensorMessage.setState(dto.getState());
        sensorMessage.setMessage(dto.getMessage());
        sensorMessage.setData(dto.getGpsData());
        return sensorMessage;
    }
}

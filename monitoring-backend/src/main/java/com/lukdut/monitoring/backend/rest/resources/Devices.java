package com.lukdut.monitoring.backend.rest.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.ResourceSupport;

public class Devices extends ResourceSupport {
    private final Page<Long> devices;

    @JsonCreator
    public Devices(Page<Long> devices) {
        this.devices = devices;
    }

    public Page<Long> getDevices() {
        return devices;
    }
}

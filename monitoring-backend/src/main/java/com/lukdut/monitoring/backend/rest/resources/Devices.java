package com.lukdut.monitoring.backend.rest.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

public class Devices extends ResourceSupport {
    private final List<Long> imeis;

    @JsonCreator
    public Devices(@JsonProperty("imeis") List<Long> imeis) {
        this.imeis = imeis;
    }

    public List<Long> getContent() {
        return imeis;
    }
}

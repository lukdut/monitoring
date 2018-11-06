package com.lukdut.monitoring.backend.rest.resources;

import org.springframework.hateoas.ResourceSupport;

public class Status extends ResourceSupport {
    private final String value;

    public Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

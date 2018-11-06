package com.lukdut.monitoring.backend.rest.resources;

import org.springframework.hateoas.ResourceSupport;

public class Status extends ResourceSupport {
    private final String status;

    public Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

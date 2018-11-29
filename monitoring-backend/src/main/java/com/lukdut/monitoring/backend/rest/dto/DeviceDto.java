package com.lukdut.monitoring.backend.rest.dto;

import java.io.Serializable;

public class DeviceDto implements Serializable {
    private Long imei;
    private String name;
    private String description;

    public Long getImei() {
        return imei;
    }

    public void setImei(Long imei) {
        this.imei = imei;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "DeviceDto{" +
                "imei=" + imei +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

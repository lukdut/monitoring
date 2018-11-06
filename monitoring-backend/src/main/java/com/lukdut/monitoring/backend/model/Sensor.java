package com.lukdut.monitoring.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.StringJoiner;

@Entity
public class Sensor {
    public Sensor(){}

    public Sensor(Long imei){
        this.imei = imei;
    }

    @Id
    @JsonProperty
    private Long imei;
    private String state;

    public Long getImei() {
        return imei;
    }

    public void setImei(Long imei) {
        this.imei = imei;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Sensor.class.getSimpleName() + "[", "]")
                .add("imei=" + imei)
                .add("state='" + state + "'")
                .toString();
    }
}

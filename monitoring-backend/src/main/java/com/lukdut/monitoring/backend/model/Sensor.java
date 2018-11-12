package com.lukdut.monitoring.backend.model;

import javax.persistence.*;

@Entity
public class Sensor {
    public Sensor(){}

    public Sensor(Long imei){
        this.imei = imei;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Long imei;
    @Column(columnDefinition = "VARCHAR(255)")
    private String name;
    @Column(columnDefinition = "VARCHAR(2047)")
    private String description;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
        return "Sensor{" +
                "id=" + id +
                ", imei=" + imei +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}

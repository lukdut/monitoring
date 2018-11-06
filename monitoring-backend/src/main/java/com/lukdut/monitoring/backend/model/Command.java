package com.lukdut.monitoring.backend.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Command {
    @Id
    private Long id;
    private Long imei;
    @Column(columnDefinition = "VARCHAR(31)")
    private String command;
    @CreationTimestamp
    private Date timestamp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getImei() {
        return imei;
    }

    public void setImei(Long imei) {
        this.imei = imei;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Command{" +
                "id=" + id +
                ", imei=" + imei +
                ", command='" + command + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

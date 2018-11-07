package com.lukdut.monitoring.backend.model;

import com.lukdut.monitoring.gateway.dto.CommandState;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Command {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long imei;
    @Column(columnDefinition = "VARCHAR(31)")
    private String command;
    @CreationTimestamp
    private Date timestamp;
    private CommandState state = CommandState.CREATED;

    public Command(){}

    public Command(long imei, String command) {
        this.imei = imei;
        this.command = command;
    }

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getImei() {
        return imei;
    }

    public void setImei(long imei) {
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

    public CommandState getState() {
        return state;
    }

    public void setState(CommandState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Command{" + "id=" + id +
                ", imei=" + imei +
                ", command='" + command + '\'' +
                ", timestamp=" + timestamp +
                ", state=" + state +
                '}';
    }
}

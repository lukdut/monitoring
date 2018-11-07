package com.lukdut.monitoring.gateway.dto;

import java.io.Serializable;
import java.util.Date;

public class IntermodularSensorCommand implements Serializable {
    private long imei;
    private long id;
    private String command;
    private Date creationDate;
    private CommandState state = CommandState.CREATED;

    public IntermodularSensorCommand() {
    }

    public IntermodularSensorCommand(long id, long imei, String command,Date creationDate) {
        this.id = id;
        this.imei = imei;
        this.command = command;
        this.creationDate = creationDate;
    }

    public long getImei() {
        return imei;
    }

    public void setImei(long imei) {
        this.imei = imei;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public CommandState getState() {
        return state;
    }

    public void setState(CommandState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "IntermodularSensorCommand{" +
                "imei=" + imei +
                ", id=" + id +
                ", command='" + command + '\'' +
                ", creationDate=" + creationDate +
                ", state=" + state +
                '}';
    }
}

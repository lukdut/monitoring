package com.lukdut.monitoring.gateway.dto;

import java.io.Serializable;
import java.util.StringJoiner;

public class OutcomingSensorCommand implements Serializable {
    private long imei;
    private String command;

    public OutcomingSensorCommand() {
    }

    public OutcomingSensorCommand(long imei, String command) {
        this.imei = imei;
        this.command = command;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", OutcomingSensorCommand.class.getSimpleName() + "[", "]")
                .add("imei=" + imei)
                .add("command='" + command + "'")
                .toString();
    }
}

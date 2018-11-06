package com.lukdut.monitoring.backend.rest.resources;

import java.io.Serializable;
import java.util.StringJoiner;

public class CommandDto implements Serializable {
    private long imei;
    private String command;

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
        return new StringJoiner(", ", CommandDto.class.getSimpleName() + "[", "]")
                .add("imei=" + imei)
                .add("command='" + command + "'")
                .toString();
    }
}

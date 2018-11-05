package com.lukdut.monitoring.gateway.dto;

import java.io.Serializable;
import java.util.StringJoiner;

public class IncomingSensorMessage implements Serializable {
    private long imei;
    private String state;
    private String sms;
    private String gpsData;
    private String message;

    public long getImei() {
        return imei;
    }

    public void setImei(long imei) {
        this.imei = imei;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getGpsData() {
        return gpsData;
    }

    public void setGpsData(String gpsData) {
        this.gpsData = gpsData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IncomingSensorMessage.class.getSimpleName() + "[", "]")
                .add("imei=" + imei)
                .add("state='" + state + "'")
                .add("sms='" + sms + "'")
                .add("gpsData='" + gpsData + "'")
                .add("message='" + message + "'")
                .toString();
    }
}

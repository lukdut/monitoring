package com.lukdut.monitoring.gateway.dto;

import java.io.Serializable;

public class IncomingSensorMessage implements Serializable {
    private long imei;
    private String state;
    private String sms;
    private String ussd;
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

    public String getUssd() {
        return ussd;
    }

    public void setUssd(String ussd) {
        this.ussd = ussd;
    }

    @Override
    public String toString() {
        return "IncomingSensorMessage{" +
                "imei=" + imei +
                ", state='" + state + '\'' +
                ", sms='" + sms + '\'' +
                ", ussd='" + ussd + '\'' +
                ", gpsData='" + gpsData + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

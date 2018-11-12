package com.lukdut.monitoring.backend.model;


import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
public class SensorMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @CreationTimestamp
    private Date timestamp;
    private long imei;
    @Column(columnDefinition="VARCHAR(31)")
    private String state;
    @Column(columnDefinition="VARCHAR(511)")
    private String sms;
    @Column(columnDefinition="VARCHAR(511)")
    private String ussd;
    @Column(columnDefinition="VARCHAR(127)")
    private String data;
    @Column(columnDefinition="VARCHAR(511)")
    private String message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUssd() {
        return ussd;
    }

    public void setUssd(String ussd) {
        this.ussd = ussd;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "SensorMessage{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", imei=" + imei +
                ", state='" + state + '\'' +
                ", sms='" + sms + '\'' +
                ", ussd='" + ussd + '\'' +
                ", data='" + data + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

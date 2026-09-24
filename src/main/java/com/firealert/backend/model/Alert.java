package com.firealert.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "alerts")
@Getter
@Setter
public class Alert {

    @Id
    private String id;

    private String email;
    private String deviceId;
    private String alertType;
    private String status;
    private String deviceStatus;
    private Instant timestamp;

    public Alert() {
    }

    public Alert(String email, String deviceId, String alertType,
                 String status, String deviceStatus, Instant timestamp) {

        this.email = email;
        this.deviceId = deviceId;
        this.alertType = alertType;
        this.status = status;
        this.deviceStatus=deviceStatus;
        this.timestamp = timestamp;
    }
}
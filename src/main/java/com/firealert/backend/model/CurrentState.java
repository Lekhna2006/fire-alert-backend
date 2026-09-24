package com.firealert.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "currentStates")
@Getter
@Setter
public class CurrentState {

    @Id
    private String id;

    private String deviceId;
    private String fire;
    private String smoke;
    private Instant lastSeen;
    private String deviceStatus;

    public CurrentState() {
    }
}
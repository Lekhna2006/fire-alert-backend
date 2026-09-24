package com.firealert.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlertRequest {

    private String deviceId;
    private String fire;
    private String smoke;
}
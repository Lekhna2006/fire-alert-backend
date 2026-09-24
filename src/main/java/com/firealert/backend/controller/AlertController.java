package com.firealert.backend.controller;

import com.firealert.backend.model.Alert;
import com.firealert.backend.service.AlertService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="http://localhost:5173")
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public Alert createAlert(@RequestBody com.firealert.backend.dto.AlertRequest request) {
        return alertService.createAlert(request);
    }

    @GetMapping("/{deviceId}")
    public List<Alert> getAlerts(@PathVariable String deviceId) {
        return alertService.getAlertsByDeviceId(deviceId);
    }

    @DeleteMapping("/{deviceId}")
    public void deleteAlerts(@PathVariable String deviceId){
        alertService.deleteAlertsByDeviceId(deviceId);
    }
}
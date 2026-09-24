package com.firealert.backend.controller;

import com.firealert.backend.model.CurrentState;
import com.firealert.backend.repository.CurrentStateRepository;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="http://localhost:5173")
@RestController
@RequestMapping("/api/current-state")
public class CurrentStateController {

    private final CurrentStateRepository currentStateRepository;

    public CurrentStateController(CurrentStateRepository currentStateRepository) {
        this.currentStateRepository = currentStateRepository;
    }

    @GetMapping("/{deviceId}")
    public CurrentState getCurrentState(@PathVariable String deviceId) {

        CurrentState currentState = currentStateRepository
                .findByDeviceId(deviceId)
                .orElseThrow(() ->
                        new RuntimeException("Device state not found"));

        long secondsSinceLastSeen =
                java.time.Duration.between(
                        currentState.getLastSeen(),
                        java.time.Instant.now()
                ).getSeconds();

        if (secondsSinceLastSeen > 15) {
            currentState.setDeviceStatus("OFFLINE");
        } else {
            currentState.setDeviceStatus("ONLINE");
        }

        return currentState;
    }
}
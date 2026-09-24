package com.firealert.backend.service;

import com.firealert.backend.dto.AlertRequest;
import com.firealert.backend.model.Alert;
import com.firealert.backend.model.CurrentState;
import com.firealert.backend.model.User;
import com.firealert.backend.repository.AlertRepository;
import com.firealert.backend.repository.CurrentStateRepository;
import com.firealert.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final CurrentStateRepository currentStateRepository;
    private final EmailService emailService;

    public AlertService(AlertRepository alertRepository,
                        UserRepository userRepository,
                        CurrentStateRepository currentStateRepository, EmailService emailService) {

        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.currentStateRepository = currentStateRepository;
        this.emailService = emailService;
    }

    public Alert createAlert(AlertRequest request) {

        // Find the user registered with this device
        User user = userRepository
                .findByDeviceId(request.getDeviceId())
                .orElseThrow(() ->
                        new RuntimeException("Device not registered"));

        // Check the previous current state
        CurrentState currentState = currentStateRepository
                .findByDeviceId(request.getDeviceId())
                .orElse(null);

        boolean previousFireDanger =
                currentState != null &&
                        "DETECTED".equals(currentState.getFire());

        boolean previousSmokeDanger =
                currentState != null &&
                        "DETECTED".equals(currentState.getSmoke());

        boolean currentFireDanger =
                "DETECTED".equals(request.getFire());

        boolean currentSmokeDanger =
                "DETECTED".equals(request.getSmoke());

        // Create current-state document for first signal
        if (currentState == null) {
            currentState = new CurrentState();
            currentState.setDeviceId(request.getDeviceId());
        }

        // Update the SAME current-state document
        currentState.setFire(request.getFire());
        currentState.setSmoke(request.getSmoke());
        currentState.setLastSeen(Instant.now());

        currentStateRepository.save(currentState);

        // FIRE became dangerous now
        if (currentFireDanger && !previousFireDanger) {

            Alert alert = new Alert();

            alert.setEmail(user.getEmail());
            alert.setDeviceId(request.getDeviceId());
            alert.setAlertType("FIRE");
            alert.setStatus("DETECTED");
            alert.setDeviceStatus("ONLINE");
            alert.setTimestamp(Instant.now());

            Alert savedAlert = alertRepository.save(alert);

            emailService.sendAlertEmail(
                    user.getEmail(),
                    "FIRE",
                    request.getDeviceId()
            );

            return savedAlert;
        }

        // SMOKE became dangerous now
        if (currentSmokeDanger && !previousSmokeDanger) {

            Alert alert = new Alert();

            alert.setEmail(user.getEmail());
            alert.setDeviceId(request.getDeviceId());
            alert.setAlertType("SMOKE");
            alert.setStatus("DETECTED");
            alert.setDeviceStatus("ONLINE");
            alert.setTimestamp(Instant.now());

            Alert savedAlert = alertRepository.save(alert);

            emailService.sendAlertEmail(
                    user.getEmail(),
                    "SMOKE",
                    request.getDeviceId()
            );

            return savedAlert;
        }

        // SAFE signal or already-existing danger
        return null;
    }

    public List<Alert> getAlertsByDeviceId(String deviceId) {
        return alertRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public void deleteAlertsByDeviceId(String deviceId) {
        alertRepository.deleteByDeviceId(deviceId);
    }
}
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
                        CurrentStateRepository currentStateRepository,
                        EmailService emailService) {

        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.currentStateRepository = currentStateRepository;
        this.emailService = emailService;
    }

    public Alert createAlert(AlertRequest request) {

        // Find ALL users registered with this device
        List<User> users = userRepository
                .findAllByDeviceId(request.getDeviceId());

        if (users.isEmpty()) {
            throw new RuntimeException("Device not registered");
        }

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
        currentState.setDeviceStatus("ONLINE");

        currentStateRepository.save(currentState);

        Alert lastSavedAlert = null;

        // FIRE became dangerous now
        if (currentFireDanger && !previousFireDanger) {

            Alert fireAlert = new Alert();

            fireAlert.setEmail(users.get(0).getEmail());
            fireAlert.setDeviceId(request.getDeviceId());
            fireAlert.setAlertType("FIRE");
            fireAlert.setStatus("DETECTED");
            fireAlert.setDeviceStatus("ONLINE");
            fireAlert.setTimestamp(Instant.now());

            lastSavedAlert = alertRepository.save(fireAlert);

            // Send FIRE email to ALL users
            for (User user : users) {

                emailService.sendAlertEmail(
                        user.getEmail(),
                        "FIRE",
                        request.getDeviceId()
                );
            }
        }

        // SMOKE became dangerous now
        if (currentSmokeDanger && !previousSmokeDanger) {

            Alert smokeAlert = new Alert();

            smokeAlert.setEmail(users.get(0).getEmail());
            smokeAlert.setDeviceId(request.getDeviceId());
            smokeAlert.setAlertType("SMOKE");
            smokeAlert.setStatus("DETECTED");
            smokeAlert.setDeviceStatus("ONLINE");
            smokeAlert.setTimestamp(Instant.now());

            lastSavedAlert = alertRepository.save(smokeAlert);

            // Send SMOKE email to ALL users
            for (User user : users) {

                emailService.sendAlertEmail(
                        user.getEmail(),
                        "SMOKE",
                        request.getDeviceId()
                );
            }
        }

        // SAFE signal or already-existing danger
        return lastSavedAlert;
    }

    public List<Alert> getAlertsByDeviceId(String deviceId) {
        return alertRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    public void deleteAlertsByDeviceId(String deviceId) {
        alertRepository.deleteByDeviceId(deviceId);
    }
}
package com.firealert.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAlertEmail(String to, String alertType, String deviceId) {

        SimpleMailMessage message = new SimpleMailMessage();

        Instant now = Instant.now();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")
                        .withZone(ZoneId.of("Asia/Kolkata"));

        String dateTime = formatter.format(now);

        message.setTo(to);

        message.setSubject("Fire Alert - " + alertType + " Detected");

        message.setText(
                "🔥 FIRE ALERT\n\n" +
                        "Alert Type: " + alertType + "\n" +
                        "Device ID: " + deviceId + "\n" +
                        "Date & Time: " + dateTime + "\n\n" +
                        "Please check your Fire Alert website for more details.\n\n" +
                        "🌐 Website:\n" +
                        "Website link will be added after deployment."
        );

        mailSender.send(message);
    }
}
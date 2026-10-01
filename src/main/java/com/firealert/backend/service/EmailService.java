/*package com.firealert.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class EmailService {

    private final RestClient restClient;

    @Value("${resend.api.key}")
    private String resendApiKey;

    public EmailService(RestClient resendRestClient) {
        this.restClient = resendRestClient;
    }

    public void sendAlertEmail(String to, String alertType, String deviceId) {

        Instant now = Instant.now();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")
                        .withZone(ZoneId.of("Asia/Kolkata"));

        String dateTime = formatter.format(now);

        String emailText =
                "🔥 FIRE ALERT\n\n" +
                        "Alert Type: " + alertType + "\n" +
                        "Device ID: " + deviceId + "\n" +
                        "Date & Time: " + dateTime + "\n\n" +
                        "Please check your Fire Alert website for more details.\n\n" +
                        "🌐 Website:\n" +
                        "https://fire-alert-frontend.vercel.app/";

        Map<String, Object> requestBody = Map.of(
                "from", "Fire Alert <onboarding@resend.dev>",
                "to", new String[]{"lekhna910@gmail.com"},
                "subject", "Fire Alert - " + alertType + " Detected",
                "text", emailText
        );

        restClient.post()
                .uri("/emails")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + resendApiKey
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toBodilessEntity();
    }
}*/

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
                        "https://fire-alert-frontend.vercel.app/"

        );

        mailSender.send(message);
    }
}
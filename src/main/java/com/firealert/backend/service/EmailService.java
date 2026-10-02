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

/*package com.firealert.backend.service;

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
}*/

package com.firealert.backend.service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Properties;

@Service
public class EmailService {

    private final Gmail gmail;

    @Value("${google.sender-email}")
    private String senderEmail;

    public EmailService(Gmail gmail) {
        this.gmail = gmail;
    }

    public void sendAlertEmail(String to, String alertType, String deviceId) {

        try {
            Instant now = Instant.now();

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")
                            .withZone(ZoneId.of("Asia/Kolkata"));

            String dateTime = formatter.format(now);

            String subject = "Fire Alert - " + alertType + " Detected";

            String body =
                    "🔥 FIRE ALERT\n\n" +
                            "Alert Type: " + alertType + "\n" +
                            "Device ID: " + deviceId + "\n" +
                            "Date & Time: " + dateTime + "\n\n" +
                            "Please check your Fire Alert website for more details.\n\n" +
                            "🌐 Website:\n" +
                            "https://fire-alert-frontend.vercel.app/";

            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);

            MimeMessage email = new MimeMessage(session);

            email.setFrom(new InternetAddress(senderEmail));
            email.addRecipient(
                    jakarta.mail.Message.RecipientType.TO,
                    new InternetAddress(to)
            );

            email.setSubject(subject, "UTF-8");
            email.setText(body, "UTF-8");

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);

            String encodedEmail = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(buffer.toByteArray());

            Message message = new Message();
            message.setRaw(encodedEmail);

            gmail.users()
                    .messages()
                    .send("me", message)
                    .execute();

            System.out.println("Alert email sent successfully to: " + to);

        } catch (Exception e) {
            System.err.println("Failed to send alert email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package com.koolboks.creditProject.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BrevoEmailService {

    private static final Logger log = LoggerFactory.getLogger(BrevoEmailService.class);
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    private final RestTemplate restTemplate;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${notification.email.from:foltim256@gmail.com}")
    private String fromEmail;

    public BrevoEmailService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendEmail(String toEmail, String toName, String subject, String content) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);
            headers.set("accept", "application/json");

            Map<String, Object> sender = new HashMap<>();
            sender.put("name", "KoolKredit");
            sender.put("email", fromEmail);

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("email", toEmail);
            recipient.put("name", toName != null ? toName : toEmail);

            Map<String, Object> body = new HashMap<>();
            body.put("sender", sender);
            body.put("to", List.of(recipient));
            body.put("subject", subject);

            // Use htmlContent or textContent depending on content type
            if (content.trim().startsWith("<")) {
                body.put("htmlContent", content);
            } else {
                body.put("textContent", content);
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                BREVO_API_URL, HttpMethod.POST, entity, String.class
            );

            log.info("Email sent via Brevo API to: {} - Status: {}", toEmail, response.getStatusCode());

        } catch (Exception e) {
            log.error("Error sending email via Brevo API to: {}", toEmail, e);
        }
    }


    public void sendEmailWithAttachment(String toEmail, String toName, String subject,
                                     String htmlContent, byte[] attachmentBytes,
                                     String attachmentName) {
    try {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", brevoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("sender", Map.of("name", "KoolKredit", "email", fromEmail));
        body.put("to", List.of(Map.of("email", toEmail, "name", toName)));
        body.put("subject", subject);
        body.put("htmlContent", htmlContent);

        // Base64 encode the attachment
        String encodedPdf = java.util.Base64.getEncoder().encodeToString(attachmentBytes);
        body.put("attachment", List.of(Map.of(
            "content", encodedPdf,
            "name", attachmentName
        )));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            BREVO_API_URL, HttpMethod.POST, request, String.class
        );

        log.info("Email with attachment sent via Brevo API to: {} - Status: {}", toEmail, response.getStatusCode());

    } catch (Exception e) {
        log.error("Failed to send email with attachment via Brevo API: {}", e.getMessage(), e);
        throw new RuntimeException("Failed to send email with attachment", e);
    }
}
}
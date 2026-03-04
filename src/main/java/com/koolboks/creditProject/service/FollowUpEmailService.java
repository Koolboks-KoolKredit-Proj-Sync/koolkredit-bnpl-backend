package com.koolboks.creditProject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.koolboks.creditProject.dto.AgentFollowUpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class FollowUpEmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.email.to:foltim256@gmail.com}")
    private String notificationEmail;

    @Value("${notification.email.from:foltim256@gmail.com}")
    private String fromEmail;

    public FollowUpEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendCrcReportEmail(AgentFollowUpRequest req, JsonNode crcJson, String classification, double dti) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(notificationEmail);
            message.setSubject("Agent Follow-Up CRC Report - " + classification);

            StringBuilder sb = new StringBuilder();
            sb.append("Classification: ").append(classification).append("\n");
            sb.append("DTI (%): ").append(String.format("%.2f", dti)).append("\n\n");

            sb.append("Request details:\n");
            sb.append("BVN: ").append(req.getBvn()).append("\n");
            sb.append("NIN: ").append(req.getNin()).append("\n");
            sb.append("Mobile: ").append(req.getMobileNumber()).append("\n");
            sb.append("Usage: ").append(req.getUsageType()).append("\n");
            sb.append("Monthly Income: ").append(req.getMonthlyIncome()).append("\n");
            sb.append("Monthly Sales: ").append(req.getMonthlySales()).append("\n\n");

            sb.append("Full CRC JSON:\n");
            sb.append(crcJson == null ? "{}" : crcJson.toPrettyString());

            message.setText(sb.toString());

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}










//package com.koolboks.creditProject.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.koolboks.creditProject.dto.AgentFollowUpRequest;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//public class FollowUpEmailService {
//
//    private final JavaMailSender mailSender;
//
//    @Value("${notification.email.to:admin@example.com}")
//    private String notificationEmail;
//
//    @Value("${notification.email.from:noreply@agententry.com}")
//    private String fromEmail;
//
//    public FollowUpEmailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    public void sendCrcReportEmail(AgentFollowUpRequest req, JsonNode crcJson, String classification, double dti) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(fromEmail);
//            message.setTo(notificationEmail);
//            message.setSubject("CRC Report & DTI - " + req.getBvn() + " - " + classification);
//
//            String body = buildBody(req, crcJson, classification, dti);
//            message.setText(body);
//            mailSender.send(message);
//        } catch (Exception ex) {
//            // log error
//            System.err.println("Error sending CRC email: " + ex.getMessage());
//        }
//    }
//
//    private String buildBody(AgentFollowUpRequest req, JsonNode crcJson, String classification, double dti) {
//        return String.format(
//                "Agent FollowUp CRC Report\n\nBVN: %s\nMobile: %s\nUsage: %s\nClassification: %s\nDTI: %.2f%%\n\nFull CRC JSON:\n%s",
//                req.getBvn(), req.getMobileNumber(), req.getUsage(), classification, dti,
//                crcJson.toPrettyString()
//        );
//    }
//}
//
//
//

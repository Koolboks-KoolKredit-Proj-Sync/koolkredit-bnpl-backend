package com.koolboks.creditProject.service.delivery;


import com.koolboks.creditProject.service.BrevoEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;



@Service
public class AgentDeliveryEmailService {

    private final BrevoEmailService brevoEmailService;

    @Value("${notification.email.from}")
    private String fromEmail;

    public AgentDeliveryEmailService(BrevoEmailService brevoEmailService) {
        this.brevoEmailService = brevoEmailService;
    }

    public void sendAgentDeliveryEmail(Map<String, Object> deliveryData) {
        String htmlContent = buildAgentEmailContent(deliveryData);
        brevoEmailService.sendEmail(
            String.valueOf(deliveryData.get("agentEmail")),
            "Agent",
            "🚚 Delivery Scheduled - Order ID: " + deliveryData.get("orderId"),
            htmlContent
        );
        System.out.println("Agent delivery email sent to: " + deliveryData.get("agentEmail"));
    }

    // keep buildAgentEmailContent unchanged



//@Service
//public class AgentDeliveryEmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${notification.email.from}")
//    private String fromEmail;
//
//    public void sendAgentDeliveryEmail(Map<String, Object> deliveryData) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(fromEmail);
//            helper.setTo(String.valueOf(deliveryData.get("agentEmail")));
//            helper.setSubject("🚚 Delivery Scheduled - Order ID: " + deliveryData.get("orderId"));
//
//            String htmlContent = buildAgentEmailContent(deliveryData);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//
//            System.out.println("Agent delivery email sent to: " + deliveryData.get("agentEmail"));
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send agent delivery email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private String buildAgentEmailContent(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<h1 style=\"color:#ffffff;margin:0;\">Delivery Scheduled</h1>");
        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:18px;font-weight:bold;\">Order ID: ").append(data.get("orderId")).append("</p>");
        html.append("</div>");

        // Success Banner
        html.append("<div style=\"background-color:#d4edda;border-left:4px solid #28a745;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0;color:#155724;font-weight:bold;\">");
        html.append("✅ Delivery has been scheduled for your customer.");
        html.append("</p></div>");

        // Delivery Details
        html.append("<div style=\"margin:20px;background-color:#f7623b;border-radius:8px;padding:20px;\">");
        html.append("<h2 style=\"color:#000000;margin:0 0 15px 0;\">Delivery Schedule</h2>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Date:</strong> ").append(data.get("deliveryDate")).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Time:</strong> ").append(data.get("deliveryTime")).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Unit Serial:</strong> ").append(data.get("unitSerialNumber")).append("</p>");
        html.append("</div>");

        // Customer Details
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Customer Information</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Name:</strong> ").append(data.get("customerFirstName")).append(" ").append(data.get("customerLastName")).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Phone:</strong> ").append(data.get("customerPhoneNumber")).append("</p>");
        html.append("</div>");

        // Action Required
        html.append("<div style=\"background-color:#fff3cd;padding:15px 20px;margin:20px;border-radius:8px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">📋 Your Actions:</p>");
        html.append("<ul style=\"margin:10px 0;padding-left:20px;color:#856404;\">");
        html.append("<li>Coordinate with customer for delivery</li>");
        html.append("<li>Be available during installation</li>");
        html.append("<li>You'll receive the verification PIN for final confirmation</li>");
        html.append("</ul>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Agent Portal</p>");
        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }
}



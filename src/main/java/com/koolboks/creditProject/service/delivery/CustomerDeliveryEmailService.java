package com.koolboks.creditProject.service.delivery;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class CustomerDeliveryEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email.from}")
    private String fromEmail;

    public void sendCustomerDeliveryEmail(Map<String, Object> deliveryData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(String.valueOf(deliveryData.get("customerEmail")));
            helper.setSubject("🚚 Your Delivery is Scheduled - Order ID: " + deliveryData.get("orderId"));

            String htmlContent = buildCustomerEmailContent(deliveryData);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("Customer delivery email sent to: " + deliveryData.get("customerEmail"));

        } catch (MessagingException e) {
            System.err.println("Failed to send customer delivery email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildCustomerEmailContent(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;\">");
        html.append("<span style=\"font-size:48px;\">🚚</span>");
        html.append("</div>");
        html.append("<h1 style=\"color:#ffffff;margin:0;\">Your Delivery is Scheduled!</h1>");
        html.append("</div>");

        // Success Banner
        html.append("<div style=\"background-color:#d4edda;border-left:4px solid #28a745;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0;color:#155724;font-weight:bold;\">");
        html.append("✅ Great news! Your ").append(data.get("productName")).append(" delivery has been scheduled.");
        html.append("</p></div>");

        // Delivery Details
        html.append("<div style=\"margin:20px;background-color:#f7623b;border-radius:8px;padding:20px;text-align:center;\">");
        html.append("<h2 style=\"color:#000000;margin:0 0 15px 0;\">Delivery Details</h2>");
        html.append("<p style=\"color:#000000;font-size:24px;font-weight:bold;margin:10px 0;\">📅 ").append(data.get("deliveryDate")).append("</p>");
        html.append("<p style=\"color:#000000;font-size:20px;margin:10px 0;\">🕐 ").append(data.get("deliveryTime")).append("</p>");
        html.append("</div>");

        // Product & Serial
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">What's Being Delivered</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Product:</strong> ").append(data.get("productName")).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Brand:</strong> ").append(data.get("productBrand")).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Serial Number:</strong> ").append(data.get("unitSerialNumber")).append("</p>");
        html.append("</div>");

        // Important Notice
        html.append("<div style=\"background-color:#fff3cd;padding:15px 20px;margin:20px;border-radius:8px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">📋 Important:</p>");
        html.append("<ul style=\"margin:10px 0;padding-left:20px;color:#856404;\">");
        html.append("<li>Please be available at the delivery time</li>");
        html.append("<li>Installation will be done on-site</li>");
        html.append("<li>You'll receive a verification PIN for final confirmation</li>");
        html.append("</ul>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Thank you for choosing Koolboks</p>");
        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }
}

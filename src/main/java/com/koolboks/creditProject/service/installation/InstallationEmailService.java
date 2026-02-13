package com.koolboks.creditProject.service.installation;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class InstallationEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${installation.team.email}")
    private String installationTeamEmail;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    public void sendInstallationNotificationEmail(Map<String, Object> deliveryData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(installationTeamEmail);
            helper.setSubject("🔧 On-Site Installation Required - Order ID: " + deliveryData.get("orderId"));

            String htmlContent = buildInstallationEmailContent(deliveryData);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("Installation team email sent for order: " + deliveryData.get("orderId"));

        } catch (MessagingException e) {
            System.err.println("Failed to send installation email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildInstallationEmailContent(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;\">");
        html.append("<span style=\"font-size:48px;\">🔧</span>");
        html.append("</div>");
        html.append("<h1 style=\"color:#ffffff;margin:0;\">On-Site Installation Required</h1>");
        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:18px;font-weight:bold;\">Order ID: ").append(data.get("orderId")).append("</p>");
        html.append("</div>");

        // Alert Banner
        html.append("<div style=\"background-color:#fff3cd;border-left:4px solid #f7623b;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">");
        html.append("🔔 ACTION REQUIRED: On-site installation scheduled. Please prepare for deployment.");
        html.append("</p></div>");

        // Installation Schedule
        html.append("<div style=\"margin:20px;background-color:#f7623b;border-radius:8px;padding:20px;text-align:center;\">");
        html.append("<h2 style=\"color:#000000;margin:0 0 15px 0;\">Installation Schedule</h2>");
        html.append("<p style=\"color:#000000;font-size:24px;font-weight:bold;margin:10px 0;\">📅 ").append(data.get("deliveryDate")).append("</p>");
        html.append("<p style=\"color:#000000;font-size:20px;margin:10px 0;\">🕐 ").append(data.get("deliveryTime")).append("</p>");
        html.append("</div>");

        // Unit Details
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Unit Information</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Serial Number:</strong> ").append(data.get("unitSerialNumber")).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Product:</strong> ").append(data.get("productName")).append("</p>");
        html.append("</div>");

        // Customer & Location
        html.append("<div style=\"margin:20px;background-color:#0a0a0a;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Installation Location</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Customer:</strong> ").append(data.get("customerFirstName")).append(" ").append(data.get("customerLastName")).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Phone:</strong> ").append(data.get("customerPhoneNumber")).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Email:</strong> ").append(data.get("customerEmail")).append("</p>");
        html.append("</div>");

        // Confirm Installation Button
        String buttonUrl = buildButtonUrl(data);

        html.append("<div style=\"text-align:center;padding:30px 20px;\">");
        html.append("<a href=\"").append(buttonUrl).append("\" style=\"display:inline-block;padding:16px 48px;background-color:#f7623b;color:#000000;text-decoration:none;font-weight:bold;font-size:16px;border-radius:8px;box-shadow:0 4px 12px rgba(247,98,59,0.3);\">");
        html.append("🔧 Confirm On Site Installation");
        html.append("</a>");
        html.append("</div>");

        // Button Description
        html.append("<div style=\"text-align:center;padding:0 20px 30px;\">");
        html.append("<p style=\"color:#666666;font-size:14px;margin:0;\">");
        html.append("Click the button above after completing the on-site installation");
        html.append("</p></div>");

        // Installation Checklist
        html.append("<div style=\"background-color:#fff3cd;padding:15px 20px;margin:20px;border-radius:8px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">📋 Installation Checklist:</p>");
        html.append("<ul style=\"margin:10px 0;padding-left:20px;color:#856404;\">");
        html.append("<li>Verify unit serial number matches: ").append(data.get("unitSerialNumber")).append("</li>");
        html.append("<li>Complete on-site installation</li>");
        html.append("<li>Test unit functionality</li>");
        html.append("<li>Train customer on usage</li>");
        html.append("<li>Take photo of customer with installed product</li>");
        html.append("<li>Get customer's verification PIN for final confirmation</li>");
        html.append("</ul>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Installation Management System</p>");
        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

//    private String buildButtonUrl(Map<String, Object> data) {
//        try {
//            StringBuilder url = new StringBuilder(frontendBaseUrl);
//            url.append("/installation-confirmation?");
//
//            url.append("orderId=").append(java.net.URLEncoder.encode(String.valueOf(data.get("orderId")), "UTF-8"));
//            url.append("&unitSerialNumber=").append(java.net.URLEncoder.encode(String.valueOf(data.get("unitSerialNumber")), "UTF-8"));
//            url.append("&customerFirstName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerFirstName")), "UTF-8"));
//            url.append("&customerLastName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerLastName")), "UTF-8"));
//            url.append("&customerEmail=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerEmail")), "UTF-8"));
//            url.append("&customerPhoneNumber=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerPhoneNumber")), "UTF-8"));
//            url.append("&deliveryDate=").append(java.net.URLEncoder.encode(String.valueOf(data.get("deliveryDate")), "UTF-8"));
//            url.append("&productName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("productName")), "UTF-8"));
//            url.append("&productBrand=").append(java.net.URLEncoder.encode(String.valueOf(data.get("productBrand")), "UTF-8"));
//            url.append("&productSize=").append(java.net.URLEncoder.encode(String.valueOf(data.get("productSize")), "UTF-8"));
//
//            return url.toString();
//
//        } catch (java.io.UnsupportedEncodingException e) {
//            System.err.println("Error encoding URL parameters: " + e.getMessage());
//            return frontendBaseUrl + "/installation-confirmation";
//        }
//    }



    private String buildButtonUrl(Map<String, Object> data) {
    try {
        StringBuilder url = new StringBuilder(frontendBaseUrl);
        url.append("/installation-confirmation?");

        url.append("orderId=").append(java.net.URLEncoder.encode(String.valueOf(data.get("orderId")), "UTF-8"));
        url.append("&unitSerialNumber=").append(java.net.URLEncoder.encode(String.valueOf(data.get("unitSerialNumber")), "UTF-8"));
        url.append("&customerFirstName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerFirstName")), "UTF-8"));
        url.append("&customerLastName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerLastName")), "UTF-8"));
        url.append("&customerEmail=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerEmail")), "UTF-8"));
        url.append("&customerPhoneNumber=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerPhoneNumber")), "UTF-8"));
        url.append("&deliveryDate=").append(java.net.URLEncoder.encode(String.valueOf(data.get("deliveryDate")), "UTF-8"));
        url.append("&productName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("productName")), "UTF-8"));
        url.append("&productBrand=").append(java.net.URLEncoder.encode(String.valueOf(data.get("productBrand")), "UTF-8"));
        url.append("&productSize=").append(java.net.URLEncoder.encode(String.valueOf(data.get("productSize")), "UTF-8"));

        return url.toString();

    } catch (java.io.UnsupportedEncodingException e) {
        System.err.println("Error encoding URL parameters: " + e.getMessage());
        return frontendBaseUrl + "/installation-confirmation";
    }
}
}


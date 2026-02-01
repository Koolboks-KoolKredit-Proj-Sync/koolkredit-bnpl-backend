//package com.koolboks.creditProject.service.email_paygo_config;
//
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.TemplateEngine;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import java.util.Map;
//
//@Service
//public class PaygoEmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private TemplateEngine templateEngine;
//
//    @Value("${after.sales.team.email}")
//    private String afterSalesTeamEmail;
//
//    @Value("${notification.email.from}")
//    private String fromEmail;
//
//    /**
//     * Send Paygo Configuration Email to After Sales Team
//     */
//    public void sendPaygoConfigurationEmail(Map<String, Object> emailData) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            // Set email properties
//            helper.setFrom(fromEmail);
//            helper.setTo(afterSalesTeamEmail);
//            helper.setSubject("🔔 Store Payment Confirmed - Paygo Configuration Required - Loan Ref: "
//                + emailData.get("customerLoanRef"));
//
//            // Process HTML template
//            String htmlContent = buildEmailContent(emailData);
//            helper.setText(htmlContent, true);
//
//            // Send email
//            mailSender.send(message);
//
//            System.out.println("Paygo configuration email sent successfully for loan ref: "
//                + emailData.get("customerLoanRef"));
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send paygo configuration email: " + e.getMessage());
//            e.printStackTrace();
//            // Consider logging to a proper logging system
//        }
//    }
//
//    /**
//     * Build HTML email content
//     */
//    private String buildEmailContent(Map<String, Object> data) {
//        StringBuilder html = new StringBuilder();
//
//        html.append("<!DOCTYPE html>");
//        html.append("<html lang=\"en\">");
//        html.append("<head>");
//        html.append("<meta charset=\"UTF-8\">");
//        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
//        html.append("<title>Store Payment Confirmation - Paygo Configuration Required</title>");
//        html.append("</head>");
//        html.append("<body style=\"margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; background-color: #f7623b;\">");
//
//        html.append("<table role=\"presentation\" style=\"width: 100%; border-collapse: collapse; background-color: #f7623b; padding: 40px 20px;\">");
//        html.append("<tr><td align=\"center\">");
//        html.append("<table role=\"presentation\" style=\"max-width: 600px; width: 100%; background-color: #000000; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 40px rgba(0,0,0,0.3);\">");
//
//        // Header
//        html.append("<tr><td style=\"background-color: #f7623b; padding: 40px 30px; text-align: center;\">");
//        html.append("<div style=\"display: inline-block; width: 64px; height: 64px; background-color: #000000; border-radius: 50%; line-height: 64px; margin-bottom: 16px;\">");
//        html.append("<span style=\"color: #f7623b; font-size: 32px;\">⚡</span>");
//        html.append("</div>");
//        html.append("<h1 style=\"margin: 0; color: #000000; font-size: 28px; font-weight: bold;\">Store Payment Confirmed</h1>");
//        html.append("<p style=\"margin: 8px 0 0 0; color: #000000; font-size: 16px; opacity: 0.9;\">Paygo Configuration Required</p>");
//        html.append("</td></tr>");
//
//        // Alert Banner
//        html.append("<tr><td style=\"padding: 24px 30px; background-color: #1a1a1a; border-left: 4px solid #f7623b;\">");
//        html.append("<p style=\"margin: 0; color: #ffffff; font-size: 14px;\">");
//        html.append("<strong style=\"color: #f7623b;\">🔔 ACTION REQUIRED:</strong> A store payment has been confirmed. Please configure the Paygo unit immediately.");
//        html.append("</p></td></tr>");
//
//        // Customer Information
//        html.append(buildSection("Customer Information", new String[][]{
//            {"👤 Full Name", data.get("customerFirstName") + " " + data.get("customerLastName")},
//            {"📧 Email", String.valueOf(data.get("customerEmail"))},
//            {"📱 Phone Number", String.valueOf(data.get("customerPhoneNumber"))},
//            {"🔖 Loan Reference", String.valueOf(data.get("customerLoanRef"))},
//            {"⏱️ Loan Duration", data.get("customerLoanDuration") + " months"}
//        }, "#000000"));
//
//        // Agent Information
//        html.append(buildSection("Agent Information", new String[][]{
//            {"👨‍💼 Agent Name", String.valueOf(data.get("agentName"))},
//            {"🆔 Agent ID", String.valueOf(data.get("agentId"))},
//            {"📧 Email", String.valueOf(data.get("agentEmail"))},
//            {"📱 Phone Number", String.valueOf(data.get("agentNumber"))}
//        }, "#0a0a0a"));
//
//        // Store & Payment Information
//        html.append(buildSection("Store & Payment Details", new String[][]{
//            {"🏪 Store Name", String.valueOf(data.get("storeName"))},
//            {"📍 Store Location", String.valueOf(data.get("storeLocation"))},
//            {"💰 Loan Amount", "₦" + String.valueOf(data.get("initialInstalment"))},
//            {"📅 Payment Date", String.valueOf(data.get("paymentDate"))}
//        }, "#000000"));
//
//        // Product Information
//        html.append(buildSection("Product Information", new String[][]{
//            {"📦 Product Name", String.valueOf(data.get("productName"))},
//            {"🏷️ Brand", String.valueOf(data.get("productBrand"))},
//            {"📏 Size", String.valueOf(data.get("productSize"))}
//        }, "#0a0a0a"));
//
//        // Action Button
//        boolean isConfigured = Boolean.TRUE.equals(data.get("isConfigured"));
//        String buttonColor = isConfigured ? "#666666" : "#f7623b";
//        String buttonText = isConfigured ? "✓ Paygo Configured" : "⚡ Confirm Paygo Configuration";
//        String buttonStyle = isConfigured ?
//            "display: inline-block; padding: 16px 48px; background-color: " + buttonColor + "; color: #ffffff; text-decoration: none; font-weight: bold; font-size: 16px; border-radius: 8px; cursor: not-allowed; opacity: 0.6;" :
//            "display: inline-block; padding: 16px 48px; background-color: " + buttonColor + "; color: #000000; text-decoration: none; font-weight: bold; font-size: 16px; border-radius: 8px; box-shadow: 0 4px 12px rgba(247, 98, 59, 0.3);";
//
//        html.append("<tr><td style=\"padding: 40px 30px; background-color: #000000; text-align: center;\">");
//        if (!isConfigured) {
//            html.append("<a href=\"").append(data.get("confirmationUrl")).append("\" style=\"").append(buttonStyle).append("\">");
//            html.append(buttonText);
//            html.append("</a>");
//        } else {
//            html.append("<span style=\"").append(buttonStyle).append("\">");
//            html.append(buttonText);
//            html.append("</span>");
//        }
//        html.append("<p style=\"margin: 16px 0 0 0; color: #888888; font-size: 12px;\">");
//        html.append(isConfigured ?
//            "Paygo configuration has been confirmed" :
//            "Click the button above once you have completed the Paygo configuration");
//        html.append("</p></td></tr>");
//
//        // Footer
//        html.append("<tr><td style=\"padding: 30px; background-color: #0a0a0a; text-align: center; border-top: 1px solid #1a1a1a;\">");
//        html.append("<p style=\"margin: 0 0 8px 0; color: #888888; font-size: 12px;\">");
//        html.append("This is an automated notification from the Koolboks Loan Management System");
//        html.append("</p>");
//        html.append("<p style=\"margin: 0; color: #666666; font-size: 11px;\">");
//        html.append("© 2026 Koolboks. All rights reserved.");
//        html.append("</p></td></tr>");
//
//        html.append("</table>");
//        html.append("</td></tr></table>");
//        html.append("</body></html>");
//
//        return html.toString();
//    }
//
//    /**
//     * Build a section with data rows
//     */
//    private String buildSection(String title, String[][] rows, String bgColor) {
//        StringBuilder html = new StringBuilder();
//
//        html.append("<tr><td style=\"padding: 30px; background-color: ").append(bgColor).append(";\">");
//        html.append("<h2 style=\"margin: 0 0 20px 0; color: #f7623b; font-size: 20px; font-weight: bold; border-bottom: 2px solid #f7623b; padding-bottom: 10px;\">");
//        html.append(title);
//        html.append("</h2>");
//
//        html.append("<table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">");
//
//        for (int i = 0; i < rows.length; i++) {
//            String borderStyle = i > 0 ? "border-top: 1px solid #1a1a1a;" : "";
//            String[] row = rows[i];
//
//            html.append("<tr>");
//            html.append("<td style=\"padding: 12px 0; color: #888888; font-size: 14px; width: 40%; ").append(borderStyle).append("\">");
//            html.append("<strong>").append(row[0]).append("</strong>");
//            html.append("</td>");
//            html.append("<td style=\"padding: 12px 0; color: #ffffff; font-size: 14px; ").append(borderStyle).append("\">");
//            html.append(row[1]);
//            html.append("</td>");
//            html.append("</tr>");
//        }
//
//        html.append("</table>");
//        html.append("</td></tr>");
//
//        return html.toString();
//    }
//}








//   package com.koolboks.creditProject.service.email_paygo_config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import java.io.UnsupportedEncodingException;
//import java.net.URLEncoder;
//import java.util.Map;
//
//@Service
//public class PaygoEmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${after.sales.team.email}")
//    private String afterSalesTeamEmail;
//
//    @Value("${notification.email.from}")
//    private String fromEmail;
//
//    @Value("${frontend.base.url}")
//    private String frontendBaseUrl; // e.g., http://localhost:3000
//
//    /**
//     * Send Paygo Configuration Email to After Sales Team
//     */
//    public void sendPaygoConfigurationEmail(Map<String, Object> emailData) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            // Set email properties
//            helper.setFrom(fromEmail);
//            helper.setTo(afterSalesTeamEmail);
//            helper.setSubject("🔔 Store Payment Confirmed - Paygo Configuration Required - Loan Ref: "
//                + emailData.get("customerLoanRef"));
//
//            // Process HTML template
//            String htmlContent = buildEmailContent(emailData);
//            helper.setText(htmlContent, true);
//
//            // Send email
//            mailSender.send(message);
//
//            System.out.println("Paygo configuration email sent successfully for loan ref: "
//                + emailData.get("customerLoanRef"));
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send paygo configuration email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Build HTML email content
//     */
//    private String buildEmailContent(Map<String, Object> data) {
//        StringBuilder html = new StringBuilder();
//
//        html.append("<!DOCTYPE html>");
//        html.append("<html>");
//        html.append("<head>");
//        html.append("<meta charset=\"UTF-8\">");
//        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
//        html.append("<title>Paygo Configuration Required</title>");
//        html.append("</head>");
//        html.append("<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;\">");
//        html.append("<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff;\">");
//
//        // Header
//        html.append("<div style=\"background: linear-gradient(135deg, #000000 0%, #1a1a1a 100%); padding: 40px 20px; text-align: center;\">");
//        html.append("<div style=\"background-color: #f7623b; width: 80px; height: 80px; border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center;\">");
//        html.append("<span style=\"font-size: 48px;\">⚡</span>");
//        html.append("</div>");
//        html.append("<h1 style=\"color: #ffffff; margin: 0 0 10px 0; font-size: 28px;\">Store Payment Confirmed</h1>");
//        html.append("<p style=\"color: #f7623b; margin: 0; font-size: 16px; font-weight: bold;\">Paygo Configuration Required</p>");
//        html.append("</div>");
//
//        // Alert Banner
//        html.append("<div style=\"background-color: #fff3cd; border-left: 4px solid #f7623b; padding: 15px 20px; margin: 20px;\">");
//        html.append("<p style=\"margin: 0; color: #856404; font-weight: bold;\">");
//        html.append("🔔 ACTION REQUIRED: A store payment has been confirmed. Please configure the Paygo unit immediately.");
//        html.append("</p>");
//        html.append("</div>");
//
//        // Customer Information
//        html.append(buildSection("Customer Information", new String[][]{
//            {"👤 Full Name", data.get("customerFirstName") + " " + data.get("customerLastName")},
//            {"📧 Email", String.valueOf(data.get("customerEmail"))},
//            {"📱 Phone Number", String.valueOf(data.get("customerPhoneNumber"))},
//            {"🔖 Loan Reference", String.valueOf(data.get("customerLoanRef"))},
//            {"⏱️ Loan Duration", data.get("customerLoanDuration") + " months"}
//        }, "#000000"));
//
//        // Agent Information
//        html.append(buildSection("Agent Information", new String[][]{
//            {"👨‍💼 Agent Name", String.valueOf(data.get("agentName"))},
//            {"🆔 Agent ID", String.valueOf(data.get("agentId"))},
//            {"📧 Email", String.valueOf(data.get("agentEmail"))},
//            {"📱 Phone Number", String.valueOf(data.get("agentNumber"))}
//        }, "#0a0a0a"));
//
//        // Store & Payment Information
//        html.append(buildSection("Store & Payment Details", new String[][]{
//            {"🏪 Store Name", String.valueOf(data.get("storeName"))},
//            {"📍 Store Location", String.valueOf(data.get("storeLocation"))},
//            {"💰 Initial Installment", "₦" + String.valueOf(data.get("initialInstalment"))},
//            {"📅 Payment Date", String.valueOf(data.get("paymentDate"))}
//        }, "#000000"));
//
//        // Product Information
//        html.append(buildSection("Product Information", new String[][]{
//            {"📦 Product Name", String.valueOf(data.get("productName"))},
//            {"🏷️ Brand", String.valueOf(data.get("productBrand"))},
//            {"📏 Size", String.valueOf(data.get("productSize"))}
//        }, "#0a0a0a"));
//
//        // Action Button
//        boolean isConfigured = Boolean.TRUE.equals(data.get("isConfigured"));
//        String buttonUrl = buildButtonUrl(data);
//
//        String buttonColor = isConfigured ? "#666666" : "#f7623b";
//        String buttonText = isConfigured ? "✓ Paygo Configured" : "⚡ Configure Paygo";
//        String buttonStyle = isConfigured
//            ? "display: inline-block; padding: 16px 48px; background-color: " + buttonColor + "; color: #ffffff; text-decoration: none; font-weight: bold; font-size: 16px; border-radius: 8px; cursor: not-allowed; opacity: 0.6;"
//            : "display: inline-block; padding: 16px 48px; background-color: " + buttonColor + "; color: #000000; text-decoration: none; font-weight: bold; font-size: 16px; border-radius: 8px; box-shadow: 0 4px 12px rgba(247, 98, 59, 0.3);";
//
//        html.append("<div style=\"text-align: center; padding: 30px 20px;\">");
//
//        if (!isConfigured) {
//            html.append("<a href=\"" + buttonUrl + "\" style=\"" + buttonStyle + "\">");
//            html.append(buttonText);
//            html.append("</a>");
//        } else {
//            html.append("<span style=\"" + buttonStyle + "\">");
//            html.append(buttonText);
//            html.append("</span>");
//        }
//
//        html.append("</div>");
//
//        // Button Description
//        html.append("<div style=\"text-align: center; padding: 0 20px 30px;\">");
//        html.append("<p style=\"color: #666666; font-size: 14px; margin: 0;\">");
//        html.append(isConfigured
//            ? "Paygo configuration has been confirmed"
//            : "Please click on the button to confirm paygo configuration");
//        html.append("</p>");
//        html.append("</div>");
//
//        // Footer
//        html.append("<div style=\"background-color: #000000; padding: 30px 20px; text-align: center;\">");
//        html.append("<p style=\"color: #999999; margin: 0 0 10px 0; font-size: 12px;\">");
//        html.append("This is an automated notification from the Koolboks Loan Management System");
//        html.append("</p>");
//        html.append("<p style=\"color: #666666; margin: 0; font-size: 12px;\">");
//        html.append("© 2026 Koolboks. All rights reserved.");
//        html.append("</p>");
//        html.append("</div>");
//
//        html.append("</div>");
//        html.append("</body>");
//        html.append("</html>");
//
//        return html.toString();
//    }
//
//    /**
//     * Build button URL with all data as query parameters
//     */
//    private String buildButtonUrl(Map<String, Object> data) {
//        try {
//            StringBuilder url = new StringBuilder(frontendBaseUrl);
//            url.append("/after-sales?");
//
//            // Add all parameters
//            url.append("customerFirstName=").append(URLEncoder.encode(String.valueOf(data.get("customerFirstName")), "UTF-8"));
//            url.append("&customerLastName=").append(URLEncoder.encode(String.valueOf(data.get("customerLastName")), "UTF-8"));
//            url.append("&customerEmail=").append(URLEncoder.encode(String.valueOf(data.get("customerEmail")), "UTF-8"));
//            url.append("&customerPhoneNumber=").append(URLEncoder.encode(String.valueOf(data.get("customerPhoneNumber")), "UTF-8"));
//            url.append("&customerLoanRef=").append(URLEncoder.encode(String.valueOf(data.get("customerLoanRef")), "UTF-8"));
//            url.append("&customerLoanDuration=").append(URLEncoder.encode(String.valueOf(data.get("customerLoanDuration")), "UTF-8"));
//
//            url.append("&agentName=").append(URLEncoder.encode(String.valueOf(data.get("agentName")), "UTF-8"));
//            url.append("&agentId=").append(URLEncoder.encode(String.valueOf(data.get("agentId")), "UTF-8"));
//            url.append("&agentEmail=").append(URLEncoder.encode(String.valueOf(data.get("agentEmail")), "UTF-8"));
//            url.append("&agentNumber=").append(URLEncoder.encode(String.valueOf(data.get("agentNumber")), "UTF-8"));
//
//            url.append("&storeName=").append(URLEncoder.encode(String.valueOf(data.get("storeName")), "UTF-8"));
//            url.append("&storeLocation=").append(URLEncoder.encode(String.valueOf(data.get("storeLocation")), "UTF-8"));
//            url.append("&initialInstalment=").append(URLEncoder.encode(String.valueOf(data.get("initialInstalment")), "UTF-8"));
//            url.append("&paymentDate=").append(URLEncoder.encode(String.valueOf(data.get("paymentDate")), "UTF-8"));
//
//            url.append("&productName=").append(URLEncoder.encode(String.valueOf(data.get("productName")), "UTF-8"));
//            url.append("&productBrand=").append(URLEncoder.encode(String.valueOf(data.get("productBrand")), "UTF-8"));
//            url.append("&productSize=").append(URLEncoder.encode(String.valueOf(data.get("productSize")), "UTF-8"));
//
//            return url.toString();
//
//        } catch (UnsupportedEncodingException e) {
//            System.err.println("Error encoding URL parameters: " + e.getMessage());
//            return frontendBaseUrl + "/after-sales";
//        }
//    }
//
//    /**
//     * Build a section with data rows
//     */
//    private String buildSection(String title, String[][] rows, String bgColor) {
//        StringBuilder html = new StringBuilder();
//
//        html.append("<div style=\"margin: 20px; background-color: " + bgColor + "; border-radius: 8px; overflow: hidden;\">");
//        html.append("<div style=\"background-color: #f7623b; padding: 12px 20px;\">");
//        html.append("<h2 style=\"color: #000000; margin: 0; font-size: 16px; font-weight: bold;\">" + title + "</h2>");
//        html.append("</div>");
//        html.append("<div style=\"padding: 20px;\">");
//
//        for (int i = 0; i < rows.length; i++) {
//            String borderStyle = i > 0 ? "border-top: 1px solid #1a1a1a;" : "";
//            String[] row = rows[i];
//
//            html.append("<div style=\"display: flex; justify-content: space-between; padding: 12px 0; " + borderStyle + "\">");
//            html.append("<span style=\"color: #999999; font-size: 14px;\">");
//            html.append(row[0]);
//            html.append("</span>");
//            html.append("<span style=\"color: #ffffff; font-size: 14px; font-weight: 600; text-align: right;\">");
//            html.append(row[1]);
//            html.append("</span>");
//            html.append("</div>");
//        }
//
//        html.append("</div>");
//        html.append("</div>");
//
//        return html.toString();
//    }
//}






package com.koolboks.creditProject.service.email_paygo_config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Service
public class PaygoEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${after.sales.team.email}")
    private String afterSalesTeamEmail;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl; // e.g., http://localhost:3000

    /**
     * Send Paygo Configuration Email to After Sales Team
     */
    public void sendPaygoConfigurationEmail(Map<String, Object> emailData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Set email properties
            helper.setFrom(fromEmail);
            helper.setTo(afterSalesTeamEmail);
            helper.setSubject("🔔 Store Payment Confirmed - Paygo Configuration Required - Loan Ref: "
                + emailData.get("customerLoanRef"));

            // Process HTML template
            String htmlContent = buildEmailContent(emailData);
            helper.setText(htmlContent, true);

            // Send email
            mailSender.send(message);

            System.out.println("Paygo configuration email sent successfully for loan ref: "
                + emailData.get("customerLoanRef"));

        } catch (MessagingException e) {
            System.err.println("Failed to send paygo configuration email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Build HTML email content
     */
    private String buildEmailContent(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html>");
        html.append("<head>");
        html.append("<meta charset=\"UTF-8\">");
        html.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        html.append("<title>Paygo Configuration Required</title>");
        html.append("</head>");
        html.append("<body style=\"margin: 0; padding: 0; font-family: Arial, sans-serif; background-color: #f5f5f5;\">");
        html.append("<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff;\">");

        // Header
        html.append("<div style=\"background: linear-gradient(135deg, #000000 0%, #1a1a1a 100%); padding: 40px 20px; text-align: center;\">");
        html.append("<div style=\"background-color: #f7623b; width: 80px; height: 80px; border-radius: 50%; margin: 0 auto 20px; display: flex; align-items: center; justify-content: center;\">");
        html.append("<span style=\"font-size: 48px;\">⚡</span>");
        html.append("</div>");
        html.append("<h1 style=\"color: #ffffff; margin: 0 0 10px 0; font-size: 28px;\">Store Payment Confirmed</h1>");
        html.append("<p style=\"color: #f7623b; margin: 0; font-size: 16px; font-weight: bold;\">Paygo Configuration Required</p>");
        html.append("</div>");

        // Alert Banner
        html.append("<div style=\"background-color: #fff3cd; border-left: 4px solid #f7623b; padding: 15px 20px; margin: 20px;\">");
        html.append("<p style=\"margin: 0; color: #856404; font-weight: bold;\">");
        html.append("🔔 ACTION REQUIRED: A store payment has been confirmed. Please configure the Paygo unit immediately.");
        html.append("</p>");
        html.append("</div>");

        // Customer Information
        html.append(buildSection("Customer Information", new String[][]{
            {"👤 Full Name", data.get("customerFirstName") + " " + data.get("customerLastName")},
            {"📧 Email", String.valueOf(data.get("customerEmail"))},
            {"📱 Phone Number", String.valueOf(data.get("customerPhoneNumber"))},
            {"🔖 Loan Reference", String.valueOf(data.get("customerLoanRef"))},
            {"⏱️ Loan Duration", data.get("customerLoanDuration") + " months"}
        }, "#000000"));

        // Agent Information
        html.append(buildSection("Agent Information", new String[][]{
            {"👨‍💼 Agent Name", String.valueOf(data.get("agentName"))},
            {"🆔 Agent ID", String.valueOf(data.get("agentId"))},
            {"📧 Email", String.valueOf(data.get("agentEmail"))},
            {"📱 Phone Number", String.valueOf(data.get("agentNumber"))}
        }, "#0a0a0a"));

        // Store & Payment Information
        html.append(buildSection("Store & Payment Details", new String[][]{
            {"🏪 Store Name", String.valueOf(data.get("storeName"))},
            {"📍 Store Location", String.valueOf(data.get("storeLocation"))},
            {"💰 Initial Installment", "₦" + String.valueOf(data.get("initialInstalment"))},
            {"📅 Payment Date", String.valueOf(data.get("paymentDate"))}
        }, "#000000"));

        // Product Information
        html.append(buildSection("Product Information", new String[][]{
            {"📦 Product Name", String.valueOf(data.get("productName"))},
            {"🏷️ Brand", String.valueOf(data.get("productBrand"))},
            {"📏 Size", String.valueOf(data.get("productSize"))}
        }, "#0a0a0a"));

        // Action Button
        boolean isConfigured = Boolean.TRUE.equals(data.get("isConfigured"));
        String buttonUrl = buildButtonUrl(data);

        html.append("<div style=\"text-align: center; padding: 30px 20px;\">");

        if (!isConfigured) {
            // Active button - bright orange, clickable
            html.append("<a href=\"" + buttonUrl + "\" style=\"display: inline-block; padding: 16px 48px; background-color: #f7623b; color: #000000; text-decoration: none; font-weight: bold; font-size: 16px; border-radius: 8px; box-shadow: 0 4px 12px rgba(247, 98, 59, 0.3);\">");
            html.append("⚡ Configure Paygo");
            html.append("</a>");
        } else {
            // Disabled button - gray, not clickable
            html.append("<span style=\"display: inline-block; padding: 16px 48px; background-color: #666666; color: #ffffff; text-decoration: none; font-weight: bold; font-size: 16px; border-radius: 8px; cursor: not-allowed; opacity: 0.6;\">");
            html.append("✓ Paygo Configured");
            html.append("</span>");
        }

        html.append("</div>");

        // Button Description
        html.append("<div style=\"text-align: center; padding: 0 20px 30px;\">");
        html.append("<p style=\"color: #666666; font-size: 14px; margin: 0;\">");
        html.append(isConfigured
            ? "Paygo configuration has been confirmed"
            : "Please click on the button to confirm paygo configuration");
        html.append("</p>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color: #000000; padding: 30px 20px; text-align: center;\">");
        html.append("<p style=\"color: #999999; margin: 0 0 10px 0; font-size: 12px;\">");
        html.append("This is an automated notification from the Koolboks Loan Management System");
        html.append("</p>");
        html.append("<p style=\"color: #666666; margin: 0; font-size: 12px;\">");
        html.append("© 2026 Koolboks. All rights reserved.");
        html.append("</p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Build button URL with all data as query parameters
     */
    private String buildButtonUrl(Map<String, Object> data) {
        try {
            StringBuilder url = new StringBuilder(frontendBaseUrl);
            url.append("/after-sales?");

            // Add all parameters
            url.append("customerFirstName=").append(URLEncoder.encode(String.valueOf(data.get("customerFirstName")), "UTF-8"));
            url.append("&customerLastName=").append(URLEncoder.encode(String.valueOf(data.get("customerLastName")), "UTF-8"));
            url.append("&customerEmail=").append(URLEncoder.encode(String.valueOf(data.get("customerEmail")), "UTF-8"));
            url.append("&customerPhoneNumber=").append(URLEncoder.encode(String.valueOf(data.get("customerPhoneNumber")), "UTF-8"));
            url.append("&customerLoanRef=").append(URLEncoder.encode(String.valueOf(data.get("customerLoanRef")), "UTF-8"));
            url.append("&customerLoanDuration=").append(URLEncoder.encode(String.valueOf(data.get("customerLoanDuration")), "UTF-8"));

            url.append("&agentName=").append(URLEncoder.encode(String.valueOf(data.get("agentName")), "UTF-8"));
            url.append("&agentId=").append(URLEncoder.encode(String.valueOf(data.get("agentId")), "UTF-8"));
            url.append("&agentEmail=").append(URLEncoder.encode(String.valueOf(data.get("agentEmail")), "UTF-8"));
            url.append("&agentNumber=").append(URLEncoder.encode(String.valueOf(data.get("agentNumber")), "UTF-8"));

            url.append("&storeName=").append(URLEncoder.encode(String.valueOf(data.get("storeName")), "UTF-8"));
            url.append("&storeLocation=").append(URLEncoder.encode(String.valueOf(data.get("storeLocation")), "UTF-8"));
            url.append("&initialInstalment=").append(URLEncoder.encode(String.valueOf(data.get("initialInstalment")), "UTF-8"));
            url.append("&paymentDate=").append(URLEncoder.encode(String.valueOf(data.get("paymentDate")), "UTF-8"));

            url.append("&productName=").append(URLEncoder.encode(String.valueOf(data.get("productName")), "UTF-8"));
            url.append("&productBrand=").append(URLEncoder.encode(String.valueOf(data.get("productBrand")), "UTF-8"));
            url.append("&productSize=").append(URLEncoder.encode(String.valueOf(data.get("productSize")), "UTF-8"));

            return url.toString();

        } catch (UnsupportedEncodingException e) {
            System.err.println("Error encoding URL parameters: " + e.getMessage());
            return frontendBaseUrl + "/after-sales";
        }
    }

    /**
     * Build a section with data rows
     */
    private String buildSection(String title, String[][] rows, String bgColor) {
        StringBuilder html = new StringBuilder();

        html.append("<div style=\"margin: 20px; background-color: " + bgColor + "; border-radius: 8px; overflow: hidden;\">");
        html.append("<div style=\"background-color: #f7623b; padding: 12px 20px;\">");
        html.append("<h2 style=\"color: #000000; margin: 0; font-size: 16px; font-weight: bold;\">" + title + "</h2>");
        html.append("</div>");
        html.append("<div style=\"padding: 20px;\">");

        for (int i = 0; i < rows.length; i++) {
            String borderStyle = i > 0 ? "border-top: 1px solid #1a1a1a;" : "";
            String[] row = rows[i];

            html.append("<div style=\"display: flex; justify-content: space-between; padding: 12px 0; " + borderStyle + "\">");
            html.append("<span style=\"color: #999999; font-size: 14px;\">");
            html.append(row[0]);
            html.append("</span>");
            html.append("<span style=\"color: #ffffff; font-size: 14px; font-weight: 600; text-align: right;\">");
            html.append(row[1]);
            html.append("</span>");
            html.append("</div>");
        }

        html.append("</div>");
        html.append("</div>");

        return html.toString();
    }
}
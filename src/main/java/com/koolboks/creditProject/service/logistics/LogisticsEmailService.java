package com.koolboks.creditProject.service.logistics;



//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import java.util.Map;
//
//@Service
//public class LogisticsEmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${logistics.team.email}")
//    private String logisticsTeamEmail;
//
//    @Value("${notification.email.from}")
//    private String fromEmail;
//
//    /**
//     * Send Delivery Scheduling Email to Logistics Team
//     */
//    public void sendDeliverySchedulingEmail(Map<String, Object> orderData) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(fromEmail);
//            helper.setTo(logisticsTeamEmail);
//            helper.setSubject("🚚 Delivery Required - Order ID: " + orderData.get("orderId") + " - Stock Confirmed");
//
//            String htmlContent = buildLogisticsEmailContent(orderData);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//
//            System.out.println("Logistics delivery email sent for order: " + orderData.get("orderId"));
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send logistics email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private String buildLogisticsEmailContent(Map<String, Object> data) {
//        StringBuilder html = new StringBuilder();
//
//        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
//        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
//        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");
//
//        // Header
//        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
//        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;\">");
//        html.append("<span style=\"font-size:48px;\">🚚</span>");
//        html.append("</div>");
//        html.append("<h1 style=\"color:#ffffff;margin:0 0 10px 0;font-size:28px;\">Delivery Scheduling Required</h1>");
//        html.append("<p style=\"color:#f7623b;margin:0;font-size:18px;font-weight:bold;\">Order ID: ")
//            .append(data.get("orderId")).append("</p>");
//        html.append("</div>");
//
//        // Success Banner
//        html.append("<div style=\"background-color:#d4edda;border-left:4px solid #28a745;padding:15px 20px;margin:20px;\">");
//        html.append("<p style=\"margin:0;color:#155724;font-weight:bold;\">");
//        html.append("✅ STOCK CONFIRMED: Unit has been allocated to this order. Please schedule delivery.");
//        html.append("</p></div>");
//
//        // Order Information
//        html.append(buildSection("Order Details", new String[][]{
//            {"📦 Order ID", String.valueOf(data.get("orderId"))},
//            {"🔖 Loan Reference", String.valueOf(data.get("customerLoanRef"))},
//            {"⏱️ Loan Duration", data.get("customerLoanDuration") + " months"},
//            {"📅 Paygo Config Date", String.valueOf(data.get("paygoConfigurationDate"))},
//            {"⚡ Configured By", String.valueOf(data.get("paygoConfiguredBy"))}
//        }, "#000000"));
//
//        // Stock Confirmation Details
//        html.append(buildSection("Stock Confirmation", new String[][]{
//            {"✅ Stock Status", "CONFIRMED"},
//            {"📅 Confirmation Date", String.valueOf(data.get("stockConfirmationDate"))},
//            {"👤 Confirmed By", String.valueOf(data.get("stockConfirmedBy"))}
//        }, "#0a0a0a"));
//
//        // Customer Delivery Information
//        html.append(buildSection("Customer Delivery Information", new String[][]{
//            {"👤 Full Name", data.get("customerFirstName") + " " + data.get("customerLastName")},
//            {"📧 Email", String.valueOf(data.get("customerEmail"))},
//            {"📱 Phone Number", String.valueOf(data.get("customerPhoneNumber"))}
//        }, "#000000"));
//
//        // Delivery Address (Store Location)
//        html.append(buildSection("Delivery Address", new String[][]{
//            {"🏪 Store Name", String.valueOf(data.get("storeName"))},
//            {"📍 Location", String.valueOf(data.get("storeLocation"))}
//        }, "#0a0a0a"));
//
//        // Product Information
//        html.append(buildSection("Product to Deliver", new String[][]{
//            {"📱 Product Name", String.valueOf(data.get("productName"))},
//            {"🏷️ Brand", String.valueOf(data.get("productBrand"))},
//            {"📏 Size", String.valueOf(data.get("productSize"))}
//        }, "#000000"));
//
//        // Agent Information
//        html.append(buildSection("Agent Contact", new String[][]{
//            {"🧑‍💼 Agent Name", String.valueOf(data.get("agentName"))},
//            {"🆔 Agent ID", String.valueOf(data.get("agentId"))},
//            {"📧 Agent Email", String.valueOf(data.get("agentEmail"))},
//            {"📱 Agent Phone", String.valueOf(data.get("agentNumber"))}
//        }, "#0a0a0a"));
//
//        // Action Required Box
//        html.append("<div style=\"background:#f7623b;color:#000000;padding:20px;margin:20px;border-radius:8px;text-align:center;\">");
//        html.append("<h3 style=\"margin:0 0 10px 0;\">🚚 Action Required</h3>");
//        html.append("<p style=\"margin:5px 0;font-size:16px;font-weight:bold;\">Please schedule delivery for this order</p>");
//        html.append("<p style=\"margin:5px 0;font-size:14px;\">Contact the customer to arrange delivery</p>");
//        html.append("</div>");
//
//        // Important Notes
//        html.append("<div style=\"background-color:#fff3cd;border-left:4px solid #ffc107;padding:15px 20px;margin:20px;\">");
//        html.append("<p style=\"margin:0 0 10px 0;color:#856404;font-weight:bold;\">📋 Important Notes:</p>");
//        html.append("<ul style=\"margin:0;padding-left:20px;color:#856404;\">");
//        html.append("<li>Ensure unit serial number is recorded</li>");
//        html.append("<li>Confirm delivery with customer before dispatch</li>");
//        html.append("<li>Update delivery status in system</li>");
//        html.append("</ul>");
//        html.append("</div>");
//
//        // Footer
//        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
//        html.append("<p style=\"color:#999999;margin:0 0 10px 0;font-size:12px;\">");
//        html.append("This is an automated notification from the Koolboks Logistics Management System");
//        html.append("</p>");
//        html.append("<p style=\"color:#666666;margin:0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
//        html.append("</div>");
//
//        html.append("</div></body></html>");
//
//        return html.toString();
//    }
//
//    private String buildSection(String title, String[][] rows, String bgColor) {
//        StringBuilder html = new StringBuilder();
//
//        html.append("<div style=\"margin:20px;background-color:").append(bgColor).append(";border-radius:8px;overflow:hidden;\">");
//        html.append("<div style=\"background-color:#f7623b;padding:12px 20px;\">");
//        html.append("<h2 style=\"color:#000000;margin:0;font-size:16px;font-weight:bold;\">").append(title).append("</h2>");
//        html.append("</div>");
//        html.append("<div style=\"padding:20px;\">");
//
//        for (int i = 0; i < rows.length; i++) {
//            String borderStyle = i > 0 ? "border-top:1px solid #1a1a1a;" : "";
//            String[] row = rows[i];
//
//            html.append("<div style=\"display:flex;justify-content:space-between;padding:12px 0;").append(borderStyle).append("\">");
//            html.append("<span style=\"color:#999999;font-size:14px;\">").append(row[0]).append("</span>");
//            html.append("<span style=\"color:#ffffff;font-size:14px;font-weight:600;text-align:right;\">").append(row[1]).append("</span>");
//            html.append("</div>");
//        }
//
//        html.append("</div></div>");
//
//        return html.toString();
//    }
//}









import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
public class LogisticsEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${logistics.team.email}")
    private String logisticsTeamEmail;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    /**
     * Send Delivery Scheduling Email to Logistics Team
     */
    public void sendDeliverySchedulingEmail(Map<String, Object> orderData) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(logisticsTeamEmail);
            helper.setSubject("🚚 Delivery Required - Order ID: " + orderData.get("orderId") + " - Stock Confirmed");

            String htmlContent = buildLogisticsEmailContent(orderData);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("Logistics delivery email sent for order: " + orderData.get("orderId"));

        } catch (MessagingException e) {
            System.err.println("Failed to send logistics email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildLogisticsEmailContent(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;\">");
        html.append("<span style=\"font-size:48px;\">🚚</span>");
        html.append("</div>");
        html.append("<h1 style=\"color:#ffffff;margin:0 0 10px 0;font-size:28px;\">Delivery Scheduling Required</h1>");
        html.append("<p style=\"color:#f7623b;margin:0;font-size:18px;font-weight:bold;\">Order ID: ")
            .append(data.get("orderId")).append("</p>");
        html.append("</div>");

        // Success Banner
        html.append("<div style=\"background-color:#d4edda;border-left:4px solid #28a745;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0;color:#155724;font-weight:bold;\">");
        html.append("✅ STOCK CONFIRMED: Unit has been allocated to this order. Please schedule delivery.");
        html.append("</p></div>");

        // Order Information
        html.append(buildSection("Order Details", new String[][]{
            {"📦 Order ID", String.valueOf(data.get("orderId"))},
            {"🔖 Loan Reference", String.valueOf(data.get("customerLoanRef"))},
            {"⏱️ Loan Duration", data.get("customerLoanDuration") + " months"},
            {"📅 Paygo Config Date", String.valueOf(data.get("paygoConfigurationDate"))},
            {"⚡ Configured By", String.valueOf(data.get("paygoConfiguredBy"))}
        }, "#000000"));

        // Stock Confirmation Details
        html.append(buildSection("Stock Confirmation", new String[][]{
            {"✅ Stock Status", "CONFIRMED"},
            {"📅 Confirmation Date", String.valueOf(data.get("stockConfirmationDate"))},
            {"👤 Confirmed By", String.valueOf(data.get("stockConfirmedBy"))}
        }, "#0a0a0a"));

        // Customer Delivery Information
        html.append(buildSection("Customer Delivery Information", new String[][]{
            {"👤 Full Name", data.get("customerFirstName") + " " + data.get("customerLastName")},
            {"📧 Email", String.valueOf(data.get("customerEmail"))},
            {"📱 Phone Number", String.valueOf(data.get("customerPhoneNumber"))}
        }, "#000000"));

        // Delivery Address (Store Location)
        html.append(buildSection("Delivery Address", new String[][]{
            {"🏪 Store Name", String.valueOf(data.get("storeName"))},
            {"📍 Location", String.valueOf(data.get("storeLocation"))}
        }, "#0a0a0a"));

        // Product Information
        html.append(buildSection("Product to Deliver", new String[][]{
            {"📱 Product Name", String.valueOf(data.get("productName"))},
            {"🏷️ Brand", String.valueOf(data.get("productBrand"))},
            {"📏 Size", String.valueOf(data.get("productSize"))}
        }, "#000000"));

        // Agent Information
        html.append(buildSection("Agent Contact", new String[][]{
            {"🧑‍💼 Agent Name", String.valueOf(data.get("agentName"))},
            {"🆔 Agent ID", String.valueOf(data.get("agentId"))},
            {"📧 Agent Email", String.valueOf(data.get("agentEmail"))},
            {"📱 Agent Phone", String.valueOf(data.get("agentNumber"))}
        }, "#0a0a0a"));

        // Action Required Box
        html.append("<div style=\"background:#f7623b;color:#000000;padding:20px;margin:20px;border-radius:8px;text-align:center;\">");
        html.append("<h3 style=\"margin:0 0 10px 0;\">🚚 Action Required</h3>");
        html.append("<p style=\"margin:5px 0;font-size:16px;font-weight:bold;\">Please schedule delivery for this order</p>");
        html.append("<p style=\"margin:5px 0;font-size:14px;\">Contact the customer to arrange delivery</p>");
        html.append("</div>");

        // Schedule Delivery Button
        String buttonUrl = buildButtonUrl(data);

        html.append("<div style=\"text-align:center;padding:30px 20px;\">");
        html.append("<a href=\"").append(buttonUrl).append("\" style=\"display:inline-block;padding:16px 48px;background-color:#f7623b;color:#000000;text-decoration:none;font-weight:bold;font-size:16px;border-radius:8px;box-shadow:0 4px 12px rgba(247,98,59,0.3);\">");
        html.append("🚚 Schedule Delivery");
        html.append("</a>");
        html.append("</div>");

        // Button Description
        html.append("<div style=\"text-align:center;padding:0 20px 30px;\">");
        html.append("<p style=\"color:#666666;font-size:14px;margin:0;\">");
        html.append("Click the button above to schedule delivery date and assign unit");
        html.append("</p></div>");

        // Important Notes
        html.append("<div style=\"background-color:#fff3cd;border-left:4px solid #ffc107;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0 0 10px 0;color:#856404;font-weight:bold;\">📋 Important Notes:</p>");
        html.append("<ul style=\"margin:0;padding-left:20px;color:#856404;\">");
        html.append("<li>Ensure unit serial number is recorded</li>");
        html.append("<li>Confirm delivery with customer before dispatch</li>");
        html.append("<li>Update delivery status in system</li>");
        html.append("</ul>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0 0 10px 0;font-size:12px;\">");
        html.append("This is an automated notification from the Koolboks Logistics Management System");
        html.append("</p>");
        html.append("<p style=\"color:#666666;margin:0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    private String buildButtonUrl(Map<String, Object> data) {
        try {
            StringBuilder url = new StringBuilder(frontendBaseUrl);
            url.append("/delivery-scheduling?");

            // Add all parameters
            url.append("orderId=").append(java.net.URLEncoder.encode(String.valueOf(data.get("orderId")), "UTF-8"));
            url.append("&customerFirstName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerFirstName")), "UTF-8"));
            url.append("&customerLastName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerLastName")), "UTF-8"));
            url.append("&customerEmail=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerEmail")), "UTF-8"));
            url.append("&customerPhoneNumber=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerPhoneNumber")), "UTF-8"));
            url.append("&customerLoanRef=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerLoanRef")), "UTF-8"));
            url.append("&customerLoanDuration=").append(java.net.URLEncoder.encode(String.valueOf(data.get("customerLoanDuration")), "UTF-8"));
            url.append("&agentName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("agentName")), "UTF-8"));
            url.append("&agentId=").append(java.net.URLEncoder.encode(String.valueOf(data.get("agentId")), "UTF-8"));
            url.append("&agentEmail=").append(java.net.URLEncoder.encode(String.valueOf(data.get("agentEmail")), "UTF-8"));
            url.append("&agentNumber=").append(java.net.URLEncoder.encode(String.valueOf(data.get("agentNumber")), "UTF-8"));
            url.append("&storeName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("storeName")), "UTF-8"));
            url.append("&storeLocation=").append(java.net.URLEncoder.encode(String.valueOf(data.get("storeLocation")), "UTF-8"));
            url.append("&productName=").append(java.net.URLEncoder.encode(String.valueOf(data.get("productName")), "UTF-8"));
            url.append("&productBrand=").append(java.net.URLEncoder.encode(String.valueOf(data.get("productBrand")), "UTF-8"));
            url.append("&productSize=").append(java.net.URLEncoder.encode(String.valueOf(data.get("productSize")), "UTF-8"));
            url.append("&initialInstalment=").append(java.net.URLEncoder.encode(String.valueOf(data.get("initialInstalment")), "UTF-8"));
            url.append("&paymentDate=").append(java.net.URLEncoder.encode(String.valueOf(data.get("paymentDate")), "UTF-8"));
            url.append("&paygoConfigurationDate=").append(java.net.URLEncoder.encode(String.valueOf(data.get("paygoConfigurationDate")), "UTF-8"));
            url.append("&stockConfirmationDate=").append(java.net.URLEncoder.encode(String.valueOf(data.get("stockConfirmationDate")), "UTF-8"));

            return url.toString();

        } catch (java.io.UnsupportedEncodingException e) {
            System.err.println("Error encoding URL parameters: " + e.getMessage());
            return frontendBaseUrl + "/delivery-scheduling";
        }
    }

    private String buildSection(String title, String[][] rows, String bgColor) {
        StringBuilder html = new StringBuilder();

        html.append("<div style=\"margin:20px;background-color:").append(bgColor).append(";border-radius:8px;overflow:hidden;\">");
        html.append("<div style=\"background-color:#f7623b;padding:12px 20px;\">");
        html.append("<h2 style=\"color:#000000;margin:0;font-size:16px;font-weight:bold;\">").append(title).append("</h2>");
        html.append("</div>");
        html.append("<div style=\"padding:20px;\">");

        for (int i = 0; i < rows.length; i++) {
            String borderStyle = i > 0 ? "border-top:1px solid #1a1a1a;" : "";
            String[] row = rows[i];

            html.append("<div style=\"display:flex;justify-content:space-between;padding:12px 0;").append(borderStyle).append("\">");
            html.append("<span style=\"color:#999999;font-size:14px;\">").append(row[0]).append("</span>");
            html.append("<span style=\"color:#ffffff;font-size:14px;font-weight:600;text-align:right;\">").append(row[1]).append("</span>");
            html.append("</div>");
        }

        html.append("</div></div>");

        return html.toString();
    }
}
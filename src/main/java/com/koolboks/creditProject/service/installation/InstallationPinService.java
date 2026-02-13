//package com.koolboks.creditProject.service.installation;
//
//
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Random;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Service
//public class InstallationPinService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${notification.email.from}")
//    private String fromEmail;
//
//    @Value("${twilio.from.number}")
//    private String twilioPhoneNumber;
//
//    // Store PINs in memory (use Redis in production)
//    private final Map<String, String> pinStore = new ConcurrentHashMap<>();
//
//    /**
//     * Generate 6-digit PIN
//     */
//    public String generatePin(String orderId) {
//        Random random = new Random();
//        String pin = String.format("%06d", random.nextInt(1000000));
//        pinStore.put(orderId, pin);
//        System.out.println("Generated PIN for order " + orderId + ": " + pin);
//        return pin;
//    }
//
//    /**
//     * Verify PIN
//     */
//    public boolean verifyPin(String orderId, String providedPin) {
//        String storedPin = pinStore.get(orderId);
//        if (storedPin != null && storedPin.equals(providedPin)) {
//            pinStore.remove(orderId); // Remove after successful verification
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * Send PIN via Email
//     */
//    public void sendPinByEmail(String email, String pin, String orderId, String customerName) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(fromEmail);
//            helper.setTo(email);
//            helper.setSubject("🔐 Installation Verification PIN - Order " + orderId);
//
//            String htmlContent = buildPinEmailContent(pin, orderId, customerName);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//
//            System.out.println("PIN email sent to: " + email);
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send PIN email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Send PIN via SMS (Twilio)
//     */
//    public void sendPinBySms(String phoneNumber, String pin, String orderId) {
//        try {
//            // Use fallback number if customer number fails
//            String recipientNumber = phoneNumber;
//            String fallbackNumber = "+18777804236";
//
//            try {
//                Message message = Message.creator(
//                    new PhoneNumber(recipientNumber),
//                    new PhoneNumber(twilioPhoneNumber),
//                    "Koolboks Installation PIN for Order " + orderId + ": " + pin +
//                    "\n\nPlease provide this PIN to the installer to confirm installation."
//                ).create();
//
//                System.out.println("PIN SMS sent to: " + recipientNumber + " (SID: " + message.getSid() + ")");
//
//            } catch (Exception primaryError) {
//                System.err.println("Failed to send to primary number: " + primaryError.getMessage());
//                System.out.println("Attempting fallback number: " + fallbackNumber);
//
//                // Try fallback number
//                Message message = Message.creator(
//                    new PhoneNumber(fallbackNumber),
//                    new PhoneNumber(twilioPhoneNumber),
//                    "Koolboks Installation PIN for Order " + orderId + ": " + pin +
//                    "\n\nCustomer: " + recipientNumber +
//                    "\n\nPlease provide this PIN to the installer."
//                ).create();
//
//                System.out.println("PIN SMS sent to fallback: " + fallbackNumber);
//            }
//
//        } catch (Exception e) {
//            System.err.println("Failed to send PIN SMS: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    private String buildPinEmailContent(String pin, String orderId, String customerName) {
//        StringBuilder html = new StringBuilder();
//
//        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
//        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
//        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");
//
//        // Header
//        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
//        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;\">");
//        html.append("<span style=\"font-size:48px;\">🔐</span>");
//        html.append("</div>");
//        html.append("<h1 style=\"color:#ffffff;margin:0;\">Installation Verification PIN</h1>");
//        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:14px;\">Order ID: ").append(orderId).append("</p>");
//        html.append("</div>");
//
//        // PIN Display
//        html.append("<div style=\"margin:30px 20px;background-color:#f7623b;border-radius:12px;padding:30px;text-align:center;\">");
//        html.append("<p style=\"color:#000000;margin:0 0 10px 0;font-size:16px;font-weight:bold;\">Your Verification PIN</p>");
//        html.append("<p style=\"color:#000000;font-size:48px;font-weight:bold;letter-spacing:8px;margin:10px 0;\">");
//        html.append(pin);
//        html.append("</p>");
//        html.append("</div>");
//
//        // Instructions
//        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
//        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">How to Use This PIN</h3>");
//        html.append("<p style=\"color:#ffffff;margin:5px 0;line-height:1.6;\">");
//        html.append("1. The installer will ask you for this 6-digit PIN<br>");
//        html.append("2. Provide the PIN after verifying the installation is complete<br>");
//        html.append("3. The installer will enter the PIN to finalize the installation<br>");
//        html.append("4. This confirms your satisfaction with the installation");
//        html.append("</p>");
//        html.append("</div>");
//
//        // Important Notice
//        html.append("<div style=\"background-color:#fff3cd;padding:15px 20px;margin:20px;border-radius:8px;\">");
//        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">⚠️ Important:</p>");
//        html.append("<ul style=\"margin:10px 0;padding-left:20px;color:#856404;\">");
//        html.append("<li>This PIN is valid for ONE use only</li>");
//        html.append("<li>Only provide the PIN after installation is complete</li>");
//        html.append("<li>Verify the product is working properly before sharing the PIN</li>");
//        html.append("</ul>");
//        html.append("</div>");
//
//        // Footer
//        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
//        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Installation Verification System</p>");
//        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
//        html.append("</div>");
//
//        html.append("</div></body></html>");
//
//        return html.toString();
//    }
//}
//



package com.koolboks.creditProject.service.installation;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InstallationPinService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${twilio.from.number}")
    private String twilioPhoneNumber;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    // Store PINs in memory (use Redis in production)
    private final Map<String, String> pinStore = new ConcurrentHashMap<>();

    /**
     * Generate 6-digit PIN
     */
    public String generatePin(String orderId) {
        Random random = new Random();
        String pin = String.format("%06d", random.nextInt(1000000));
        pinStore.put(orderId, pin);
        System.out.println("Generated PIN for order " + orderId + ": " + pin);
        return pin;
    }

    /**
     * Verify PIN
     */
    public boolean verifyPin(String orderId, String providedPin) {
        String storedPin = pinStore.get(orderId);
        if (storedPin != null && storedPin.equals(providedPin)) {
            pinStore.remove(orderId); // Remove after successful verification
            return true;
        }
        return false;
    }

    /**
     * NEW: Send verification link to customer email
     */
    public void sendVerificationLinkEmail(String email, String orderId, String customerName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("🔐 Verify Your Mandate - Installation Order " + orderId);

            String verificationUrl = frontendBaseUrl + "/mandate-form?orderId=" + orderId;
            String htmlContent = buildVerificationLinkEmail(verificationUrl, orderId, customerName);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("Verification link email sent to: " + email);

        } catch (MessagingException e) {
            System.err.println("Failed to send verification link email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send PIN via Email
     */
    public void sendPinByEmail(String email, String pin, String orderId, String customerName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("🔐 Installation Verification PIN - Order " + orderId);

            String htmlContent = buildPinEmailContent(pin, orderId, customerName);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("PIN email sent to: " + email);

        } catch (MessagingException e) {
            System.err.println("Failed to send PIN email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send PIN via SMS (Twilio)
     */
    public void sendPinBySms(String phoneNumber, String pin, String orderId) {
        try {
            // Use fallback number if customer number fails
            String recipientNumber = phoneNumber;
            String fallbackNumber = "+18777804236";

            try {
                Message message = Message.creator(
                    new PhoneNumber(recipientNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    "Koolboks Installation PIN for Order " + orderId + ": " + pin +
                    "\n\nPlease provide this PIN to the installer to confirm installation."
                ).create();

                System.out.println("PIN SMS sent to: " + recipientNumber + " (SID: " + message.getSid() + ")");

            } catch (Exception primaryError) {
                System.err.println("Failed to send to primary number: " + primaryError.getMessage());
                System.out.println("Attempting fallback number: " + fallbackNumber);

                // Try fallback number
                Message message = Message.creator(
                    new PhoneNumber(fallbackNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    "Koolboks Installation PIN for Order " + orderId + ": " + pin +
                    "\n\nCustomer: " + recipientNumber +
                    "\n\nPlease provide this PIN to the installer."
                ).create();

                System.out.println("PIN SMS sent to fallback: " + fallbackNumber);
            }

        } catch (Exception e) {
            System.err.println("Failed to send PIN SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildVerificationLinkEmail(String verificationUrl, String orderId, String customerName) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;\">");
        html.append("<span style=\"font-size:48px;\">🔐</span>");
        html.append("</div>");
        html.append("<h1 style=\"color:#ffffff;margin:0;\">Mandate Verification Required</h1>");
        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:14px;\">Order ID: ").append(orderId).append("</p>");
        html.append("</div>");

        // Welcome Message
        html.append("<div style=\"padding:30px 20px;\">");
        html.append("<p style=\"color:#000000;font-size:16px;margin:0 0 15px 0;\">Dear ").append(customerName).append(",</p>");
        html.append("<p style=\"color:#333333;line-height:1.6;margin:0 0 20px 0;\">");
        html.append("Before we can send you the installation PIN, we need to verify your direct debit mandate. ");
        html.append("This is a one-time process to ensure secure payment authorization.");
        html.append("</p>");
        html.append("</div>");

        // Verification Button
        html.append("<div style=\"text-align:center;padding:20px;\">");
        html.append("<a href=\"").append(verificationUrl).append("\" style=\"display:inline-block;padding:18px 50px;background-color:#f7623b;color:#000000;text-decoration:none;font-weight:bold;font-size:16px;border-radius:8px;box-shadow:0 4px 12px rgba(247,98,59,0.3);\">");
        html.append("🔐 Verify Mandate Now");
        html.append("</a>");
        html.append("</div>");

        // Instructions
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">What to expect:</h3>");
        html.append("<ol style=\"color:#ffffff;margin:0;padding-left:20px;line-height:2;\">");
        html.append("<li>Click the button above to open the verification form</li>");
        html.append("<li>Select your verification method (BVN, Email, or Phone)</li>");
        html.append("<li>Enter your details and submit</li>");
        html.append("<li>Your mandate will be created (may require a small verification transfer)</li>");
        html.append("<li>Once verified, you'll receive your installation PIN immediately</li>");
        html.append("</ol>");
        html.append("</div>");

        // Important Note
        html.append("<div style=\"background-color:#fff3cd;padding:15px 20px;margin:20px;border-radius:8px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">⚠️ Important:</p>");
        html.append("<p style=\"margin:10px 0 0 0;color:#856404;\">");
        html.append("This verification link is specific to your order. Do not share it with anyone. ");
        html.append("If you did not request installation, please ignore this email.");
        html.append("</p>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Installation Verification System</p>");
        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    private String buildPinEmailContent(String pin, String orderId, String customerName) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;\">");
        html.append("<span style=\"font-size:48px;\">🔐</span>");
        html.append("</div>");
        html.append("<h1 style=\"color:#ffffff;margin:0;\">Installation Verification PIN</h1>");
        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:14px;\">Order ID: ").append(orderId).append("</p>");
        html.append("</div>");

        // PIN Display
        html.append("<div style=\"margin:30px 20px;background-color:#f7623b;border-radius:12px;padding:30px;text-align:center;\">");
        html.append("<p style=\"color:#000000;margin:0 0 10px 0;font-size:16px;font-weight:bold;\">Your Verification PIN</p>");
        html.append("<p style=\"color:#000000;font-size:48px;font-weight:bold;letter-spacing:8px;margin:10px 0;\">");
        html.append(pin);
        html.append("</p>");
        html.append("</div>");

        // Instructions
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">How to Use This PIN</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;line-height:1.6;\">");
        html.append("1. The installer will ask you for this 6-digit PIN<br>");
        html.append("2. Provide the PIN after verifying the installation is complete<br>");
        html.append("3. The installer will enter the PIN to finalize the installation<br>");
        html.append("4. This confirms your satisfaction with the installation");
        html.append("</p>");
        html.append("</div>");

        // Important Notice
        html.append("<div style=\"background-color:#fff3cd;padding:15px 20px;margin:20px;border-radius:8px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">⚠️ Important:</p>");
        html.append("<ul style=\"margin:10px 0;padding-left:20px;color:#856404;\">");
        html.append("<li>This PIN is valid for ONE use only</li>");
        html.append("<li>Only provide the PIN after installation is complete</li>");
        html.append("<li>Verify the product is working properly before sharing the PIN</li>");
        html.append("</ul>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Installation Verification System</p>");
        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }
}
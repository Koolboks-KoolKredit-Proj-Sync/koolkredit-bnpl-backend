// ================================================================
// SPRING BOOT - PAYMENT REMINDER SERVICE
// ================================================================
// Sends payment reminders 7 days before due date:
// 1. Customer EMAIL (with payment button)
// 2. Guarantor EMAIL (with payment button)
// 3. Customer SMS (via Twilio)
// ================================================================
// Location: src/main/java/com/koolboks/creditProject/service/loan/PaymentReminderService.java

//package com.koolboks.creditProject.service.loan;
//
//import com.koolboks.creditProject.entity.LoanRepayment;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.time.temporal.ChronoUnit;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class PaymentReminderService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Value("${notification.email.from}")
//    private String fromEmail;
//
//    @Value("${twilio.from.number}")
//    private String twilioPhoneNumber;
//
//    @Value("${frontend.base.url}")
//    private String frontendBaseUrl;
//
//    @Value("${django.api.url:http://127.0.0.1:8000}")
//    private String djangoApiUrl;
//
//    /**
//     * Send payment reminder to customer and guarantor (both with buttons) + SMS
//     */
//    public void sendPaymentReminders(LoanRepayment loan, int instalmentNumber, LocalDate dueDate) {
//        try {
//            System.out.println("=== SENDING PAYMENT REMINDERS ===");
//            System.out.println("Loan Reference: " + loan.getLoanReference());
//            System.out.println("Instalment Number: " + instalmentNumber);
//            System.out.println("Due Date: " + dueDate);
//
//            // Step 1: Create payment token in Django
//            String paymentToken = createPaymentTokenInDjango(loan, instalmentNumber, dueDate);
//
//            if (paymentToken == null) {
//                System.err.println("Failed to create payment token");
//                return;
//            }
//
//            // Step 2: Send reminders with token
//            Map<String, Object> reminderData = buildReminderData(loan, instalmentNumber, dueDate, paymentToken);
//
//            // Send customer email
//            sendCustomerReminderEmail(reminderData);
//
//            // Send guarantor email
//            sendGuarantorReminderEmail(reminderData);
//
//            // Send customer SMS
//            sendCustomerReminderSms(reminderData);
//
//            System.out.println("=== PAYMENT REMINDERS SENT SUCCESSFULLY ===");
//
//        } catch (Exception e) {
//            System.err.println("Error sending payment reminders: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Create payment token in Django via API
//     */
//    private String createPaymentTokenInDjango(LoanRepayment loan, int instalmentNumber, LocalDate dueDate) {
//        try {
//            String url = djangoApiUrl + "/v1/api/create-payment-token";
//
//            // Calculate expiration date (31 days from today, or 29 for February)
//            LocalDate today = LocalDate.now();
//            LocalDate expirationDate = calculateExpirationDate(today);
//
//            Map<String, Object> tokenRequest = new HashMap<>();
//            tokenRequest.put("loanReference", loan.getLoanReference());
//            tokenRequest.put("instalmentNumber", instalmentNumber);
//            tokenRequest.put("amount", loan.getMonthlyRepayment());
//            tokenRequest.put("expiresAt", expirationDate.toString());
//
//            Map<String, Object> response = restTemplate.postForObject(url, tokenRequest, Map.class);
//
//            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
//                String token = (String) response.get("token");
//                System.out.println("✅ Payment token created: " + token);
//                return token;
//            } else {
//                System.err.println("Failed to create payment token in Django");
//                return null;
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error creating payment token: " + e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * Calculate expiration date (calendar-aware)
//     */
//    private LocalDate calculateExpirationDate(LocalDate startDate) {
//        LocalDate expiration = startDate.plusDays(31);
//
//        // If expiration falls in February, limit to 29th
//        if (expiration.getMonthValue() == 2 && expiration.getDayOfMonth() > 29) {
//            expiration = LocalDate.of(expiration.getYear(), 2, 29);
//        }
//
//        return expiration;
//    }
//
//    private Map<String, Object> buildReminderData(LoanRepayment loan, int instalmentNumber, LocalDate dueDate, String token) {
//        Map<String, Object> data = new HashMap<>();
//
//        data.put("loanReference", loan.getLoanReference());
//        data.put("instalmentNumber", instalmentNumber);
//        data.put("customerName", loan.getFullName());
//        data.put("customerEmail", loan.getEmail());
//        data.put("customerPhone", loan.getPhone());
//        data.put("guarantorEmail", loan.getGuarantorContact());
//        data.put("monthlyRepayment", loan.getMonthlyRepayment());
//        data.put("amountPaid", loan.getAmountPaid());
//        data.put("remainingBalance", loan.getRemainingBalance());
//        data.put("totalInstalments", loan.getNumberOfMonths());
//        data.put("dueDate", dueDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
//        data.put("paymentToken", token);
//        data.put("expirationDate", calculateExpirationDate(LocalDate.now()).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
//
//        return data;
//    }
//
//    /**
//     * Send payment reminder email to CUSTOMER (with payment button)
//     */
//    private void sendCustomerReminderEmail(Map<String, Object> data) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            String customerEmail = (String) data.get("customerEmail");
//            helper.setFrom(fromEmail);
//            helper.setTo(customerEmail);
//
//            String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
//            helper.setSubject("💳 Payment Reminder: " + instalmentOrdinal + " Instalment Due Soon");
//
//            String htmlContent = buildCustomerReminderEmail(data);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//
//            System.out.println("✅ Customer reminder email sent to: " + customerEmail);
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send customer reminder email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Send payment reminder email to GUARANTOR (with payment button)
//     */
//    private void sendGuarantorReminderEmail(Map<String, Object> data) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            String guarantorEmail = (String) data.get("guarantorEmail");
//            helper.setFrom(fromEmail);
//            helper.setTo(guarantorEmail);
//
//            String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
//            helper.setSubject("🔔 Payment Reminder for " + data.get("customerName") + " - " + instalmentOrdinal + " Instalment");
//
//            String htmlContent = buildGuarantorReminderEmail(data);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//
//            System.out.println("✅ Guarantor reminder email sent to: " + guarantorEmail);
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send guarantor reminder email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Send payment reminder SMS to CUSTOMER
//     */
//    private void sendCustomerReminderSms(Map<String, Object> data) {
//        try {
//            String phoneNumber = (String) data.get("customerPhone");
//            String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
//            BigDecimal amount = (BigDecimal) data.get("monthlyRepayment");
//            String dueDate = (String) data.get("dueDate");
//
//            String smsText = "Koolboks Payment Reminder\n\n" +
//                           "Your " + instalmentOrdinal + " instalment of ₦" + String.format("%,.2f", amount) +
//                           " is due on " + dueDate + ".\n\n" +
//                           "Check your email for the secure payment link.\n\n" +
//                           "Loan Ref: " + data.get("loanReference");
//
//            try {
//                Message twilioMessage = Message.creator(
//                    new PhoneNumber(phoneNumber),
//                    new PhoneNumber(twilioPhoneNumber),
//                    smsText
//                ).create();
//
//                System.out.println("✅ Customer reminder SMS sent to: " + phoneNumber + " (SID: " + twilioMessage.getSid() + ")");
//
//            } catch (Exception primaryError) {
//                System.err.println("Failed to send to primary number: " + primaryError.getMessage());
//
//                // Try fallback number
//                String fallbackNumber = "+18777804236";
//                Message fallbackMessage = Message.creator(
//                    new PhoneNumber(fallbackNumber),
//                    new PhoneNumber(twilioPhoneNumber),
//                    "Koolboks: Payment reminder for " + phoneNumber + "\n" + smsText
//                ).create();
//
//                System.out.println("✅ Customer reminder SMS sent to fallback: " + fallbackNumber);
//            }
//
//        } catch (Exception e) {
//            System.err.println("Failed to send customer reminder SMS: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Build customer reminder email HTML (BLACK/ORANGE STYLE)
//     */
//    private String buildCustomerReminderEmail(Map<String, Object> data) {
//        StringBuilder html = new StringBuilder();
//
//        String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
//        String paymentUrl = frontendBaseUrl + "/repayment-payment?token=" + data.get("paymentToken");
//        BigDecimal amount = (BigDecimal) data.get("monthlyRepayment");
//
//        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
//        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
//        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");
//
//        // Header
//        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
//        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;\">");
//        html.append("<span style=\"font-size:48px;\">💳</span>");
//        html.append("</div>");
//        html.append("<h1 style=\"color:#ffffff;margin:0;\">Payment Reminder</h1>");
//        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:18px;font-weight:bold;\">").append(instalmentOrdinal).append(" Instalment Due Soon</p>");
//        html.append("</div>");
//
//        // Alert Banner
//        html.append("<div style=\"background-color:#fff3cd;border-left:4px solid#f7623b;padding:15px 20px;margin:20px;\">");
//        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">");
//        html.append("🔔 Your payment is due in 7 days. Please make your payment to avoid late fees.");
//        html.append("</p></div>");
//
//        // Payment Amount (Gradient)
//        html.append("<div style=\"margin:20px;background:linear-gradient(135deg,#f7623b,#ff8c42);border-radius:8px;padding:20px;text-align:center;\">");
//        html.append("<h2 style=\"color:#000000;margin:0 0 15px 0;\">Payment Due</h2>");
//        html.append("<p style=\"color:#000000;font-size:36px;font-weight:bold;margin:10px 0;\">₦").append(String.format("%,.2f", amount)).append("</p>");
//        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Due Date:</strong> ").append(data.get("dueDate")).append("</p>");
//        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Loan Ref:</strong> ").append(data.get("loanReference")).append("</p>");
//        html.append("</div>");
//
//        // Progress
//        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
//        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Your Payment Progress</h3>");
//        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Amount Paid:</strong> ₦").append(String.format("%,.2f", (BigDecimal) data.get("amountPaid"))).append("</p>");
//        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Remaining Balance:</strong> ₦").append(String.format("%,.2f", (BigDecimal) data.get("remainingBalance"))).append("</p>");
//        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>This Payment:</strong> ").append(instalmentOrdinal).append(" of ").append(data.get("totalInstalments")).append("</p>");
//        html.append("</div>");
//
//        // Payment Button
//        html.append("<div style=\"text-align:center;padding:30px 20px;\">");
//        html.append("<a href=\"").append(paymentUrl).append("\" style=\"display:inline-block;padding:16px 48px;background-color:#f7623b;color:#000000;text-decoration:none;font-weight:bold;font-size:16px;border-radius:8px;box-shadow:0 4px 12px rgba(247,98,59,0.3);\">");
//        html.append("💳 Pay ").append(instalmentOrdinal).append(" Instalment");
//        html.append("</a>");
//        html.append("</div>");
//
//        // Button Description
//        html.append("<div style=\"text-align:center;padding:0 20px 30px;\">");
//        html.append("<p style=\"color:#666666;font-size:14px;margin:0;\">");
//        html.append("Click the button above to make your payment securely via Paystack");
//        html.append("</p>");
//        html.append("<p style=\"color:#dc3545;font-size:12px;margin:10px 0;font-weight:bold;\">");
//        html.append("⚠️ This payment link expires on: ").append(data.get("expirationDate"));
//        html.append("</p>");
//        html.append("</div>");
//
//        // Important Info
//        html.append("<div style=\"background-color:#fff3cd;padding:15px 20px;margin:20px;border-radius:8px;\">");
//        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">📋 Important Information:</p>");
//        html.append("<ul style=\"margin:10px 0;padding-left:20px;color:#856404;\">");
//        html.append("<li>Payment link is valid until ").append(data.get("expirationDate")).append("</li>");
//        html.append("<li>Late payments may incur additional fees and affect your credit score</li>");
//        html.append("<li>You will receive a payment confirmation email after successful payment</li>");
//        html.append("<li>Contact support immediately if you need payment assistance</li>");
//        html.append("</ul>");
//        html.append("</div>");
//
//        // Footer
//        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
//        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Loan Management System</p>");
//        html.append("<p style=\"color:#999999;margin:5px 0;font-size:12px;\">For support: support@koolboks.com</p>");
//        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
//        html.append("</div>");
//
//        html.append("</div></body></html>");
//
//        return html.toString();
//    }
//
//    /**
//     * Build guarantor reminder email HTML (BLACK/ORANGE STYLE WITH BUTTON)
//     */
//    private String buildGuarantorReminderEmail(Map<String, Object> data) {
//        StringBuilder html = new StringBuilder();
//
//        String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
//        String paymentUrl = frontendBaseUrl + "/repayment-payment?token=" + data.get("paymentToken");
//        BigDecimal amount = (BigDecimal) data.get("monthlyRepayment");
//
//        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
//        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
//        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");
//
//        // Header
//        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
//        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;\">");
//        html.append("<span style=\"font-size:48px;\">🔔</span>");
//        html.append("</div>");
//        html.append("<h1 style=\"color:#ffffff;margin:0;\">Payment Reminder</h1>");
//        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:16px;font-weight:bold;\">Guarantor Notification</p>");
//        html.append("</div>");
//
//        // Info Banner
//        html.append("<div style=\"background-color:#d1ecf1;border-left:4px solid #0c5460;padding:15px 20px;margin:20px;\">");
//        html.append("<p style=\"margin:0;color:#0c5460;font-weight:bold;\">");
//        html.append("This is a payment reminder for <strong>").append(data.get("customerName")).append("</strong>, whom you are guaranteeing.");
//        html.append("</p></div>");
//
//        // Payment Details
//        html.append("<div style=\"margin:20px;background:linear-gradient(135deg,#f7623b,#ff8c42);border-radius:8px;padding:20px;text-align:center;\">");
//        html.append("<h2 style=\"color:#000000;margin:0 0 15px 0;\">Payment Due</h2>");
//        html.append("<p style=\"color:#000000;font-size:32px;font-weight:bold;margin:10px 0;\">₦").append(String.format("%,.2f", amount)).append("</p>");
//        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Due Date:</strong> ").append(data.get("dueDate")).append("</p>");
//        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Instalment:</strong> ").append(instalmentOrdinal).append(" of ").append(data.get("totalInstalments")).append("</p>");
//        html.append("</div>");
//
//        // Borrower Info
//        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
//        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Borrower Information</h3>");
//        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Name:</strong> ").append(data.get("customerName")).append("</p>");
//        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Loan Reference:</strong> ").append(data.get("loanReference")).append("</p>");
//        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Amount Paid So Far:</strong> ₦").append(String.format("%,.2f", (BigDecimal) data.get("amountPaid"))).append("</p>");
//        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Remaining Balance:</strong> ₦").append(String.format("%,.2f", (BigDecimal) data.get("remainingBalance"))).append("</p>");
//        html.append("</div>");
//
//        // Payment Button for Guarantor
//        html.append("<div style=\"text-align:center;padding:30px 20px;\">");
//        html.append("<a href=\"").append(paymentUrl).append("\" style=\"display:inline-block;padding:16px 48px;background-color:#f7623b;color:#000000;text-decoration:none;font-weight:bold;font-size:16px;border-radius:8px;box-shadow:0 4px 12px rgba(247,98,59,0.3);\">");
//        html.append("💳 Pay ").append(instalmentOrdinal).append(" Instalment");
//        html.append("</a>");
//        html.append("</div>");
//
//        // Button Description
//        html.append("<div style=\"text-align:center;padding:0 20px 30px;\">");
//        html.append("<p style=\"color:#666666;font-size:14px;margin:0;\">");
//        html.append("You can make the payment on behalf of the borrower if needed");
//        html.append("</p>");
//        html.append("<p style=\"color:#dc3545;font-size:12px;margin:10px 0;font-weight:bold;\">");
//        html.append("⚠️ This payment link expires on: ").append(data.get("expirationDate"));
//        html.append("</p>");
//        html.append("</div>");
//
//        // Action Required
//        html.append("<div style=\"background-color:#fff3cd;padding:15px 20px;margin:20px;border-radius:8px;\">");
//        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">👥 As a Guarantor:</p>");
//        html.append("<ul style=\"margin:10px 0;padding-left:20px;color:#856404;\">");
//        html.append("<li>Please remind ").append(data.get("customerName")).append(" about this upcoming payment</li>");
//        html.append("<li>The borrower has also received a payment reminder via email and SMS</li>");
//        html.append("<li>You can make the payment on their behalf using the button above</li>");
//        html.append("<li>Contact us if there are any payment difficulties</li>");
//        html.append("</ul>");
//        html.append("</div>");
//
//        // Footer
//        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
//        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Loan Management System</p>");
//        html.append("<p style=\"color:#999999;margin:5px 0;font-size:12px;\">For support: support@koolboks.com</p>");
//        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
//        html.append("</div>");
//
//        html.append("</div></body></html>");
//
//        return html.toString();
//    }
//
//    private String getOrdinal(int number) {
//        if (number >= 11 && number <= 13) {
//            return number + "th";
//        }
//        switch (number % 10) {
//            case 1: return number + "st";
//            case 2: return number + "nd";
//            case 3: return number + "rd";
//            default: return number + "th";
//        }
//    }
//}


































// ================================================================
// COMPLETE PaymentReminderService.java - WITH SPRING BOOT TOKEN CREATION
// ================================================================
// Location: src/main/java/com/koolboks/creditProject/service/loan/PaymentReminderService.java
// This version creates tokens directly in Spring Boot database - NO CSRF ISSUES!
// ================================================================

package com.koolboks.creditProject.service.loan;

import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.entity.paymentToken.PaymentToken;
import com.koolboks.creditProject.repository.PaymentTokenRepository;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentReminderService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PaymentTokenRepository paymentTokenRepository;

    @Value("${notification.email.from}")
    private String fromEmail;

    @Value("${twilio.from.number}")
    private String twilioPhoneNumber;

    @Value("${frontend.base.url}")
    private String frontendBaseUrl;

    /**
     * Send payment reminder to customer and guarantor (both with buttons) + SMS
     */
    public void sendPaymentReminders(LoanRepayment loan, int instalmentNumber, LocalDate dueDate) {
        try {
            System.out.println("=== SENDING PAYMENT REMINDERS ===");
            System.out.println("Loan Reference: " + loan.getLoanReference());
            System.out.println("Instalment Number: " + instalmentNumber);
            System.out.println("Due Date: " + dueDate);

            // Step 1: Create payment token DIRECTLY in Spring Boot database (NO DJANGO!)
            String paymentToken = createPaymentTokenInSpringBoot(loan, instalmentNumber, dueDate);

            if (paymentToken == null) {
                System.err.println("Failed to create payment token");
                return;
            }

            // Step 2: Send reminders with token
            Map<String, Object> reminderData = buildReminderData(loan, instalmentNumber, dueDate, paymentToken);

            // Send customer email
            sendCustomerReminderEmail(reminderData);

            // Send guarantor email
            sendGuarantorReminderEmail(reminderData);

            // Send customer SMS
            sendCustomerReminderSms(reminderData);

            System.out.println("=== PAYMENT REMINDERS SENT SUCCESSFULLY ===");

        } catch (Exception e) {
            System.err.println("Error sending payment reminders: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create payment token directly in Spring Boot database (NO DJANGO API CALL!)
     */
    private String createPaymentTokenInSpringBoot(LoanRepayment loan, int instalmentNumber, LocalDate dueDate) {
        try {
            // Calculate expiration date (31 days from today, or 29 for February)
            LocalDateTime expirationDateTime = calculateExpirationDateTime(LocalDate.now());

            // Create token entity
            PaymentToken paymentToken = new PaymentToken(
                loan.getLoanReference(),
                instalmentNumber,
                loan.getMonthlyRepayment(),
                expirationDateTime
            );

            // Save to Spring Boot database
            PaymentToken savedToken = paymentTokenRepository.save(paymentToken);

            System.out.println("✅ Payment token created in Spring Boot: " + savedToken.getToken());
            System.out.println("   Expires at: " + savedToken.getExpiresAt());

            return savedToken.getToken();

        } catch (Exception e) {
            System.err.println("Error creating payment token in Spring Boot: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Calculate expiration date (calendar-aware)
     */
    private LocalDateTime calculateExpirationDateTime(LocalDate startDate) {
        LocalDate expiration = startDate.plusDays(31);

        // If expiration falls in February, limit to 29th
        if (expiration.getMonthValue() == 2 && expiration.getDayOfMonth() > 29) {
            expiration = LocalDate.of(expiration.getYear(), 2, 29);
        }

        return expiration.atTime(23, 59, 59);
    }

    private Map<String, Object> buildReminderData(LoanRepayment loan, int instalmentNumber, LocalDate dueDate, String token) {
        Map<String, Object> data = new HashMap<>();

        data.put("loanReference", loan.getLoanReference());
        data.put("instalmentNumber", instalmentNumber);
        data.put("customerName", loan.getFullName());
        data.put("customerEmail", loan.getEmail());
        data.put("customerPhone", loan.getPhone());
        data.put("guarantorEmail", loan.getGuarantorContact());
        data.put("monthlyRepayment", loan.getMonthlyRepayment());
        data.put("amountPaid", loan.getAmountPaid());
        data.put("remainingBalance", loan.getRemainingBalance());
        data.put("totalInstalments", loan.getNumberOfMonths());
        data.put("dueDate", dueDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        data.put("paymentToken", token);
        data.put("expirationDate", calculateExpirationDateTime(LocalDate.now()).toLocalDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));

        return data;
    }

    /**
     * Send payment reminder email to CUSTOMER (with payment button)
     */
    private void sendCustomerReminderEmail(Map<String, Object> data) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String customerEmail = (String) data.get("customerEmail");
            helper.setFrom(fromEmail);
            helper.setTo(customerEmail);

            String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
            helper.setSubject("💳 Payment Reminder: " + instalmentOrdinal + " Instalment Due Soon");

            String htmlContent = buildCustomerReminderEmail(data);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("✅ Customer reminder email sent to: " + customerEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send customer reminder email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send payment reminder email to GUARANTOR (with payment button)
     */
    private void sendGuarantorReminderEmail(Map<String, Object> data) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String guarantorEmail = (String) data.get("guarantorEmail");
            helper.setFrom(fromEmail);
            helper.setTo(guarantorEmail);

            String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
            helper.setSubject("🔔 Payment Reminder for " + data.get("customerName") + " - " + instalmentOrdinal + " Instalment");

            String htmlContent = buildGuarantorReminderEmail(data);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            System.out.println("✅ Guarantor reminder email sent to: " + guarantorEmail);

        } catch (MessagingException e) {
            System.err.println("Failed to send guarantor reminder email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Send payment reminder SMS to CUSTOMER
     */
    private void sendCustomerReminderSms(Map<String, Object> data) {
        try {
            String phoneNumber = (String) data.get("customerPhone");
            String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
            BigDecimal amount = (BigDecimal) data.get("monthlyRepayment");
            String dueDate = (String) data.get("dueDate");

            String smsText = "Koolboks Payment Reminder\n\n" +
                           "Your " + instalmentOrdinal + " instalment of ₦" + String.format("%,.2f", amount) +
                           " is due on " + dueDate + ".\n\n" +
                           "Check your email for the secure payment link.\n\n" +
                           "Loan Ref: " + data.get("loanReference");

            try {
                Message twilioMessage = Message.creator(
                    new PhoneNumber(phoneNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    smsText
                ).create();

                System.out.println("✅ Customer reminder SMS sent to: " + phoneNumber + " (SID: " + twilioMessage.getSid() + ")");

            } catch (Exception primaryError) {
                System.err.println("Failed to send to primary number: " + primaryError.getMessage());

                // Try fallback number
                String fallbackNumber = "+18777804236";
                Message fallbackMessage = Message.creator(
                    new PhoneNumber(fallbackNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    "Koolboks: Payment reminder for " + phoneNumber + "\n" + smsText
                ).create();

                System.out.println("✅ Customer reminder SMS sent to fallback: " + fallbackNumber);
            }

        } catch (Exception e) {
            System.err.println("Failed to send customer reminder SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Build customer reminder email HTML (BLACK/ORANGE STYLE)
     */
    private String buildCustomerReminderEmail(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
        String paymentUrl = frontendBaseUrl + "/repayment-payment?token=" + data.get("paymentToken");
        BigDecimal amount = (BigDecimal) data.get("monthlyRepayment");

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;\">");
        html.append("<span style=\"font-size:48px;\">💳</span>");
        html.append("</div>");
        html.append("<h1 style=\"color:#ffffff;margin:0;\">Payment Reminder</h1>");
        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:18px;font-weight:bold;\">").append(instalmentOrdinal).append(" Instalment Due Soon</p>");
        html.append("</div>");

        // Alert Banner
        html.append("<div style=\"background-color:#fff3cd;border-left:4px solid #f7623b;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">");
        html.append("🔔 Your payment is due in 7 days. Please make your payment to avoid late fees.");
        html.append("</p></div>");

        // Payment Amount (Gradient)
        html.append("<div style=\"margin:20px;background:linear-gradient(135deg,#f7623b,#ff8c42);border-radius:8px;padding:20px;text-align:center;\">");
        html.append("<h2 style=\"color:#000000;margin:0 0 15px 0;\">Payment Due</h2>");
        html.append("<p style=\"color:#000000;font-size:36px;font-weight:bold;margin:10px 0;\">₦").append(String.format("%,.2f", amount)).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Due Date:</strong> ").append(data.get("dueDate")).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Loan Ref:</strong> ").append(data.get("loanReference")).append("</p>");
        html.append("</div>");

        // Progress
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Your Payment Progress</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Amount Paid:</strong> ₦").append(String.format("%,.2f", (BigDecimal) data.get("amountPaid"))).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Remaining Balance:</strong> ₦").append(String.format("%,.2f", (BigDecimal) data.get("remainingBalance"))).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>This Payment:</strong> ").append(instalmentOrdinal).append(" of ").append(data.get("totalInstalments")).append("</p>");
        html.append("</div>");

        // Payment Button
        html.append("<div style=\"text-align:center;padding:30px 20px;\">");
        html.append("<a href=\"").append(paymentUrl).append("\" style=\"display:inline-block;padding:16px 48px;background-color:#f7623b;color:#000000;text-decoration:none;font-weight:bold;font-size:16px;border-radius:8px;box-shadow:0 4px 12px rgba(247,98,59,0.3);\">");
        html.append("💳 Pay ").append(instalmentOrdinal).append(" Instalment");
        html.append("</a>");
        html.append("</div>");

        // Button Description
        html.append("<div style=\"text-align:center;padding:0 20px 30px;\">");
        html.append("<p style=\"color:#666666;font-size:14px;margin:0;\">");
        html.append("Click the button above to make your payment securely via Paystack");
        html.append("</p>");
        html.append("<p style=\"color:#dc3545;font-size:12px;margin:10px 0;font-weight:bold;\">");
        html.append("⚠️ This payment link expires on: ").append(data.get("expirationDate"));
        html.append("</p>");
        html.append("</div>");

        // Important Info
        html.append("<div style=\"background-color:#fff3cd;padding:15px 20px;margin:20px;border-radius:8px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">📋 Important Information:</p>");
        html.append("<ul style=\"margin:10px 0;padding-left:20px;color:#856404;\">");
        html.append("<li>Payment link is valid until ").append(data.get("expirationDate")).append("</li>");
        html.append("<li>Late payments may incur additional fees and affect your credit score</li>");
        html.append("<li>You will receive a payment confirmation email after successful payment</li>");
        html.append("<li>Contact support immediately if you need payment assistance</li>");
        html.append("</ul>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Loan Management System</p>");
        html.append("<p style=\"color:#999999;margin:5px 0;font-size:12px;\">For support: support@koolboks.com</p>");
        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    /**
     * Build guarantor reminder email HTML (BLACK/ORANGE STYLE WITH BUTTON)
     */
    private String buildGuarantorReminderEmail(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
        String paymentUrl = frontendBaseUrl + "/repayment-payment?token=" + data.get("paymentToken");
        BigDecimal amount = (BigDecimal) data.get("monthlyRepayment");

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;\">");
        html.append("<span style=\"font-size:48px;\">🔔</span>");
        html.append("</div>");
        html.append("<h1 style=\"color:#ffffff;margin:0;\">Payment Reminder</h1>");
        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:16px;font-weight:bold;\">Guarantor Notification</p>");
        html.append("</div>");

        // Info Banner
        html.append("<div style=\"background-color:#d1ecf1;border-left:4px solid #0c5460;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0;color:#0c5460;font-weight:bold;\">");
        html.append("This is a payment reminder for <strong>").append(data.get("customerName")).append("</strong>, whom you are guaranteeing.");
        html.append("</p></div>");

        // Payment Details
        html.append("<div style=\"margin:20px;background:linear-gradient(135deg,#f7623b,#ff8c42);border-radius:8px;padding:20px;text-align:center;\">");
        html.append("<h2 style=\"color:#000000;margin:0 0 15px 0;\">Payment Due</h2>");
        html.append("<p style=\"color:#000000;font-size:32px;font-weight:bold;margin:10px 0;\">₦").append(String.format("%,.2f", amount)).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Due Date:</strong> ").append(data.get("dueDate")).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Instalment:</strong> ").append(instalmentOrdinal).append(" of ").append(data.get("totalInstalments")).append("</p>");
        html.append("</div>");

        // Borrower Info
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Borrower Information</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Name:</strong> ").append(data.get("customerName")).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Loan Reference:</strong> ").append(data.get("loanReference")).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Amount Paid So Far:</strong> ₦").append(String.format("%,.2f", (BigDecimal) data.get("amountPaid"))).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Remaining Balance:</strong> ₦").append(String.format("%,.2f", (BigDecimal) data.get("remainingBalance"))).append("</p>");
        html.append("</div>");

        // Payment Button for Guarantor
        html.append("<div style=\"text-align:center;padding:30px 20px;\">");
        html.append("<a href=\"").append(paymentUrl).append("\" style=\"display:inline-block;padding:16px 48px;background-color:#f7623b;color:#000000;text-decoration:none;font-weight:bold;font-size:16px;border-radius:8px;box-shadow:0 4px 12px rgba(247,98,59,0.3);\">");
        html.append("💳 Pay ").append(instalmentOrdinal).append(" Instalment");
        html.append("</a>");
        html.append("</div>");

        // Button Description
        html.append("<div style=\"text-align:center;padding:0 20px 30px;\">");
        html.append("<p style=\"color:#666666;font-size:14px;margin:0;\">");
        html.append("You can make the payment on behalf of the borrower if needed");
        html.append("</p>");
        html.append("<p style=\"color:#dc3545;font-size:12px;margin:10px 0;font-weight:bold;\">");
        html.append("⚠️ This payment link expires on: ").append(data.get("expirationDate"));
        html.append("</p>");
        html.append("</div>");

        // Action Required
        html.append("<div style=\"background-color:#fff3cd;padding:15px 20px;margin:20px;border-radius:8px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">👥 As a Guarantor:</p>");
        html.append("<ul style=\"margin:10px 0;padding-left:20px;color:#856404;\">");
        html.append("<li>Please remind ").append(data.get("customerName")).append(" about this upcoming payment</li>");
        html.append("<li>The borrower has also received a payment reminder via email and SMS</li>");
        html.append("<li>You can make the payment on their behalf using the button above</li>");
        html.append("<li>Contact us if there are any payment difficulties</li>");
        html.append("</ul>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Loan Management System</p>");
        html.append("<p style=\"color:#999999;margin:5px 0;font-size:12px;\">For support: support@koolboks.com</p>");
        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    private String getOrdinal(int number) {
        if (number >= 11 && number <= 13) {
            return number + "th";
        }
        switch (number % 10) {
            case 1: return number + "st";
            case 2: return number + "nd";
            case 3: return number + "rd";
            default: return number + "th";
        }
    }
}
// ================================================================
// SPRING BOOT - PAYMENT CONFIRMATION EMAIL SERVICE
// ================================================================
// Sends "Payment Received" confirmation to:
// 1. Customer
// 2. Guarantor
// 3. Agent
// All emails include remaining months information
// ================================================================
// Location: src/main/java/com/koolboks/creditProject/service/loan/PaymentConfirmationEmailService.java

package com.koolboks.creditProject.service.loan;

import com.koolboks.creditProject.service.BrevoEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Service
public class PaymentConfirmationEmailService {

    private final BrevoEmailService brevoEmailService;

    public PaymentConfirmationEmailService(BrevoEmailService brevoEmailService) {
        this.brevoEmailService = brevoEmailService;
    }

    public void sendPaymentConfirmationEmails(Map<String, Object> confirmationData) {
        try {
            System.out.println("=== SENDING PAYMENT CONFIRMATION EMAILS ===");

            CompletableFuture<Void> customerEmail = CompletableFuture.runAsync(() -> {
                sendCustomerConfirmationEmail(confirmationData);
            });

            CompletableFuture<Void> guarantorEmail = CompletableFuture.runAsync(() -> {
                sendGuarantorConfirmationEmail(confirmationData);
            });

            CompletableFuture<Void> agentEmail = CompletableFuture.runAsync(() -> {
                sendAgentConfirmationEmail(confirmationData);
            });

            CompletableFuture.allOf(customerEmail, guarantorEmail, agentEmail)
                .get(20, java.util.concurrent.TimeUnit.SECONDS);

            System.out.println("=== ALL PAYMENT CONFIRMATION EMAILS SENT ===");

        } catch (Exception e) {
            System.err.println("Error sending payment confirmation emails: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendCustomerConfirmationEmail(Map<String, Object> data) {
        String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
        String htmlContent = buildCustomerConfirmationEmail(data);
        brevoEmailService.sendEmail(
            (String) data.get("customerEmail"),
            "Customer",
            "✅ Payment Received - " + instalmentOrdinal + " Instalment Confirmed",
            htmlContent
        );
        System.out.println("✅ Customer confirmation email sent to: " + data.get("customerEmail"));
    }

    private void sendGuarantorConfirmationEmail(Map<String, Object> data) {
        String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
        String htmlContent = buildGuarantorConfirmationEmail(data);
        brevoEmailService.sendEmail(
            (String) data.get("guarantorEmail"),
            "Guarantor",
            "✅ Payment Received - " + data.get("customerName") + " - " + instalmentOrdinal + " Instalment",
            htmlContent
        );
        System.out.println("✅ Guarantor confirmation email sent to: " + data.get("guarantorEmail"));
    }

    private void sendAgentConfirmationEmail(Map<String, Object> data) {
        String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
        String agentEmail = "agent@koolboks.com";
        String htmlContent = buildAgentConfirmationEmail(data);
        brevoEmailService.sendEmail(
            agentEmail,
            "Agent",
            "📊 Payment Received - " + data.get("customerName") + " - " + instalmentOrdinal + " Instalment",
            htmlContent
        );
        System.out.println("✅ Agent confirmation email sent to: " + agentEmail);
    }

    // keep buildCustomerConfirmationEmail, buildGuarantorConfirmationEmail,
    // buildAgentConfirmationEmail, and getOrdinal unchanged


//@Service
//public class PaymentConfirmationEmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${notification.email.from}")
//    private String fromEmail;
//
//    /**
//     * Send payment confirmation emails to customer, guarantor, and agent SIMULTANEOUSLY
//     */
//    public void sendPaymentConfirmationEmails(Map<String, Object> confirmationData) {
//        try {
//            System.out.println("=== SENDING PAYMENT CONFIRMATION EMAILS ===");
//            System.out.println("Loan Reference: " + confirmationData.get("loanReference"));
//            System.out.println("Instalment Number: " + confirmationData.get("instalmentNumber"));
//
//            // Send all 3 emails in parallel
//            CompletableFuture<Void> customerEmail = CompletableFuture.runAsync(() -> {
//                sendCustomerConfirmationEmail(confirmationData);
//            });
//
//            CompletableFuture<Void> guarantorEmail = CompletableFuture.runAsync(() -> {
//                sendGuarantorConfirmationEmail(confirmationData);
//            });
//
//            CompletableFuture<Void> agentEmail = CompletableFuture.runAsync(() -> {
//                sendAgentConfirmationEmail(confirmationData);
//            });
//
//            // Wait for all to complete
//            CompletableFuture.allOf(customerEmail, guarantorEmail, agentEmail)
//                .get(20, java.util.concurrent.TimeUnit.SECONDS);
//
//            System.out.println("=== ALL PAYMENT CONFIRMATION EMAILS SENT ===");
//
//        } catch (Exception e) {
//            System.err.println("Error sending payment confirmation emails: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Send confirmation to CUSTOMER
//     */
//    private void sendCustomerConfirmationEmail(Map<String, Object> data) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            String customerEmail = (String) data.get("customerEmail");
//            helper.setFrom(fromEmail);
//            helper.setTo(customerEmail);
//
//            String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
//            helper.setSubject("✅ Payment Received - " + instalmentOrdinal + " Instalment Confirmed");
//
//            String htmlContent = buildCustomerConfirmationEmail(data);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//
//            System.out.println("✅ Customer confirmation email sent to: " + customerEmail);
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send customer confirmation email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Send confirmation to GUARANTOR
//     */
//    private void sendGuarantorConfirmationEmail(Map<String, Object> data) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            String guarantorEmail = (String) data.get("guarantorEmail");
//            helper.setFrom(fromEmail);
//            helper.setTo(guarantorEmail);
//
//            String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
//            helper.setSubject("✅ Payment Received - " + data.get("customerName") + " - " + instalmentOrdinal + " Instalment");
//
//            String htmlContent = buildGuarantorConfirmationEmail(data);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//
//            System.out.println("✅ Guarantor confirmation email sent to: " + guarantorEmail);
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send guarantor confirmation email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Send confirmation to AGENT
//     */
//    private void sendAgentConfirmationEmail(Map<String, Object> data) {
//        try {
//            // Get agent email from payment data
//            // You may need to fetch this from your database
//            String agentEmail = "agent@koolboks.com"; // Replace with actual agent email lookup
//
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            helper.setFrom(fromEmail);
//            helper.setTo(agentEmail);
//
//            String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
//            helper.setSubject("📊 Payment Received - " + data.get("customerName") + " - " + instalmentOrdinal + " Instalment");
//
//            String htmlContent = buildAgentConfirmationEmail(data);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//
//            System.out.println("✅ Agent confirmation email sent to: " + agentEmail);
//
//        } catch (MessagingException e) {
//            System.err.println("Failed to send agent confirmation email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    /**
     * Build CUSTOMER confirmation email
     */
    private String buildCustomerConfirmationEmail(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
        BigDecimal paymentAmount = (BigDecimal) data.get("paymentAmount");
        BigDecimal amountPaid = (BigDecimal) data.get("amountPaid");
        BigDecimal remainingBalance = (BigDecimal) data.get("remainingBalance");
        int remainingMonths = (Integer) data.get("remainingMonths");

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header with SUCCESS theme
        html.append("<div style=\"background:linear-gradient(135deg,#28a745 0%,#20c997 100%);padding:40px 20px;text-align:center;\">");
        html.append("<div style=\"background-color:#ffffff;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;\">");
        html.append("<span style=\"font-size:48px;\">✅</span>");
        html.append("</div>");
        html.append("<h1 style=\"color:#ffffff;margin:0;font-size:32px;\">Payment Received!</h1>");
        html.append("<p style=\"color:#ffffff;margin:10px 0;font-size:18px;font-weight:bold;\">Thank you for your payment</p>");
        html.append("</div>");

        // Success Banner
        html.append("<div style=\"background-color:#d4edda;border-left:4px solid #28a745;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0;color:#155724;font-weight:bold;\">");
        html.append("✅ Your ").append(instalmentOrdinal).append(" instalment payment has been successfully processed!");
        html.append("</p></div>");

        // Payment Details (Gradient)
        html.append("<div style=\"margin:20px;background:linear-gradient(135deg,#f7623b,#ff8c42);border-radius:8px;padding:20px;text-align:center;\">");
        html.append("<h2 style=\"color:#000000;margin:0 0 15px 0;\">Payment Details</h2>");
        html.append("<p style=\"color:#000000;font-size:36px;font-weight:bold;margin:10px 0;\">₦").append(String.format("%,.2f", paymentAmount)).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Instalment:</strong> ").append(instalmentOrdinal).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Reference:</strong> ").append(data.get("paymentReference")).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Loan Ref:</strong> ").append(data.get("loanReference")).append("</p>");
        html.append("</div>");

        // Remaining Balance
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Your Loan Progress</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Total Paid So Far:</strong> ₦").append(String.format("%,.2f", amountPaid)).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Remaining Balance:</strong> ₦").append(String.format("%,.2f", remainingBalance)).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Remaining Payments:</strong> ").append(remainingMonths).append(" months</p>");

        if (remainingMonths == 0) {
            html.append("<div style=\"margin-top:15px;padding:15px;background-color:#28a745;border-radius:8px;\">");
            html.append("<p style=\"color:#ffffff;margin:0;font-weight:bold;font-size:18px;\">🎉 Congratulations! You have completed all payments!</p>");
            html.append("</div>");
        }

        html.append("</div>");

        // Next Payment Info (if any remaining)
        if (remainingMonths > 0) {
            html.append("<div style=\"margin:20px;background-color:#fff3cd;border-left:4px solid #ffc107;padding:15px 20px;border-radius:8px;\">");
            html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">📅 Next Payment:</p>");
            html.append("<p style=\"margin:10px 0;color:#856404;\">");
            html.append("You have <strong>").append(remainingMonths).append(" more payment(s)</strong> remaining. ");
            html.append("You'll receive a reminder 7 days before your next payment is due.");
            html.append("</p>");
            html.append("</div>");
        }

        // Receipt Info
        html.append("<div style=\"margin:20px;background-color:#0a0a0a;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Receipt Information</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\">Keep this email as proof of payment.</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\">Payment Reference: <strong>").append(data.get("paymentReference")).append("</strong></p>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Thank you for your payment!</p>");
        html.append("<p style=\"color:#999999;margin:5px 0;font-size:12px;\">Koolboks Loan Management System</p>");
        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    /**
     * Build GUARANTOR confirmation email
     */
    private String buildGuarantorConfirmationEmail(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
        BigDecimal paymentAmount = (BigDecimal) data.get("paymentAmount");
        BigDecimal amountPaid = (BigDecimal) data.get("amountPaid");
        BigDecimal remainingBalance = (BigDecimal) data.get("remainingBalance");
        int remainingMonths = (Integer) data.get("remainingMonths");

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#28a745 0%,#20c997 100%);padding:40px 20px;text-align:center;\">");
        html.append("<div style=\"background-color:#ffffff;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;\">");
        html.append("<span style=\"font-size:48px;\">✅</span>");
        html.append("</div>");
        html.append("<h1 style=\"color:#ffffff;margin:0;\">Payment Received</h1>");
        html.append("<p style=\"color:#ffffff;margin:10px 0;font-size:16px;font-weight:bold;\">Guarantor Notification</p>");
        html.append("</div>");

        // Info Banner
        html.append("<div style=\"background-color:#d1ecf1;border-left:4px solid #0c5460;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0;color:#0c5460;font-weight:bold;\">");
        html.append("<strong>").append(data.get("customerName")).append("</strong> has successfully made their ").append(instalmentOrdinal).append(" instalment payment.");
        html.append("</p></div>");

        // Payment Details
        html.append("<div style=\"margin:20px;background:linear-gradient(135deg,#f7623b,#ff8c42);border-radius:8px;padding:20px;text-align:center;\">");
        html.append("<h2 style=\"color:#000000;margin:0 0 15px 0;\">Payment Received</h2>");
        html.append("<p style=\"color:#000000;font-size:32px;font-weight:bold;margin:10px 0;\">₦").append(String.format("%,.2f", paymentAmount)).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Instalment:</strong> ").append(instalmentOrdinal).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Borrower:</strong> ").append(data.get("customerName")).append("</p>");
        html.append("</div>");

        // Loan Progress
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Loan Progress</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Total Paid:</strong> ₦").append(String.format("%,.2f", amountPaid)).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Remaining Balance:</strong> ₦").append(String.format("%,.2f", remainingBalance)).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Remaining Payments:</strong> ").append(remainingMonths).append(" months</p>");
        html.append("</div>");

        // Status Update
        if (remainingMonths == 0) {
            html.append("<div style=\"margin:20px;background-color:#d4edda;border-left:4px solid #28a745;padding:15px 20px;border-radius:8px;\">");
            html.append("<p style=\"margin:0;color:#155724;font-weight:bold;\">");
            html.append("🎉 Great news! ").append(data.get("customerName")).append(" has completed all loan payments!");
            html.append("</p></div>");
        } else {
            html.append("<div style=\"margin:20px;background-color:#fff3cd;border-left:4px solid #ffc107;padding:15px 20px;border-radius:8px;\">");
            html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">📊 Loan Status:</p>");
            html.append("<p style=\"margin:10px 0;color:#856404;\">");
            html.append(data.get("customerName")).append(" has <strong>").append(remainingMonths).append(" more payment(s)</strong> remaining.");
            html.append("</p></div>");
        }

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Loan Management System</p>");
        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }

    /**
     * Build AGENT confirmation email
     */
    private String buildAgentConfirmationEmail(Map<String, Object> data) {
        StringBuilder html = new StringBuilder();

        String instalmentOrdinal = getOrdinal((Integer) data.get("instalmentNumber"));
        BigDecimal paymentAmount = (BigDecimal) data.get("paymentAmount");
        int remainingMonths = (Integer) data.get("remainingMonths");

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<h1 style=\"color:#ffffff;margin:0;\">Payment Received</h1>");
        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:16px;font-weight:bold;\">Agent Notification</p>");
        html.append("</div>");

        // Success Banner
        html.append("<div style=\"background-color:#d4edda;border-left:4px solid #28a745;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0;color:#155724;font-weight:bold;\">");
        html.append("✅ ").append(data.get("customerName")).append(" has paid their ").append(instalmentOrdinal).append(" instalment");
        html.append("</p></div>");

        // Payment Summary
        html.append("<div style=\"margin:20px;background-color:#f7623b;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#000000;margin:0 0 15px 0;\">Payment Summary</h3>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Customer:</strong> ").append(data.get("customerName")).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Amount:</strong> ₦").append(String.format("%,.2f", paymentAmount)).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Instalment:</strong> ").append(instalmentOrdinal).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Remaining Months:</strong> ").append(remainingMonths).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Loan Ref:</strong> ").append(data.get("loanReference")).append("</p>");
        html.append("</div>");

        // Status
        if (remainingMonths == 0) {
            html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
            html.append("<p style=\"color:#28a745;margin:0;font-weight:bold;font-size:18px;\">");
            html.append("🎉 Loan Completed! All instalments have been paid.");
            html.append("</p></div>");
        }

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Koolboks Agent Portal</p>");
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
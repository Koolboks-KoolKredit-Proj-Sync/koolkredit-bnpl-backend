

package com.koolboks.creditProject.service.loan;

import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.repository.LoanRepaymentRepository;
import com.koolboks.creditProject.service.BrevoEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;


@Service
public class LoanActivationEmailService {

    private final BrevoEmailService brevoEmailService;
    private final LoanRepaymentRepository loanRepaymentRepository;

    @Value("${notification.email.from}")
    private String fromEmail;

    public LoanActivationEmailService(BrevoEmailService brevoEmailService,
                                      LoanRepaymentRepository loanRepaymentRepository) {
        this.brevoEmailService = brevoEmailService;
        this.loanRepaymentRepository = loanRepaymentRepository;
    }

    public void sendLoanActivationEmail(Map<String, Object> activationData) {
        try {
            String loanReference = String.valueOf(activationData.get("customerLoanRef"));
            System.out.println("=== SENDING LOAN ACTIVATION EMAIL ===");

            LoanRepayment loanRepayment = loanRepaymentRepository.findByLoanReference(loanReference)
                .orElseThrow(() -> new RuntimeException("Loan repayment not found: " + loanReference));

            if (loanRepayment.getRepaymentStatus() == LoanRepayment.RepaymentStatus.PENDING) {
                loanRepayment.setRepaymentStatus(LoanRepayment.RepaymentStatus.ACTIVE);
                loanRepaymentRepository.save(loanRepayment);
                System.out.println("✅ Loan status updated to ACTIVE");
            }

            String installationDateStr = String.valueOf(activationData.get("installationDate"));
            LocalDate installationDate = LocalDate.parse(installationDateStr);
            LocalDate firstDueDate = installationDate.plusDays(30);

            String customerEmail = String.valueOf(activationData.get("customerEmail"));
            String htmlContent = buildActivationEmailContent(activationData, loanRepayment, firstDueDate);

            brevoEmailService.sendEmail(
                customerEmail,
                String.valueOf(activationData.get("customerFirstName")),
                "🎉 Loan Activated - Welcome to Koolboks Financing!",
                htmlContent
            );

            System.out.println("✅ Loan activation email sent to: " + customerEmail);

        } catch (Exception e) {
            System.err.println("Failed to send loan activation email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // keep buildActivationEmailContent unchanged


//@Service
//public class LoanActivationEmailService {
//
//    @Autowired
//    private JavaMailSender mailSender;
//
//    @Autowired
//    private LoanRepaymentRepository loanRepaymentRepository;  // INJECT REPOSITORY DIRECTLY
//
//    @Value("${notification.email.from}")
//    private String fromEmail;
//
//    /**
//     * Send loan activation email and update loan status
//     */
//    public void sendLoanActivationEmail(Map<String, Object> activationData) {
//        try {
//            String loanReference = String.valueOf(activationData.get("customerLoanRef"));
//
//            System.out.println("=== SENDING LOAN ACTIVATION EMAIL ===");
//            System.out.println("Loan Reference: " + loanReference);
//
//            // Get loan repayment details
//            LoanRepayment loanRepayment = loanRepaymentRepository.findByLoanReference(loanReference)
//                .orElseThrow(() -> new RuntimeException("Loan repayment not found: " + loanReference));
//
//            System.out.println("Current status: " + loanRepayment.getRepaymentStatus());
//
//            // Update repayment status to ACTIVE (if currently PENDING)
//            if (loanRepayment.getRepaymentStatus() == LoanRepayment.RepaymentStatus.PENDING) {
//                loanRepayment.setRepaymentStatus(LoanRepayment.RepaymentStatus.ACTIVE);
//                loanRepaymentRepository.save(loanRepayment);  // SAVE THE CHANGE
//                System.out.println("✅ Loan status updated to ACTIVE");
//            } else {
//                System.out.println("ℹ️ Loan status already: " + loanRepayment.getRepaymentStatus());
//            }
//
//            // Calculate first payment due date (installation date + 30 days)
//            String installationDateStr = String.valueOf(activationData.get("installationDate"));
//            LocalDate installationDate = LocalDate.parse(installationDateStr);
//            LocalDate firstDueDate = installationDate.plusDays(30);
//
//            System.out.println("First payment due: " + firstDueDate);
//
//            // Send email
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//            String customerEmail = String.valueOf(activationData.get("customerEmail"));
//            helper.setFrom(fromEmail);
//            helper.setTo(customerEmail);
//            helper.setSubject("🎉 Loan Activated - Welcome to Koolboks Financing!");
//
//            String htmlContent = buildActivationEmailContent(activationData, loanRepayment, firstDueDate);
//            helper.setText(htmlContent, true);
//
//            mailSender.send(message);
//
//            System.out.println("✅ Loan activation email sent to: " + customerEmail);
//            System.out.println("=== LOAN ACTIVATION COMPLETED ===");
//
//        } catch (Exception e) {
//            System.err.println("Failed to send loan activation email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private String buildActivationEmailContent(Map<String, Object> data, LoanRepayment loan, LocalDate firstDueDate) {
        StringBuilder html = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        html.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f5f5f5;\">");
        html.append("<div style=\"max-width:600px;margin:0 auto;background-color:#ffffff;\">");

        // Header with gradient
        html.append("<div style=\"background:linear-gradient(135deg,#000000 0%,#1a1a1a 100%);padding:40px 20px;text-align:center;\">");
        html.append("<div style=\"background-color:#f7623b;width:80px;height:80px;border-radius:50%;margin:0 auto 20px;display:flex;align-items:center;justify-content:center;\">");
        html.append("<span style=\"font-size:48px;\">🎉</span>");
        html.append("</div>");
        html.append("<h1 style=\"color:#ffffff;margin:0;font-size:32px;\">Congratulations!</h1>");
        html.append("<p style=\"color:#f7623b;margin:10px 0;font-size:18px;font-weight:bold;\">Your Loan is Now Active</p>");
        html.append("</div>");

        // Success Banner
        html.append("<div style=\"background-color:#d4edda;border-left:4px solid #28a745;padding:15px 20px;margin:20px;\">");
        html.append("<p style=\"margin:0;color:#155724;font-weight:bold;\">");
        html.append("✅ Installation completed! Your ").append(data.get("productName")).append(" is ready to use.");
        html.append("</p></div>");

        // Installation Details
        html.append("<div style=\"margin:20px;background-color:#f7623b;border-radius:8px;padding:20px;\">");
        html.append("<h2 style=\"color:#000000;margin:0 0 15px 0;\">Installation Confirmed</h2>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Date:</strong> ").append(data.get("installationDate")).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Product:</strong> ").append(data.get("productName")).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Installed By:</strong> ").append(data.get("installerName")).append("</p>");
        html.append("<p style=\"color:#000000;margin:5px 0;\"><strong>Order ID:</strong> ").append(data.get("orderId")).append("</p>");
        html.append("</div>");

        // Loan Repayment Details
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h2 style=\"color:#f7623b;margin:0 0 15px 0;\">Your Repayment Plan</h2>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Loan Reference:</strong> ").append(loan.getLoanReference()).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Total Amount:</strong> ₦").append(String.format("%,.2f", loan.getTotalInstalment())).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Monthly Payment:</strong> ₦").append(String.format("%,.2f", loan.getMonthlyRepayment())).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Loan Duration:</strong> ").append(loan.getLoanDuration()).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Number of Payments:</strong> ").append(loan.getNumberOfMonths()).append(" instalments</p>");
        html.append("</div>");

        // First Payment Due (with gradient)
        html.append("<div style=\"margin:20px;background:linear-gradient(135deg,#f7623b,#ff8c42);border-radius:8px;padding:20px;text-align:center;\">");
        html.append("<h3 style=\"color:#000000;margin:0 0 10px 0;\">📅 First Payment Due Date</h3>");
        html.append("<p style=\"color:#000000;font-size:28px;font-weight:bold;margin:10px 0;\">")
            .append(firstDueDate.format(formatter)).append("</p>");
        html.append("<p style=\"color:#000000;margin:10px 0;font-size:14px;\">");
        html.append("You will receive a payment reminder 7 days before this date via email and SMS");
        html.append("</p>");
        html.append("</div>");

        // Payment Schedule Info
        html.append("<div style=\"margin:20px;background-color:#0a0a0a;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">What Happens Next?</h3>");
        html.append("<ul style=\"color:#ffffff;line-height:1.8;padding-left:20px;margin:10px 0;\">");
        html.append("<li><strong>7 days before each due date</strong>, you'll receive a payment reminder</li>");
        html.append("<li>The reminder will include a <strong>secure payment link</strong> valid for 31 days</li>");
        html.append("<li>You can pay directly via the link sent to your email and phone</li>");
        html.append("<li>After each payment, you'll receive a <strong>confirmation receipt</strong></li>");
        html.append("<li>Your guarantor will also be notified of payment reminders</li>");
        html.append("</ul>");
        html.append("</div>");

        // Important Reminders
        html.append("<div style=\"background-color:#fff3cd;border-left:4px solid #ffc107;padding:15px 20px;margin:20px;border-radius:8px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-weight:bold;\">⚠️ Important Reminders:</p>");
        html.append("<ul style=\"margin:10px 0;padding-left:20px;color:#856404;\">");
        html.append("<li>Your repayment countdown has officially begun from ").append(data.get("installationDate")).append("</li>");
        html.append("<li>Payment links expire 31 days after being sent (29 days for February)</li>");
        html.append("<li>Late payments may affect your credit score and incur additional fees</li>");
        html.append("<li>Contact support immediately if you have payment difficulties</li>");
        html.append("<li>Keep your contact information up to date to receive reminders</li>");
        html.append("</ul>");
        html.append("</div>");

        // Customer Details
        html.append("<div style=\"margin:20px;background-color:#000000;border-radius:8px;padding:20px;\">");
        html.append("<h3 style=\"color:#f7623b;margin:0 0 15px 0;\">Your Account Details</h3>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Name:</strong> ").append(data.get("customerFirstName")).append(" ").append(data.get("customerLastName")).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Email:</strong> ").append(data.get("customerEmail")).append("</p>");
        html.append("<p style=\"color:#ffffff;margin:5px 0;\"><strong>Guarantor:</strong> ").append(loan.getGuarantorContact()).append("</p>");
        html.append("</div>");

        // Footer
        html.append("<div style=\"background-color:#000000;padding:30px 20px;text-align:center;\">");
        html.append("<p style=\"color:#999999;margin:0;font-size:12px;\">Thank you for choosing Koolboks!</p>");
        html.append("<p style=\"color:#999999;margin:5px 0;font-size:12px;\">For support: support@koolboks.com</p>");
        html.append("<p style=\"color:#666666;margin:5px 0;font-size:12px;\">© 2026 Koolboks. All rights reserved.</p>");
        html.append("</div>");

        html.append("</div></body></html>");

        return html.toString();
    }
}
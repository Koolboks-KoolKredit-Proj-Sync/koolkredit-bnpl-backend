//package com.koolboks.creditProject.service;
//
//import com.koolboks.creditProject.entity.AgentFollowUp;
//import com.koolboks.creditProject.entity.Guarantor;
//import com.koolboks.creditProject.entity.LoanRepayment;
//import com.koolboks.creditProject.repository.GuarantorRepository;
//import com.koolboks.creditProject.service.loan_calculator.LoanCalculatorService;
//import com.koolboks.creditProject.service.loan_calculator.LoanCalculatorService.LoanCalculation;
//import com.koolboks.creditProject.service.loan_repayment_service.LoanRepaymentService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import jakarta.mail.internet.MimeMessage;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class OfferLetterService {
//
//    private static final Logger log = LoggerFactory.getLogger(OfferLetterService.class);
//    private static final BigDecimal DEFAULT_FEE_RATE = new BigDecimal("0.02"); // 2% defaulter fee
//
//    private final GuarantorRepository guarantorRepository;
//    private final LoanCalculatorService loanCalculatorService;
//    private final JavaMailSender mailSender;
//    private final SmsService smsService;
//
//
//    // Add this field to OfferLetterService
//    private final LoanRepaymentService loanRepaymentService;
//
//
//    @Value("${notification.email.from:noreply@koolkredit.com}")
//    private String fromEmail;
//
//    @Value("${payment.url:https://pay.koolkredit.com}")
//    private String paymentUrl;
//
//    public OfferLetterService(GuarantorRepository guarantorRepository,
//                              LoanCalculatorService loanCalculatorService,
//                              JavaMailSender mailSender,
//                              SmsService smsService, LoanRepaymentService loanRepaymentService) {
//        this.guarantorRepository = guarantorRepository;
//        this.loanCalculatorService = loanCalculatorService;
//        this.mailSender = mailSender;
//        this.smsService = smsService;
//        this.loanRepaymentService = loanRepaymentService;
//    }
//
//    /**
//     * Generate and send offer letter after guarantor verification
//     */
//    public Map<String, Object> generateAndSendOfferLetter(Long guarantorId) {
//    Map<String, Object> response = new HashMap<>();
//
//
//
//
//
//
//    try {
//        log.info("=== STARTING OFFER LETTER GENERATION FOR GUARANTOR ID: {} ===", guarantorId);
//
//        // Get guarantor record
//        Guarantor guarantor = guarantorRepository.findById(guarantorId)
//                .orElseThrow(() -> new RuntimeException("Guarantor not found with ID: " + guarantorId));
//
//        log.info("Found guarantor for customer: {} {}",
//            guarantor.getCustomerFirstName(), guarantor.getCustomerLastName());
//
//        // Verify guarantor is OTP verified
//        if (!guarantor.getOtpVerified()) {
//            log.warn("Guarantor ID {} is not yet OTP verified", guarantorId);
//            response.put("success", false);
//            response.put("message", "Guarantor not yet verified");
//            return response;
//        }
//
//        // Verify customer email exists
//        if (guarantor.getCustomerEmail() == null || guarantor.getCustomerEmail().isBlank()) {
//            throw new RuntimeException(
//                "Customer email is missing for guarantor ID: " + guarantor.getId()
//            );
//        }
//
//        log.info("Guarantor verified. OTP verified at: {}", guarantor.getOtpVerifiedAt());
//
//        // ✅ GET ACTUAL VALUES FROM GUARANTOR ENTITY (NOT PARSING FROM STRINGS!)
//        BigDecimal storePrice = guarantor.getStorePrice();
//        String plan = guarantor.getCustomerPlan(); // "Easy 35", "Easy 20", "Omolope", etc.
//
//        // ✅ USE THE SAME RESOLUTION LOGIC
//        //Integer duration = parseDuration(resolveInstallmentDuration(guarantor));
//
//
//        String installmentDuration = resolveInstallmentDuration(guarantor);
//        Integer duration = parseDuration(installmentDuration);
//
//        //Integer duration = parseDuration(guarantor.getCustomerInstallmentDuration());
//
//        // ✅ VALIDATE STORE PRICE
//        if (storePrice == null || storePrice.compareTo(BigDecimal.ZERO) <= 0) {
//            log.error("❌ Invalid store price for guarantor ID {}: {}", guarantorId, storePrice);
//            throw new RuntimeException("Store price is missing or invalid. Please check the guarantor data.");
//        }
//
//        log.info("✅ Using values from database:");
//        log.info("   Store Price: {}", storePrice);
//        log.info("   Plan: {}", plan);
//        log.info("   Duration: {}", duration);
//
//        // Calculate loan details
//        log.info("Calculating loan details using LoanCalculatorService...");
//        LoanCalculation calculation = loanCalculatorService.calculate(storePrice, plan, duration);
//
//        log.info("✅ Calculation complete:");
//        log.info("   Down Payment: {}", calculation.getUpfront());
//        log.info("   Loan Amount: {}", calculation.getLoanAmount());
//        log.info("   Total Payable: {}", calculation.getUnlockPrice());
//
//        // Calculate defaulter fee
//        BigDecimal defaulterFee = calculateDefaulterFee(calculation);
//        log.info("Defaulter fee calculated: {}", defaulterFee);
//
//        // Generate unique application reference
//        String applicationRef = generateApplicationReference(guarantor.getCustomerBvn());
//        log.info("Generated application reference: {}", applicationRef);
//
//        // Generate payment link
//        String paymentLink = generatePaymentLink(applicationRef, calculation.getUpfront());
//        log.info("Generated payment link: {}", paymentLink);
//
//        // Send email to customer
//        log.info("Sending offer letter email to customer: {}", guarantor.getCustomerEmail());
//        sendCustomerOfferLetter(guarantor, calculation, defaulterFee, paymentLink, applicationRef);
//
//        // Send email to guarantor
//        log.info("Sending offer letter email to guarantor: {}", guarantor.getGuarantorEmail());
//        sendGuarantorOfferLetter(guarantor, calculation, applicationRef);
//
//        // Send SMS to customer
//        log.info("Sending SMS to customer...");
//        sendCustomerSms(guarantor, calculation, paymentLink);
//
//        // Send SMS to guarantor
//        log.info("Sending SMS to guarantor: {}", guarantor.getGuarantorPhoneNumber());
//        sendGuarantorSms(guarantor, calculation);
//
//        // Update guarantor record
//        guarantor.setOfferLetterSent(true);
//        guarantor.setOfferLetterSentAt(LocalDateTime.now());
//        guarantor.setApplicationReference(applicationRef);
//        guarantorRepository.save(guarantor);
//
//
//        // ✅ CREATE LOAN REPAYMENT RECORD
//        try {
//            log.info("Creating loan repayment record...");
//            LoanRepayment loanRepayment = loanRepaymentService.createLoanRepayment(
//                guarantor,
//                calculation,
//                applicationRef
//            );
//            log.info("✅ Loan repayment record created with ID: {}", loanRepayment.getId());
//        } catch (Exception e) {
//            log.error("❌ Failed to create loan repayment record (continuing anyway): {}", e.getMessage());
//            // Don't throw - we still want to send emails even if repayment record fails
//        }
//
//
//        log.info("Guarantor record updated with offer letter details");
//        log.info("=== OFFER LETTER PROCESS COMPLETED SUCCESSFULLY FOR GUARANTOR ID: {} ===", guarantorId);
//
//        response.put("success", true);
//        response.put("message", "Offer letter and payment details sent successfully");
//        response.put("applicationReference", applicationRef);
//        response.put("downPayment", calculation.getUpfront());
//        response.put("paymentLink", paymentLink);
//
//        return response;
//
//    } catch (Exception e) {
//        log.error("=== ERROR GENERATING OFFER LETTER FOR GUARANTOR ID: {} ===", guarantorId, e);
//        response.put("success", false);
//        response.put("message", "Error: " + e.getMessage());
//        response.put("errorDetails", e.toString());
//        return response;
//    }
//}
//
//    private void sendCustomerOfferLetter(Guarantor guarantor, LoanCalculation calc,
//                                         BigDecimal defaulterFee, String paymentLink,
//                                         String applicationRef) {
//        try {
//            log.info("Creating customer offer letter email for: {}", guarantor.getCustomerEmail());
//
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//
//            helper.setFrom(fromEmail);
//            helper.setTo(guarantor.getCustomerEmail());
//            helper.setSubject("🎉 Congratulations! Your Loan Application is Approved");
//
//            String htmlContent = buildCustomerOfferLetterEmail(guarantor, calc, defaulterFee,
//                                                               paymentLink, applicationRef);
//            helper.setText(htmlContent, true);
//
//            log.info("Sending customer offer letter email...");
//            mailSender.send(mimeMessage);
//            log.info("✅ Customer offer letter email sent successfully to: {}", guarantor.getCustomerEmail());
//
//        } catch (Exception e) {
//            log.error("❌ FAILED to send customer offer letter email to: {}", guarantor.getCustomerEmail(), e);
//            throw new RuntimeException("Failed to send customer email: " + e.getMessage(), e);
//        }
//    }
//
//    private void sendGuarantorOfferLetter(Guarantor guarantor, LoanCalculation calc,
//                                          String applicationRef) {
//        try {
//            log.info("Creating guarantor offer letter email for: {}", guarantor.getGuarantorEmail());
//
//            MimeMessage mimeMessage = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
//
//            helper.setFrom(fromEmail);
//            helper.setTo(guarantor.getGuarantorEmail());
//            helper.setSubject("📋 Loan Agreement - You're Listed as Guarantor");
//
//            String htmlContent = buildGuarantorOfferLetterEmail(guarantor, calc, applicationRef);
//            helper.setText(htmlContent, true);
//
//            log.info("Sending guarantor offer letter email...");
//            mailSender.send(mimeMessage);
//            log.info("✅ Guarantor offer letter email sent successfully to: {}", guarantor.getGuarantorEmail());
//
//        } catch (Exception e) {
//            log.error("❌ FAILED to send guarantor offer letter email to: {}", guarantor.getGuarantorEmail(), e);
//            throw new RuntimeException("Failed to send guarantor email: " + e.getMessage(), e);
//        }
//    }
//
//    private void sendCustomerSms(Guarantor guarantor, LoanCalculation calc, String paymentLink) {
//        try {
//            String customerPhone = getCustomerPhoneNumber(guarantor);
//
//            if (customerPhone == null || customerPhone.isBlank()) {
//                log.warn("⚠️ Customer phone number not available. Skipping customer SMS.");
//                return;
//            }
//
//            log.info("Preparing customer SMS for: {}", customerPhone);
//
//            String message = String.format(
//                "Congratulations %s! Your loan is APPROVED. Down Payment: NGN %,.2f. " +
//                "Pay now: %s. Ref: %s",
//                guarantor.getCustomerFirstName(),
//                calc.getUpfront(),
//                paymentLink,
//                guarantor.getApplicationReference()
//            );
//
//            log.info("Sending customer SMS...");
//            boolean sent = smsService.sendSms(customerPhone, message);
//
//            if (sent) {
//                log.info("✅ Customer SMS sent successfully to: {}", customerPhone);
//            } else {
//                log.warn("⚠️ Customer SMS sending failed (no exception thrown)");
//            }
//
//        } catch (Exception e) {
//            log.error("❌ Error sending customer SMS", e);
//            // Don't throw exception - SMS failure shouldn't stop the whole process
//        }
//    }
//
//    private void sendGuarantorSms(Guarantor guarantor, LoanCalculation calc) {
//        try {
//            log.info("Preparing guarantor SMS for: {}", guarantor.getGuarantorPhoneNumber());
//
//            String message = String.format(
//                "Hello, you're confirmed as guarantor for %s %s. Loan Amount: NGN %,.2f. " +
//                "Duration: %s. Thank you for your support.",
//                guarantor.getCustomerFirstName(),
//                guarantor.getCustomerLastName(),
//                calc.getLoanAmount(),
//                guarantor.getCustomerInstallmentDuration()
//            );
//
//            log.info("Sending guarantor SMS...");
//            boolean sent = smsService.sendSms(guarantor.getGuarantorPhoneNumber(), message);
//
//            if (sent) {
//                log.info("✅ Guarantor SMS sent successfully to: {}", guarantor.getGuarantorPhoneNumber());
//            } else {
//                log.warn("⚠️ Guarantor SMS sending failed (no exception thrown)");
//            }
//
//        } catch (Exception e) {
//            log.error("❌ Error sending guarantor SMS to: {}", guarantor.getGuarantorPhoneNumber(), e);
//            // Don't throw exception - SMS failure shouldn't stop the whole process
//        }
//    }
//
//    private String buildCustomerOfferLetterEmail(Guarantor guarantor, LoanCalculation calc,
//                                                 BigDecimal defaulterFee, String paymentLink,
//                                                 String applicationRef) {
//        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
//        String paymentSchedule = generatePaymentSchedule(calc);
//
//        // ✅ FIX: Create consistent duration string from calculation
//        String duration = "Omolope".equals(calc.getPlan())
//            ? calc.getDays() + " days"
//            : calc.getMonths() + " months";
//
//        return String.format("""
//<!DOCTYPE html>
//<html>
//<head>
//    <style>
//        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
//        .container { max-width: 700px; margin: 0 auto; padding: 20px; background-color: #f5f5f5; }
//        .header { background: linear-gradient(135deg, #f7623b 0%%, #d5541f 100%%); color: white; padding: 40px 20px; text-align: center; border-radius: 10px 10px 0 0; }
//        .header h1 { margin: 0; font-size: 28px; }
//        .header p { margin: 10px 0 0 0; font-size: 14px; opacity: 0.9; }
//        .content { background-color: white; padding: 40px 30px; border-radius: 0 0 10px 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
//        .congratulations { background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); color: white; padding: 25px; text-align: center; border-radius: 8px; margin-bottom: 30px; }
//        .congratulations h2 { margin: 0; font-size: 24px; }
//        .section { margin: 30px 0; padding: 20px; background-color: #f9f9f9; border-left: 4px solid #f7623b; border-radius: 5px; }
//        .section-title { color: #f7623b; font-size: 18px; font-weight: bold; margin-bottom: 15px; }
//        .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin: 15px 0; }
//        .info-item { padding: 12px; background-color: white; border-radius: 5px; border: 1px solid #e0e0e0; }
//        .info-label { font-size: 12px; color: #777; text-transform: uppercase; margin-bottom: 5px; }
//        .info-value { font-size: 16px; font-weight: bold; color: #333; }
//        .highlight-amount { font-size: 28px; color: #f7623b; font-weight: bold; text-align: center; margin: 20px 0; }
//        .button { display: inline-block; padding: 18px 50px; background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); color: white !important; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 16px; margin: 25px 0; box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3); transition: all 0.3s; }
//        .button:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4); }
//        .payment-schedule { margin: 20px 0; }
//        .schedule-item { padding: 15px; background-color: white; border-radius: 5px; margin-bottom: 10px; border: 1px solid #e0e0e0; }
//        .warning-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; margin: 20px 0; border-radius: 5px; }
//        .warning-box strong { color: #856404; }
//        .footer { text-align: center; margin-top: 40px; padding: 30px 20px; background-color: #f9f9f9; border-radius: 8px; }
//        .footer p { margin: 5px 0; color: #777; font-size: 13px; }
//        .reference { background-color: #e3f2fd; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0; }
//        .reference strong { color: #1976d2; font-size: 18px; }
//        table { width: 100%%; border-collapse: collapse; margin: 15px 0; }
//        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
//        th { background-color: #f7623b; color: white; font-weight: bold; }
//        tr:hover { background-color: #f5f5f5; }
//    </style>
//</head>
//<body>
//    <div class="container">
//        <div class="header">
//            <h1>🎉 LOAN APPROVED!</h1>
//            <p>KoolKredit - Empowering Your Dreams</p>
//        </div>
//
//        <div class="content">
//            <div class="congratulations">
//                <h2>Congratulations, %s!</h2>
//                <p style="margin: 10px 0 0 0;">Your loan application has been approved</p>
//            </div>
//
//            <p>Dear <strong>%s %s</strong>,</p>
//
//            <p>We are pleased to inform you that your loan application has been <strong>APPROVED</strong> as of <strong>%s</strong>.</p>
//
//            <div class="reference">
//                <div style="color: #666; font-size: 13px; margin-bottom: 5px;">Application Reference</div>
//                <strong>%s</strong>
//            </div>
//
//            <div class="section">
//                <div class="section-title">📊 Loan Summary</div>
//                <div class="info-grid">
//                    <div class="info-item">
//                        <div class="info-label">Store Price</div>
//                        <div class="info-value">NGN %,.2f</div>
//                    </div>
//                    <div class="info-item">
//                        <div class="info-label">Total Outright Price</div>
//                        <div class="info-value">NGN %,.2f</div>
//                    </div>
//                    <div class="info-item">
//                        <div class="info-label">Loan Amount</div>
//                        <div class="info-value">NGN %,.2f</div>
//                    </div>
//                    <div class="info-item">
//                        <div class="info-label">Total Interest</div>
//                        <div class="info-value">NGN %,.2f</div>
//                    </div>
//                    <div class="info-item">
//                        <div class="info-label">Plan</div>
//                        <div class="info-value">%s</div>
//                    </div>
//                    <div class="info-item">
//                        <div class="info-label">Duration</div>
//                        <div class="info-value">%s</div>
//                    </div>
//                </div>
//            </div>
//
//            <div class="section">
//                <div class="section-title">💰 Immediate Payment Required</div>
//                <div style="text-align: center;">
//                    <div style="color: #666; margin-bottom: 10px;">DOWN PAYMENT</div>
//                    <div class="highlight-amount">NGN %,.2f</div>
//                    <p style="color: #666; margin: 15px 0;">This includes your initial deposit and insurance fee</p>
//
//                    <a href="%s" class="button">
//                        💳 PAY NOW
//                    </a>
//                </div>
//            </div>
//
//            <div class="section">
//                <div class="section-title">📅 Payment Schedule</div>
//                %s
//            </div>
//
//            <div class="warning-box">
//                <strong>⚠️ Important: Default Payment Policy</strong>
//                <p style="margin: 10px 0 0 0;">
//                    A default fee of <strong>2%% (%,.2f NGN)</strong> will be charged on any missed payment.
//                    Please ensure timely payments to avoid additional charges.
//                </p>
//            </div>
//
//            <div class="section">
//                <div class="section-title">📋 Loan Agreement Terms</div>
//                <table>
//                    <tr>
//                        <th>Term</th>
//                        <th>Details</th>
//                    </tr>
//                    <tr>
//                        <td>Loan Type</td>
//                        <td>%s Payment Plan</td>
//                    </tr>
//                    <tr>
//                        <td>Interest Rate</td>
//                        <td>4%% per month</td>
//                    </tr>
//                    <tr>
//                        <td>Management Fee</td>
//                        <td>10%% of store price</td>
//                    </tr>
//                    <tr>
//                        <td>Insurance Fee</td>
//                        <td>1.36%% of outright price</td>
//                    </tr>
//                    <tr>
//                        <td>Default Fee</td>
//                        <td>2%% per missed payment</td>
//                    </tr>
//                    <tr>
//                        <td>Total Amount Payable</td>
//                        <td><strong>NGN %,.2f</strong></td>
//                    </tr>
//                </table>
//            </div>
//
//            <div class="section">
//                <div class="section-title">👤 Guarantor Information</div>
//                <p>Your guarantor has been verified and notified:</p>
//                <div class="info-item" style="margin-top: 15px;">
//                    <div class="info-label">Guarantor Contact</div>
//                    <div class="info-value">%s</div>
//                </div>
//            </div>
//
//            <div style="background-color: #e8f5e9; padding: 20px; border-radius: 8px; margin: 30px 0;">
//                <h3 style="margin-top: 0; color: #2e7d32;">✅ Next Steps</h3>
//                <ol style="margin: 10px 0; padding-left: 20px;">
//                    <li>Click the "PAY NOW" button above to make your down payment</li>
//                    <li>Save your application reference number for future reference</li>
//                    <li>Set up reminders for your payment schedule</li>
//                    <li>Contact us if you have any questions</li>
//                </ol>
//            </div>
//
//            <div class="footer">
//                <p><strong>KoolKredit</strong></p>
//                <p>📧 Email: support@koolkredit.com | 📱 Phone: +234-XXX-XXXX</p>
//                <p style="margin-top: 15px; font-size: 11px; color: #999;">
//                    This is an automated message. Please do not reply to this email.<br>
//                    For inquiries, please contact our support team.
//                </p>
//            </div>
//        </div>
//    </div>
//</body>
//</html>
//                """,
//                guarantor.getCustomerFirstName(),
//                guarantor.getCustomerFirstName(),
//                guarantor.getCustomerLastName(),
//                currentDate,
//                applicationRef,
//                calc.getStorePrice(),
//                calc.getOutrightPrice(),
//                calc.getLoanAmount(),
//                calc.getTotalInterest(),
//                calc.getPlan(),
//                duration,
//                //guarantor.getCustomerInstallmentDuration(),
//                calc.getUpfront(),
//                paymentLink,
//                paymentSchedule,
//                defaulterFee,
//                calc.getPlan(),
//                calc.getUnlockPrice(),
//                guarantor.getGuarantorEmail()
//        );
//    }
//
//    private String buildGuarantorOfferLetterEmail(Guarantor guarantor, LoanCalculation calc,
//                                                  String applicationRef) {
//        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
//
//        // ✅ FIX: Create consistent duration string from calculation
//        String duration = "Omolope".equals(calc.getPlan())
//            ? calc.getDays() + " days"
//            : calc.getMonths() + " months";
//
//        return String.format("""
//<!DOCTYPE html>
//<html>
//<head>
//    <style>
//        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
//        .container { max-width: 700px; margin: 0 auto; padding: 20px; background-color: #f5f5f5; }
//        .header { background: linear-gradient(135deg, #2196F3 0%%, #1976D2 100%%); color: white; padding: 40px 20px; text-align: center; border-radius: 10px 10px 0 0; }
//        .header h1 { margin: 0; font-size: 28px; }
//        .content { background-color: white; padding: 40px 30px; border-radius: 0 0 10px 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
//        .section { margin: 30px 0; padding: 20px; background-color: #f9f9f9; border-left: 4px solid #2196F3; border-radius: 5px; }
//        .section-title { color: #2196F3; font-size: 18px; font-weight: bold; margin-bottom: 15px; }
//        .info-item { padding: 12px; background-color: white; border-radius: 5px; margin-bottom: 10px; border: 1px solid #e0e0e0; }
//        .info-label { font-size: 12px; color: #777; text-transform: uppercase; margin-bottom: 5px; }
//        .info-value { font-size: 16px; font-weight: bold; color: #333; }
//        .reference { background-color: #e3f2fd; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0; }
//        .footer { text-align: center; margin-top: 40px; padding: 30px 20px; background-color: #f9f9f9; border-radius: 8px; }
//        table { width: 100%%; border-collapse: collapse; margin: 15px 0; }
//        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
//        th { background-color: #2196F3; color: white; }
//    </style>
//</head>
//<body>
//    <div class="container">
//        <div class="header">
//            <h1>📋 Loan Agreement Notice</h1>
//            <p>You're Listed as a Guarantor</p>
//        </div>
//
//        <div class="content">
//            <p>Dear Guarantor,</p>
//
//            <p>This is to inform you that the loan application for <strong>%s %s</strong> has been approved on <strong>%s</strong>.</p>
//
//            <div class="reference">
//                <div style="color: #666; font-size: 13px; margin-bottom: 5px;">Application Reference</div>
//                <strong>%s</strong>
//            </div>
//
//            <div class="section">
//                <div class="section-title">👤 Customer Information</div>
//                <div class="info-item">
//                    <div class="info-label">Customer Name</div>
//                    <div class="info-value">%s %s</div>
//                </div>
//                <div class="info-item">
//                    <div class="info-label">Email</div>
//                    <div class="info-value">%s</div>
//                </div>
//            </div>
//
//            <div class="section">
//                <div class="section-title">💰 Loan Details</div>
//                <table>
//                    <tr>
//                        <td><strong>Loan Amount</strong></td>
//                        <td><strong>NGN %,.2f</strong></td>
//                    </tr>
//                    <tr>
//                        <td>Plan</td>
//                        <td>%s</td>
//                    </tr>
//                    <tr>
//                        <td>Duration</td>
//                        <td>%s</td>
//                    </tr>
//                    <tr>
//                        <td>Total Payable</td>
//                        <td>NGN %,.2f</td>
//                    </tr>
//                </table>
//            </div>
//
//            <div class="section">
//                <div class="section-title">⚠️ Your Responsibilities as Guarantor</div>
//                <ul style="margin: 10px 0; padding-left: 20px; line-height: 2;">
//                    <li>You have confirmed your agreement to guarantee this loan</li>
//                    <li>You may be contacted in case of payment defaults</li>
//                    <li>You may be liable for outstanding payments if the customer defaults</li>
//                    <li>Please maintain open communication with the customer</li>
//                </ul>
//            </div>
//
//            <div style="background-color: #fff3cd; padding: 20px; border-left: 4px solid #ffc107; border-radius: 5px; margin: 20px 0;">
//                <strong>📞 Keep in Touch</strong>
//                <p style="margin: 10px 0 0 0;">
//                    We recommend staying in contact with the customer to ensure smooth repayment.
//                    You will be notified of any issues that may arise during the loan period.
//                </p>
//            </div>
//
//            <div class="footer">
//                <p><strong>KoolKredit</strong></p>
//                <p>📧 Email: support@koolkredit.com | 📱 Phone: +234-XXX-XXXX</p>
//                <p style="margin-top: 15px; font-size: 11px; color: #999;">
//                    This is an automated notice. For questions, please contact our support team.
//                </p>
//            </div>
//        </div>
//    </div>
//</body>
//</html>
//                """,
//                guarantor.getCustomerFirstName(),
//                guarantor.getCustomerLastName(),
//                currentDate,
//                applicationRef,
//                guarantor.getCustomerFirstName(),
//                guarantor.getCustomerLastName(),
//                guarantor.getCustomerEmail(),
//                calc.getLoanAmount(),
//                calc.getPlan(),
//                duration,
//                //guarantor.getCustomerInstallmentDuration(),
//                calc.getUnlockPrice()
//        );
//    }
//
//    private String generatePaymentSchedule(LoanCalculation calc) {
//        StringBuilder schedule = new StringBuilder();
//
//        if ("Omolope".equals(calc.getPlan())) {
//            // Daily payment schedule
//            schedule.append(String.format("""
//                <div class="schedule-item">
//                    <strong>Daily Payment:</strong> NGN %,.2f
//                </div>
//                <div class="schedule-item">
//                    <strong>Total Days:</strong> %d days
//                </div>
//                <div class="schedule-item">
//                    <strong>Total Installment:</strong> NGN %,.2f
//                </div>
//                """,
//                calc.getDailyPayment(),
//                calc.getDays(),
//                calc.getInstalmentTotal()
//            ));
//        } else {
//            // Monthly payment schedule
//            schedule.append(String.format("""
//                <div class="schedule-item">
//                    <strong>Monthly Payment:</strong> NGN %,.2f
//                </div>
//                <div class="schedule-item">
//                    <strong>Number of Months:</strong> %d months
//                </div>
//                <div class="schedule-item">
//                    <strong>Total Installment:</strong> NGN %,.2f
//                </div>
//                """,
//                calc.getMonthlyPayment(),
//                calc.getMonths(),
//                calc.getInstalmentTotal()
//            ));
//        }
//
//        return schedule.toString();
//    }
//
//    private BigDecimal calculateDefaulterFee(LoanCalculation calc) {
//        BigDecimal paymentAmount;
//        if ("Omolope".equals(calc.getPlan())) {
//            paymentAmount = calc.getDailyPayment();
//        } else {
//            paymentAmount = calc.getMonthlyPayment();
//        }
//        return paymentAmount.multiply(DEFAULT_FEE_RATE).setScale(2, java.math.RoundingMode.HALF_UP);
//    }
//
//    private String generateApplicationReference(String customerBvn) {
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        String bvnPart = customerBvn.substring(customerBvn.length() - 4);
//        return "KK-" + bvnPart + "-" + timestamp.substring(timestamp.length() - 6);
//    }
//
//    private String generatePaymentLink(String reference, BigDecimal amount) {
//        return String.format("%s/pay?ref=%s&amount=%s", paymentUrl, reference, amount.toString());
//    }
//
////    private BigDecimal parseStorePrice(String planInfo) {
////        log.info("Attempting to parse store price from: '{}'", planInfo);
////
////        try {
////            // Try to extract price from various formats
////            // Format examples: "Easy 35 - NGN 50000", "Easy 35 - 50000", "Easy 35"
////
////            if (planInfo == null || planInfo.isBlank()) {
////                log.warn("Plan info is null or blank, using default price");
////                return new BigDecimal("50000");
////            }
////
////            if (planInfo.contains("-")) {
////                String[] parts = planInfo.split("-");
////                if (parts.length > 1) {
////                    String priceStr = parts[1].trim()
////                        .replace("NGN", "")
////                        .replace("₦", "")
////                        .replace(",", "")
////                        .replace(" ", "")
////                        .trim();
////
////                    if (!priceStr.isEmpty()) {
////                        BigDecimal price = new BigDecimal(priceStr);
////                        log.info("Successfully parsed price: {} from '{}'", price, planInfo);
////                        return price;
////                    }
////                }
////            }
////
////            // Try to find any number in the string
////            String numericOnly = planInfo.replaceAll("[^0-9]", "");
////            if (numericOnly.length() >= 4) { // Assume prices are at least 4 digits
////                BigDecimal price = new BigDecimal(numericOnly);
////                log.info("Extracted numeric price: {} from '{}'", price, planInfo);
////                return price;
////            }
////
////        } catch (Exception e) {
////            log.error("Error parsing store price from '{}': {}", planInfo, e.getMessage());
////        }
////
////        log.warn("Could not parse price from '{}', using default: 50000", planInfo);
////        return new BigDecimal("50000");
////    }
//
//    private Integer parseDuration(String duration) {
//        try {
//            return Integer.parseInt(duration.replaceAll("[^0-9]", ""));
//        } catch (Exception e) {
//            log.error("Error parsing duration: {}", duration, e);
//            return 6; // Default 6 months
//        }
//    }
//
//    private String extractPlanName(String planInfo) {
//        if (planInfo.contains("Easy 35")) return "Easy 35";
//        if (planInfo.contains("Easy 20")) return "Easy 20";
//        if (planInfo.contains("Omolope")) return "Omolope";
//        if (planInfo.contains("OutrightFlex")) return "OutrightFlex";
//        return "Easy 35"; // Default
//    }
//
//
//
//
//
//
//    private String getCustomerPhoneNumber(Guarantor guarantor) {
//        // Get customer phone from AgentFollowUp relationship
//        try {
//            String phoneNumber = guarantor.getMobileNumber();
//            if (phoneNumber != null && !phoneNumber.isBlank()) {
//                log.info("Customer phone number retrieved from AgentFollowUp: {}", phoneNumber);
//                return phoneNumber;
//            } else {
//                log.warn("Customer phone number not available in AgentFollowUp for guarantor ID: {}", guarantor.getId());
//                return null;
//            }
//        } catch (Exception e) {
//            log.warn("Could not retrieve customer phone number: {}", e.getMessage());
//            return null;
//        }
//    }
//
//
//// Add this helper method to OfferLetterService
//    private String resolveInstallmentDuration(Guarantor guarantor) {
//        // 1. Use value stored directly on guarantor
//        if (guarantor.getCustomerInstallmentDuration() != null &&
//            !guarantor.getCustomerInstallmentDuration().isBlank()) {
//            return guarantor.getCustomerInstallmentDuration();
//        }
//
//        // 2. Fallback to AgentFollowUp.installmentOption
//        try {
//            AgentFollowUp followUp = guarantor.getAgentFollowUp();
//            if (followUp != null &&
//                followUp.getInstallmentOption() != null &&
//                !followUp.getInstallmentOption().isBlank()) {
//                return followUp.getInstallmentOption();
//            }
//        } catch (Exception e) {
//            log.warn("Could not fetch AgentFollowUp installment option", e);
//        }
//
//        // 3. Final fallback
//        return "6"; // Default 6 months
//    }
//
//
//
////    private String getCustomerPhoneNumber(Guarantor guarantor) {
////        // Get customer phone from AgentFollowUp relationship
////        try {
////            String phoneNumber = guarantor.getMobileNumber();
////            if (phoneNumber != null && !phoneNumber.isBlank()) {
////                log.info("Customer phone number retrieved from AgentFollowUp: {}", phoneNumber);
////                return phoneNumber;
////            } else {
////                log.warn("Customer phone number not available in AgentFollowUp for guarantor ID: {}", guarantor.getId());
////                return null;
////            }
////        } catch (Exception e) {
////            log.warn("Could not retrieve customer phone number: {}", e.getMessage());
////            return null;
////        }
////    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//




package com.koolboks.creditProject.service;

import com.koolboks.creditProject.entity.AgentFollowUp;
import com.koolboks.creditProject.entity.Guarantor;
import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
import com.koolboks.creditProject.repository.GuarantorRepository;
import com.koolboks.creditProject.service.debitMandate.DebitMandateService;
import com.koolboks.creditProject.service.loan_calculator.LoanCalculatorService;
import com.koolboks.creditProject.service.loan_calculator.LoanCalculatorService.LoanCalculation;
import com.koolboks.creditProject.service.loan_repayment_service.LoanRepaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class OfferLetterService {

    private static final Logger log = LoggerFactory.getLogger(OfferLetterService.class);
    private static final BigDecimal DEFAULT_FEE_RATE = new BigDecimal("0.02"); // 2% defaulter fee

    private final GuarantorRepository guarantorRepository;
    private final LoanCalculatorService loanCalculatorService;
    private final JavaMailSender mailSender;
    private final SmsService smsService;
    private final LoanRepaymentService loanRepaymentService;
    private final DebitMandateService debitMandateService;

    @Value("${notification.email.from:foltim256@gmail.com}")
    private String fromEmail;

    @Value("${payment.url:https://koolkredit-bnpl-frontend.vercel.app/pay}")
    private String paymentUrl;

    public OfferLetterService(GuarantorRepository guarantorRepository,
                              LoanCalculatorService loanCalculatorService,
                              JavaMailSender mailSender,
                              SmsService smsService,
                              LoanRepaymentService loanRepaymentService,
                              DebitMandateService debitMandateService) {
        this.guarantorRepository = guarantorRepository;
        this.loanCalculatorService = loanCalculatorService;
        this.mailSender = mailSender;
        this.smsService = smsService;
        this.loanRepaymentService = loanRepaymentService;
        this.debitMandateService = debitMandateService;
    }

    /**
     * Generate and send offer letter after guarantor verification
     */
    public Map<String, Object> generateAndSendOfferLetter(Long guarantorId) {
        Map<String, Object> response = new HashMap<>();

        try {
            log.info("=== STARTING OFFER LETTER GENERATION FOR GUARANTOR ID: {} ===", guarantorId);

            // Get guarantor record
            Guarantor guarantor = guarantorRepository.findById(guarantorId)
                    .orElseThrow(() -> new RuntimeException("Guarantor not found with ID: " + guarantorId));

            log.info("Found guarantor for customer: {} {}",
                guarantor.getCustomerFirstName(), guarantor.getCustomerLastName());

            // Verify guarantor is OTP verified
            if (!guarantor.getOtpVerified()) {
                log.warn("Guarantor ID {} is not yet OTP verified", guarantorId);
                response.put("success", false);
                response.put("message", "Guarantor not yet verified");
                return response;
            }

            // Verify customer email exists
            if (guarantor.getCustomerEmail() == null || guarantor.getCustomerEmail().isBlank()) {
                throw new RuntimeException(
                    "Customer email is missing for guarantor ID: " + guarantor.getId()
                );
            }

            log.info("Guarantor verified. OTP verified at: {}", guarantor.getOtpVerifiedAt());

            // ✅ GET ACTUAL VALUES FROM GUARANTOR ENTITY
            BigDecimal storePrice = guarantor.getStorePrice();
            String plan = guarantor.getCustomerPlan();
            String installmentDuration = resolveInstallmentDuration(guarantor);
            Integer duration = parseDuration(installmentDuration);

            // ✅ VALIDATE STORE PRICE
            if (storePrice == null || storePrice.compareTo(BigDecimal.ZERO) <= 0) {
                log.error("❌ Invalid store price for guarantor ID {}: {}", guarantorId, storePrice);
                throw new RuntimeException("Store price is missing or invalid. Please check the guarantor data.");
            }

            log.info("✅ Using values from database:");
            log.info("   Store Price: {}", storePrice);
            log.info("   Plan: {}", plan);
            log.info("   Duration: {}", duration);

            // Calculate loan details
            log.info("Calculating loan details using LoanCalculatorService...");
            LoanCalculation calculation = loanCalculatorService.calculate(storePrice, plan, duration);

            log.info("✅ Calculation complete:");
            log.info("   Down Payment: {}", calculation.getUpfront());
            log.info("   Loan Amount: {}", calculation.getLoanAmount());
            log.info("   Total Payable: {}", calculation.getUnlockPrice());
            log.info("   Total Installment: {}", calculation.getInstalmentTotal());

            // Calculate defaulter fee
            BigDecimal defaulterFee = calculateDefaulterFee(calculation);
            log.info("Defaulter fee calculated: {}", defaulterFee);

            // Generate unique application reference
            String applicationRef = generateApplicationReference(guarantor.getCustomerBvn());
            log.info("Generated application reference: {}", applicationRef);

            // Generate payment link
            String paymentLink = generatePaymentLink(applicationRef, calculation.getUpfront());
            log.info("Generated payment link: {}", paymentLink);

            // Send email to customer
            log.info("Sending offer letter email to customer: {}", guarantor.getCustomerEmail());
            sendCustomerOfferLetter(guarantor, calculation, defaulterFee, paymentLink, applicationRef);

            // Send email to guarantor
            log.info("Sending offer letter email to guarantor: {}", guarantor.getGuarantorEmail());
            sendGuarantorOfferLetter(guarantor, calculation, applicationRef);

            // Send SMS to customer
            log.info("Sending SMS to customer...");
            sendCustomerSms(guarantor, calculation, paymentLink);

            // Send SMS to guarantor
            log.info("Sending SMS to guarantor: {}", guarantor.getGuarantorPhoneNumber());
            sendGuarantorSms(guarantor, calculation);

            // Update guarantor record
            guarantor.setOfferLetterSent(true);
            guarantor.setOfferLetterSentAt(LocalDateTime.now());
            guarantor.setApplicationReference(applicationRef);
            guarantorRepository.save(guarantor);

            // ✅ CREATE LOAN REPAYMENT RECORD
            try {
                log.info("Creating loan repayment record...");
                LoanRepayment loanRepayment = loanRepaymentService.createLoanRepayment(
                    guarantor,
                    calculation,
                    applicationRef
                );
                log.info("✅ Loan repayment record created with ID: {}", loanRepayment.getId());
            } catch (Exception e) {
                log.error("❌ Failed to create loan repayment record (continuing anyway): {}", e.getMessage());
                // Don't throw - we still want to send emails even if repayment record fails
            }

            // ✅ UPDATE DEBIT MANDATE WITH TOTAL INSTALLMENT AMOUNT
            try {
                log.info("Updating DebitMandate with total installment amount...");

                // Convert BigDecimal to int (installment total)
                int instalmentAmount = calculation.getInstalmentTotal().intValue();

                DebitMandate updatedMandate = debitMandateService.updateAmount(
                    guarantor.getCustomerBvn(),
                    instalmentAmount
                );

                log.info("✅ DebitMandate updated successfully. Amount set to: {}", instalmentAmount);
                response.put("debitMandateId", updatedMandate.getId());
            } catch (Exception e) {
                log.error("❌ Failed to update DebitMandate amount (continuing anyway): {}", e.getMessage());
                // Don't throw - we still want to complete the offer letter process
            }

            log.info("Guarantor record updated with offer letter details");
            log.info("=== OFFER LETTER PROCESS COMPLETED SUCCESSFULLY FOR GUARANTOR ID: {} ===", guarantorId);

            response.put("success", true);
            response.put("message", "Offer letter and payment details sent successfully");
            response.put("applicationReference", applicationRef);
            response.put("downPayment", calculation.getUpfront());
            response.put("paymentLink", paymentLink);

            return response;

        } catch (Exception e) {
            log.error("=== ERROR GENERATING OFFER LETTER FOR GUARANTOR ID: {} ===", guarantorId, e);
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            response.put("errorDetails", e.toString());
            return response;
        }
    }

    private void sendCustomerOfferLetter(Guarantor guarantor, LoanCalculation calc,
                                         BigDecimal defaulterFee, String paymentLink,
                                         String applicationRef) {
        try {
            log.info("Creating customer offer letter email for: {}", guarantor.getCustomerEmail());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(guarantor.getCustomerEmail());
            helper.setSubject("🎉 Congratulations! Your Loan Application is Approved");

            String htmlContent = buildCustomerOfferLetterEmail(guarantor, calc, defaulterFee,
                                                               paymentLink, applicationRef);
            helper.setText(htmlContent, true);

            log.info("Sending customer offer letter email...");
            mailSender.send(mimeMessage);
            log.info("✅ Customer offer letter email sent successfully to: {}", guarantor.getCustomerEmail());

        } catch (Exception e) {
            log.error("❌ FAILED to send customer offer letter email to: {}", guarantor.getCustomerEmail(), e);
            throw new RuntimeException("Failed to send customer email: " + e.getMessage(), e);
        }
    }

    private void sendGuarantorOfferLetter(Guarantor guarantor, LoanCalculation calc,
                                          String applicationRef) {
        try {
            log.info("Creating guarantor offer letter email for: {}", guarantor.getGuarantorEmail());

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(guarantor.getGuarantorEmail());
            helper.setSubject("📋 Loan Agreement - You're Listed as Guarantor");

            String htmlContent = buildGuarantorOfferLetterEmail(guarantor, calc, applicationRef);
            helper.setText(htmlContent, true);

            log.info("Sending guarantor offer letter email...");
            mailSender.send(mimeMessage);
            log.info("✅ Guarantor offer letter email sent successfully to: {}", guarantor.getGuarantorEmail());

        } catch (Exception e) {
            log.error("❌ FAILED to send guarantor offer letter email to: {}", guarantor.getGuarantorEmail(), e);
            throw new RuntimeException("Failed to send guarantor email: " + e.getMessage(), e);
        }
    }

    private void sendCustomerSms(Guarantor guarantor, LoanCalculation calc, String paymentLink) {
        try {
            String customerPhone = getCustomerPhoneNumber(guarantor);

            if (customerPhone == null || customerPhone.isBlank()) {
                log.warn("⚠️ Customer phone number not available. Skipping customer SMS.");
                return;
            }

            log.info("Preparing customer SMS for: {}", customerPhone);

            String message = String.format(
                "Congratulations %s! Your loan is APPROVED. Down Payment: NGN %,.2f. " +
                "Pay now: %s. Ref: %s",
                guarantor.getCustomerFirstName(),
                calc.getUpfront(),
                paymentLink,
                guarantor.getApplicationReference()
            );

            log.info("Sending customer SMS...");
            boolean sent = smsService.sendSms(customerPhone, message);

            if (sent) {
                log.info("✅ Customer SMS sent successfully to: {}", customerPhone);
            } else {
                log.warn("⚠️ Customer SMS sending failed (no exception thrown)");
            }

        } catch (Exception e) {
            log.error("❌ Error sending customer SMS", e);
            // Don't throw exception - SMS failure shouldn't stop the whole process
        }
    }

    private void sendGuarantorSms(Guarantor guarantor, LoanCalculation calc) {
        try {
            log.info("Preparing guarantor SMS for: {}", guarantor.getGuarantorPhoneNumber());

            String message = String.format(
                "Hello, you're confirmed as guarantor for %s %s. Loan Amount: NGN %,.2f. " +
                "Duration: %s. Thank you for your support.",
                guarantor.getCustomerFirstName(),
                guarantor.getCustomerLastName(),
                calc.getLoanAmount(),
                guarantor.getCustomerInstallmentDuration()
            );

            log.info("Sending guarantor SMS...");
            boolean sent = smsService.sendSms(guarantor.getGuarantorPhoneNumber(), message);

            if (sent) {
                log.info("✅ Guarantor SMS sent successfully to: {}", guarantor.getGuarantorPhoneNumber());
            } else {
                log.warn("⚠️ Guarantor SMS sending failed (no exception thrown)");
            }

        } catch (Exception e) {
            log.error("❌ Error sending guarantor SMS to: {}", guarantor.getGuarantorPhoneNumber(), e);
            // Don't throw exception - SMS failure shouldn't stop the whole process
        }
    }

    private String buildCustomerOfferLetterEmail(Guarantor guarantor, LoanCalculation calc,
                                                 BigDecimal defaulterFee, String paymentLink,
                                                 String applicationRef) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        String paymentSchedule = generatePaymentSchedule(calc);

        // ✅ FIX: Create consistent duration string from calculation
        String duration = "Omolope".equals(calc.getPlan())
            ? calc.getDays() + " days"
            : calc.getMonths() + " months";

        return String.format("""
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
        .container { max-width: 700px; margin: 0 auto; padding: 20px; background-color: #f5f5f5; }
        .header { background: linear-gradient(135deg, #f7623b 0%%, #d5541f 100%%); color: white; padding: 40px 20px; text-align: center; border-radius: 10px 10px 0 0; }
        .header h1 { margin: 0; font-size: 28px; }
        .header p { margin: 10px 0 0 0; font-size: 14px; opacity: 0.9; }
        .content { background-color: white; padding: 40px 30px; border-radius: 0 0 10px 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .congratulations { background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); color: white; padding: 25px; text-align: center; border-radius: 8px; margin-bottom: 30px; }
        .congratulations h2 { margin: 0; font-size: 24px; }
        .section { margin: 30px 0; padding: 20px; background-color: #f9f9f9; border-left: 4px solid #f7623b; border-radius: 5px; }
        .section-title { color: #f7623b; font-size: 18px; font-weight: bold; margin-bottom: 15px; }
        .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 15px; margin: 15px 0; }
        .info-item { padding: 12px; background-color: white; border-radius: 5px; border: 1px solid #e0e0e0; }
        .info-label { font-size: 12px; color: #777; text-transform: uppercase; margin-bottom: 5px; }
        .info-value { font-size: 16px; font-weight: bold; color: #333; }
        .highlight-amount { font-size: 28px; color: #f7623b; font-weight: bold; text-align: center; margin: 20px 0; }
        .button { display: inline-block; padding: 18px 50px; background: linear-gradient(135deg, #4CAF50 0%%, #45a049 100%%); color: white !important; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 16px; margin: 25px 0; box-shadow: 0 4px 15px rgba(76, 175, 80, 0.3); transition: all 0.3s; }
        .button:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4); }
        .payment-schedule { margin: 20px 0; }
        .schedule-item { padding: 15px; background-color: white; border-radius: 5px; margin-bottom: 10px; border: 1px solid #e0e0e0; }
        .warning-box { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 20px; margin: 20px 0; border-radius: 5px; }
        .warning-box strong { color: #856404; }
        .footer { text-align: center; margin-top: 40px; padding: 30px 20px; background-color: #f9f9f9; border-radius: 8px; }
        .footer p { margin: 5px 0; color: #777; font-size: 13px; }
        .reference { background-color: #e3f2fd; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0; }
        .reference strong { color: #1976d2; font-size: 18px; }
        table { width: 100%%; border-collapse: collapse; margin: 15px 0; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #f7623b; color: white; font-weight: bold; }
        tr:hover { background-color: #f5f5f5; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🎉 LOAN APPROVED!</h1>
            <p>KoolKredit - Empowering Your Dreams</p>
        </div>
        
        <div class="content">
            <div class="congratulations">
                <h2>Congratulations, %s!</h2>
                <p style="margin: 10px 0 0 0;">Your loan application has been approved</p>
            </div>

            <p>Dear <strong>%s %s</strong>,</p>
            
            <p>We are pleased to inform you that your loan application has been <strong>APPROVED</strong> as of <strong>%s</strong>.</p>

            <div class="reference">
                <div style="color: #666; font-size: 13px; margin-bottom: 5px;">Application Reference</div>
                <strong>%s</strong>
            </div>

            <div class="section">
                <div class="section-title">📊 Loan Summary</div>
                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-label">Store Price</div>
                        <div class="info-value">NGN %,.2f</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Total Outright Price</div>
                        <div class="info-value">NGN %,.2f</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Loan Amount</div>
                        <div class="info-value">NGN %,.2f</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Total Interest</div>
                        <div class="info-value">NGN %,.2f</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Plan</div>
                        <div class="info-value">%s</div>
                    </div>
                    <div class="info-item">
                        <div class="info-label">Duration</div>
                        <div class="info-value">%s</div>
                    </div>
                </div>
            </div>

            <div class="section">
                <div class="section-title">💰 Immediate Payment Required</div>
                <div style="text-align: center;">
                    <div style="color: #666; margin-bottom: 10px;">DOWN PAYMENT</div>
                    <div class="highlight-amount">NGN %,.2f</div>
                    <p style="color: #666; margin: 15px 0;">This includes your initial deposit and insurance fee</p>
                    
                    <a href="%s" class="button">
                        💳 PAY NOW
                    </a>
                </div>
            </div>

            <div class="section">
                <div class="section-title">📅 Payment Schedule</div>
                %s
            </div>

            <div class="warning-box">
                <strong>⚠️ Important: Default Payment Policy</strong>
                <p style="margin: 10px 0 0 0;">
                    A default fee of <strong>2%% (%,.2f NGN)</strong> will be charged on any missed payment. 
                    Please ensure timely payments to avoid additional charges.
                </p>
            </div>

            <div class="section">
                <div class="section-title">📋 Loan Agreement Terms</div>
                <table>
                    <tr>
                        <th>Term</th>
                        <th>Details</th>
                    </tr>
                    <tr>
                        <td>Loan Type</td>
                        <td>%s Payment Plan</td>
                    </tr>
                    <tr>
                        <td>Interest Rate</td>
                        <td>4%% per month</td>
                    </tr>
                    <tr>
                        <td>Management Fee</td>
                        <td>10%% of store price</td>
                    </tr>
                    <tr>
                        <td>Insurance Fee</td>
                        <td>1.36%% of outright price</td>
                    </tr>
                    <tr>
                        <td>Default Fee</td>
                        <td>2.5%% per missed payment</td>
                    </tr>
                    <tr>
                        <td>Total Amount Payable</td>
                        <td><strong>NGN %,.2f</strong></td>
                    </tr>
                </table>
            </div>

            <div class="section">
                <div class="section-title">👤 Guarantor Information</div>
                <p>Your guarantor has been verified and notified:</p>
                <div class="info-item" style="margin-top: 15px;">
                    <div class="info-label">Guarantor Contact</div>
                    <div class="info-value">%s</div>
                </div>
            </div>

            <div style="background-color: #e8f5e9; padding: 20px; border-radius: 8px; margin: 30px 0;">
                <h3 style="margin-top: 0; color: #2e7d32;">✅ Next Steps</h3>
                <ol style="margin: 10px 0; padding-left: 20px;">
                    <li>Click the "PAY NOW" button above to make your down payment</li>
                    <li>Save your application reference number for future reference</li>
                    <li>Set up reminders for your payment schedule</li>
                    <li>Contact us if you have any questions</li>
                </ol>
            </div>

            <div class="footer">
                <p><strong>KoolKredit</strong></p>
                <p>📧 Email: support@koolkredit.com | 📱 Phone: +234-XXX-XXXX</p>
                <p style="margin-top: 15px; font-size: 11px; color: #999;">
                    This is an automated message. Please do not reply to this email.<br>
                    For inquiries, please contact our support team.
                </p>
            </div>
        </div>
    </div>
</body>
</html>
                """,
                guarantor.getCustomerFirstName(),
                guarantor.getCustomerFirstName(),
                guarantor.getCustomerLastName(),
                currentDate,
                applicationRef,
                calc.getStorePrice(),
                calc.getOutrightPrice(),
                calc.getLoanAmount(),
                calc.getTotalInterest(),
                calc.getPlan(),
                duration,
                calc.getUpfront(),
                paymentLink,
                paymentSchedule,
                defaulterFee,
                calc.getPlan(),
                calc.getUnlockPrice(),
                guarantor.getGuarantorEmail()
        );
    }

    private String buildGuarantorOfferLetterEmail(Guarantor guarantor, LoanCalculation calc,
                                                  String applicationRef) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));

        // ✅ FIX: Create consistent duration string from calculation
        String duration = "Omolope".equals(calc.getPlan())
            ? calc.getDays() + " days"
            : calc.getMonths() + " months";

        return String.format("""
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: 'Segoe UI', Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
        .container { max-width: 700px; margin: 0 auto; padding: 20px; background-color: #f5f5f5; }
        .header { background: linear-gradient(135deg, #2196F3 0%%, #1976D2 100%%); color: white; padding: 40px 20px; text-align: center; border-radius: 10px 10px 0 0; }
        .header h1 { margin: 0; font-size: 28px; }
        .content { background-color: white; padding: 40px 30px; border-radius: 0 0 10px 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .section { margin: 30px 0; padding: 20px; background-color: #f9f9f9; border-left: 4px solid #2196F3; border-radius: 5px; }
        .section-title { color: #2196F3; font-size: 18px; font-weight: bold; margin-bottom: 15px; }
        .info-item { padding: 12px; background-color: white; border-radius: 5px; margin-bottom: 10px; border: 1px solid #e0e0e0; }
        .info-label { font-size: 12px; color: #777; text-transform: uppercase; margin-bottom: 5px; }
        .info-value { font-size: 16px; font-weight: bold; color: #333; }
        .reference { background-color: #e3f2fd; padding: 15px; border-radius: 5px; text-align: center; margin: 20px 0; }
        .footer { text-align: center; margin-top: 40px; padding: 30px 20px; background-color: #f9f9f9; border-radius: 8px; }
        table { width: 100%%; border-collapse: collapse; margin: 15px 0; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #2196F3; color: white; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📋 Loan Agreement Notice</h1>
            <p>You're Listed as a Guarantor</p>
        </div>
        
        <div class="content">
            <p>Dear Guarantor,</p>
            
            <p>This is to inform you that the loan application for <strong>%s %s</strong> has been approved on <strong>%s</strong>.</p>

            <div class="reference">
                <div style="color: #666; font-size: 13px; margin-bottom: 5px;">Application Reference</div>
                <strong>%s</strong>
            </div>

            <div class="section">
                <div class="section-title">👤 Customer Information</div>
                <div class="info-item">
                    <div class="info-label">Customer Name</div>
                    <div class="info-value">%s %s</div>
                </div>
                <div class="info-item">
                    <div class="info-label">Email</div>
                    <div class="info-value">%s</div>
                </div>
            </div>

            <div class="section">
                <div class="section-title">💰 Loan Details</div>
                <table>
                    <tr>
                        <td><strong>Loan Amount</strong></td>
                        <td><strong>NGN %,.2f</strong></td>
                    </tr>
                    <tr>
                        <td>Plan</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td>Duration</td>
                        <td>%s</td>
                    </tr>
                    <tr>
                        <td>Total Payable</td>
                        <td>NGN %,.2f</td>
                    </tr>
                </table>
            </div>

            <div class="section">
                <div class="section-title">⚠️ Your Responsibilities as Guarantor</div>
                <ul style="margin: 10px 0; padding-left: 20px; line-height: 2;">
                    <li>You have confirmed your agreement to guarantee this loan</li>
                    <li>You may be contacted in case of payment defaults</li>
                    <li>You may be liable for outstanding payments if the customer defaults</li>
                    <li>Please maintain open communication with the customer</li>
                </ul>
            </div>

            <div style="background-color: #fff3cd; padding: 20px; border-left: 4px solid #ffc107; border-radius: 5px; margin: 20px 0;">
                <strong>📞 Keep in Touch</strong>
                <p style="margin: 10px 0 0 0;">
                    We recommend staying in contact with the customer to ensure smooth repayment. 
                    You will be notified of any issues that may arise during the loan period.
                </p>
            </div>

            <div class="footer">
                <p><strong>KoolKredit</strong></p>
                <p>📧 Email: support@koolkredit.com | 📱 Phone: +234-XXX-XXXX</p>
                <p style="margin-top: 15px; font-size: 11px; color: #999;">
                    This is an automated notice. For questions, please contact our support team.
                </p>
            </div>
        </div>
    </div>
</body>
</html>
                """,
                guarantor.getCustomerFirstName(),
                guarantor.getCustomerLastName(),
                currentDate,
                applicationRef,
                guarantor.getCustomerFirstName(),
                guarantor.getCustomerLastName(),
                guarantor.getCustomerEmail(),
                calc.getLoanAmount(),
                calc.getPlan(),
                duration,
                calc.getUnlockPrice()
        );
    }

    private String generatePaymentSchedule(LoanCalculation calc) {
        StringBuilder schedule = new StringBuilder();

        if ("Omolope".equals(calc.getPlan())) {
            // Daily payment schedule
            schedule.append(String.format("""
                <div class="schedule-item">
                    <strong>Daily Payment:</strong> NGN %,.2f
                </div>
                <div class="schedule-item">
                    <strong>Total Days:</strong> %d days
                </div>
                <div class="schedule-item">
                    <strong>Total Installment:</strong> NGN %,.2f
                </div>
                """,
                calc.getDailyPayment(),
                calc.getDays(),
                calc.getInstalmentTotal()
            ));
        } else {
            // Monthly payment schedule
            schedule.append(String.format("""
                <div class="schedule-item">
                    <strong>Monthly Payment:</strong> NGN %,.2f
                </div>
                <div class="schedule-item">
                    <strong>Number of Months:</strong> %d months
                </div>
                <div class="schedule-item">
                    <strong>Total Installment:</strong> NGN %,.2f
                </div>
                """,
                calc.getMonthlyPayment(),
                calc.getMonths(),
                calc.getInstalmentTotal()
            ));
        }

        return schedule.toString();
    }

    private BigDecimal calculateDefaulterFee(LoanCalculation calc) {
        BigDecimal paymentAmount;
        if ("Omolope".equals(calc.getPlan())) {
            paymentAmount = calc.getDailyPayment();
        } else {
            paymentAmount = calc.getMonthlyPayment();
        }
        return paymentAmount.multiply(DEFAULT_FEE_RATE).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    private String generateApplicationReference(String customerBvn) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String bvnPart = customerBvn.substring(customerBvn.length() - 4);
        return "KK-" + bvnPart + "-" + timestamp.substring(timestamp.length() - 6);
    }

    private String generatePaymentLink(String reference, BigDecimal amount) {
        return String.format("%s/pay?ref=%s&amount=%s", paymentUrl, reference, amount.toString());
    }

    private Integer parseDuration(String duration) {
        try {
            return Integer.parseInt(duration.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            log.error("Error parsing duration: {}", duration, e);
            return 6; // Default 6 months
        }
    }

    private String getCustomerPhoneNumber(Guarantor guarantor) {
        // Get customer phone from AgentFollowUp relationship
        try {
            String phoneNumber = guarantor.getMobileNumber();
            if (phoneNumber != null && !phoneNumber.isBlank()) {
                log.info("Customer phone number retrieved from AgentFollowUp: {}", phoneNumber);
                return phoneNumber;
            } else {
                log.warn("Customer phone number not available in AgentFollowUp for guarantor ID: {}", guarantor.getId());
                return null;
            }
        } catch (Exception e) {
            log.warn("Could not retrieve customer phone number: {}", e.getMessage());
            return null;
        }
    }

    private String resolveInstallmentDuration(Guarantor guarantor) {
        // 1. Use value stored directly on guarantor
        if (guarantor.getCustomerInstallmentDuration() != null &&
            !guarantor.getCustomerInstallmentDuration().isBlank()) {
            return guarantor.getCustomerInstallmentDuration();
        }

        // 2. Fallback to AgentFollowUp.installmentOption
        try {
            AgentFollowUp followUp = guarantor.getAgentFollowUp();
            if (followUp != null &&
                followUp.getInstallmentOption() != null &&
                !followUp.getInstallmentOption().isBlank()) {
                return followUp.getInstallmentOption();
            }
        } catch (Exception e) {
            log.warn("Could not fetch AgentFollowUp installment option", e);
        }

        // 3. Final fallback
        return "6"; // Default 6 months
    }
}
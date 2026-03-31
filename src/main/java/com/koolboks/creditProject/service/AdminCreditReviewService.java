package com.koolboks.creditProject.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koolboks.creditProject.controller.AdminCreditReviewController.AdminReviewDecisionRequest;
import com.koolboks.creditProject.entity.AgentFollowUp;
import com.koolboks.creditProject.repository.AgentFollowUpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles admin credit review workflow:
 *   1. getReviewData()        — fetches follow-up + stored credit report for admin page
 *   2. processReviewDecision()— applies overrides, then either sends OTP (APPROVE) or
 *                               emails the customer a rejection notice (REJECT)
 */
@Service
public class AdminCreditReviewService {

    private static final Logger log = LoggerFactory.getLogger(AdminCreditReviewService.class);

    private final AgentFollowUpRepository repository;
    private final OtpService              otpService;
    private final BrevoEmailService       brevoEmailService;
    private final ObjectMapper            objectMapper = new ObjectMapper();

    @Value("${app.frontend.base-url:https://your-frontend.com}")
    private String frontendBaseUrl;

    @Value("${notification.email.to:foltim256@gmail.com}")
    private String adminEmail;

    public AdminCreditReviewService(AgentFollowUpRepository repository,
                                    OtpService otpService,
                                    BrevoEmailService brevoEmailService) {
        this.repository        = repository;
        this.otpService        = otpService;
        this.brevoEmailService = brevoEmailService;
    }

    // ── 1. Get review data for admin page ─────────────────────────────────────

    public Map<String, Object> getReviewData(String token) {
        AgentFollowUp followUp = repository.findByReviewToken(token).orElse(null);
        if (followUp == null) return null;

        Map<String, Object> result = new HashMap<>();

        // Follow-up summary (safe fields only)
        Map<String, Object> fu = new HashMap<>();
        fu.put("bvn",               followUp.getBvn());
        fu.put("firstName",         followUp.getFirstName());
        fu.put("lastName",          followUp.getLastName());
        fu.put("mobileNumber",      followUp.getMobileNumber());
        fu.put("customerEmail",     followUp.getCustomerEmail());
        fu.put("guarantorEmail",    followUp.getGuarantorEmail());
        fu.put("homeAddress",       followUp.getHomeAddress());
        fu.put("workAddress",       followUp.getWorkAddress());
        fu.put("storeAddress",      followUp.getStoreAddress());
        fu.put("usageType",         followUp.getUsageType());
        fu.put("monthlyIncome",     followUp.getMonthlyIncome());
        fu.put("monthlySales",      followUp.getMonthlySales());
        fu.put("plan",              followUp.getPlan());
        fu.put("installmentOption", followUp.getInstallmentOption());
        fu.put("creditStatus",      followUp.getCreditStatus());
        fu.put("dti",               followUp.getDti());
        fu.put("otpSent",           followUp.getOtpSent());
        fu.put("adminReviewed",     followUp.isAdminReviewed());
        result.put("followUp", fu);

        // Credit report JSON (stored at submission time)
        Object creditReport = null;
        if (followUp.getCreditReportJson() != null && !followUp.getCreditReportJson().isBlank()) {
            try {
                creditReport = objectMapper.readValue(followUp.getCreditReportJson(), Object.class);
            } catch (Exception e) {
                log.warn("Could not parse stored credit report JSON: {}", e.getMessage());
                creditReport = Map.of("raw", followUp.getCreditReportJson());
            }
        }
        result.put("creditReport", creditReport);

        return result;
    }

    // ── 2. Process admin decision ─────────────────────────────────────────────

    @Transactional
    public boolean processReviewDecision(String token, AdminReviewDecisionRequest req) {
        AgentFollowUp followUp = repository.findByReviewToken(token).orElse(null);
        if (followUp == null) return false;

        // Apply overrides
        if (req.getOverrideClassification() != null && !req.getOverrideClassification().isBlank()) {
            followUp.setCreditStatus(req.getOverrideClassification().toUpperCase());
        }
        if (req.getOverridePlan() != null && !req.getOverridePlan().isBlank()) {
            followUp.setPlan(req.getOverridePlan());
        }
        if (req.getOverrideInstalment() != null && !req.getOverrideInstalment().isBlank()) {
            followUp.setInstallmentOption(req.getOverrideInstalment());
        }

        followUp.setAdminReviewed(true);
        followUp.setAdminNotes(req.getAdminNotes());

        if ("APPROVE".equalsIgnoreCase(req.getDecision())) {
            // Send OTP now
            String formattedPhone = formatPhone(followUp.getMobileNumber());
            String pin = otpService.sendOtp(formattedPhone);
            if (pin != null) {
                followUp.setApprovalPin(pin);
                followUp.setOtpSent(true);
                log.info("OTP sent to {} after admin approval", formattedPhone);
            }
            followUp.setAdminDecision("APPROVED");

        } else if ("REJECT".equalsIgnoreCase(req.getDecision())) {
            followUp.setAdminDecision("REJECTED");
            followUp.setRejectionReason(req.getRejectionReason());

            // Email customer rejection notice
            sendRejectionEmailToCustomer(followUp, req.getRejectionReason());
        }

        repository.save(followUp);
        return true;
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void sendRejectionEmailToCustomer(AgentFollowUp followUp, String reason) {
        if (followUp.getCustomerEmail() == null || followUp.getCustomerEmail().isBlank()) {
            log.warn("Cannot send rejection email — no customer email for BVN {}", followUp.getBvn());
            return;
        }

        String name = ((followUp.getFirstName() != null ? followUp.getFirstName() : "") + " " +
                       (followUp.getLastName()  != null ? followUp.getLastName()  : "")).trim();
        if (name.isBlank()) name = "Valued Customer";

        // Build rejection URL (for the React rejection page)
        String rejectionUrl = String.format("%s/application-rejected/%s?name=%s&reason=%s",
                frontendBaseUrl,
                followUp.getReviewToken(),
                urlEncode(name),
                urlEncode(reason != null ? reason : "Your application did not meet our current credit criteria."));

        String subject = "Your Koolboks Application — Update";

        String body = String.format("""
                Dear %s,
                
                Thank you for your interest in Koolboks financing.
                
                After a careful review of your application, we regret to inform you that we are
                unable to approve your request at this time.
                
                Reason: %s
                
                You may view the full details here:
                %s
                
                If you have any questions or would like to discuss your options, please contact
                our support team at support@koolboks.com or call us.
                
                We appreciate your understanding and hope to serve you in the future.
                
                Warm regards,
                The Koolboks Credit Team
                """, name, reason != null ? reason : "Please contact support for more information.", rejectionUrl);

        try {
            brevoEmailService.sendEmail(followUp.getCustomerEmail(), name, subject, body);
            log.info("Rejection email sent to {}", followUp.getCustomerEmail());
        } catch (Exception e) {
            log.error("Failed to send rejection email: {}", e.getMessage(), e);
        }
    }

    /**
     * Generates and saves a review token when the follow-up is first created
     * (called from AgentFollowUpService).
     */
    public String generateAndSendReviewEmail(AgentFollowUp followUp) {
        String token = java.util.UUID.randomUUID().toString().replace("-", "");
        followUp.setReviewToken(token);
        repository.save(followUp);

        String reviewUrl = String.format("%s/admin/credit-review/%s", frontendBaseUrl, token);

        String name = ((followUp.getFirstName() != null ? followUp.getFirstName() : "") + " " +
                       (followUp.getLastName()  != null ? followUp.getLastName()  : "")).trim();
        if (name.isBlank()) name = "New Applicant";

        String classLabel = followUp.getCreditStatus() != null ? followUp.getCreditStatus() : "UNKNOWN";
        String classIcon  = getClassIcon(classLabel);

        String subject = classIcon + " New Credit Application — Admin Review Required | " + name;

        String emailBody = String.format("""
                NEW CREDIT APPLICATION — ADMIN REVIEW REQUIRED
                ================================================
                
                Customer: %s
                BVN: %s
                Mobile: %s
                Plan: %s
                Instalment: %s
                Usage Type: %s
                
                SYSTEM ASSESSMENT:
                ------------------
                Classification: %s %s
                DTI: %.2f%%
                
                ACTION REQUIRED:
                ----------------
                Please review this application and approve or reject it.
                Clicking "Review" below will open the full credit report and decision panel.
                
                → REVIEW APPLICATION:
                %s
                
                ---
                This is an automated notification from the Koolboks Agent Entry System.
                """,
                name, followUp.getBvn(), followUp.getMobileNumber(),
                followUp.getPlan() != null ? followUp.getPlan() : "N/A",
                followUp.getInstallmentOption() != null ? followUp.getInstallmentOption() : "N/A",
                followUp.getUsageType() != null ? followUp.getUsageType() : "N/A",
                classIcon, classLabel,
                followUp.getDti() != null ? followUp.getDti() : 0.0,
                reviewUrl
        );

        // Send HTML email with a proper Review button
        String htmlBody = buildReviewEmailHtml(name, followUp, classLabel, classIcon, reviewUrl);

        try {
            brevoEmailService.sendHtmlEmail(adminEmail, "Admin", subject, htmlBody);
            log.info("Admin review email sent for BVN {} — token: {}", followUp.getBvn(), token);
        } catch (Exception e) {
            // Fallback to plain text
            try {
                brevoEmailService.sendEmail(adminEmail, "Admin", subject, emailBody);
            } catch (Exception ex) {
                log.error("Failed to send admin review email: {}", ex.getMessage(), ex);
            }
        }

        return token;
    }

    private String buildReviewEmailHtml(String name, AgentFollowUp followUp,
                                         String classLabel, String classIcon, String reviewUrl) {
        String classColour = switch (classLabel.toUpperCase()) {
            case "GREEN" -> "#16a34a";
            case "AMBER" -> "#d97706";
            default      -> "#dc2626";
        };

        return String.format("""
                <!DOCTYPE html>
                <html lang="en">
                <head><meta charset="UTF-8"/>
                <style>
                  * { box-sizing:border-box; margin:0; padding:0; }
                  body { font-family: Arial, sans-serif; background:#f5f5f5; color:#1f2937; }
                  .wrap { max-width:560px; margin:32px auto; background:#000; border-radius:16px; overflow:hidden; }
                  .header { background:#f7623b; padding:28px 32px; text-align:center; }
                  .header h1 { color:#fff; font-size:20px; font-weight:800; }
                  .header p  { color:#fff; opacity:.85; font-size:13px; margin-top:4px; }
                  .body { padding:28px 32px; }
                  .badge { display:inline-block; padding:6px 16px; border-radius:999px;
                           font-size:13px; font-weight:700; color:#fff; background:%s; margin:12px 0; }
                  .row { display:flex; justify-content:space-between; padding:8px 0;
                         border-bottom:1px solid #1a1a1a; font-size:13px; }
                  .row .lbl { color:#6b7280; }
                  .row .val { color:#e5e7eb; font-weight:600; }
                  .btn-wrap { text-align:center; margin:28px 0 8px; }
                  .btn { display:inline-block; padding:14px 40px; background:#000;
                         border:2px solid #f7623b; color:#f7623b; font-size:15px;
                         font-weight:800; border-radius:10px; text-decoration:none;
                         letter-spacing:.5px; }
                  .footer { padding:16px 32px; border-top:1px solid #111;
                            text-align:center; font-size:11px; color:#4b5563; }
                </style>
                </head>
                <body>
                <div class="wrap">
                  <div class="header">
                    <h1>Credit Application — Review Required</h1>
                    <p>A new application is awaiting your decision</p>
                  </div>
                  <div class="body">
                    <p style="color:#9ca3af;font-size:13px;margin-bottom:16px;">
                      Hello Admin, a new credit application has been submitted and requires your review.
                    </p>
                    <div class="row"><span class="lbl">Customer Name</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">BVN</span><span class="val" style="font-family:monospace">%s</span></div>
                    <div class="row"><span class="lbl">Mobile</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">Plan</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">Instalment</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">Usage</span><span class="val">%s</span></div>
                    <div class="row"><span class="lbl">DTI</span><span class="val">%.2f%%</span></div>
                    <div style="text-align:center;margin-top:12px;">
                      <p style="font-size:12px;color:#6b7280;margin-bottom:4px;">System Classification</p>
                      <span class="badge">%s %s</span>
                    </div>
                    <div class="btn-wrap">
                      <a href="%s" class="btn">Review Application</a>
                    </div>
                    <p style="text-align:center;font-size:12px;color:#4b5563;">
                      Click the button above to open the full credit report and make your decision.
                    </p>
                  </div>
                  <div class="footer">
                    Koolboks Credit System &middot; Automated Notification &middot; Confidential
                  </div>
                </div>
                </body>
                </html>
                """,
                classColour,
                name,
                followUp.getBvn(),
                followUp.getMobileNumber() != null ? followUp.getMobileNumber() : "N/A",
                followUp.getPlan() != null ? followUp.getPlan() : "N/A",
                followUp.getInstallmentOption() != null ? followUp.getInstallmentOption() : "N/A",
                followUp.getUsageType() != null ? followUp.getUsageType() : "N/A",
                followUp.getDti() != null ? followUp.getDti() : 0.0,
                classIcon, classLabel,
                reviewUrl
        );
    }

    private String getClassIcon(String cls) {
        if (cls == null) return "ℹ️";
        return switch (cls.toUpperCase()) {
            case "GREEN" -> "✅";
            case "AMBER" -> "⚠️";
            case "RED"   -> "❌";
            default      -> "ℹ️";
        };
    }

    private String formatPhone(String phone) {
        if (phone == null) return null;
        phone = phone.trim();
        if (phone.startsWith("0"))  return "+234" + phone.substring(1);
        if (!phone.startsWith("+")) return "+" + phone;
        return phone;
    }

    private String urlEncode(String s) {
        try {
            return java.net.URLEncoder.encode(s, "UTF-8");
        } catch (Exception e) {
            return s;
        }
    }
}
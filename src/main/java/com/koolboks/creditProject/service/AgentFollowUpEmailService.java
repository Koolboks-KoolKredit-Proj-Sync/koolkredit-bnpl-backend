package com.koolboks.creditProject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.koolboks.creditProject.dto.AgentFollowUpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class AgentFollowUpEmailService {

    private static final Logger log = LoggerFactory.getLogger(AgentFollowUpEmailService.class);

    private final BrevoEmailService brevoEmailService;

    @Value("${notification.email.to:foltim256@gmail.com}")
    private String notificationEmail;

    public AgentFollowUpEmailService(BrevoEmailService brevoEmailService) {
        this.brevoEmailService = brevoEmailService;
    }

    public void sendCrcReportEmail(AgentFollowUpRequest req, JsonNode crcJson, String classification, double dti) {
        String statusIcon = getClassificationIcon(classification);
        String subject = statusIcon + " CRC Report - " + classification;
        String content = buildCrcReportEmailBody(req, crcJson, classification, dti);
        brevoEmailService.sendEmail(notificationEmail, "Admin", subject, content);
        log.info("CRC Report email sent to: {} - Classification: {}", notificationEmail, classification);
    }

    // keep all private helper methods unchanged


//@Service
//public class AgentFollowUpEmailService {
//
//    private static final Logger log = LoggerFactory.getLogger(AgentFollowUpEmailService.class);
//
//    private final JavaMailSender mailSender;
//
//    @Value("${notification.email.to:foltim256@gmail.com}")
//    private String notificationEmail;
//
//    @Value("${notification.email.from:foltim256@gmail.com}")
//    private String fromEmail;
//
//    public AgentFollowUpEmailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    public void sendCrcReportEmail(AgentFollowUpRequest req, JsonNode crcJson, String classification, double dti) {
//        try {
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(fromEmail);
//            message.setTo(notificationEmail);
//
//            // Set subject with emoji/status indicator based on classification
//            String statusIcon = getClassificationIcon(classification);
//            message.setSubject(statusIcon + " CRC Report - " + classification);
//
//            message.setText(buildCrcReportEmailBody(req, crcJson, classification, dti));
//
//            mailSender.send(message);
//            log.info("CRC Report email sent successfully to: {} - Classification: {}", notificationEmail, classification);
//
//        } catch (Exception e) {
//            log.error("Error sending CRC report email: ", e);
//        }
//    }

    private String buildCrcReportEmailBody(AgentFollowUpRequest req, JsonNode crcJson, String classification, double dti) {
        return String.format("""
                AGENT FOLLOW-UP CRC REPORT
                ===========================
                
                CREDIT ASSESSMENT RESULTS:
                --------------------------
                Classification: %s %s
                Debt-to-Income Ratio: %.2f%%
                Status: %s
                
                CUSTOMER IDENTIFICATION:
                ------------------------
                BVN: %s
                NIN: %s
                Mobile Number: %s
                
                PRODUCT USAGE:
                --------------
                Usage Type: %s
                %s
                
                ADDRESSES:
                ----------
                Home Address: %s
                %s
                
                CREDIT BUREAU ANALYSIS:
                -----------------------
                %s
                
                RECOMMENDATION:
                ---------------
                %s
                
                ---
                This is an automated message from the Agent Entry System.
                Report generated on: %s
                """,
                getClassificationIcon(classification),
                classification,
                dti,
                getAssessmentStatus(classification, dti),
                req.getBvn(),
                req.getNin() != null ? req.getNin() : "N/A",
                req.getMobileNumber(),
                capitalizeFirst(req.getUsageType()),
                getUsageDetails(req),
                req.getHomeAddress() != null ? req.getHomeAddress() : "N/A",
                getAdditionalAddress(req),
                getCreditBureauSummary(crcJson),
                getRecommendation(classification, dti),
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String getClassificationIcon(String classification) {
        if (classification == null) return "ℹ️";
        return switch (classification.toUpperCase()) {
            case "GOOD" -> "✅";
            case "FAIR" -> "⚠️";
            case "POOR", "BAD" -> "❌";
            default -> "ℹ️";
        };
    }

    private String getAssessmentStatus(String classification, double dti) {
        if (classification == null) return "Pending Review";

        String dtiStatus = dti > 40 ? "High DTI - Review Required" : "Acceptable DTI";

        return switch (classification.toUpperCase()) {
            case "GOOD" -> "✓ APPROVED - Low Risk Customer";
            case "FAIR" -> "⚠ REVIEW REQUIRED - Moderate Risk";
            case "POOR", "BAD" -> "✗ HIGH RISK - Approval Not Recommended";
            default -> dtiStatus;
        };
    }

    private String getUsageDetails(AgentFollowUpRequest req) {
        if ("personal".equalsIgnoreCase(req.getUsageType())) {
            return String.format("Monthly Gross Income: ₦%s\nWork Address: %s",
                    formatCurrency(req.getMonthlyIncome()),
                    req.getWorkAddress() != null ? req.getWorkAddress() : "N/A");
        } else if ("commercial".equalsIgnoreCase(req.getUsageType())) {
            return String.format("Monthly Sales: ₦%s\nStore Address: %s",
                    formatCurrency(req.getMonthlySales()),
                    req.getStoreAddress() != null ? req.getStoreAddress() : "N/A");
        }
        return "No usage details provided";
    }

    private String getAdditionalAddress(AgentFollowUpRequest req) {
        if ("personal".equalsIgnoreCase(req.getUsageType()) && req.getWorkAddress() != null) {
            return "Work Address: " + req.getWorkAddress();
        } else if ("commercial".equalsIgnoreCase(req.getUsageType()) && req.getStoreAddress() != null) {
            return "Store Address: " + req.getStoreAddress();
        }
        return "";
    }

    private String getCreditBureauSummary(JsonNode crcJson) {
        if (crcJson == null || crcJson.isEmpty()) {
            return "No credit bureau data available";
        }

        StringBuilder summary = new StringBuilder();

        // Navigate to the data node
        JsonNode dataNode = crcJson.path("data");
        if (dataNode.isMissingNode()) {
            return "Credit bureau check completed - No detailed data available";
        }

        // Extract credit history information
        JsonNode creditHistory = dataNode.path("credit_history");
        if (creditHistory.isArray() && creditHistory.size() > 0) {
            int totalLoans = 0;
            int openLoans = 0;
            int closedLoans = 0;
            int performingLoans = 0;
            int nonPerformingLoans = 0;
            double totalRepaymentAmount = 0.0;

            // Loop through each institution's credit history
            for (JsonNode institution : creditHistory) {
                String institutionName = institution.path("institution").asText("Unknown");
                JsonNode history = institution.path("history");

                if (history.isArray()) {
                    for (JsonNode loan : history) {
                        totalLoans++;

                        String loanStatus = loan.path("loan_status").asText("").toLowerCase();
                        String performanceStatus = loan.path("performance_status").asText("").toLowerCase();
                        double repaymentAmount = loan.path("repayment_amount").asDouble(0.0);

                        // Count loan statuses
                        if ("open".equals(loanStatus)) {
                            openLoans++;
                            totalRepaymentAmount += repaymentAmount;
                        } else if ("closed".equals(loanStatus)) {
                            closedLoans++;
                        }

                        // Count performance
                        if ("performing".equals(performanceStatus)) {
                            performingLoans++;
                        } else if (!"".equals(performanceStatus)) {
                            nonPerformingLoans++;
                        }
                    }
                }
            }

            // Build summary
            summary.append("Total Loan Accounts: ").append(totalLoans).append("\n");
            summary.append("Open Loans: ").append(openLoans).append("\n");
            summary.append("Closed Loans: ").append(closedLoans).append("\n");
            summary.append("Performing Loans: ").append(performingLoans).append("\n");

            if (nonPerformingLoans > 0) {
                summary.append("⚠ Non-Performing Loans: ").append(nonPerformingLoans).append("\n");
            }

            if (totalRepaymentAmount > 0) {
                summary.append("Total Monthly Obligations: ₦").append(formatCurrency(totalRepaymentAmount)).append("\n");
            }

            summary.append("\nCredit Bureau Provider: ").append(dataNode.path("providers").toString());
        } else {
            summary.append("No credit history found for this customer\n");
            summary.append("Customer appears to have no previous loan records");
        }

        return summary.toString().trim();
    }

    private String getRecommendation(String classification, double dti) {
        if (classification == null) {
            return "Please review customer details manually.";
        }

        return switch (classification.toUpperCase()) {
            case "GOOD" -> dti <= 35
                ? "✓ RECOMMEND APPROVAL - Customer shows excellent credit history and manageable debt levels."
                : "⚠ CONDITIONAL APPROVAL - Good credit history but elevated DTI. Consider smaller installments.";

            case "FAIR" -> dti <= 40
                ? "⚠ REVIEW REQUIRED - Acceptable credit with moderate DTI. May approve with stricter terms."
                : "⚠ CAUTION - Fair credit with high DTI. Recommend larger down payment or shorter term.";

            case "POOR", "BAD" ->
                "✗ DO NOT APPROVE - High risk profile. Customer has poor credit history and/or excessive debt burden. " +
                "Recommend declining or requiring substantial collateral.";

            default -> "Status unclear. Manual review recommended.";
        };
    }

    private String formatCurrency(Double amount) {
        if (amount == null) return "0.00";
        return String.format("%,.2f", amount);
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}



















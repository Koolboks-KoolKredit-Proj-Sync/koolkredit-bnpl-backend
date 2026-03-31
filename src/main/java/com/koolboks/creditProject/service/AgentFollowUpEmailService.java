package com.koolboks.creditProject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koolboks.creditProject.dto.AgentFollowUpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Sends CRC + Mono financial report emails.
 *
 * After AgentFollowUp is submitted the service:
 *  1. Builds a plain-text CRC report (existing behaviour).
 *  2. If monoFinancialDataJson is present, builds an HTML financial
 *     report and sends it as a PDF attachment to the admin email.
 */
@Service
public class AgentFollowUpEmailService {

    private static final Logger log = LoggerFactory.getLogger(AgentFollowUpEmailService.class);

    private final BrevoEmailService brevoEmailService;
    private final PdfGeneratorService pdfGeneratorService;   // NEW — see companion class
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${notification.email.to:foltim256@gmail.com}")
    private String notificationEmail;

    public AgentFollowUpEmailService(BrevoEmailService brevoEmailService,
                                     PdfGeneratorService pdfGeneratorService) {
        this.brevoEmailService = brevoEmailService;
        this.pdfGeneratorService = pdfGeneratorService;
    }

    // ── existing method, kept unchanged ──────────────────────────────────────
    public void sendCrcReportEmail(AgentFollowUpRequest req,
                                   JsonNode crcJson,
                                   String classification,
                                   double dti) {
        String statusIcon = getClassificationIcon(classification);
        String subject    = statusIcon + " CRC Report - " + classification;
        String content    = buildCrcReportEmailBody(req, crcJson, classification, dti);
        brevoEmailService.sendEmail(notificationEmail, "Admin", subject, content);
        log.info("CRC Report email sent to: {} — Classification: {}", notificationEmail, classification);
    }

    // ── NEW: send Mono financial report as PDF attachment ────────────────────
    /**
     * Called after AgentFollowUp is saved.
     *
     * @param req                    the follow-up request (contains BVN, name, plan…)
     * @param monoFinancialDataJson  raw JSON string passed from the frontend
     *                               (null / blank → skip silently)
     * @param classification         GREEN / AMBER / RED
     * @param dti                    computed DTI percentage
     */
    public void sendMonoFinancialReportEmail(AgentFollowUpRequest req,
                                             String monoFinancialDataJson,
                                             String classification,
                                             double dti) {
        if (monoFinancialDataJson == null || monoFinancialDataJson.isBlank()) {
            log.info("No Mono financial data present — skipping PDF report email.");
            return;
        }

        try {
            JsonNode financialData = objectMapper.readTree(monoFinancialDataJson);

            // 1. Build HTML body
            String htmlReport = buildMonoFinancialHtml(req, financialData, classification, dti);

            // 2. Convert HTML → PDF bytes
            byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml(htmlReport);

            // 3. Build a short plain-text email body
            String emailBody = String.format("""
                    MONO BANK STATEMENT REPORT
                    ==========================
                    Customer: %s %s
                    BVN: %s
                    Plan: %s
                    Credit Classification: %s %s
                    DTI: %.2f%%
                    
                    Please find the full financial report attached as a PDF.
                    
                    Generated: %s
                    """,
                    req.getFirstName() != null ? req.getFirstName() : "",
                    req.getLastName()  != null ? req.getLastName()  : "",
                    req.getBvn(),
                    req.getPlan() != null ? req.getPlan() : "N/A",
                    getClassificationIcon(classification), classification,
                    dti,
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            );

            String subject = getClassificationIcon(classification)
                    + " Mono Financial Report — " + req.getBvn();

            // 4. Send via Brevo with PDF attachment
            brevoEmailService.sendEmailWithAttachment(
                    notificationEmail,
                    "Admin",
                    subject,
                    emailBody,
                    pdfBytes,
                    "mono_financial_report_" + req.getBvn() + ".pdf"
            );

            log.info("Mono financial PDF report sent to: {}", notificationEmail);

        } catch (Exception e) {
            log.error("Failed to send Mono financial PDF report: {}", e.getMessage(), e);
        }
    }

    // ── HTML builder for the PDF ──────────────────────────────────────────────
    private String buildMonoFinancialHtml(AgentFollowUpRequest req,
                                          JsonNode fin,
                                          String classification,
                                          double dti) {

        JsonNode account = fin.path("account");
        JsonNode credits = fin.path("credits");
        JsonNode debits  = fin.path("debits");

        long balanceKobo   = account.path("balance").asLong(0);
        String accountName = account.path("name").asText("N/A");
        String bankName    = account.path("institution").path("name").asText(
                             account.path("institution").asText("N/A"));
        String accountNo   = account.path("accountNumber").asText(
                             account.path("account_number").asText("N/A"));
        String currency    = account.path("currency").asText("NGN");

        double balanceNaira = balanceKobo / 100.0;

        String classColour = switch (classification.toUpperCase()) {
            case "GREEN", "GOOD" -> "#16a34a";
            case "AMBER", "FAIR" -> "#d97706";
            default              -> "#dc2626";
        };

        String generatedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss"));

        // build transaction rows
        StringBuilder creditRows = buildTransactionRows(credits, "#16a34a", "Credit");
        StringBuilder debitRows  = buildTransactionRows(debits,  "#dc2626", "Debit");

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <style>
                    * { box-sizing: border-box; margin: 0; padding: 0; }
                    body { font-family: Arial, sans-serif; font-size: 13px;
                           color: #1f2937; background: #fff; padding: 24px; }
                    h1  { font-size: 22px; color: #f7623b; margin-bottom: 4px; }
                    h2  { font-size: 15px; color: #374151; margin: 18px 0 8px; border-bottom: 2px solid #f7623b; padding-bottom: 4px; }
                    .meta { color: #6b7280; font-size: 11px; margin-bottom: 20px; }
                    .card { background: #f9fafb; border: 1px solid #e5e7eb;
                            border-radius: 8px; padding: 14px; margin-bottom: 14px; }
                    .card-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
                    .label { font-size: 11px; color: #6b7280; margin-bottom: 2px; }
                    .value { font-size: 14px; font-weight: bold; color: #111827; }
                    .balance { font-size: 28px; font-weight: 800; color: #f7623b; }
                    .badge { display: inline-block; padding: 4px 12px; border-radius: 999px;
                             font-size: 12px; font-weight: bold; color: #fff; }
                    table { width: 100%%; border-collapse: collapse; margin-top: 6px; }
                    th { background: #f3f4f6; text-align: left; padding: 7px 10px;
                         font-size: 11px; color: #6b7280; text-transform: uppercase; }
                    td { padding: 7px 10px; border-bottom: 1px solid #f3f4f6; font-size: 12px; }
                    tr:last-child td { border-bottom: none; }
                    .footer { margin-top: 24px; font-size: 11px; color: #9ca3af;
                              border-top: 1px solid #e5e7eb; padding-top: 10px; text-align: center; }
                  </style>
                </head>
                <body>
                  <h1>Koolboks — Mono Financial Report</h1>
                  <p class="meta">Generated: %s &nbsp;|&nbsp; Prepared for admin review</p>
                
                  <!-- Customer -->
                  <h2>Customer Details</h2>
                  <div class="card card-grid">
                    <div><p class="label">Full Name</p><p class="value">%s %s</p></div>
                    <div><p class="label">BVN</p><p class="value">%s</p></div>
                    <div><p class="label">Plan</p><p class="value">%s</p></div>
                    <div><p class="label">Usage Type</p><p class="value">%s</p></div>
                  </div>
                
                  <!-- Account -->
                  <h2>Bank Account Summary</h2>
                  <div class="card">
                    <div class="card-grid" style="margin-bottom:12px">
                      <div><p class="label">Account Name</p><p class="value">%s</p></div>
                      <div><p class="label">Bank</p><p class="value">%s</p></div>
                      <div><p class="label">Account Number</p><p class="value">%s</p></div>
                      <div><p class="label">Currency</p><p class="value">%s</p></div>
                    </div>
                    <p class="label">Current Balance</p>
                    <p class="balance">₦%s</p>
                  </div>
                
                  <!-- Credit Assessment -->
                  <h2>Credit Assessment</h2>
                  <div class="card card-grid">
                    <div>
                      <p class="label">Classification</p>
                      <span class="badge" style="background:%s">%s %s</span>
                    </div>
                    <div><p class="label">Debt-to-Income Ratio</p><p class="value">%.2f%%</p></div>
                  </div>
                
                  <!-- Credits -->
                  <h2>Credit Transactions</h2>
                  <table>
                    <thead><tr><th>#</th><th>Period</th><th>Amount (₦)</th></tr></thead>
                    <tbody>%s</tbody>
                  </table>
                
                  <!-- Debits -->
                  <h2>Debit Transactions</h2>
                  <table>
                    <thead><tr><th>#</th><th>Period</th><th>Amount (₦)</th></tr></thead>
                    <tbody>%s</tbody>
                  </table>
                
                  <div class="footer">
                    This report was automatically generated by the Koolboks Agent Entry System.<br/>
                    Confidential — for internal use only.
                  </div>
                </body>
                </html>
                """.formatted(
                generatedAt,
                // customer
                req.getFirstName() != null ? req.getFirstName() : "",
                req.getLastName()  != null ? req.getLastName()  : "",
                req.getBvn(),
                req.getPlan() != null ? req.getPlan() : "N/A",
                req.getUsageType() != null ? capitalizeFirst(req.getUsageType()) : "N/A",
                // account
                accountName, bankName, accountNo, currency,
                String.format("%,.2f", balanceNaira),
                // assessment
                classColour,
                getClassificationIcon(classification), classification,
                dti,
                // transactions
                creditRows.toString(),
                debitRows.toString()
        );
    }

    private StringBuilder buildTransactionRows(JsonNode txArray, String colour, String type) {
        StringBuilder sb = new StringBuilder();
        if (txArray == null || !txArray.isArray() || txArray.isEmpty()) {
            sb.append("<tr><td colspan='3' style='color:#9ca3af;text-align:center'>No ")
              .append(type).append(" transactions found</td></tr>");
            return sb;
        }
        int i = 1;
        for (JsonNode tx : txArray) {
            long   amountKobo = tx.path("amount").asLong(0);
            double amountNaira = amountKobo / 100.0;
            String period = tx.path("period").asText(
                            tx.path("date").asText("N/A"));
            sb.append("<tr>")
              .append("<td>").append(i++).append("</td>")
              .append("<td>").append(period).append("</td>")
              .append("<td style='color:").append(colour).append(";font-weight:bold'>")
              .append(String.format("₦%,.2f", amountNaira)).append("</td>")
              .append("</tr>");
        }
        return sb;
    }

    // ── unchanged helpers ─────────────────────────────────────────────────────
    private String buildCrcReportEmailBody(AgentFollowUpRequest req,
                                           JsonNode crcJson,
                                           String classification,
                                           double dti) {
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
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    private String getClassificationIcon(String classification) {
        if (classification == null) return "ℹ️";
        return switch (classification.toUpperCase()) {
            case "GOOD", "GREEN" -> "✅";
            case "FAIR", "AMBER" -> "⚠️";
            case "POOR", "BAD", "RED" -> "❌";
            default -> "ℹ️";
        };
    }

    private String getAssessmentStatus(String classification, double dti) {
        if (classification == null) return "Pending Review";
        String dtiStatus = dti > 40 ? "High DTI - Review Required" : "Acceptable DTI";
        return switch (classification.toUpperCase()) {
            case "GOOD", "GREEN" -> "✓ APPROVED - Low Risk Customer";
            case "FAIR", "AMBER" -> "⚠ REVIEW REQUIRED - Moderate Risk";
            case "POOR", "BAD", "RED" -> "✗ HIGH RISK - Approval Not Recommended";
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
        if ("personal".equalsIgnoreCase(req.getUsageType()) && req.getWorkAddress() != null)
            return "Work Address: " + req.getWorkAddress();
        if ("commercial".equalsIgnoreCase(req.getUsageType()) && req.getStoreAddress() != null)
            return "Store Address: " + req.getStoreAddress();
        return "";
    }

    private String getCreditBureauSummary(JsonNode crcJson) {
        if (crcJson == null || crcJson.isEmpty())
            return "No credit bureau data available";

        JsonNode dataNode = crcJson.path("data");
        if (dataNode.isMissingNode())
            return "Credit bureau check completed - No detailed data available";

        StringBuilder summary = new StringBuilder();
        JsonNode creditHistory = dataNode.path("credit_history");
        if (creditHistory.isArray() && creditHistory.size() > 0) {
            int totalLoans = 0, openLoans = 0, closedLoans = 0,
                performingLoans = 0, nonPerformingLoans = 0;
            double totalRepaymentAmount = 0.0;

            for (JsonNode institution : creditHistory) {
                JsonNode history = institution.path("history");
                if (history.isArray()) {
                    for (JsonNode loan : history) {
                        totalLoans++;
                        String loanStatus  = loan.path("loan_status").asText("").toLowerCase();
                        String perfStatus  = loan.path("performance_status").asText("").toLowerCase();
                        double repayAmount = loan.path("repayment_amount").asDouble(0.0);

                        if ("open".equals(loanStatus))  { openLoans++;   totalRepaymentAmount += repayAmount; }
                        if ("closed".equals(loanStatus)) closedLoans++;
                        if ("performing".equals(perfStatus)) performingLoans++;
                        else if (!perfStatus.isEmpty())      nonPerformingLoans++;
                    }
                }
            }

            summary.append("Total Loan Accounts: ").append(totalLoans).append("\n")
                   .append("Open Loans: ").append(openLoans).append("\n")
                   .append("Closed Loans: ").append(closedLoans).append("\n")
                   .append("Performing Loans: ").append(performingLoans).append("\n");
            if (nonPerformingLoans > 0)
                summary.append("⚠ Non-Performing Loans: ").append(nonPerformingLoans).append("\n");
            if (totalRepaymentAmount > 0)
                summary.append("Total Monthly Obligations: ₦").append(formatCurrency(totalRepaymentAmount)).append("\n");
            summary.append("\nCredit Bureau Provider: ").append(dataNode.path("providers").toString());
        } else {
            summary.append("No credit history found for this customer\n")
                   .append("Customer appears to have no previous loan records");
        }
        return summary.toString().trim();
    }

    private String getRecommendation(String classification, double dti) {
        if (classification == null) return "Please review customer details manually.";
        return switch (classification.toUpperCase()) {
            case "GOOD", "GREEN" -> dti <= 35
                ? "✓ RECOMMEND APPROVAL - Customer shows excellent credit history and manageable debt levels."
                : "⚠ CONDITIONAL APPROVAL - Good credit history but elevated DTI. Consider smaller installments.";
            case "FAIR", "AMBER" -> dti <= 40
                ? "⚠ REVIEW REQUIRED - Acceptable credit with moderate DTI. May approve with stricter terms."
                : "⚠ CAUTION - Fair credit with high DTI. Recommend larger down payment or shorter term.";
            case "POOR", "BAD", "RED" ->
                "✗ DO NOT APPROVE - High risk profile. Customer has poor credit history and/or excessive debt burden.";
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









//package com.koolboks.creditProject.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.koolboks.creditProject.dto.AgentFollowUpRequest;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//
//@Service
//public class AgentFollowUpEmailService {
//
//    private static final Logger log = LoggerFactory.getLogger(AgentFollowUpEmailService.class);
//
//    private final BrevoEmailService brevoEmailService;
//
//    @Value("${notification.email.to:foltim256@gmail.com}")
//    private String notificationEmail;
//
//    public AgentFollowUpEmailService(BrevoEmailService brevoEmailService) {
//        this.brevoEmailService = brevoEmailService;
//    }
//
//    public void sendCrcReportEmail(AgentFollowUpRequest req, JsonNode crcJson, String classification, double dti) {
//        String statusIcon = getClassificationIcon(classification);
//        String subject = statusIcon + " CRC Report - " + classification;
//        String content = buildCrcReportEmailBody(req, crcJson, classification, dti);
//        brevoEmailService.sendEmail(notificationEmail, "Admin", subject, content);
//        log.info("CRC Report email sent to: {} - Classification: {}", notificationEmail, classification);
//    }
//
//
//
//
//    private String buildCrcReportEmailBody(AgentFollowUpRequest req, JsonNode crcJson, String classification, double dti) {
//        return String.format("""
//                AGENT FOLLOW-UP CRC REPORT
//                ===========================
//
//                CREDIT ASSESSMENT RESULTS:
//                --------------------------
//                Classification: %s %s
//                Debt-to-Income Ratio: %.2f%%
//                Status: %s
//
//                CUSTOMER IDENTIFICATION:
//                ------------------------
//                BVN: %s
//                NIN: %s
//                Mobile Number: %s
//
//                PRODUCT USAGE:
//                --------------
//                Usage Type: %s
//                %s
//
//                ADDRESSES:
//                ----------
//                Home Address: %s
//                %s
//
//                CREDIT BUREAU ANALYSIS:
//                -----------------------
//                %s
//
//                RECOMMENDATION:
//                ---------------
//                %s
//
//                ---
//                This is an automated message from the Agent Entry System.
//                Report generated on: %s
//                """,
//                getClassificationIcon(classification),
//                classification,
//                dti,
//                getAssessmentStatus(classification, dti),
//                req.getBvn(),
//                req.getNin() != null ? req.getNin() : "N/A",
//                req.getMobileNumber(),
//                capitalizeFirst(req.getUsageType()),
//                getUsageDetails(req),
//                req.getHomeAddress() != null ? req.getHomeAddress() : "N/A",
//                getAdditionalAddress(req),
//                getCreditBureauSummary(crcJson),
//                getRecommendation(classification, dti),
//                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//        );
//    }
//
//    private String getClassificationIcon(String classification) {
//        if (classification == null) return "ℹ️";
//        return switch (classification.toUpperCase()) {
//            case "GOOD" -> "✅";
//            case "FAIR" -> "⚠️";
//            case "POOR", "BAD" -> "❌";
//            default -> "ℹ️";
//        };
//    }
//
//    private String getAssessmentStatus(String classification, double dti) {
//        if (classification == null) return "Pending Review";
//
//        String dtiStatus = dti > 40 ? "High DTI - Review Required" : "Acceptable DTI";
//
//        return switch (classification.toUpperCase()) {
//            case "GOOD" -> "✓ APPROVED - Low Risk Customer";
//            case "FAIR" -> "⚠ REVIEW REQUIRED - Moderate Risk";
//            case "POOR", "BAD" -> "✗ HIGH RISK - Approval Not Recommended";
//            default -> dtiStatus;
//        };
//    }
//
//    private String getUsageDetails(AgentFollowUpRequest req) {
//        if ("personal".equalsIgnoreCase(req.getUsageType())) {
//            return String.format("Monthly Gross Income: ₦%s\nWork Address: %s",
//                    formatCurrency(req.getMonthlyIncome()),
//                    req.getWorkAddress() != null ? req.getWorkAddress() : "N/A");
//        } else if ("commercial".equalsIgnoreCase(req.getUsageType())) {
//            return String.format("Monthly Sales: ₦%s\nStore Address: %s",
//                    formatCurrency(req.getMonthlySales()),
//                    req.getStoreAddress() != null ? req.getStoreAddress() : "N/A");
//        }
//        return "No usage details provided";
//    }
//
//    private String getAdditionalAddress(AgentFollowUpRequest req) {
//        if ("personal".equalsIgnoreCase(req.getUsageType()) && req.getWorkAddress() != null) {
//            return "Work Address: " + req.getWorkAddress();
//        } else if ("commercial".equalsIgnoreCase(req.getUsageType()) && req.getStoreAddress() != null) {
//            return "Store Address: " + req.getStoreAddress();
//        }
//        return "";
//    }
//
//    private String getCreditBureauSummary(JsonNode crcJson) {
//        if (crcJson == null || crcJson.isEmpty()) {
//            return "No credit bureau data available";
//        }
//
//        StringBuilder summary = new StringBuilder();
//
//        // Navigate to the data node
//        JsonNode dataNode = crcJson.path("data");
//        if (dataNode.isMissingNode()) {
//            return "Credit bureau check completed - No detailed data available";
//        }
//
//        // Extract credit history information
//        JsonNode creditHistory = dataNode.path("credit_history");
//        if (creditHistory.isArray() && creditHistory.size() > 0) {
//            int totalLoans = 0;
//            int openLoans = 0;
//            int closedLoans = 0;
//            int performingLoans = 0;
//            int nonPerformingLoans = 0;
//            double totalRepaymentAmount = 0.0;
//
//            // Loop through each institution's credit history
//            for (JsonNode institution : creditHistory) {
//                String institutionName = institution.path("institution").asText("Unknown");
//                JsonNode history = institution.path("history");
//
//                if (history.isArray()) {
//                    for (JsonNode loan : history) {
//                        totalLoans++;
//
//                        String loanStatus = loan.path("loan_status").asText("").toLowerCase();
//                        String performanceStatus = loan.path("performance_status").asText("").toLowerCase();
//                        double repaymentAmount = loan.path("repayment_amount").asDouble(0.0);
//
//                        // Count loan statuses
//                        if ("open".equals(loanStatus)) {
//                            openLoans++;
//                            totalRepaymentAmount += repaymentAmount;
//                        } else if ("closed".equals(loanStatus)) {
//                            closedLoans++;
//                        }
//
//                        // Count performance
//                        if ("performing".equals(performanceStatus)) {
//                            performingLoans++;
//                        } else if (!"".equals(performanceStatus)) {
//                            nonPerformingLoans++;
//                        }
//                    }
//                }
//            }
//
//            // Build summary
//            summary.append("Total Loan Accounts: ").append(totalLoans).append("\n");
//            summary.append("Open Loans: ").append(openLoans).append("\n");
//            summary.append("Closed Loans: ").append(closedLoans).append("\n");
//            summary.append("Performing Loans: ").append(performingLoans).append("\n");
//
//            if (nonPerformingLoans > 0) {
//                summary.append("⚠ Non-Performing Loans: ").append(nonPerformingLoans).append("\n");
//            }
//
//            if (totalRepaymentAmount > 0) {
//                summary.append("Total Monthly Obligations: ₦").append(formatCurrency(totalRepaymentAmount)).append("\n");
//            }
//
//            summary.append("\nCredit Bureau Provider: ").append(dataNode.path("providers").toString());
//        } else {
//            summary.append("No credit history found for this customer\n");
//            summary.append("Customer appears to have no previous loan records");
//        }
//
//        return summary.toString().trim();
//    }
//
//    private String getRecommendation(String classification, double dti) {
//        if (classification == null) {
//            return "Please review customer details manually.";
//        }
//
//        return switch (classification.toUpperCase()) {
//            case "GOOD" -> dti <= 35
//                ? "✓ RECOMMEND APPROVAL - Customer shows excellent credit history and manageable debt levels."
//                : "⚠ CONDITIONAL APPROVAL - Good credit history but elevated DTI. Consider smaller installments.";
//
//            case "FAIR" -> dti <= 40
//                ? "⚠ REVIEW REQUIRED - Acceptable credit with moderate DTI. May approve with stricter terms."
//                : "⚠ CAUTION - Fair credit with high DTI. Recommend larger down payment or shorter term.";
//
//            case "POOR", "BAD" ->
//                "✗ DO NOT APPROVE - High risk profile. Customer has poor credit history and/or excessive debt burden. " +
//                "Recommend declining or requiring substantial collateral.";
//
//            default -> "Status unclear. Manual review recommended.";
//        };
//    }
//
//    private String formatCurrency(Double amount) {
//        if (amount == null) return "0.00";
//        return String.format("%,.2f", amount);
//    }
//
//    private String capitalizeFirst(String str) {
//        if (str == null || str.isEmpty()) return str;
//        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
//    }
//}
//














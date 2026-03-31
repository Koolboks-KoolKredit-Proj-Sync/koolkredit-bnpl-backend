package com.koolboks.creditProject.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.koolboks.creditProject.dto.AgentFollowUpRequest;
import com.koolboks.creditProject.entity.AgentFollowUp;
import com.koolboks.creditProject.repository.AgentFollowUpRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgentFollowUpService {

    private final MonoClientService monoClientService;
    private final AgentFollowUpRepository repository;
    private final AgentFollowUpEmailService emailService;
    private final OtpService otpService;
    private final AgentFollowUpRepository agentFollowUpRepository;

    @Value("${dti.threshold.green:40}")
    private int greenThreshold;

    @Value("${dti.threshold.amber:60}")
    private int amberThreshold;

    public AgentFollowUpService(MonoClientService monoClientService,
                                AgentFollowUpRepository repository,
                                AgentFollowUpEmailService emailService,
                                OtpService otpService,
                                AgentFollowUpRepository agentFollowUpRepository) {
        this.monoClientService      = monoClientService;
        this.repository             = repository;
        this.emailService           = emailService;
        this.otpService             = otpService;
        this.agentFollowUpRepository = agentFollowUpRepository;
    }

    public AgentFollowUp findByCustomerBvn(String customerBvn) {
        return agentFollowUpRepository
                .findTopByBvnOrderByCreatedAtDesc(customerBvn)
                .orElse(null);
    }

    // ── main processing method ────────────────────────────────────────────────
    /**
     * @param monoFinancialDataJson  JSON string forwarded from the frontend;
     *                               null/blank when the agent skipped the bank-
     *                               statement step.
     */
    public FollowUpResult processFollowUp(AgentFollowUpRequest req,
                                          String monoFinancialDataJson) {

        // 1. Fetch CRC (Mono) as JsonNode
        JsonNode crcJson     = monoClientService.fetchCrcByBvn(req.getBvn());
        String   fullResponse = crcJson == null ? "{}" : crcJson.toString();

        // 2. Parse credit history
        double  monthlyObligations        = 0.0;
        boolean hasCreditHistory          = false;
        boolean anyNonPerformingOpenLoan  = false;

        JsonNode creditHistoryNode = crcJson == null
                ? null
                : crcJson.path("data").path("credit_history");

        if (creditHistoryNode != null
                && creditHistoryNode.isArray()
                && creditHistoryNode.size() > 0) {

            hasCreditHistory = true;
            for (JsonNode inst : creditHistoryNode) {
                JsonNode historyArr = inst.path("history");
                if (historyArr != null && historyArr.isArray()) {
                    for (JsonNode loan : historyArr) {
                        String loanStatus  = loan.path("loan_status").asText("");
                        String perfStatus  = loan.path("performance_status").asText("");
                        double repayAmount = loan.path("repayment_amount").asDouble(0.0);

                        if ("open".equalsIgnoreCase(loanStatus)
                                && !"performing".equalsIgnoreCase(perfStatus)) {
                            anyNonPerformingOpenLoan = true;
                        }
                        if ("open".equalsIgnoreCase(loanStatus)
                                && "performing".equalsIgnoreCase(perfStatus)) {
                            monthlyObligations += repayAmount;
                        }
                    }
                }
            }
        }

        // 3. Non-performing open loan → immediate decline
        if (anyNonPerformingOpenLoan) {
            AgentFollowUp decl = mapToEntity(req, 0.0, "RED", false);
            decl.setMonoFullResponse(fullResponse);
            repository.save(decl);
            emailService.sendCrcReportEmail(req, crcJson, "RED", 0.0);
            // ── send Mono PDF even on decline so admin has the full picture ──
            emailService.sendMonoFinancialReportEmail(req, monoFinancialDataJson, "RED", 0.0);
            return new FollowUpResult("RED", 0.0, false,
                    "Found non-performing/open loans");
        }

        // 4. Monthly income base
        double monthlyBase = "Personal".equalsIgnoreCase(req.getUsageType())
                ? (req.getMonthlyIncome()  != null ? req.getMonthlyIncome()  : 0.0)
                : (req.getMonthlySales()   != null ? req.getMonthlySales()   : 0.0);

        if (monthlyBase <= 0.0) {
            AgentFollowUp amber = mapToEntity(req, 0.0, "AMBER", false);
            amber.setMonoFullResponse(fullResponse);
            repository.save(amber);
            emailService.sendCrcReportEmail(req, crcJson, "AMBER", 0.0);
            emailService.sendMonoFinancialReportEmail(req, monoFinancialDataJson, "AMBER", 0.0);
            return new FollowUpResult("AMBER", 0.0, false,
                    "Missing or zero income/sales to compute DTI");
        }

        // 5. DTI
        double dtiPercent = (monthlyObligations / monthlyBase) * 100.0;

        // 6. Classification
        String  classification  = "RED";
        boolean eligibleForOtp  = false;

        if (!hasCreditHistory) {
            classification = "GREEN";
            eligibleForOtp = true;
        } else if (dtiPercent <= greenThreshold) {
            classification = "GREEN";
            eligibleForOtp = true;
        } else if (dtiPercent <= amberThreshold) {
            classification = "AMBER";
        }

        // 7. Persist
        AgentFollowUp entity = mapToEntity(req, dtiPercent, classification, false);
        entity.setMonoFullResponse(fullResponse);
        repository.save(entity);

        // 8. Send CRC plain-text report (existing behaviour)
        emailService.sendCrcReportEmail(req, crcJson, classification, dtiPercent);

        // 9. Send Mono financial PDF report (NEW)
        emailService.sendMonoFinancialReportEmail(req, monoFinancialDataJson,
                classification, dtiPercent);

        // 10. OTP if eligible
        boolean otpSent = false;
        if (eligibleForOtp) {
            String formattedPhone = formatPhone(req.getMobileNumber());
            String pin = otpService.sendOtp(formattedPhone);
            if (pin != null) {
                otpSent = true;
                entity.setApprovalPin(pin);
                entity.setOtpSent(true);
                repository.save(entity);
            }
        }

        return new FollowUpResult(classification, dtiPercent, otpSent, null);
    }

    // ── backward-compat overload (no Mono data) ───────────────────────────────
    public FollowUpResult processFollowUp(AgentFollowUpRequest req) {
        return processFollowUp(req, null);
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private String formatPhone(String phone) {
        if (phone == null) return null;
        phone = phone.trim();
        if (phone.startsWith("0"))  return "+234" + phone.substring(1);
        if (!phone.startsWith("+")) return "+" + phone;
        return phone;
    }

    private String saveUtilityBill(MultipartFile file) {
        try {
            String uploadDir = "uploads/utility-bills/";
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path   filePath = Paths.get(uploadDir + fileName);
            Files.write(filePath, file.getBytes());
            return filePath.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save utility bill file", e);
        }
    }

    private AgentFollowUp mapToEntity(AgentFollowUpRequest req,
                                      double dtiPercent,
                                      String classification,
                                      boolean otpSent) {
        AgentFollowUp e = new AgentFollowUp();
        e.setBvn(req.getBvn());
        e.setNin(req.getNin());
        e.setMobileNumber(req.getMobileNumber());
        e.setUsageType(req.getUsageType());
        e.setHomeAddress(req.getHomeAddress());
        e.setWorkAddress(req.getWorkAddress());
        e.setStoreAddress(req.getStoreAddress());
        e.setCustomerEmail(req.getCustomerEmail());
        e.setGuarantorEmail(req.getGuarantorEmail());
        e.setMonthlyIncome(req.getMonthlyIncome());
        e.setMonthlySales(req.getMonthlySales());
        e.setPlan(req.getPlan());
        e.setInstallmentOption(req.getInstallmentOption());

        if (req.getUtilityBill() != null && !req.getUtilityBill().isEmpty()) {
            e.setUtilityBillFilePath(saveUtilityBill(req.getUtilityBill()));
        }

        e.setDti(dtiPercent);
        e.setCreditStatus(classification);
        e.setOtpSent(otpSent);
        return e;
    }

    // ── result DTO ────────────────────────────────────────────────────────────
    public static class FollowUpResult {
        private final String  classification;
        private final double  dti;
        private final boolean otpSent;
        private final String  reason;

        public FollowUpResult(String classification, double dti,
                              boolean otpSent, String reason) {
            this.classification = classification;
            this.dti            = dti;
            this.otpSent        = otpSent;
            this.reason         = reason;
        }

        public String  getClassification() { return classification; }
        public double  getDti()            { return dti; }
        public boolean isOtpSent()         { return otpSent; }
        public String  getReason()         { return reason; }
    }

    // ── OTP verification ──────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public boolean verifyOtp(String bvn, String otp) {
        if (bvn == null || otp == null || bvn.isBlank() || otp.isBlank())
            return false;

        AgentFollowUp followUp = repository
                .findTopByBvnOrderByCreatedAtDesc(bvn)
                .orElse(null);

        if (followUp == null)                            return false;
        if (!Boolean.TRUE.equals(followUp.getOtpSent())) return false;
        if (!otp.equals(followUp.getApprovalPin()))       return false;

        followUp.setOtpVerified(true);
        repository.save(followUp);
        return true;
    }
}



































//package com.koolboks.creditProject.service;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.koolboks.creditProject.dto.AgentFollowUpRequest;
//import com.koolboks.creditProject.entity.AgentFollowUp;
//import com.koolboks.creditProject.repository.AgentFollowUpRepository;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//
//
//@Service
//public class AgentFollowUpService {
//
//    private final MonoClientService monoClientService;
//    private final AgentFollowUpRepository repository;
//    private final AgentFollowUpEmailService emailService; // Use the new email service
//    private final OtpService otpService;
//    private final AgentFollowUpRepository agentFollowUpRepository;
//
//
//    @Value("${dti.threshold.green:40}")
//    private int greenThreshold;
//
//    @Value("${dti.threshold.amber:60}")
//    private int amberThreshold;
//
//    // Constructor now injects AgentFollowUpEmailService
//    public AgentFollowUpService(MonoClientService monoClientService,
//                                AgentFollowUpRepository repository,
//                                AgentFollowUpEmailService emailService,
//                                OtpService otpService, AgentFollowUpRepository agentFollowUpRepository) {
//        this.monoClientService = monoClientService;
//        this.repository = repository;
//        this.emailService = emailService;
//        this.otpService = otpService;
//        this.agentFollowUpRepository = agentFollowUpRepository;
//    }
//
////    public AgentFollowUp findByCustomerBvn(String customerBvn) {
////        return agentFollowUpRepository.findByBvn(customerBvn).orElse(null);
////    }
//
//    public AgentFollowUp findByCustomerBvn(String customerBvn) {
//    return agentFollowUpRepository
//            .findTopByBvnOrderByCreatedAtDesc(customerBvn)
//            .orElse(null);
//}
//
//
//    private String saveUtilityBill(MultipartFile file) {
//        try {
//            String uploadDir = "uploads/utility-bills/";
//            Files.createDirectories(Paths.get(uploadDir));
//
//            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//            Path filePath = Paths.get(uploadDir + fileName);
//
//            Files.write(filePath, file.getBytes());
//
//            return filePath.toString(); // Store path in DB
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to save utility bill file", e);
//        }
//    }
//
//    public FollowUpResult processFollowUp(AgentFollowUpRequest req) {
//        // 1. Fetch CRC (Mono) as JsonNode
//        JsonNode crcJson = monoClientService.fetchCrcByBvn(req.getBvn());
//
//        // Save full JSON for audit
//        String fullResponse = crcJson == null ? "{}" : crcJson.toString();
//
//        // 2. Extract credit_history and compute obligations
//        double monthlyObligations = 0.0;
//        boolean hasCreditHistory = false;
//        boolean anyNonPerformingOpenLoan = false;
//
//        JsonNode creditHistoryNode = crcJson == null ? null : crcJson.path("data").path("credit_history");
//        if (creditHistoryNode != null && creditHistoryNode.isArray() && creditHistoryNode.size() > 0) {
//            hasCreditHistory = true;
//            for (JsonNode inst : creditHistoryNode) {
//                JsonNode historyArr = inst.path("history");
//                if (historyArr != null && historyArr.isArray()) {
//                    for (JsonNode loan : historyArr) {
//                        String loanStatus = loan.path("loan_status").asText("");
//                        String perfStatus = loan.path("performance_status").asText("");
//                        double repayAmount = loan.path("repayment_amount").asDouble(0.0);
//
//                        if ("open".equalsIgnoreCase(loanStatus) && !"performing".equalsIgnoreCase(perfStatus)) {
//                            anyNonPerformingOpenLoan = true;
//                        }
//
//                        if ("open".equalsIgnoreCase(loanStatus) && "performing".equalsIgnoreCase(perfStatus)) {
//                            monthlyObligations += repayAmount;
//                        }
//                    }
//                }
//            }
//        }
//
//        // 3. If any non-performing open loan => decline
//        if (anyNonPerformingOpenLoan) {
//            AgentFollowUp decl = mapToEntity(req, 0.0, "RED", false);
//            decl.setMonoFullResponse(fullResponse);
//            repository.save(decl);
//            // Now sends professional formatted email
//            emailService.sendCrcReportEmail(req, crcJson, "RED", 0.0);
//            return new FollowUpResult("RED", 0.0, false, "Found non-performing/open loans");
//        }
//
//        // 4. Pick monthly base depending on usageType
//        double monthlyBase = 0.0;
//        if ("Personal".equalsIgnoreCase(req.getUsageType())) {
//            monthlyBase = req.getMonthlyIncome() == null ? 0.0 : req.getMonthlyIncome();
//        } else {
//            monthlyBase = req.getMonthlySales() == null ? 0.0 : req.getMonthlySales();
//        }
//
//        if (monthlyBase <= 0.0) {
//            AgentFollowUp amber = mapToEntity(req, 0.0, "AMBER", false);
//            amber.setMonoFullResponse(fullResponse);
//            repository.save(amber);
//            emailService.sendCrcReportEmail(req, crcJson, "AMBER", 0.0);
//            return new FollowUpResult("AMBER", 0.0, false, "Missing or zero income/sales to compute DTI");
//        }
//
//        // 5. Compute DTI (percentage)
//        double dtiPercent = (monthlyObligations / monthlyBase) * 100.0;
//
//        // 6. Classification thresholds
//        String classification;
//        boolean eligibleForOtp = false;
//
//        if (!hasCreditHistory) {
//            classification = "GREEN";
//            eligibleForOtp = true;
//        } else {
//            if (dtiPercent <= greenThreshold) {
//                classification = "GREEN";
//                eligibleForOtp = true;
//            } else if (dtiPercent <= amberThreshold) {
//                classification = "AMBER";
//            } else {
//                classification = "RED";
//            }
//        }
//
//        // 7. Persist follow up
//        AgentFollowUp entity = mapToEntity(req, dtiPercent, classification, false);
//        entity.setMonoFullResponse(fullResponse);
//        repository.save(entity);
//
//        // 8. Email CRC JSON for records (always) - !
//        emailService.sendCrcReportEmail(req, crcJson, classification, dtiPercent);
//
//        // 9. If eligible => send OTP (generate 6-digit pin and send SMS via OTP service)
//        boolean otpSent = false;
//        if (eligibleForOtp) {
//            String formattedPhone = formatPhone(req.getMobileNumber());
//            String pin = otpService.sendOtp(formattedPhone);
//            if (pin != null) {
//                otpSent = true;
//                entity.setApprovalPin(pin);
//                entity.setOtpSent(true);
//                repository.save(entity);
//            }
//        }
//
//        return new FollowUpResult(classification, dtiPercent, otpSent, null);
//    }
//
//    private String formatPhone(String phone) {
//        if (phone == null) return null;
//        phone = phone.trim();
//        if (phone.startsWith("0")) return "+234" + phone.substring(1);
//        if (!phone.startsWith("+")) return "+" + phone;
//        return phone;
//    }
//
//    private AgentFollowUp mapToEntity(AgentFollowUpRequest req, double dtiPercent, String classification, boolean otpSent) {
//        AgentFollowUp e = new AgentFollowUp();
//        e.setBvn(req.getBvn());
//        e.setNin(req.getNin());
//        e.setMobileNumber(req.getMobileNumber());
//        e.setUsageType(req.getUsageType());
//        e.setHomeAddress(req.getHomeAddress());
//        e.setWorkAddress(req.getWorkAddress());
//        e.setStoreAddress(req.getStoreAddress());
//        e.setCustomerEmail(req.getCustomerEmail());
//        e.setGuarantorEmail(req.getGuarantorEmail());
//        e.setMonthlyIncome(req.getMonthlyIncome());
//        e.setMonthlySales(req.getMonthlySales());
//        e.setPlan(req.getPlan());
//        e.setInstallmentOption(req.getInstallmentOption());
//
//        if (req.getUtilityBill() != null && !req.getUtilityBill().isEmpty()) {
//            String path = saveUtilityBill(req.getUtilityBill());
//            e.setUtilityBillFilePath(path);
//        }
//
//        e.setDti(dtiPercent);
//        e.setCreditStatus(classification);
//        e.setOtpSent(otpSent);
//        return e;
//    }
//
//    // Lightweight result DTO
//    public static class FollowUpResult {
//        private final String classification;
//        private final double dti;
//        private final boolean otpSent;
//        private final String reason;
//
//        public FollowUpResult(String classification, double dti, boolean otpSent, String reason) {
//            this.classification = classification;
//            this.dti = dti;
//            this.otpSent = otpSent;
//            this.reason = reason;
//        }
//
//        public String getClassification() { return classification; }
//        public double getDti() { return dti; }
//        public boolean isOtpSent() { return otpSent; }
//        public String getReason() { return reason; }
//    }
//
//    // Verify OTP
//
//    @Transactional(readOnly = true)
//    public boolean verifyOtp(String bvn, String otp) {
//        if (bvn == null || otp == null || bvn.isBlank() || otp.isBlank()) {
//            return false;
//        }
//
//        AgentFollowUp followUp = repository.findTopByBvnOrderByCreatedAtDesc(bvn)
//                .orElse(null);
//
//        if (followUp == null) {
//            return false;
//        }
//
//        // Only read non-Lob fields while inside transaction
//        if (!Boolean.TRUE.equals(followUp.getOtpSent())) {
//            return false;
//        }
//
//        if (!otp.equals(followUp.getApprovalPin())) {
//            return false;
//        }
//
//        followUp.setOtpVerified(true);
//        repository.save(followUp);
//        return true;
//    }

//    public boolean verifyOtp(String bvn, String otp) {
//    if (bvn == null || otp == null || bvn.isBlank() || otp.isBlank()) {
//        return false;
//    }
//
//    // Find the most recent follow-up record for this BVN
//    AgentFollowUp followUp = repository.findTopByBvnOrderByCreatedAtDesc(bvn)
//            .orElse(null);
//
//    if (followUp == null) {
//        return false;
//    }
//
//    // Check if OTP was sent
//    if (!followUp.isOtpSent()) {
//        return false;
//    }
//
//    // Check if OTP matches
//    if (!otp.equals(followUp.getApprovalPin())) {
//        return false;
//    }
//
//    // Mark as verified
//    followUp.setOtpVerified(true);
//    repository.save(followUp);
//
//    return true;
//}






















//package com.koolboks.creditProject.service;
//
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import org.springframework.web.multipart.MultipartFile;
//
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.koolboks.creditProject.dto.AgentFollowUpRequest;
//import com.koolboks.creditProject.entity.AgentFollowUp;
//import com.koolboks.creditProject.repository.AgentFollowUpRepository;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class AgentFollowUpService {
//
//    private String saveUtilityBill(MultipartFile file) {
//    try {
//        String uploadDir = "uploads/utility-bills/";
//        Files.createDirectories(Paths.get(uploadDir));
//
//        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
//        Path filePath = Paths.get(uploadDir + fileName);
//
//        Files.write(filePath, file.getBytes());
//
//        return filePath.toString(); // Store path in DB
//    } catch (Exception e) {
//        throw new RuntimeException("Failed to save utility bill file", e);
//    }
//}
//
//
//
//
//
//    private final MonoClientService monoClientService; // or CrcService if you named it that
//    private final AgentFollowUpRepository repository;
//    private final FollowUpEmailService emailService;
//    private final OtpService otpService;
//
//    @Value("${dti.threshold.green:40}")
//    private int greenThreshold;
//
//    @Value("${dti.threshold.amber:60}")
//    private int amberThreshold;
//
//    public AgentFollowUpService(MonoClientService monoClientService,
//                                AgentFollowUpRepository repository,
//                                FollowUpEmailService emailService,
//                                OtpService otpService) {
//        this.monoClientService = monoClientService;
//        this.repository = repository;
//        this.emailService = emailService;
//        this.otpService = otpService;
//    }
//
//    public FollowUpResult processFollowUp(AgentFollowUpRequest req) {
//        // 1. Fetch CRC (Mono) as JsonNode
//        JsonNode crcJson = monoClientService.fetchCrcByBvn(req.getBvn());
//
//        // Save full JSON for audit
//        String fullResponse = crcJson == null ? "{}" : crcJson.toString();
//
//        // 2. Extract credit_history and compute obligations
//        double monthlyObligations = 0.0;
//        boolean hasCreditHistory = false;
//        boolean anyNonPerformingOpenLoan = false;
//
//        JsonNode creditHistoryNode = crcJson == null ? null : crcJson.path("data").path("credit_history");
//        if (creditHistoryNode != null && creditHistoryNode.isArray() && creditHistoryNode.size() > 0) {
//            hasCreditHistory = true;
//            for (JsonNode inst : creditHistoryNode) {
//                JsonNode historyArr = inst.path("history");
//                if (historyArr != null && historyArr.isArray()) {
//                    for (JsonNode loan : historyArr) {
//                        String loanStatus = loan.path("loan_status").asText("");
//                        String perfStatus = loan.path("performance_status").asText("");
//                        double repayAmount = loan.path("repayment_amount").asDouble(0.0);
//
//                        if ("open".equalsIgnoreCase(loanStatus) && !"performing".equalsIgnoreCase(perfStatus)) {
//                            anyNonPerformingOpenLoan = true;
//                        }
//
//                        if ("open".equalsIgnoreCase(loanStatus) && "performing".equalsIgnoreCase(perfStatus)) {
//                            monthlyObligations += repayAmount;
//                        }
//                    }
//                }
//            }
//        }
//
//        // 3. If any non-performing open loan => decline
//        if (anyNonPerformingOpenLoan) {
//            AgentFollowUp decl = mapToEntity(req, 0.0, "RED", false);
//            decl.setMonoFullResponse(fullResponse);
//            repository.save(decl);
//            emailService.sendCrcReportEmail(req, crcJson, "RED", 0.0);
//            return new FollowUpResult("RED", 0.0, false, "Found non-performing/open loans");
//        }
//
//        // 4. Pick monthly base depending on usageType
//        double monthlyBase = 0.0;
//        if ("Personal".equalsIgnoreCase(req.getUsageType())) {
//            monthlyBase = req.getMonthlyIncome() == null ? 0.0 : req.getMonthlyIncome();
//        } else {
//            monthlyBase = req.getMonthlySales() == null ? 0.0 : req.getMonthlySales();
//        }
//
//        if (monthlyBase <= 0.0) {
//            AgentFollowUp amber = mapToEntity(req, 0.0, "AMBER", false);
//            amber.setMonoFullResponse(fullResponse);
//            repository.save(amber);
//            emailService.sendCrcReportEmail(req, crcJson, "AMBER", 0.0);
//            return new FollowUpResult("AMBER", 0.0, false, "Missing or zero income/sales to compute DTI");
//        }
//
//        // 5. Compute DTI (percentage)
//        double dtiPercent = (monthlyObligations / monthlyBase) * 100.0;
//
//        // 6. Classification thresholds
//        String classification;
//        boolean eligibleForOtp = false;
//
//        if (!hasCreditHistory) {
//            classification = "GREEN";
//            eligibleForOtp = true;
//        } else {
//            if (dtiPercent <= greenThreshold) {
//                classification = "GREEN";
//                eligibleForOtp = true;
//            } else if (dtiPercent <= amberThreshold) {
//                classification = "AMBER";
//            } else {
//                classification = "RED";
//            }
//        }
//
//        // 7. Persist follow up
//        AgentFollowUp entity = mapToEntity(req, dtiPercent, classification, false);
//        entity.setMonoFullResponse(fullResponse);
//        repository.save(entity);
//
//        // 8. Email CRC JSON for records (always)
//        emailService.sendCrcReportEmail(req, crcJson, classification, dtiPercent);
//
//        // 9. If eligible => send OTP (generate 6-digit pin and send SMS via OTP service)
//        boolean otpSent = false;
//        if (eligibleForOtp) {
//            String formattedPhone = formatPhone(req.getMobileNumber());
//            String pin = otpService.sendOtp(formattedPhone); // returns the generated pin on success, null on fail
//            if (pin != null) {
//                otpSent = true;
//                entity.setApprovalPin(pin);
//                entity.setOtpSent(true);
//                repository.save(entity);
//            }
//        }
//
//        return new FollowUpResult(classification, dtiPercent, otpSent, null);
//    }
//
//    private String formatPhone(String phone) {
//        if (phone == null) return null;
//        phone = phone.trim();
//        if (phone.startsWith("0")) return "+234" + phone.substring(1);
//        if (!phone.startsWith("+")) return "+" + phone;
//        return phone;
//    }
//
//    private AgentFollowUp mapToEntity(AgentFollowUpRequest req, double dtiPercent, String classification, boolean otpSent) {
//
//
//        AgentFollowUp e = new AgentFollowUp();
//        e.setBvn(req.getBvn());
//        e.setNin(req.getNin());
//        e.setMobileNumber(req.getMobileNumber());
//        e.setUsageType(req.getUsageType());
//        e.setHomeAddress(req.getHomeAddress());
//        e.setWorkAddress(req.getWorkAddress());
//        e.setStoreAddress(req.getStoreAddress());
//        e.setMonthlyIncome(req.getMonthlyIncome());
//        e.setMonthlySales(req.getMonthlySales());
//        e.setPlan(req.getPlan());
//        e.setInstallmentOption(req.getInstallmentOption());
//        //e.setUtilityBillFilePath(req.getUtilityBillFilePath());
//
//
//        if (req.getUtilityBill() != null && !req.getUtilityBill().isEmpty()) {
//        String path = saveUtilityBill(req.getUtilityBill());
//        e.setUtilityBillFilePath(path);
//    }
//
//
//        e.setDti(dtiPercent);
//        e.setCreditStatus(classification);
//        e.setOtpSent(otpSent);
//        return e;
//    }
//
//    // lightweight result DTO
//    public static class FollowUpResult {
//        private final String classification;
//        private final double dti;
//        private final boolean otpSent;
//        private final String reason;
//
//        public FollowUpResult(String classification, double dti, boolean otpSent, String reason) {
//            this.classification = classification;
//            this.dti = dti;
//            this.otpSent = otpSent;
//            this.reason = reason;
//        }
//
//        public String getClassification() { return classification; }
//        public double getDti() { return dti; }
//        public boolean isOtpSent() { return otpSent; }
//        public String getReason() { return reason; }
//    }
//}
//
//

















//package com.koolboks.creditProject.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.koolboks.creditProject.dto.AgentFollowUpRequest;
//import com.koolboks.creditProject.entity.AgentFollowUp;
//import com.koolboks.creditProject.repository.AgentFollowUpRepository;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.Iterator;
//
//@Service
//public class AgentFollowUpService {
//
//    private final CrcService crcService;
//    private final AgentFollowUpRepository repository;
//    private final FollowUpEmailService emailService;
//    private final OtpService otpService;
//
//    @Value("${dti.threshold.green:40}")
//    private int greenThreshold;
//
//    @Value("${dti.threshold.amber:60}")
//    private int amberThreshold;
//
//    public AgentFollowUpService(CrcService crcService,
//                                AgentFollowUpRepository repository,
//                                FollowUpEmailService emailService,
//                                OtpService otpService) {
//        this.crcService = crcService;
//        this.repository = repository;
//        this.emailService = emailService;
//        this.otpService = otpService;
//    }
//
//    public FollowUpResult processFollowUp(AgentFollowUpRequest req) {
//        // 1. Fetch CRC
//        JsonNode crcJson = crcService.fetchCrcByBvn(req.getBvn());
//
//        // 2. Determine credit history and sum obligations from open performing loans
//        double monthlyObligations = 0.0;
//        boolean hasCreditHistory = false;
//        boolean anyNonPerformingOpenLoan = false;
//
//        JsonNode creditHistoryNode = crcJson.path("data").path("credit_history");
//        if (creditHistoryNode.isArray() && creditHistoryNode.size() > 0) {
//            hasCreditHistory = true;
//            // credit_history is array of institutions -> each has history array
//            for (JsonNode inst : creditHistoryNode) {
//                JsonNode historyArr = inst.path("history");
//                if (historyArr.isArray()) {
//                    for (JsonNode loan : historyArr) {
//                        String loanStatus = loan.path("loan_status").asText("");
//                        String perfStatus = loan.path("performance_status").asText("");
//                        double repayAmount = loan.path("repayment_amount").asDouble(0.0);
//
//                        // If loan is open and not performing -> we mark non-performing
//                        if ("open".equalsIgnoreCase(loanStatus) && !"performing".equalsIgnoreCase(perfStatus)) {
//                            anyNonPerformingOpenLoan = true;
//                        }
//
//                        // If open and performing -> add to obligations
//                        if ("open".equalsIgnoreCase(loanStatus) && "performing".equalsIgnoreCase(perfStatus)) {
//                            monthlyObligations += repayAmount;
//                        }
//                    }
//                }
//            }
//        }
//
//        // 3. Decide eligibility per your rule:
//        // If no credit history -> eligible (we treat as green candidate)
//        // If any open loan not performing -> treat as decline (RED)
//        if (anyNonPerformingOpenLoan) {
//            // Save result and email
//            AgentFollowUp entity = mapToEntity(req, 0.0, "RED", false);
//            repository.save(entity);
//            emailService.sendCrcReportEmail(req, crcJson, "RED", 0.0);
//            return new FollowUpResult("RED", 0.0, false, "Found non-performing/open loans");
//        }
//
//        // 4. Determine monthly income base depending on usage
//        double monthlyBase = 0.0;
//        if ("Personal".equalsIgnoreCase(req.getUsage())) {
//            monthlyBase = req.getMonthlyGrossIncome() == null ? 0.0 : req.getMonthlyGrossIncome();
//        } else {
//            // Commercial
//            monthlyBase = req.getMonthlySales() == null ? 0.0 : req.getMonthlySales();
//        }
//
//        // Avoid division by zero
//        if (monthlyBase <= 0.0) {
//            // can't compute DTI properly => mark as REVIEW (AMBER)
//            AgentFollowUp entity = mapToEntity(req, 0.0, "AMBER", false);
//            repository.save(entity);
//            emailService.sendCrcReportEmail(req, crcJson, "AMBER", 0.0);
//            return new FollowUpResult("AMBER", 0.0, false, "Missing or zero income/sales to compute DTI");
//        }
//
//        // Add obligations (open performing loans) to monthly obligations (already summed)
//        double dti = (monthlyObligations / monthlyBase) * 100.0;
//
//        // 5. Classification thresholds
//        String classification;
//        boolean eligibleForOtp = false;
//
//        if (!hasCreditHistory) {
//            classification = "GREEN";
//            eligibleForOtp = true;
//        } else {
//            if (dti <= greenThreshold) {
//                classification = "GREEN";
//                eligibleForOtp = true;
//            } else if (dti <= amberThreshold) {
//                classification = "AMBER";
//                eligibleForOtp = false;
//            } else {
//                classification = "RED";
//                eligibleForOtp = false;
//            }
//        }
//
//        // 6. Persist follow up
//        AgentFollowUp entity = mapToEntity(req, dti, classification, false);
//        repository.save(entity);
//
//        // 7. Send CRC JSON to admin email for records (always)
//        emailService.sendCrcReportEmail(req, crcJson, classification, dti);
//
//        // 8. If eligible => send OTP (Twilio Verify)
//        boolean otpSent = false;
//        if (eligibleForOtp) {
//            String formattedPhone = formatPhone(req.getMobileNumber());
//            String sendStatus = otpService.sendOtp(formattedPhone); // returns "pending"
//            if (sendStatus != null && sendStatus.equalsIgnoreCase("pending")) {
//                otpSent = true;
//                entity.setOtpSent(true);
//                repository.save(entity); // update OTP flag
//            }
//        }
//
//        return new FollowUpResult(classification, dti, otpSent, null);
//    }
//
//    private String formatPhone(String phone) {
//        if (phone == null) return phone;
//        phone = phone.trim();
//        if (phone.startsWith("0")) return "+234" + phone.substring(1);
//        if (!phone.startsWith("+")) return "+" + phone;
//        return phone;
//    }
//
//    private AgentFollowUp mapToEntity(AgentFollowUpRequest req, double dti, String classification, boolean otpSent) {
//        AgentFollowUp e = new AgentFollowUp();
//        e.setBvn(req.getBvn());
//        e.setMobileNumber(req.getMobileNumber());
//        e.setUsage(req.getUsage());
//        e.setHomeAddress(req.getHomeAddress());
//        e.setWorkAddress(req.getWorkAddress());
//        e.setStoreAddress(req.getStoreAddress());
//        e.setMonthlyGrossIncome(req.getMonthlyGrossIncome());
//        e.setMonthlySales(req.getMonthlySales());
//        e.setDtiPercentage(dti);
//        e.setClassification(classification);
//        e.setOtpSent(otpSent);
//        // Note: utilityBillPath handling (store file) not implemented here; you can add file storage logic
//        return e;
//    }
//
//    // Small result carrier
//    public static class FollowUpResult {
//        private final String classification;
//        private final double dti;
//        private final boolean otpSent;
//        private final String reason;
//
//        public FollowUpResult(String classification, double dti, boolean otpSent, String reason) {
//            this.classification = classification;
//            this.dti = dti;
//            this.otpSent = otpSent;
//            this.reason = reason;
//        }
//
//        public String getClassification() { return classification; }
//        public double getDti() { return dti; }
//        public boolean isOtpSent() { return otpSent; }
//        public String getReason() { return reason; }
//    }
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
////package com.koolboks.creditProject.service;
////
////import com.koolboks.creditProject.dto.AgentFollowUpRequest;
////import com.koolboks.creditProject.entity.AgentFollowUp;
////import com.koolboks.creditProject.repository.AgentFollowUpRepository;
////import org.slf4j.Logger;
////import org.slf4j.LoggerFactory;
////import org.springframework.stereotype.Service;
////
////import java.util.*;
////
////@Service
////public class AgentFollowUpService {
////
////    private static final Logger log = LoggerFactory.getLogger(AgentFollowUpService.class);
////
////    private final AgentFollowUpRepository repository;
////    private final MonoClientService monoClientService;
////    private final SMSService smsService;
////    private final AgentFollowUpEmailService emailService;
////
////    public AgentFollowUpService(AgentFollowUpRepository repository,
////                                MonoClientService monoClientService,
//                                SMSService smsService,
//                                AgentFollowUpEmailService emailService) {
//        this.repository = repository;
//        this.monoClientService = monoClientService;
//        this.smsService = smsService;
//        this.emailService = emailService;
//    }
//
//    public AgentFollowUp process(AgentFollowUpRequest req) {
//        AgentFollowUp followUp = new AgentFollowUp();
//        followUp.setBvn(req.getBvn());
//        followUp.setNin(req.getNin());
//        followUp.setMobileNumber(req.getMobileNumber());
//        followUp.setUsageType(req.getUsageType());
//        followUp.setHomeAddress(req.getHomeAddress());
//        followUp.setWorkAddress(req.getWorkAddress());
//        followUp.setStoreAddress(req.getStoreAddress());
//        followUp.setMonthlyIncome(req.getMonthlyIncome());
//        followUp.setMonthlySales(req.getMonthlySales());
//        followUp.setPlan(req.getPlan());
//        followUp.setInstallmentOption(req.getInstallmentOption());
//        followUp.setUtilityBillFilePath(req.getUtilityBillFilePath());
//
//        // 1) call mono
//        Map<String, Object> monoResp = monoClientService.fetchCreditReportByBvn(req.getBvn());
//        String monoRespString = monoResp == null ? "" : monoResp.toString();
//        followUp.setMonoFullResponse(monoRespString);
//
//        // 2) parse credit history
//        double totalLiabilities = 0.0;
//        boolean hasNonPerforming = false;
//        Object dataObj = monoResp.get("data");
//        if (dataObj instanceof Map) {
//            Map<String, Object> dataMap = (Map<String, Object>) dataObj;
//            Object creditHistoryObj = dataMap.get("credit_history");
//            if (creditHistoryObj instanceof List) {
//                List<Map<String, Object>> creditHistory = (List<Map<String, Object>>) creditHistoryObj;
//                for (Map<String, Object> inst : creditHistory) {
//                    Object historyObj = inst.get("history");
//                    if (historyObj instanceof List) {
//                        List<Map<String, Object>> historyList = (List<Map<String, Object>>) historyObj;
//                        for (Map<String, Object> loan : historyList) {
//                            String loanStatus = safeToString(loan.get("loan_status"));
//                            String perf = safeToString(loan.get("performance_status"));
//                            double repayment = 0.0;
//                            Object repObj = loan.get("repayment_amount");
//                            if (repObj instanceof Number) {
//                                repayment = ((Number) repObj).doubleValue();
//                            } else {
//                                try {
//                                    repayment = Double.parseDouble(safeToString(repObj));
//                                } catch (Exception ignored) {}
//                            }
//
//                            // If loan is open AND performing -> add to liabilities
//                            if ("open".equalsIgnoreCase(loanStatus) && "performing".equalsIgnoreCase(perf)) {
//                                totalLiabilities += repayment;
//                            } else {
//                                // any loan that is not open+performing sets non-performing flag
//                                hasNonPerforming = true;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        // 3) pick income base based on usage
//        double incomeBase = 0.0;
//        if ("Personal".equalsIgnoreCase(req.getUsageType())) {
//            incomeBase = req.getMonthlyIncome() != null ? req.getMonthlyIncome() : 0.0;
//        } else if ("Commercial".equalsIgnoreCase(req.getUsageType())) {
//            incomeBase = req.getMonthlySales() != null ? req.getMonthlySales() : 0.0;
//        }
//
//        double dti = 0.0;
//        if (incomeBase > 0) {
//            dti = totalLiabilities / incomeBase;
//        } else {
//            // If no income base, set DTI high so it's treated as risky
//            dti = totalLiabilities > 0 ? Double.POSITIVE_INFINITY : 0.0;
//        }
//        followUp.setDti(dti);
//
//        // 4) classification rules
//        String classification;
//        if (hasNonPerforming) {
//            classification = "Red";
//        } else {
//            if (Double.isInfinite(dti)) {
//                classification = "Red";
//            } else if (dti < 0.4) {
//                classification = "Green";
//            } else if (dti >= 0.4 && dti < 0.6) {
//                classification = "Amber";
//            } else {
//                classification = "Red";
//            }
//        }
//        followUp.setCreditStatus(classification);
//
//        // 5) generate PIN & send SMS if Green
//        if ("Green".equalsIgnoreCase(classification)) {
//            String pin = generatePin();
//            followUp.setApprovalPin(pin);
//            try {
//                smsService.sendPin(req.getMobileNumber(), pin);
//            } catch (Exception ex) {
//                log.error("Failed to send PIN SMS", ex);
//                // still continue; we save the pin and email admin
//            }
//        }
//
//        // 6) save and email report
//        AgentFollowUp saved = repository.save(followUp);
//
//        try {
//            emailService.sendCreditReportEmail(saved, monoRespString);
//        } catch (Exception ex) {
//            log.error("Email send failed", ex);
//        }
//
//        return saved;
//    }
//
//    private String safeToString(Object o) {
//        return o == null ? "" : o.toString();
//    }
//
//    private String generatePin() {
//        int v = new Random().nextInt(900_000) + 100_000;
//        return String.valueOf(v);
//    }
//}

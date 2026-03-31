package com.koolboks.creditProject.controller;

import com.koolboks.creditProject.dto.AgentFollowUpRequest;
import com.koolboks.creditProject.dto.OtpVerificationRequest;
import com.koolboks.creditProject.entity.AgentFollowUp;
import com.koolboks.creditProject.repository.AgentFollowUpRepository;
import com.koolboks.creditProject.service.AdminCreditReviewService;
import com.koolboks.creditProject.service.AgentFollowUpService;
import com.koolboks.creditProject.service.AgentFollowUpService.FollowUpResult;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/agent-followup")
public class AgentFollowUpController {

    private final AgentFollowUpService      followUpService;
    private final AgentFollowUpRepository   agentFollowUpRepository;
    private final AdminCreditReviewService  adminCreditReviewService;

    public AgentFollowUpController(AgentFollowUpService followUpService,
                                   AgentFollowUpRepository agentFollowUpRepository,
                                   AdminCreditReviewService adminCreditReviewService) {
        this.followUpService         = followUpService;
        this.agentFollowUpRepository = agentFollowUpRepository;
        this.adminCreditReviewService = adminCreditReviewService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitFollowUp(
            @ModelAttribute AgentFollowUpRequest req,
            @RequestParam(value = "monoFinancialDataJson",   required = false)                    String  monoFinancialDataJson,
            @RequestParam(value = "firstName",               required = false)                    String  firstName,
            @RequestParam(value = "lastName",                required = false)                    String  lastName,
            @RequestParam(value = "creditReportJson",        required = false)                    String  creditReportJson,
            @RequestParam(value = "holdOtpForAdminReview",   required = false, defaultValue = "false") boolean holdOtpForAdminReview
    ) {
        try {
            // ── Validation ────────────────────────────────────────────────────
            if (req.getBvn() == null || req.getBvn().isBlank()) {
                return ResponseEntity.badRequest().body(error("BVN is required"));
            }

            // ── Populate extra fields onto the DTO ────────────────────────────
            req.setFirstName(firstName);
            req.setLastName(lastName);
            req.setMonoFinancialDataJson(monoFinancialDataJson);
            req.setCreditReportJson(creditReportJson);

            // ── Process follow-up (DTI, CRC, classification — NO OTP yet) ────
            FollowUpResult result = followUpService.processFollowUp(req, monoFinancialDataJson);

            // ── Fetch the entity just saved by processFollowUp ────────────────
            AgentFollowUp savedEntity = agentFollowUpRepository
                    .findTopByBvnOrderByCreatedAtDesc(req.getBvn())
                    .orElseThrow(() -> new RuntimeException("Follow-up record not found after save"));

            // ── Persist the credit report JSON for the admin review page ──────
            if (creditReportJson != null && !creditReportJson.isBlank()) {
                savedEntity.setCreditReportJson(creditReportJson);
                agentFollowUpRepository.save(savedEntity);
            }

            // ── Generate review token & email admin (replaces direct OTP send) ─
            adminCreditReviewService.generateAndSendReviewEmail(savedEntity);

            // ── Return to frontend — OTP is always held until admin approves ──
            Map<String, Object> response = new HashMap<>();
            response.put("success",        true);
            response.put("classification", result.getClassification());
            response.put("dti",            result.getDti());
            response.put("otpSent",        false);   // always false — OTP held for admin
            response.put("message",        "Application submitted. Awaiting admin review.");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(error("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        if (request.getBvn() == null || request.getBvn().isBlank()) {
            return ResponseEntity.badRequest().body(error("BVN is required"));
        }
        if (request.getOtp() == null || request.getOtp().isBlank() || request.getOtp().length() != 6) {
            return ResponseEntity.badRequest().body(error("Valid 6-digit OTP is required"));
        }

        boolean isValid = followUpService.verifyOtp(request.getBvn(), request.getOtp());

        if (isValid) {
            Map<String, Object> response = new HashMap<>();
            response.put("success",  true);
            response.put("message",  "OTP verified successfully");
            response.put("verified", true);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(400).body(error("Invalid OTP code. Please try again."));
        }
    }

    // ── Also make sure AgentFollowUpService.processFollowUp() does NOT ────────
    // ── send OTP anymore — that is now handled by AdminCreditReviewService ────
    // ── (in the APPROVE branch of processReviewDecision).                 ────

    private Map<String, Object> error(String message) {
        Map<String, Object> err = new HashMap<>();
        err.put("success", false);
        err.put("message", message);
        return err;
    }
}




















//package com.koolboks.creditProject.controller;
//
//import com.koolboks.creditProject.dto.AgentFollowUpRequest;
//import com.koolboks.creditProject.dto.OtpVerificationRequest;
//import com.koolboks.creditProject.entity.AgentFollowUp;
//import com.koolboks.creditProject.service.AgentFollowUpService;
//import com.koolboks.creditProject.service.AgentFollowUpService.FollowUpResult;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
//
//
//import org.springframework.http.MediaType;
//
//
//@RestController
//@RequestMapping("/api/agent-followup")
//public class AgentFollowUpController {
//
//    private final AgentFollowUpService followUpService;
//
//
//
//    public AgentFollowUpController(AgentFollowUpService followUpService) {
//        this.followUpService = followUpService;
//    }
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> submitFollowUp(
//            @ModelAttribute AgentFollowUpRequest req,
//            @RequestParam(value = "monoFinancialDataJson", required = false) String monoFinancialDataJson,
//            @RequestParam(value = "firstName", required = false) String firstName,
//            @RequestParam(value = "lastName", required = false) String lastName,
//            @RequestParam(value = "creditReportJson", required = false) String creditReportJson,
//            @RequestParam(value = "holdOtpForAdminReview", required = false, defaultValue = "false") boolean holdOtpForAdminReview
//    ) {
//        try {
//            // Validation
//            if (req.getBvn() == null || req.getBvn().isBlank()) {
//                return ResponseEntity.badRequest().body(createErrorResponse("BVN is required"));
//            }
//
//            // Manually set extra fields from RequestParams into the DTO
//            req.setMonoFinancialDataJson(monoFinancialDataJson);
//            req.setFirstName(firstName);
//            req.setLastName(lastName);
//
//            // Store the credit report JSON on the request or pass separately
//            req.setCreditReportJson(creditReportJson);
//
//            // Call service with the extra JSON parameter
//            FollowUpResult result = followUpService.processFollowUp(req, monoFinancialDataJson);
//
//            // Call the modified processFollowUp which NO LONGER sends OTP
//            FollowUpResult _result = agentFollowUpService.processFollowUp(req);
//
//        // Fetch the saved entity and trigger admin review email
//        AgentFollowUp savedEntity = agentFollowUpRepository
//                .findTopByBvnOrderByCreatedAtDesc(req.getBvn())
//                .orElseThrow();
//
//        // Store credit report JSON if provided
//        if (creditReportJson != null && !creditReportJson.isBlank()) {
//            savedEntity.setCreditReportJson(creditReportJson);
//            agentFollowUpRepository.save(savedEntity);
//        }
//
//            // Build Response
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("classification", result.getClassification());
//            response.put("dti", result.getDti());
//            response.put("otpSent", result.isOtpSent());
//            response.put("message", result.getReason() != null ? result.getReason() : "Follow-up processed successfully");
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body(createErrorResponse("Error: " + e.getMessage()));
//        }
//    }
//
//    @PostMapping("/verify-otp")
//    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
//        if (request.getBvn() == null || request.getBvn().isBlank()) {
//            return ResponseEntity.badRequest().body(createErrorResponse("BVN is required"));
//        }
//        if (request.getOtp() == null || request.getOtp().isBlank() || request.getOtp().length() != 6) {
//            return ResponseEntity.badRequest().body(createErrorResponse("Valid 6-digit OTP is required"));
//        }
//
//        boolean isValid = followUpService.verifyOtp(request.getBvn(), request.getOtp());
//
//        if (isValid) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "OTP verified successfully");
//            response.put("verified", true);
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.status(400).body(createErrorResponse("Invalid OTP code. Please try again."));
//        }
//    }
//
//    private Map<String, Object> createErrorResponse(String message) {
//        Map<String, Object> error = new HashMap<>();
//        error.put("success", false);
//        error.put("message", message);
//        return error;
//    }
//}
//
//





//@RestController
//@RequestMapping("/api/agent-followup")
////@CrossOrigin(origins = "") // Allow CORS from React app
//public class AgentFollowUpController {
//
//    private final AgentFollowUpService followUpService;
//
//    public AgentFollowUpController(AgentFollowUpService followUpService) {
//        this.followUpService = followUpService;
//    }
//
//    //  Accept multipart/form-data explicitly
//    @PostMapping(consumes = "multipart/form-data")
//    public ResponseEntity<?> submitFollowUp(@ModelAttribute AgentFollowUpRequest req) {
//        try {
//            // Basic validation
//            if (req.getBvn() == null || req.getBvn().isBlank()) {
//                return ResponseEntity.badRequest().body(createErrorResponse("BVN is required"));
//            }
//
//            System.out.println("Received follow-up for BVN: " + req.getBvn());
//            System.out.println("Usage Type: " + req.getUsageType());
//
//            FollowUpResult result = followUpService.processFollowUp(req);
//
//            //  Return proper JSON response with otpSent field
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("classification", result.getClassification());
//            response.put("dti", result.getDti());
//            response.put("otpSent", result.isOtpSent());
//            response.put("message", result.getReason() != null ? result.getReason() : "Follow-up processed successfully");
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500).body(createErrorResponse("Error: " + e.getMessage()));
//        }
//    }
//
//    //  Remove duplicate /api/agent-followup from path
//    @PostMapping("/verify-otp")
//    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
//        // Validate input
//        if (request.getBvn() == null || request.getBvn().isBlank()) {
//            return ResponseEntity.badRequest().body(createErrorResponse("BVN is required"));
//        }
//
//        if (request.getOtp() == null || request.getOtp().isBlank()) {
//            return ResponseEntity.badRequest().body(createErrorResponse("OTP is required"));
//        }
//
//        if (request.getOtp().length() != 6) {
//            return ResponseEntity.badRequest().body(createErrorResponse("OTP must be 6 digits"));
//        }
//
//        // Verify OTP
//        boolean isValid = followUpService.verifyOtp(request.getBvn(), request.getOtp());
//
//        if (isValid) {
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "OTP verified successfully");
//            response.put("verified", true);
//            return ResponseEntity.ok(response);
//        } else {
//            return ResponseEntity.status(400).body(createErrorResponse("Invalid OTP code. Please try again."));
//        }
//    }
//
//    // Helper method to create error response
//    private Map<String, Object> createErrorResponse(String message) {
//        Map<String, Object> error = new HashMap<>();
//        error.put("success", false);
//        error.put("message", message);
//        return error;
//    }
//}












package com.koolboks.creditProject.controller;

import com.koolboks.creditProject.dto.AgentFollowUpRequest;
import com.koolboks.creditProject.dto.OtpVerificationRequest;
import com.koolboks.creditProject.service.AgentFollowUpService;
import com.koolboks.creditProject.service.AgentFollowUpService.FollowUpResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;




import org.springframework.http.MediaType;


@RestController
@RequestMapping("/api/agent-followup")
public class AgentFollowUpController {

    private final AgentFollowUpService followUpService;

    public AgentFollowUpController(AgentFollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitFollowUp(
            @ModelAttribute AgentFollowUpRequest req,
            @RequestParam(value = "monoFinancialDataJson", required = false) String monoFinancialDataJson,
            @RequestParam(value = "firstName", required = false) String firstName,
            @RequestParam(value = "lastName", required = false) String lastName
    ) {
        try {
            // Validation
            if (req.getBvn() == null || req.getBvn().isBlank()) {
                return ResponseEntity.badRequest().body(createErrorResponse("BVN is required"));
            }

            // Manually set extra fields from RequestParams into the DTO
            req.setMonoFinancialDataJson(monoFinancialDataJson);
            req.setFirstName(firstName);
            req.setLastName(lastName);

            // Call service with the extra JSON parameter
            FollowUpResult result = followUpService.processFollowUp(req, monoFinancialDataJson);

            // Build Response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("classification", result.getClassification());
            response.put("dti", result.getDti());
            response.put("otpSent", result.isOtpSent());
            response.put("message", result.getReason() != null ? result.getReason() : "Follow-up processed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(createErrorResponse("Error: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        if (request.getBvn() == null || request.getBvn().isBlank()) {
            return ResponseEntity.badRequest().body(createErrorResponse("BVN is required"));
        }
        if (request.getOtp() == null || request.getOtp().isBlank() || request.getOtp().length() != 6) {
            return ResponseEntity.badRequest().body(createErrorResponse("Valid 6-digit OTP is required"));
        }

        boolean isValid = followUpService.verifyOtp(request.getBvn(), request.getOtp());

        if (isValid) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "OTP verified successfully");
            response.put("verified", true);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(400).body(createErrorResponse("Invalid OTP code. Please try again."));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }
}







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












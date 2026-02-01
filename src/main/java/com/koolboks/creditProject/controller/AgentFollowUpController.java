package com.koolboks.creditProject.controller;

import com.koolboks.creditProject.dto.AgentFollowUpRequest;
import com.koolboks.creditProject.dto.OtpVerificationRequest;
import com.koolboks.creditProject.service.AgentFollowUpService;
import com.koolboks.creditProject.service.AgentFollowUpService.FollowUpResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/agent-followup")
//@CrossOrigin(origins = "") // Allow CORS from React app
public class AgentFollowUpController {

    private final AgentFollowUpService followUpService;

    public AgentFollowUpController(AgentFollowUpService followUpService) {
        this.followUpService = followUpService;
    }

    //  Accept multipart/form-data explicitly
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> submitFollowUp(@ModelAttribute AgentFollowUpRequest req) {
        try {
            // Basic validation
            if (req.getBvn() == null || req.getBvn().isBlank()) {
                return ResponseEntity.badRequest().body(createErrorResponse("BVN is required"));
            }

            System.out.println("Received follow-up for BVN: " + req.getBvn());
            System.out.println("Usage Type: " + req.getUsageType());

            FollowUpResult result = followUpService.processFollowUp(req);

            //  Return proper JSON response with otpSent field
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

    //  Remove duplicate /api/agent-followup from path
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequest request) {
        // Validate input
        if (request.getBvn() == null || request.getBvn().isBlank()) {
            return ResponseEntity.badRequest().body(createErrorResponse("BVN is required"));
        }

        if (request.getOtp() == null || request.getOtp().isBlank()) {
            return ResponseEntity.badRequest().body(createErrorResponse("OTP is required"));
        }

        if (request.getOtp().length() != 6) {
            return ResponseEntity.badRequest().body(createErrorResponse("OTP must be 6 digits"));
        }

        // Verify OTP
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

    // Helper method to create error response
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        return error;
    }
}










//package com.koolboks.creditProject.controller;
//
//import com.koolboks.creditProject.dto.AgentFollowUpRequest;
//import com.koolboks.creditProject.service.AgentFollowUpService;
//import com.koolboks.creditProject.service.AgentFollowUpService.FollowUpResult;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import com.koolboks.creditProject.dto.OtpVerificationRequest;
//
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/agent-followup")
//public class AgentFollowUpController {
//
//    private final AgentFollowUpService followUpService;
//
//    public AgentFollowUpController(AgentFollowUpService followUpService) {
//        this.followUpService = followUpService;
//    }
//
//    @PostMapping
//    public ResponseEntity<?> submitFollowUp(@ModelAttribute AgentFollowUpRequest req) {
//        // Basic validation
//        if (req.getBvn() == null || req.getBvn().isBlank()) {
//            return ResponseEntity.badRequest().body("BVN is required");
//        }
//        FollowUpResult result = followUpService.processFollowUp(req);
//        return ResponseEntity.ok(result);
//    }
//
//
//    // NEW ENDPOINT: Verify OTP
//    @PostMapping("/api/agent-followup/verify-otp")
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
//        error.put("verified", false);
//        return error;
//    }
//}
//
//




//package com.koolboks.creditProject.controller;
//
//import com.koolboks.creditProject.dto.AgentFollowUpRequest;
//import com.koolboks.creditProject.service.AgentFollowUpService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//@RestController
//@RequestMapping("/api/agent-followup")
//public class AgentFollowUpController {
//
//    private final AgentFollowUpService followUpService;
//
//    public AgentFollowUpController(AgentFollowUpService followUpService) {
//        this.followUpService = followUpService;
//    }
//
//    /**
//     * Accepts multipart/form-data. Field names must match AgentFollowUpRequest properties.
//     */
//    @PostMapping(consumes = {"multipart/form-data"})
//    public ResponseEntity<?> submitFollowUp(
//            @RequestParam("bvn") String bvn,
//            @RequestParam("mobileNumber") String mobileNumber,
//            @RequestParam("usage") String usage,
//            @RequestParam(value = "homeAddress", required = false) String homeAddress,
//            @RequestParam(value = "workAddress", required = false) String workAddress,
//            @RequestParam(value = "storeAddress", required = false) String storeAddress,
//            @RequestParam(value = "monthlyGrossIncome", required = false) Double monthlyGrossIncome,
//            @RequestParam(value = "monthlySales", required = false) Double monthlySales,
//            @RequestPart(value = "utilityBill", required = false) MultipartFile utilityBill
//    ) {
//        AgentFollowUpRequest req = new AgentFollowUpRequest();
//        req.setBvn(bvn);
//        req.setMobileNumber(mobileNumber);
//        req.setUsage(usage);
//        req.setHomeAddress(homeAddress);
//        req.setWorkAddress(workAddress);
//        req.setStoreAddress(storeAddress);
//        req.setMonthlyGrossIncome(monthlyGrossIncome);
//        req.setMonthlySales(monthlySales);
//        req.setUtilityBill(utilityBill);
//
//        AgentFollowUpService.FollowUpResult result = followUpService.processFollowUp(req);
//
//        return ResponseEntity.ok().body(result);
//    }
//}
//










//package com.koolboks.creditProject.controller;
//
//import com.koolboks.creditProject.dto.AgentFollowUpRequest;
//import com.koolboks.creditProject.dto.AgentFollowUpResponse;
//import com.koolboks.creditProject.entity.AgentFollowUp;
//import com.koolboks.creditProject.service.AgentFollowUpService;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/agent-followup")
//public class AgentFollowUpController {
//
//    private final AgentFollowUpService service;
//
//    public AgentFollowUpController(AgentFollowUpService service) {
//        this.service = service;
//    }
//
//    /**
//     * Accepts application/json body matching AgentFollowUpRequest.
//     * If you need multipart file uploads for utility bill, implement a second endpoint
//     * that consumes multipart/form-data and handles the MultipartFile separately.
//     */
//    @PostMapping
//    public ResponseEntity<AgentFollowUpResponse> submitFollowUp(@Valid @RequestBody AgentFollowUpRequest request) {
//        AgentFollowUp saved = service.process(request);
//
//        AgentFollowUpResponse resp = new AgentFollowUpResponse();
//        resp.setStatus("success");
//        resp.setCreditStatus(saved.getCreditStatus());
//        resp.setDti(saved.getDti());
//        resp.setPin("Green".equalsIgnoreCase(saved.getCreditStatus()) ? saved.getApprovalPin() : null);
//
//        return ResponseEntity.ok(resp);
//    }
//}

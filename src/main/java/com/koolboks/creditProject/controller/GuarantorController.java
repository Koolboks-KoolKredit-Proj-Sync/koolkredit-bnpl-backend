package com.koolboks.creditProject.controller;

import com.koolboks.creditProject.dto.GuarantorRequest;
import com.koolboks.creditProject.service.GuarantorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/guarantor")
public class GuarantorController {

    private final GuarantorService guarantorService;

    @Value("${frontend.base.url:https://koolkredit-bnpl-frontend.vercel.app}")
    private String frontendBaseUrl;

    public GuarantorController(GuarantorService guarantorService) {
        this.guarantorService = guarantorService;
    }

    /**
     * Step 1: Customer requests guarantor after OTP verification
     */
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> requestGuarantor(@RequestBody GuarantorRequest request) {
        Map<String, Object> response = guarantorService.requestGuarantor(
                request.getCustomerBvn(),
                request.getCustomerFirstName(),
                request.getCustomerLastName(),
                request.getCustomerEmail(),
                request.getCustomerPhoneNumber(),
                request.getCustomerPlan(),
                request.getCustomerInstallmentDuration(),
                request.getGuarantorEmail(),
                request.getStorePrice(),
                request.getProductName(),
                request.getProductBrand(),
                request.getProductSize()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Step 2: Get guarantor data by token (guarantor opens link)
     */
    @GetMapping("/form/{token}")
    public ResponseEntity<Map<String, Object>> getGuarantorData(@PathVariable String token) {
        Map<String, Object> response = guarantorService.getGuarantorDataByToken(token);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 3: Guarantor submits their form
     */
    @PostMapping("/submit/{token}")
    public ResponseEntity<Map<String, Object>> submitGuarantorForm(
            @PathVariable String token,
            @RequestBody GuarantorRequest request) {
        Map<String, Object> response = guarantorService.submitGuarantorForm(token, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 4a: Admin confirms guarantor via email link (GET - browser click)
     * Returns a friendly HTML page instead of raw JSON
     */
    @GetMapping("/confirm/{guarantorId}")
    public ResponseEntity<String> confirmGuarantorViaEmail(@PathVariable Long guarantorId) {
        Map<String, Object> result = guarantorService.confirmGuarantor(guarantorId);

        boolean success = Boolean.TRUE.equals(result.get("success"));
        String message = (String) result.get("message");
        String phone = (String) result.getOrDefault("phoneNumber", "");
        boolean alreadyConfirmed = Boolean.TRUE.equals(result.get("alreadyConfirmed"));

        String icon = success ? "✅" : (alreadyConfirmed ? "ℹ️" : "❌");
        String color = success ? "#27ae60" : (alreadyConfirmed ? "#2980b9" : "#e74c3c");
        String title = success ? "Guarantor Confirmed!" : (alreadyConfirmed ? "Already Confirmed" : "Confirmation Failed");
        String phoneHtml = (success && !phone.isEmpty())
                ? "<p style='color:#555;'>OTP has been sent to: <strong>" + phone + "</strong></p>"
                : "";

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Guarantor Confirmation</title>
                    <style>
                        * { box-sizing: border-box; margin: 0; padding: 0; }
                        body {
                            font-family: Arial, sans-serif;
                            background: #f5f5f5;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                            padding: 20px;
                        }
                        .card {
                            background: white;
                            padding: 50px 40px;
                            border-radius: 12px;
                            text-align: center;
                            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                            max-width: 450px;
                            width: 100%%;
                        }
                        .icon { font-size: 64px; margin-bottom: 20px; }
                        h2 { color: %s; font-size: 24px; margin-bottom: 12px; }
                        .message { color: #666; font-size: 15px; margin-bottom: 16px; line-height: 1.5; }
                        .phone { color: #555; font-size: 14px; margin-top: 8px; }
                        .brand {
                            margin-top: 40px;
                            padding-top: 20px;
                            border-top: 1px solid #eee;
                            color: #aaa;
                            font-size: 12px;
                        }
                    </style>
                </head>
                <body>
                    <div class="card">
                        <div class="icon">%s</div>
                        <h2>%s</h2>
                        <p class="message">%s</p>
                        %s
                        <div class="brand">KoolKredit · Powered by Koolboks</div>
                    </div>
                </body>
                </html>
                """.formatted(color, icon, title, message, phoneHtml);

        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }

    /**
     * Step 4b: Admin confirms guarantor via API call (POST - programmatic)
     */
    @PostMapping("/confirm/{guarantorId}")
    public ResponseEntity<Map<String, Object>> confirmGuarantor(@PathVariable Long guarantorId) {
        Map<String, Object> response = guarantorService.confirmGuarantor(guarantorId);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 5: Guarantor verifies OTP
     */
    @PostMapping("/verify-otp/{token}")
    public ResponseEntity<Map<String, Object>> verifyOtp(
            @PathVariable String token,
            @RequestBody Map<String, String> request) {
        Map<String, Object> response = guarantorService.verifyGuarantorOtp(
                token,
                request.get("otp")
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Step 6: Customer checks guarantor status (polling)
     */
    @GetMapping("/status/{customerBvn}")
    public ResponseEntity<Map<String, Object>> getGuarantorStatus(@PathVariable String customerBvn) {
        Map<String, Object> response = guarantorService.getGuarantorStatus(customerBvn);
        return ResponseEntity.ok(response);
    }

    /**
     * Step 7: Get approved application details by customer BVN
     */
    @GetMapping("/application/success/{customerBvn}")
    public ResponseEntity<Map<String, Object>> getApprovedApplication(@PathVariable String customerBvn) {
        Map<String, Object> response = guarantorService.getApprovedApplication(customerBvn);

        if (Boolean.FALSE.equals(response.get("success"))) {
            String message = (String) response.get("message");
            if ("Application not found".equals(message)) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.status(400).body(response);
        }

        return ResponseEntity.ok(response);
    }
}





























//package com.koolboks.creditProject.controller;
//
//import com.koolboks.creditProject.dto.GuarantorRequest;
//import com.koolboks.creditProject.service.GuarantorService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/guarantor")
////@CrossOrigin(origins = "*")
//public class GuarantorController {
//
//    private final GuarantorService guarantorService;
//
//    public GuarantorController(GuarantorService guarantorService) {
//        this.guarantorService = guarantorService;
//    }
//
//    /**
//     * Step 1: Customer requests guarantor after OTP verification
//     */
//
//    // Replace the old Map-based endpoint with this DTO-based one
//    @PostMapping("/request")
//    public ResponseEntity<Map<String, Object>> requestGuarantor(@RequestBody GuarantorRequest request) {
//        Map<String, Object> response = guarantorService.requestGuarantor(
//                request.getCustomerBvn(),
//                request.getCustomerFirstName(),
//                request.getCustomerLastName(),
//                request.getCustomerEmail(),
//                request.getCustomerPhoneNumber(),
//                request.getCustomerPlan(),
//                request.getCustomerInstallmentDuration(),
//                request.getGuarantorEmail(),
//                request.getStorePrice(),
//                request.getProductName(),
//                request.getProductBrand(),
//                request.getProductSize()
//        );
//        return ResponseEntity.ok(response);
//    }
//
//
//
//
//
//
//
//   // @PostMapping("/request")
////    public ResponseEntity<Map<String, Object>> requestGuarantor(@RequestBody Map<String, String> request) {
////        // Parse store price from request
//////        BigDecimal storePrice;
//////        try {
//////            String priceStr = request.get("storePrice");
//////            if (priceStr != null) {
//////                storePrice = new BigDecimal(priceStr.replace(",", ""));
//////            } else {
//////                throw new IllegalArgumentException("storePrice is required");
//////            }
//////        } catch (Exception e) {
//////            Map<String, Object> error = new HashMap<>();
//////            error.put("success", false);
//////            error.put("message", "Invalid store price: " + e.getMessage());
//////            return ResponseEntity.badRequest().body(error);
//////        }
////
////        Map<String, Object> response = guarantorService.requestGuarantor(
////                request.get("customerBvn"),
////                request.get("customerFirstName"),
////                request.get("customerLastName"),
////                request.get("customerEmail"),
////                request.get("customerPhoneNumber"),
////                request.get("customerPlan"),
////                request.get("installmentDuration"),
////                request.get("guarantorEmail"),
////                storePrice,
////                request.get("productName"),
////                request.get("productBrand"),
////                request.get("productSize")
////        );
////        return ResponseEntity.ok(response);
////    }
//
//
//
//
//
//
////    @PostMapping("/request")
////    public ResponseEntity<Map<String, Object>> requestGuarantor(
////            @RequestBody Map<String, String> request) {
////
////        Long agentEntryId = null;
////        try {
////            String agentEntryIdStr = request.get("agentEntryId");
////            if (agentEntryIdStr != null) {
////                agentEntryId = Long.parseLong(agentEntryIdStr);
////            }
////        } catch (NumberFormatException e) {
////            Map<String, Object> error = new HashMap<>();
////            error.put("success", false);
////            error.put("message", "Invalid agentEntryId");
////            return ResponseEntity.badRequest().body(error);
////        }
////
////        Map<String, Object> response = guarantorService.requestGuarantor(
////                request.get("customerBvn"),
////                request.get("customerFirstName"),
////                request.get("customerLastName"),
////                request.get("customerEmail"),
////                request.get("customerPlan"),
////                request.get("installmentDuration"),
////                request.get("guarantorEmail"),
////                agentEntryId   // ✅ Long
////        );
////
////        return ResponseEntity.ok(response);
////    }
//
//
//
//
//
////    @PostMapping("/request")
////    public ResponseEntity<Map<String, Object>> requestGuarantor(@RequestBody Map<String, String> request) {
////        // Parse store price from request
////        BigDecimal storePrice;
////        try {
////        String priceStr = request.get("storePrice");
////        if (priceStr != null) {
////            storePrice = new BigDecimal(priceStr.replace(",", ""));
////        } else {
////            throw new IllegalArgumentException("storePrice is required");
////        }
////        } catch (Exception e) {
////            Map<String, Object> error = new HashMap<>();
////            error.put("success", false);
////            error.put("message", "Invalid store price: " + e.getMessage());
////            return ResponseEntity.badRequest().body(error);
////        }
////        Map<String, Object> response = guarantorService.requestGuarantor(
////                request.get("customerBvn"),
////                request.get("customerFirstName"),
////                request.get("customerLastName"),
////                request.get("customerEmail"),
////                request.get("customerPlan"),
////                request.get("installmentDuration"),
////                request.get("guarantorEmail"),
////                storePrice
////        );
////        return ResponseEntity.ok(response);
////    }
//
//    /**
//     * Step 2: Get guarantor data by token (guarantor opens link)
//     */
//    @GetMapping("/form/{token}")
//    public ResponseEntity<Map<String, Object>> getGuarantorData(@PathVariable String token) {
//        Map<String, Object> response = guarantorService.getGuarantorDataByToken(token);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Step 3: Guarantor submits their form
//     */
//
////    @PostMapping(
////    value = "/submit/{token}",
////    consumes = "multipart/form-data"
////    )
////    public ResponseEntity<Map<String, Object>> submitGuarantorForm(
////            @PathVariable String token,
////            @ModelAttribute GuarantorRequest request
////    ) {
////        Map<String, Object> response =
////                guarantorService.submitGuarantorForm(token, request);
////
////        return ResponseEntity.ok(response);
////    }
//
//
//
//    @PostMapping("/submit/{token}")
//    public ResponseEntity<Map<String, Object>> submitGuarantorForm(
//            @PathVariable String token,
//            @RequestBody GuarantorRequest request) {
//        Map<String, Object> response = guarantorService.submitGuarantorForm(token, request);
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Step 4: Admin confirms guarantor (from email link)
//     */
//
//    @GetMapping("/confirm/{guarantorId}")
//public ResponseEntity<Map<String, Object>> confirmGuarantorViaEmail(@PathVariable Long guarantorId) {
//    Map<String, Object> response = guarantorService.confirmGuarantor(guarantorId);
//
//    // Redirect to a friendly page instead of returning raw JSON
//        String frontendBaseUrl = "";
//        if (Boolean.TRUE.equals(response.get("success"))) {
//        return ResponseEntity.status(302)
//            .header("Location", frontendBaseUrl + "/admin/confirmed?status=success&id=" + guarantorId)
//            .build();
//    }
//    return ResponseEntity.status(302)
//        .header("Location", frontendBaseUrl + "/admin/confirmed?status=failed")
//        .build();
//}
//
//@PostMapping("/confirm/{guarantorId}")
//public ResponseEntity<Map<String, Object>> confirmGuarantor(@PathVariable Long guarantorId) {
//    Map<String, Object> response = guarantorService.confirmGuarantor(guarantorId);
//    return ResponseEntity.ok(response);
//}
//
//
////    @PostMapping("/confirm/{guarantorId}")
////    public ResponseEntity<Map<String, Object>> confirmGuarantor(@PathVariable Long guarantorId) {
////        Map<String, Object> response = guarantorService.confirmGuarantor(guarantorId);
////        return ResponseEntity.ok(response);
////    }
//
//
//
//    /**
//     * Step 5: Guarantor verifies OTP
//     */
//    @PostMapping("/verify-otp/{token}")
//    public ResponseEntity<Map<String, Object>> verifyOtp(
//            @PathVariable String token,
//            @RequestBody Map<String, String> request) {
//        Map<String, Object> response = guarantorService.verifyGuarantorOtp(
//                token,
//                request.get("otp")
//        );
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Step 6: Customer checks guarantor status (polling)
//     */
//    @GetMapping("/status/{customerBvn}")
//    public ResponseEntity<Map<String, Object>> getGuarantorStatus(@PathVariable String customerBvn) {
//        Map<String, Object> response = guarantorService.getGuarantorStatus(customerBvn);
//        return ResponseEntity.ok(response);
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
//
//
//
//
//
//
////package com.koolboks.creditProject.controller;
////
////import com.koolboks.creditProject.dto.GuarantorRequest;
////import com.koolboks.creditProject.service.GuarantorService;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////
////import java.util.HashMap;
////import java.util.Map;
////
////@RestController
////@RequestMapping("/api/guarantor")
//////@CrossOrigin(origins = "*")
////public class GuarantorController {
////
////    private final GuarantorService guarantorService;
////
////    public GuarantorController(GuarantorService guarantorService) {
////        this.guarantorService = guarantorService;
////    }
////
////    @PostMapping("/submit")
////    public ResponseEntity<Map<String, Object>> submitGuarantor(@RequestBody GuarantorRequest request) {
////        Map<String, Object> response = new HashMap<>();
////
////        try {
////            boolean success = guarantorService.processGuarantorSubmission(request);
////
////            if (success) {
////                response.put("success", true);
////                response.put("message", "Guarantor information submitted successfully. Awaiting admin confirmation.");
////            } else {
////                response.put("success", false);
////                response.put("message", "Failed to process guarantor. Guarantor may already be in use.");
////            }
////
////            return ResponseEntity.ok(response);
////
////        } catch (Exception e) {
////            response.put("success", false);
////            response.put("message", "Error processing guarantor: " + e.getMessage());
////            return ResponseEntity.badRequest().body(response);
////        }
////    }
////
////    @PostMapping("/confirm/{guarantorId}")
////    public ResponseEntity<Map<String, Object>> confirmGuarantor(@PathVariable Long guarantorId) {
////        Map<String, Object> response = new HashMap<>();
////
////        try {
////            Map<String, Object> result = guarantorService.confirmGuarantor(guarantorId);
////            return ResponseEntity.ok(result);
////
////        } catch (Exception e) {
////            response.put("success", false);
////            response.put("message", "Error confirming guarantor: " + e.getMessage());
////            return ResponseEntity.badRequest().body(response);
////        }
////    }
////
////    @PostMapping("/verify-otp")
////    public ResponseEntity<Map<String, Object>> verifyOtp(
////            @RequestParam String customerBvn,
////            @RequestParam String otp) {
////
////        Map<String, Object> response = new HashMap<>();
////
////        try {
////            boolean isValid = guarantorService.verifyGuarantorOtp(customerBvn, otp);
////
////            if (isValid) {
////                response.put("success", true);
////                response.put("verified", true);
////                response.put("message", "OTP verified successfully. You can proceed.");
////            } else {
////                response.put("success", true);
////                response.put("verified", false);
////                response.put("message", "Invalid or expired OTP.");
////            }
////
////            return ResponseEntity.ok(response);
////
////        } catch (Exception e) {
////            response.put("success", false);
////            response.put("message", "Error verifying OTP: " + e.getMessage());
////            return ResponseEntity.badRequest().body(response);
////        }
////    }
////}
////
////
//
//
//
//
//
//
//
//
//
////package com.koolboks.creditProject.controller;
////
////import com.koolboks.creditProject.dto.GuarantorRequest;
////import com.koolboks.creditProject.service.GuarantorService;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////
////import java.util.HashMap;
////import java.util.Map;
////
////@RestController
////@RequestMapping("/api/agent-followup")
//////@CrossOrigin(origins = "*")
////public class GuarantorController {
////
////    private final GuarantorService guarantorService;
////
////    public GuarantorController(GuarantorService guarantorService) {
////        this.guarantorService = guarantorService;
////    }
////
////    @PostMapping("/submit-guarantor")
////    public ResponseEntity<Map<String, Object>> submitGuarantor(@RequestBody GuarantorRequest request) {
////        try {
////            boolean success = guarantorService.processGuarantorSubmission(request);
////
////            Map<String, Object> response = new HashMap<>();
////            response.put("success", success);
////            response.put("message", success
////                ? "Guarantor information submitted successfully. Admin will contact the guarantor for verification."
////                : "Failed to process guarantor information. Please try again.");
////
////            return success ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
////
////        } catch (Exception e) {
////            Map<String, Object> errorResponse = new HashMap<>();
////            errorResponse.put("success", false);
////            errorResponse.put("message", "Error processing request: " + e.getMessage());
////            return ResponseEntity.badRequest().body(errorResponse);
////        }
////    }
////}
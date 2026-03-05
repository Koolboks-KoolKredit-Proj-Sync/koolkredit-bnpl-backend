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
                            background: #f7623b;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            min-height: 100vh;
                            padding: 20px;
                        }
                        .card {
                            background: #1a1a1a;
                            padding: 50px 40px;
                            border-radius: 12px;
                            text-align: center;
                            box-shadow: 0 8px 32px rgba(0,0,0,0.4);
                            max-width: 450px;
                            width: 100%%;
                            border-top: 5px solid #f7623b;
                        }
                        .icon { font-size: 64px; margin-bottom: 20px; }
                        h2 { color: %s; font-size: 24px; margin-bottom: 12px; }
                        .message { color: #cccccc; font-size: 15px; margin-bottom: 16px; line-height: 1.5; }
                        .phone { color: #ffffff; font-size: 14px; margin-top: 8px; }
                        .phone strong { color: #f7623b; }
                        .brand {
                            margin-top: 40px;
                            padding-top: 20px;
                            border-top: 1px solid #333;
                            color: #666;
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
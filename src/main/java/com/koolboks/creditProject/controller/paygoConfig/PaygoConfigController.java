package com.koolboks.creditProject.controller.paygoConfig;





import com.koolboks.creditProject.service.email_paygo_config.PaygoEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
//@CrossOrigin(origins = "*")
public class PaygoConfigController {

    @Autowired
    private PaygoEmailService paygoEmailService;

    /**
     * Confirm paygo configuration
     * Receives all data from AfterSales form and resends email with updated status
     */
    @PostMapping("/confirm-paygo-config")
    public ResponseEntity<?> confirmPaygoConfig(@RequestBody Map<String, String> payload) {
        try {
            // Validate required fields
            String loanRef = payload.get("customerLoanRef");
            String paygoConfigurationDate = payload.get("paygoConfigurationDate");
            String paygoConfiguredBy = payload.get("paygoConfiguredBy");

            if (loanRef == null || loanRef.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Loan reference is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (paygoConfigurationDate == null || paygoConfigurationDate.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Paygo configuration date is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            if (paygoConfiguredBy == null || paygoConfiguredBy.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Paygo configured by field is required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            // Build email data map with all the payload data
            Map<String, Object> emailData = new HashMap<>();

            // Customer Information
            emailData.put("customerFirstName", payload.get("customerFirstName"));
            emailData.put("customerLastName", payload.get("customerLastName"));
            emailData.put("customerEmail", payload.get("customerEmail"));
            emailData.put("customerPhoneNumber", payload.get("customerPhoneNumber"));
            emailData.put("customerLoanRef", payload.get("customerLoanRef"));
            emailData.put("customerLoanDuration", payload.get("customerLoanDuration"));

            // Agent Information
            emailData.put("agentName", payload.get("agentName"));
            emailData.put("agentId", payload.get("agentId"));
            emailData.put("agentEmail", payload.get("agentEmail"));
            emailData.put("agentNumber", payload.get("agentNumber"));

            // Store & Payment Details
            emailData.put("storeName", payload.get("storeName"));
            emailData.put("storeLocation", payload.get("storeLocation"));
            emailData.put("initialInstalment", payload.get("initialInstalment"));
            emailData.put("paymentDate", payload.get("paymentDate"));

            // Product Information
            emailData.put("productName", payload.get("productName"));
            emailData.put("productBrand", payload.get("productBrand"));
            emailData.put("productSize", payload.get("productSize"));

            // Mark as configured
            emailData.put("isConfigured", true);

            // Resend email with updated status
            paygoEmailService.sendPaygoConfigurationEmail(emailData);

            // Log the paygo configuration (you can store this in database later)
            System.out.println("=== PAYGO CONFIGURATION CONFIRMED ===");
            System.out.println("Loan Reference: " + loanRef);
            System.out.println("Configuration Date: " + paygoConfigurationDate);
            System.out.println("Configured By: " + paygoConfiguredBy);
            System.out.println("Customer: " + payload.get("customerFirstName") + " " + payload.get("customerLastName"));
            System.out.println("=====================================");

            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Paygo configuration confirmed successfully");
            response.put("loanRef", loanRef);
            response.put("configured", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error confirming paygo configuration: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to confirm paygo configuration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
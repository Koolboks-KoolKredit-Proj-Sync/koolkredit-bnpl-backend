package com.koolboks.creditProject.controller.installation;



import com.koolboks.creditProject.service.installation.InstallationPinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/v1/api")
//@CrossOrigin(origins = "*")
public class InstallationPinController {

    @Autowired
    private InstallationPinService installationPinService;

    @PostMapping("/generate-installation-pin")
    public ResponseEntity<?> generateInstallationPin(@RequestBody Map<String, Object> requestData) {
        try {
            String orderId = String.valueOf(requestData.get("orderId"));
            String customerEmail = String.valueOf(requestData.get("customerEmail"));
            String customerPhone = String.valueOf(requestData.get("customerPhoneNumber"));
            String agentEmail = String.valueOf(requestData.get("agentEmail"));
            String customerName = requestData.get("customerFirstName") + " " + requestData.get("customerLastName");

            System.out.println("=== GENERATING INSTALLATION PIN ===");
            System.out.println("Order ID: " + orderId);

            // Generate PIN
            String pin = installationPinService.generatePin(orderId);

            // Send PIN to customer (email + SMS) and agent (email) - PARALLEL
            CompletableFuture<Void> customerEmailTask = CompletableFuture.runAsync(() -> {
                System.out.println(">>> Sending PIN to customer email...");
                installationPinService.sendPinByEmail(customerEmail, pin, orderId, customerName);
            });

            CompletableFuture<Void> customerSmsTask = CompletableFuture.runAsync(() -> {
                System.out.println(">>> Sending PIN to customer SMS...");
                installationPinService.sendPinBySms(customerPhone, pin, orderId);
            });

            CompletableFuture<Void> agentEmailTask = CompletableFuture.runAsync(() -> {
                System.out.println(">>> Sending PIN to agent email...");
                installationPinService.sendPinByEmail(agentEmail, pin, orderId, "Agent");
            });

            // Wait for all to complete
            try {
                CompletableFuture.allOf(customerEmailTask, customerSmsTask, agentEmailTask)
                    .get(20, java.util.concurrent.TimeUnit.SECONDS);

                System.out.println("=== PIN SENT TO ALL PARTIES ===");

            } catch (Exception e) {
                System.err.println("Warning: Some notifications may have timed out: " + e.getMessage());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "PIN generated and sent successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error generating PIN: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Failed to generate PIN: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/verify-installation-pin")
    public ResponseEntity<?> verifyInstallationPin(@RequestBody Map<String, String> requestData) {
        try {
            String orderId = requestData.get("orderId");
            String pinCode = requestData.get("pinCode");

            System.out.println("=== VERIFYING INSTALLATION PIN ===");
            System.out.println("Order ID: " + orderId);

            boolean isValid = installationPinService.verifyPin(orderId, pinCode);

            if (isValid) {
                System.out.println("✅ PIN verified successfully");

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "PIN verified successfully");
                return ResponseEntity.ok(response);
            } else {
                System.out.println("❌ Invalid PIN");

                Map<String, String> error = new HashMap<>();
                error.put("success", "false");
                error.put("message", "Invalid PIN code");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

        } catch (Exception e) {
            System.err.println("Error verifying PIN: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Failed to verify PIN: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

package com.koolboks.creditProject.controller.delivery;


import com.koolboks.creditProject.service.delivery.CustomerDeliveryEmailService;
import com.koolboks.creditProject.service.delivery.AgentDeliveryEmailService;
import com.koolboks.creditProject.service.installation.InstallationEmailService;
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
public class DeliveryNotificationController {

    @Autowired
    private CustomerDeliveryEmailService customerDeliveryEmailService;

    @Autowired
    private AgentDeliveryEmailService agentDeliveryEmailService;

    @Autowired
    private InstallationEmailService installationEmailService;

    @PostMapping("/notify-delivery")
    public ResponseEntity<?> notifyDelivery(@RequestBody Map<String, Object> deliveryData) {
        try {
            System.out.println("=== NOTIFYING DELIVERY TO ALL PARTIES ===");
            System.out.println("Order ID: " + deliveryData.get("orderId"));

            // Execute all 3 emails SIMULTANEOUSLY
            CompletableFuture<Void> customerEmail = CompletableFuture.runAsync(() -> {
                System.out.println(">>> Sending delivery notification to customer...");
                customerDeliveryEmailService.sendCustomerDeliveryEmail(deliveryData);
                System.out.println(">>> Customer notified");
            });

            CompletableFuture<Void> agentEmail = CompletableFuture.runAsync(() -> {
                System.out.println(">>> Sending delivery notification to agent...");
                agentDeliveryEmailService.sendAgentDeliveryEmail(deliveryData);
                System.out.println(">>> Agent notified");
            });

            CompletableFuture<Void> installationEmail = CompletableFuture.runAsync(() -> {
                System.out.println(">>> Sending installation assignment to installation team...");
                installationEmailService.sendInstallationNotificationEmail(deliveryData);
                System.out.println(">>> Installation team notified");
            });

            try {
                CompletableFuture.allOf(customerEmail, agentEmail, installationEmail)
                    .get(30, java.util.concurrent.TimeUnit.SECONDS);

                System.out.println("=== ALL DELIVERY NOTIFICATIONS SENT SUCCESSFULLY ===");

            } catch (Exception e) {
                System.err.println("Warning: Some emails may have timed out: " + e.getMessage());
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All parties notified successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error notifying delivery: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Failed to notify delivery: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
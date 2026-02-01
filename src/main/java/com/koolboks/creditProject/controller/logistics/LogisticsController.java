package com.koolboks.creditProject.controller.logistics;



import com.koolboks.creditProject.service.logistics.LogisticsEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
//@CrossOrigin(origins = "*")
public class LogisticsController {

    @Autowired
    private LogisticsEmailService logisticsEmailService;

    /**
     * Notify Logistics Team - Send delivery scheduling email
     * This is called by Django after stock confirmation
     */
    @PostMapping("/notify-logistics")
    public ResponseEntity<?> notifyLogistics(@RequestBody Map<String, Object> orderData) {
        try {
            System.out.println("=== NOTIFYING LOGISTICS TEAM ===");
            System.out.println("Order ID: " + orderData.get("orderId"));
            System.out.println("Stock Confirmed By: " + orderData.get("stockConfirmedBy"));

            // Send delivery scheduling email to logistics team
            logisticsEmailService.sendDeliverySchedulingEmail(orderData);

            System.out.println("=== LOGISTICS TEAM NOTIFIED SUCCESSFULLY ===");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Logistics team notified successfully");
            response.put("orderId", orderData.get("orderId"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error notifying logistics team: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Failed to notify logistics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

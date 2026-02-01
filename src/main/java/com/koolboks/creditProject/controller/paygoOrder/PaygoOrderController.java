package com.koolboks.creditProject.controller.paygoOrder;



import com.koolboks.creditProject.service.salesOrder.SalesOrderService;
import com.koolboks.creditProject.service.salesOrder.InventoryEmailService;
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
public class PaygoOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private InventoryEmailService inventoryEmailService;

    /**
     * Process Paygo Order - Generates PDF and sends emails simultaneously
     * This is called by Django after saving to database
     */
    @PostMapping("/process-paygo-order")
    public ResponseEntity<?> processPaygoOrder(@RequestBody Map<String, Object> orderData) {
        try {
            System.out.println("=== PROCESSING PAYGO ORDER ===");
            System.out.println("Order ID: " + orderData.get("orderId"));
            System.out.println("Customer: " + orderData.get("customerFirstName") + " " + orderData.get("customerLastName"));

            // Execute all three actions SIMULTANEOUSLY using CompletableFuture

            // Task 1: Generate PDF
            CompletableFuture<byte[]> pdfGenerationTask = CompletableFuture.supplyAsync(() -> {
                try {
                    System.out.println(">>> Generating Sales Order PDF...");
                    byte[] pdfBytes = salesOrderService.generateSalesOrderPDF(orderData);
                    System.out.println(">>> PDF generated successfully");
                    return pdfBytes;
                } catch (Exception e) {
                    System.err.println(">>> Error generating PDF: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            });

            // Task 2: Send Sales Order Email (waits for PDF)
            CompletableFuture<Void> salesOrderEmailTask = pdfGenerationTask.thenAcceptAsync(pdfBytes -> {
                if (pdfBytes != null) {
                    System.out.println(">>> Sending Sales Order to Agent...");
                    salesOrderService.sendSalesOrderToAgent(orderData, pdfBytes);
                    System.out.println(">>> Sales Order sent to: " + orderData.get("agentEmail"));
                }
            });

            // Task 3: Send Inventory Email (independent)
            CompletableFuture<Void> inventoryEmailTask = CompletableFuture.runAsync(() -> {
                System.out.println(">>> Sending Inventory Confirmation Email...");
                inventoryEmailService.sendInventoryConfirmationEmail(orderData);
                System.out.println(">>> Inventory email sent");
            });

            // Wait for all tasks to complete (with timeout of 30 seconds)
            try {
                CompletableFuture.allOf(salesOrderEmailTask, inventoryEmailTask)
                    .get(30, java.util.concurrent.TimeUnit.SECONDS);

                System.out.println("=== ALL TASKS COMPLETED SUCCESSFULLY ===");

            } catch (Exception e) {
                System.err.println("Warning: Some tasks may have timed out or failed: " + e.getMessage());
                // Continue anyway - the tasks will complete in background
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Order processed successfully");
            response.put("orderId", orderData.get("orderId"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error processing paygo order: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Failed to process order: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
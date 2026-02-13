package com.koolboks.creditProject.controller.debitMandate.mono;



import com.koolboks.creditProject.dto.mono.AutoDebitResult;
import com.koolboks.creditProject.exceptions.DebitOperationException;
import com.koolboks.creditProject.exceptions.InsufficientBalanceException;
import com.koolboks.creditProject.exceptions.MandateNotFoundException;
import com.koolboks.creditProject.service.debitMandate.AutoDebitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/debit")
public class AutoDebitController {

    private static final Logger logger = LoggerFactory.getLogger(AutoDebitController.class);

    private final AutoDebitService autoDebitService;

    public AutoDebitController(AutoDebitService autoDebitService) {
        this.autoDebitService = autoDebitService;
    }

    /**
     * Endpoint to initiate auto-debit
     * POST /api/v1/debit/process
     */
    @PostMapping("/process")
    public ResponseEntity<AutoDebitResult> processAutoDebit(
            @RequestBody AutoDebitRequestDto request) {

        logger.info("Received auto-debit request for mandate: {}, amount: {}",
                   request.getMandateReference(), request.getAmount());

        try {
            AutoDebitResult result = autoDebitService.checkBalanceAndDebit(
                request.getMandateReference(),
                request.getAmount(),
                request.getNarration()
            );

            return ResponseEntity.ok(result);

        } catch (MandateNotFoundException e) {
            logger.error("Mandate not found: {}", e.getMessage());
            AutoDebitResult errorResult = new AutoDebitResult(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResult);

        } catch (InsufficientBalanceException e) {
            logger.error("Insufficient balance: {}", e.getMessage());
            AutoDebitResult errorResult = new AutoDebitResult(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorResult);

        } catch (DebitOperationException e) {
            logger.error("Debit operation failed: {}", e.getMessage());
            AutoDebitResult errorResult = new AutoDebitResult(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResult);

        } catch (Exception e) {
            logger.error("Unexpected error during auto-debit", e);
            AutoDebitResult errorResult = new AutoDebitResult(
                false,
                "An unexpected error occurred: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    /**
     * DTO for auto-debit request
     */
    public static class AutoDebitRequestDto {
        private String mandateReference;
        private int amount;
        private String narration;

        public AutoDebitRequestDto() {
        }

        public String getMandateReference() {
            return mandateReference;
        }

        public void setMandateReference(String mandateReference) {
            this.mandateReference = mandateReference;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getNarration() {
            return narration;
        }

        public void setNarration(String narration) {
            this.narration = narration;
        }
    }

    /**
     * Global exception handler for this controller
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        logger.error("Exception in AutoDebitController", e);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", e.getMessage());
        errorResponse.put("error", e.getClass().getSimpleName());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
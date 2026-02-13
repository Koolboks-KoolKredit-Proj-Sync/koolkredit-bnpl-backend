package com.koolboks.creditProject.controller.agent_proof;


import com.koolboks.creditProject.service.agent_proof.KoolboksConfirmationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
//@CrossOrigin(origins = "*")
public class KoolboksConfirmationController {

    private static final Logger log = LoggerFactory.getLogger(KoolboksConfirmationController.class);

    @Autowired
    private KoolboksConfirmationService confirmationService;

    /**
     * POST /v1/api/koolboks-confirm/{customerLoanRef}
     * Confirm initial payment for Koolboks loan
     */
    @PostMapping("/koolboks-confirm/{customerLoanRef}")
    public ResponseEntity<Map<String, Object>> confirmInitialPayment(
            @PathVariable String customerLoanRef) {

        log.info("Received Koolboks confirmation request for: {}", customerLoanRef);

        try {
            Map<String, Object> result = confirmationService.confirmInitialPayment(customerLoanRef);

            log.info("✅ Koolboks confirmation successful for: {}", customerLoanRef);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Koolboks confirmation failed for {}: {}", customerLoanRef, e.getMessage());

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            errorResponse.put("customerLoanRef", customerLoanRef);

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }
}
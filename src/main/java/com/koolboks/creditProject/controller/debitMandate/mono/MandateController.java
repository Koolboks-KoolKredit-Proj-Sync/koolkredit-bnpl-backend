package com.koolboks.creditProject.controller.debitMandate.mono;



import com.koolboks.creditProject.dto.mono.MonoCreateMandateResponse;
import com.koolboks.creditProject.service.debitMandate.CreateMandateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mandate")
public class MandateController {

    @Autowired
    private CreateMandateService createMandateService;

    /**
     * Create mandate for a specific DebitMandate record
     */
    @PostMapping("/create/{debitMandateId}")
    public ResponseEntity<Map<String, Object>> createMandate(@PathVariable Long debitMandateId) {
        Map<String, Object> response = new HashMap<>();

        try {
            MonoCreateMandateResponse mandateResponse = createMandateService.createMandate(debitMandateId);

            response.put("success", true);
            response.put("message", mandateResponse.getMessage());
            response.put("mandateId", mandateResponse.getData().getMandateId());
            response.put("status", mandateResponse.getData().getStatus());
            response.put("nibssCode", mandateResponse.getData().getNibssCode());
            response.put("approved", mandateResponse.getData().isApproved());
            response.put("readyToDebit", mandateResponse.getData().isReadyToDebit());

            // Include transfer destinations if mandate needs verification
            if (mandateResponse.getData().getTransferDestinations() != null &&
                !mandateResponse.getData().getTransferDestinations().isEmpty()) {

                response.put("requiresVerification", true);
                response.put("transferDestinations",
                    mandateResponse.getData().getTransferDestinations().stream()
                        .map(dest -> {
                            Map<String, Object> destMap = new HashMap<>();
                            destMap.put("bankName", dest.getBankName());
                            destMap.put("accountNumber", dest.getAccountNumber());
                            destMap.put("icon", dest.getIcon());
                            return destMap;
                        })
                        .collect(Collectors.toList())
                );
            } else {
                response.put("requiresVerification", false);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create mandate: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Batch populate missing bank codes for all DebitMandate records
     */
    @PostMapping("/populate-bank-codes")
    public ResponseEntity<Map<String, Object>> populateBankCodes() {
        Map<String, Object> response = new HashMap<>();

        try {
            createMandateService.populateAllMissingBankCodes();

            response.put("success", true);
            response.put("message", "Bank codes populated successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to populate bank codes: " + e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }
}

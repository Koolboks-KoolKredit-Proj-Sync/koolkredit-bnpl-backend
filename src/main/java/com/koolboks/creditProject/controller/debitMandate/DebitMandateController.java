package com.koolboks.creditProject.controller.debitMandate;
//
//
//
import com.koolboks.creditProject.dto.accountVerification.AccountVerificationDTO;
//import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
import com.koolboks.creditProject.service.debitMandate.DebitMandateService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/debit-mandate")
////@CrossOrigin(origins = "*") // Configure this properly for production
//public class DebitMandateController {
//
//    @Autowired
//    private DebitMandateService debitMandateService;
//
//    /**
//     * POST endpoint to save account verification data
//     * This creates a new DebitMandate record with partial data
//     * Other fields can be filled in later from other forms
//     */
//    @PostMapping("/account-verification")
//    public ResponseEntity<Map<String, Object>> saveAccountVerification(@RequestBody AccountVerificationDTO dto) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            DebitMandate savedMandate = debitMandateService.saveAccountVerification(dto);
//
//            response.put("success", true);
//            response.put("message", "Account verification data saved successfully");
//            response.put("data", Map.of(
//                "id", savedMandate.getId(),
//                "reference", savedMandate.getReference(),
//                "accountNumber", savedMandate.getAccount_number(),
//                "bankName", savedMandate.getBank_name(),
//                "customerName", savedMandate.getCustomer_name()
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "Failed to save account verification data");
//            response.put("error", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * PUT endpoint to update existing DebitMandate with account verification data
//     */
//    @PutMapping("/account-verification/{id}")
//    public ResponseEntity<Map<String, Object>> updateAccountVerification(
//            @PathVariable Long id,
//            @RequestBody AccountVerificationDTO dto) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            DebitMandate updatedMandate = debitMandateService.updateAccountVerification(id, dto);
//
//            response.put("success", true);
//            response.put("message", "Account verification data updated successfully");
//            response.put("data", Map.of(
//                "id", updatedMandate.getId(),
//                "reference", updatedMandate.getReference(),
//                "accountNumber", updatedMandate.getAccount_number(),
//                "bankName", updatedMandate.getBank_name(),
//                "customerName", updatedMandate.getCustomer_name()
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (RuntimeException e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "Failed to update account verification data");
//            response.put("error", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * GET endpoint to retrieve DebitMandate by ID
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<Map<String, Object>> getDebitMandateById(@PathVariable Long id) {
//        Map<String, Object> response = new HashMap<>();
//
//        Optional<DebitMandate> mandate = debitMandateService.getDebitMandateById(id);
//
//        if (mandate.isPresent()) {
//            response.put("success", true);
//            response.put("data", mandate.get());
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "DebitMandate not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    }
//
//    /**
//     * GET endpoint to retrieve DebitMandate by reference
//     */
//    @GetMapping("/reference/{reference}")
//    public ResponseEntity<Map<String, Object>> getDebitMandateByReference(@PathVariable String reference) {
//        Map<String, Object> response = new HashMap<>();
//
//        Optional<DebitMandate> mandate = debitMandateService.getDebitMandateByReference(reference);
//
//        if (mandate.isPresent()) {
//            response.put("success", true);
//            response.put("data", mandate.get());
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "DebitMandate not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    }
//}







//import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/debit-mandate")
////@CrossOrigin(origins = "*") // Configure this properly for production
//public class DebitMandateController {
//
//    @Autowired
//    private DebitMandateService debitMandateService;
//
//    /**
//     * POST endpoint to save account verification data
//     * This creates a new DebitMandate record with partial data
//     * Other fields can be filled in later from other forms
//     */
//    @PostMapping("/account-verification")
//    public ResponseEntity<Map<String, Object>> saveAccountVerification(@RequestBody AccountVerificationDTO dto) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            DebitMandate savedMandate = debitMandateService.saveAccountVerification(dto);
//
//            response.put("success", true);
//            response.put("message", "Account verification data saved successfully");
//            response.put("data", Map.of(
//                "id", savedMandate.getId(),
//                "reference", savedMandate.getReference(),
//                "accountNumber", savedMandate.getAccount_number(),
//                "bankName", savedMandate.getBank_name(),
//                "customerName", savedMandate.getCustomer_name()
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "Failed to save account verification data");
//            response.put("error", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * PUT endpoint to update existing DebitMandate with account verification data
//     */
//    @PutMapping("/account-verification/{id}")
//    public ResponseEntity<Map<String, Object>> updateAccountVerification(
//            @PathVariable Long id,
//            @RequestBody AccountVerificationDTO dto) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            DebitMandate updatedMandate = debitMandateService.updateAccountVerification(id, dto);
//
//            response.put("success", true);
//            response.put("message", "Account verification data updated successfully");
//            response.put("data", Map.of(
//                "id", updatedMandate.getId(),
//                "reference", updatedMandate.getReference(),
//                "accountNumber", updatedMandate.getAccount_number(),
//                "bankName", updatedMandate.getBank_name(),
//                "customerName", updatedMandate.getCustomer_name()
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (RuntimeException e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "Failed to update account verification data");
//            response.put("error", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * PUT endpoint to update DebitMandate with follow-up customer details
//     */
//    @PutMapping("/customer-details/{id}")
//    public ResponseEntity<Map<String, Object>> updateCustomerDetails(
//            @PathVariable Long id,
//            @RequestBody AccountVerificationDTO dto) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            DebitMandate updatedMandate = debitMandateService.updateWithFollowUpData(id, dto);
//
//            response.put("success", true);
//            response.put("message", "Customer details updated successfully");
//            response.put("data", Map.of(
//                "id", updatedMandate.getId(),
//                "reference", updatedMandate.getReference(),
//                "bvn", updatedMandate.getBvn() != null ? updatedMandate.getBvn() : "",
//                "customerAddress", updatedMandate.getCustomer_address() != null ? updatedMandate.getCustomer_address() : "",
//                "customerEmail", updatedMandate.getCustomer_email() != null ? updatedMandate.getCustomer_email() : "",
//                "customerPhone", updatedMandate.getCustomer_phone() != null ? updatedMandate.getCustomer_phone() : ""
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (RuntimeException e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "Failed to update customer details");
//            response.put("error", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * GET endpoint to retrieve DebitMandate by ID
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<Map<String, Object>> getDebitMandateById(@PathVariable Long id) {
//        Map<String, Object> response = new HashMap<>();
//
//        Optional<DebitMandate> mandate = debitMandateService.getDebitMandateById(id);
//
//        if (mandate.isPresent()) {
//            response.put("success", true);
//            response.put("data", mandate.get());
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "DebitMandate not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    }
//
//    /**
//     * GET endpoint to retrieve DebitMandate by reference
//     */
//    @GetMapping("/reference/{reference}")
//    public ResponseEntity<Map<String, Object>> getDebitMandateByReference(@PathVariable String reference) {
//        Map<String, Object> response = new HashMap<>();
//
//        Optional<DebitMandate> mandate = debitMandateService.getDebitMandateByReference(reference);
//
//        if (mandate.isPresent()) {
//            response.put("success", true);
//            response.put("data", mandate.get());
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "DebitMandate not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    }
//}






//import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/debit-mandate")
////@CrossOrigin(origins = "*") // Configure this properly for production
//public class DebitMandateController {
//
//    @Autowired
//    private DebitMandateService debitMandateService;
//
//    /**
//     * POST endpoint to save account verification data
//     * This creates a new DebitMandate record with partial data
//     * Other fields can be filled in later from other forms
//     */
//    @PostMapping("/account-verification")
//    public ResponseEntity<Map<String, Object>> saveAccountVerification(@RequestBody AccountVerificationDTO dto) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            DebitMandate savedMandate = debitMandateService.saveAccountVerification(dto);
//
//            response.put("success", true);
//            response.put("message", "Account verification data saved successfully");
//            response.put("data", Map.of(
//                "id", savedMandate.getId(),
//                "reference", savedMandate.getReference(),
//                "accountNumber", savedMandate.getAccount_number(),
//                "bankName", savedMandate.getBank_name(),
//                "customerName", savedMandate.getCustomer_name()
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "Failed to save account verification data");
//            response.put("error", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * PUT endpoint to update existing DebitMandate with account verification data
//     */
//    @PutMapping("/account-verification/{id}")
//    public ResponseEntity<Map<String, Object>> updateAccountVerification(
//            @PathVariable Long id,
//            @RequestBody AccountVerificationDTO dto) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            DebitMandate updatedMandate = debitMandateService.updateAccountVerification(id, dto);
//
//            response.put("success", true);
//            response.put("message", "Account verification data updated successfully");
//            response.put("data", Map.of(
//                "id", updatedMandate.getId(),
//                "reference", updatedMandate.getReference(),
//                "accountNumber", updatedMandate.getAccount_number(),
//                "bankName", updatedMandate.getBank_name(),
//                "customerName", updatedMandate.getCustomer_name()
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (RuntimeException e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "Failed to update account verification data");
//            response.put("error", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * PUT endpoint to update DebitMandate with follow-up customer details
//     */
//    @PutMapping("/customer-details/{id}")
//    public ResponseEntity<Map<String, Object>> updateCustomerDetails(
//            @PathVariable Long id,
//            @RequestBody AccountVerificationDTO dto) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            DebitMandate updatedMandate = debitMandateService.updateWithFollowUpData(id, dto);
//
//            response.put("success", true);
//            response.put("message", "Customer details updated successfully");
//            response.put("data", Map.of(
//                "id", updatedMandate.getId(),
//                "reference", updatedMandate.getReference(),
//                "bvn", updatedMandate.getBvn() != null ? updatedMandate.getBvn() : "",
//                "customerAddress", updatedMandate.getCustomer_address() != null ? updatedMandate.getCustomer_address() : "",
//                "customerEmail", updatedMandate.getCustomer_email() != null ? updatedMandate.getCustomer_email() : "",
//                "customerPhone", updatedMandate.getCustomer_phone() != null ? updatedMandate.getCustomer_phone() : ""
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (RuntimeException e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "Failed to update customer details");
//            response.put("error", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * PUT endpoint to update DebitMandate with customer details using reference (UUID)
//     */
//    @PutMapping("/customer-details/reference/{reference}")
//    public ResponseEntity<Map<String, Object>> updateCustomerDetailsByReference(
//            @PathVariable String reference,
//            @RequestBody AccountVerificationDTO dto) {
//
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            // First find the mandate by reference
//            Optional<DebitMandate> mandateOpt = debitMandateService.getDebitMandateByReference(reference);
//
//            if (mandateOpt.isEmpty()) {
//                response.put("success", false);
//                response.put("message", "DebitMandate not found with reference: " + reference);
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            }
//
//            // Update using the ID
//            DebitMandate updatedMandate = debitMandateService.updateWithFollowUpData(mandateOpt.get().getId(), dto);
//
//            response.put("success", true);
//            response.put("message", "Customer details updated successfully");
//            response.put("data", Map.of(
//                "id", updatedMandate.getId(),
//                "reference", updatedMandate.getReference(),
//                "bvn", updatedMandate.getBvn() != null ? updatedMandate.getBvn() : "",
//                "customerAddress", updatedMandate.getCustomer_address() != null ? updatedMandate.getCustomer_address() : "",
//                "customerEmail", updatedMandate.getCustomer_email() != null ? updatedMandate.getCustomer_email() : "",
//                "customerPhone", updatedMandate.getCustomer_phone() != null ? updatedMandate.getCustomer_phone() : ""
//            ));
//
//            return ResponseEntity.ok(response);
//
//        } catch (RuntimeException e) {
//            response.put("success", false);
//            response.put("message", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//
//        } catch (Exception e) {
//            response.put("success", false);
//            response.put("message", "Failed to update customer details");
//            response.put("error", e.getMessage());
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }
//
//    /**
//     * GET endpoint to retrieve DebitMandate by ID
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<Map<String, Object>> getDebitMandateById(@PathVariable Long id) {
//        Map<String, Object> response = new HashMap<>();
//
//        Optional<DebitMandate> mandate = debitMandateService.getDebitMandateById(id);
//
//        if (mandate.isPresent()) {
//            response.put("success", true);
//            response.put("data", mandate.get());
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "DebitMandate not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    }
//
//    /**
//     * GET endpoint to retrieve DebitMandate by reference
//     */
//    @GetMapping("/reference/{reference}")
//    public ResponseEntity<Map<String, Object>> getDebitMandateByReference(@PathVariable String reference) {
//        Map<String, Object> response = new HashMap<>();
//
//        Optional<DebitMandate> mandate = debitMandateService.getDebitMandateByReference(reference);
//
//        if (mandate.isPresent()) {
//            response.put("success", true);
//            response.put("data", mandate.get());
//            return ResponseEntity.ok(response);
//        } else {
//            response.put("success", false);
//            response.put("message", "DebitMandate not found");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//        }
//    }
//}





















//import com.koolboks.creditProject.dto.accountVerification.AccountVerificationDTO;
import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
//import com.koolboks.creditProject.service.debitMandate.DebitMandateService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/debit-mandate")
//@CrossOrigin(origins = "*") // Configure this properly for production
public class DebitMandateController {

    private static final Logger logger = LoggerFactory.getLogger(DebitMandateController.class);

    @Autowired
    private DebitMandateService debitMandateService;

    /**
     * POST endpoint to save account verification data
     * This creates a new DebitMandate record with partial data
     * Other fields can be filled in later from other forms
     */
    @PostMapping("/account-verification")
    public ResponseEntity<Map<String, Object>> saveAccountVerification(@RequestBody AccountVerificationDTO dto) {
        Map<String, Object> response = new HashMap<>();

        try {
            DebitMandate savedMandate = debitMandateService.saveAccountVerification(dto);

            response.put("success", true);
            response.put("message", "Account verification data saved successfully");
            response.put("data", Map.of(
                "id", savedMandate.getId(),
                "reference", savedMandate.getReference(),
                "accountNumber", savedMandate.getAccount_number(),
                "bankName", savedMandate.getBank_name(),
                "customerName", savedMandate.getCustomer_name()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to save account verification data");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT endpoint to update existing DebitMandate with account verification data
     */
    @PutMapping("/account-verification/{id}")
    public ResponseEntity<Map<String, Object>> updateAccountVerification(
            @PathVariable Long id,
            @RequestBody AccountVerificationDTO dto) {

        Map<String, Object> response = new HashMap<>();

        try {
            DebitMandate updatedMandate = debitMandateService.updateAccountVerification(id, dto);

            response.put("success", true);
            response.put("message", "Account verification data updated successfully");
            response.put("data", Map.of(
                "id", updatedMandate.getId(),
                "reference", updatedMandate.getReference(),
                "accountNumber", updatedMandate.getAccount_number(),
                "bankName", updatedMandate.getBank_name(),
                "customerName", updatedMandate.getCustomer_name()
            ));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update account verification data");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT endpoint to update DebitMandate with follow-up customer details
     */
    @PutMapping("/customer-details/{id}")
    public ResponseEntity<Map<String, Object>> updateCustomerDetails(
            @PathVariable Long id,
            @RequestBody AccountVerificationDTO dto) {

        Map<String, Object> response = new HashMap<>();

        try {
            DebitMandate updatedMandate = debitMandateService.updateWithFollowUpData(id, dto);

            response.put("success", true);
            response.put("message", "Customer details updated successfully");
            response.put("data", Map.of(
                "id", updatedMandate.getId(),
                "reference", updatedMandate.getReference(),
                "bvn", updatedMandate.getBvn() != null ? updatedMandate.getBvn() : "",
                "customerAddress", updatedMandate.getCustomer_address() != null ? updatedMandate.getCustomer_address() : "",
                "customerEmail", updatedMandate.getCustomer_email() != null ? updatedMandate.getCustomer_email() : "",
                "customerPhone", updatedMandate.getCustomer_phone() != null ? updatedMandate.getCustomer_phone() : ""
            ));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update customer details");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PUT endpoint to update DebitMandate with customer details using reference (UUID)
     * This version integrates with Mono API to create customer and store customer_id
     */
    @PutMapping("/customer-details/reference/{reference}")
    public ResponseEntity<Map<String, Object>> updateCustomerDetailsByReference(
            @PathVariable String reference,
            @RequestBody AccountVerificationDTO dto) {

        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Updating customer details for reference: {}", reference);

            // First find the mandate by reference
            Optional<DebitMandate> mandateOpt = debitMandateService.getDebitMandateByReference(reference);

            if (mandateOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "DebitMandate not found with reference: " + reference);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Update using the ID with Mono integration
            DebitMandate updatedMandate = debitMandateService.updateWithFollowUpDataAndMono(
                mandateOpt.get().getId(),
                dto,
                dto.getFirstName(),
                dto.getLastName()
            );

            // Build response data
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("id", updatedMandate.getId());
            responseData.put("reference", updatedMandate.getReference());
            responseData.put("bvn", updatedMandate.getBvn() != null ? updatedMandate.getBvn() : "");
            responseData.put("customerAddress", updatedMandate.getCustomer_address() != null ? updatedMandate.getCustomer_address() : "");
            responseData.put("customerEmail", updatedMandate.getCustomer_email() != null ? updatedMandate.getCustomer_email() : "");
            responseData.put("customerPhone", updatedMandate.getCustomer_phone() != null ? updatedMandate.getCustomer_phone() : "");
            responseData.put("monoCustomerId", updatedMandate.getCustomer_id() != null ? updatedMandate.getCustomer_id() : "");

            response.put("success", true);
            response.put("message", "Customer details updated successfully");
            response.put("data", responseData);

            // Add warning if Mono customer creation failed
            if (updatedMandate.getCustomer_id() == null) {
                response.put("warning", "Customer details saved but Mono customer creation failed. Check logs for details.");
            }

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            logger.error("Runtime error updating customer details: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);

        } catch (Exception e) {
            logger.error("Error updating customer details: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Failed to update customer details");
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * GET endpoint to retrieve DebitMandate by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDebitMandateById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Optional<DebitMandate> mandate = debitMandateService.getDebitMandateById(id);

        if (mandate.isPresent()) {
            response.put("success", true);
            response.put("data", mandate.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "DebitMandate not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    /**
     * GET endpoint to retrieve DebitMandate by reference
     */
    @GetMapping("/reference/{reference}")
    public ResponseEntity<Map<String, Object>> getDebitMandateByReference(@PathVariable String reference) {
        Map<String, Object> response = new HashMap<>();

        Optional<DebitMandate> mandate = debitMandateService.getDebitMandateByReference(reference);

        if (mandate.isPresent()) {
            response.put("success", true);
            response.put("data", mandate.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "DebitMandate not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
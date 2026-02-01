package com.koolboks.creditProject.controller;

import com.koolboks.creditProject.entity.Guarantor;
import com.koolboks.creditProject.repository.GuarantorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/application")
public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);
    private final GuarantorRepository guarantorRepository;

    public ApplicationController(GuarantorRepository guarantorRepository) {
        this.guarantorRepository = guarantorRepository;
    }

    /**
     * Get approved application details by customer BVN
     */
    @GetMapping("/success/{customerBvn}")
    public ResponseEntity<Map<String, Object>> getApprovedApplication(@PathVariable String customerBvn) {
        Map<String, Object> response = new HashMap<>();

        try {
            //Optional<Guarantor> guarantorOpt = guarantorRepository.findByCustomerBvnAndActiveTrue(customerBvn);

             Optional<Guarantor> guarantorOpt = guarantorRepository.findTopByCustomerBvnAndActiveTrueOrderByCreatedAtDesc(customerBvn);

            if (guarantorOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Application not found");
                return ResponseEntity.status(404).body(response);
            }

            Guarantor guarantor = guarantorOpt.get();

            // Check if guarantor is verified
            if (!guarantor.getOtpVerified()) {
                response.put("success", false);
                response.put("message", "Application not yet approved");
                return ResponseEntity.status(400).body(response);
            }

            // Build customer object
            Map<String, Object> customer = new HashMap<>();
            customer.put("firstName", guarantor.getCustomerFirstName());
            customer.put("lastName", guarantor.getCustomerLastName());
            customer.put("email", guarantor.getCustomerEmail());
            customer.put("bvn", guarantor.getCustomerBvn());
            customer.put("plan", guarantor.getCustomerPlan());
            customer.put("installmentDuration", guarantor.getCustomerInstallmentDuration());
            customer.put("gender", ""); // Add gender field if available in your Guarantor entity

            response.put("success", true);
            response.put("customer", customer);

            log.info("Approved application retrieved for BVN: {}", customerBvn);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching approved application: ", e);
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
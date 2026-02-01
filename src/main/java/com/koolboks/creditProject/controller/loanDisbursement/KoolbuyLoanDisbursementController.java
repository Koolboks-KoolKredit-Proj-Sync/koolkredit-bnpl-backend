package com.koolboks.creditProject.controller.loanDisbursement;



import com.koolboks.creditProject.entity.koolbuy_loan.KoolbuyLoanDisbursement;
import com.koolboks.creditProject.service.loanDisbursement.KoolbuyLoanDisbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/disbursements")
@CrossOrigin(origins = "*")
public class KoolbuyLoanDisbursementController {

    @Autowired
    private KoolbuyLoanDisbursementService disbursementService;

    /**
     * Get all disbursements with pagination
     */
    @GetMapping
    public ResponseEntity<?> getAllDisbursements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<KoolbuyLoanDisbursement> disbursements = disbursementService.getAllDisbursements(pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("disbursements", disbursements.getContent());
            response.put("currentPage", disbursements.getNumber());
            response.put("totalItems", disbursements.getTotalElements());
            response.put("totalPages", disbursements.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Get disbursement by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDisbursementById(@PathVariable Long id) {
        try {
            KoolbuyLoanDisbursement disbursement = disbursementService.getDisbursementById(id)
                    .orElseThrow(() -> new Exception("Disbursement not found with ID: " + id));
            return ResponseEntity.ok(disbursement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Get disbursement by loan reference
     */
    @GetMapping("/loan-ref/{loanRef}")
    public ResponseEntity<?> getDisbursementByLoanRef(@PathVariable String loanRef) {
        try {
            KoolbuyLoanDisbursement disbursement = disbursementService.getDisbursementByLoanRef(loanRef)
                    .orElseThrow(() -> new Exception("Disbursement not found with loan reference: " + loanRef));
            return ResponseEntity.ok(disbursement);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Get disbursements by agent ID
     */
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<?> getDisbursementsByAgentId(@PathVariable String agentId) {
        try {
            List<KoolbuyLoanDisbursement> disbursements = disbursementService.getDisbursementsByAgentId(agentId);
            return ResponseEntity.ok(disbursements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Get confirmed disbursements
     */
    @GetMapping("/confirmed")
    public ResponseEntity<?> getConfirmedDisbursements() {
        try {
            List<KoolbuyLoanDisbursement> disbursements = disbursementService.getConfirmedDisbursements();
            return ResponseEntity.ok(disbursements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Get pending disbursements
     */
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingDisbursements() {
        try {
            List<KoolbuyLoanDisbursement> disbursements = disbursementService.getPendingDisbursements();
            return ResponseEntity.ok(disbursements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Search by customer email
     */
    @GetMapping("/search/email/{email}")
    public ResponseEntity<?> searchByCustomerEmail(@PathVariable String email) {
        try {
            List<KoolbuyLoanDisbursement> disbursements = disbursementService.searchByCustomerEmail(email);
            return ResponseEntity.ok(disbursements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Create new disbursement (manual creation)
     */
    @PostMapping
    public ResponseEntity<?> createDisbursement(@RequestBody KoolbuyLoanDisbursement disbursement) {
        try {
            KoolbuyLoanDisbursement created = disbursementService.createDisbursement(disbursement);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Update disbursement
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDisbursement(
            @PathVariable Long id,
            @RequestBody KoolbuyLoanDisbursement disbursementDetails) {
        try {
            KoolbuyLoanDisbursement updated = disbursementService.updateDisbursement(id, disbursementDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Delete disbursement
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDisbursement(@PathVariable Long id) {
        try {
            disbursementService.deleteDisbursement(id);
            return ResponseEntity.ok(Map.of("message", "Disbursement deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Get statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalDisbursements", disbursementService.countAllDisbursements());
            stats.put("confirmedDisbursements", disbursementService.countConfirmedDisbursements());
            stats.put("pendingDisbursements", disbursementService.countPendingDisbursements());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
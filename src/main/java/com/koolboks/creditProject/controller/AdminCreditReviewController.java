package com.koolboks.creditProject.controller;

import com.koolboks.creditProject.service.AdminCreditReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Admin review endpoints consumed by:
 *  - AdminCreditReview.jsx  (GET data, POST decision)
 *  - Email "Review" button  (links to the React page via token)
 */
@RestController
@RequestMapping("/api/agent-followup/admin-review")
//@CrossOrigin(origins = "*")
public class AdminCreditReviewController {

    private final AdminCreditReviewService reviewService;

    public AdminCreditReviewController(AdminCreditReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * GET /api/agent-followup/admin-review/{token}/data
     * Returns the follow-up entity + stored credit report JSON for the admin page.
     */
    @GetMapping("/{token}/data")
    public ResponseEntity<?> getReviewData(@PathVariable String token) {
        try {
            Map<String, Object> data = reviewService.getReviewData(token);
            if (data == null) {
                return ResponseEntity.status(404).body(Map.of("error", "Review not found or token expired"));
            }
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/agent-followup/admin-review/{token}/submit
     * Body: {
     *   decision:                 "APPROVE" | "REJECT",
     *   overrideClassification:   "GREEN" | "AMBER" | "RED"   (optional),
     *   overridePlan:             string                       (optional),
     *   overrideInstalment:       string                       (optional),
     *   rejectionReason:          string                       (required when REJECT),
     *   adminNotes:               string                       (optional)
     * }
     */
    @PostMapping("/{token}/submit")
    public ResponseEntity<?> submitReview(
            @PathVariable String token,
            @RequestBody AdminReviewDecisionRequest body) {
        try {
            boolean success = reviewService.processReviewDecision(token, body);
            if (!success) {
                return ResponseEntity.status(404).body(Map.of("success", false, "message", "Review not found or already processed"));
            }
            return ResponseEntity.ok(Map.of("success", true, "message", "Decision recorded and customer notified"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    // ── inner DTO ──────────────────────────────────────────────────────────────
    public static class AdminReviewDecisionRequest {
        private String decision;               // "APPROVE" | "REJECT"
        private String overrideClassification; // "GREEN" | "AMBER" | "RED"
        private String overridePlan;
        private String overrideInstalment;
        private String rejectionReason;
        private String adminNotes;

        // getters & setters
        public String getDecision()               { return decision; }
        public void   setDecision(String v)       { decision = v; }
        public String getOverrideClassification() { return overrideClassification; }
        public void   setOverrideClassification(String v) { overrideClassification = v; }
        public String getOverridePlan()           { return overridePlan; }
        public void   setOverridePlan(String v)   { overridePlan = v; }
        public String getOverrideInstalment()     { return overrideInstalment; }
        public void   setOverrideInstalment(String v) { overrideInstalment = v; }
        public String getRejectionReason()        { return rejectionReason; }
        public void   setRejectionReason(String v){ rejectionReason = v; }
        public String getAdminNotes()             { return adminNotes; }
        public void   setAdminNotes(String v)     { adminNotes = v; }
    }
}
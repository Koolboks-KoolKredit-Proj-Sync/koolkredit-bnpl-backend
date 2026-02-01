package com.koolboks.creditProject.controller;

import com.koolboks.creditProject.service.OfferLetterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/offer-letter")
//@CrossOrigin(origins = "*")
public class OfferLetterController {

    private final OfferLetterService offerLetterService;

    public OfferLetterController(OfferLetterService offerLetterService) {
        this.offerLetterService = offerLetterService;
    }

    /**
     * Generate and send offer letter for a guarantor
     * This endpoint can be called manually if needed, or it's automatically
     * triggered after OTP verification
     */
    @PostMapping("/generate/{guarantorId}")
    public ResponseEntity<Map<String, Object>> generateOfferLetter(@PathVariable Long guarantorId) {
        Map<String, Object> response = offerLetterService.generateAndSendOfferLetter(guarantorId);

        if (Boolean.TRUE.equals(response.get("success"))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Resend offer letter if needed
     */
    @PostMapping("/resend/{guarantorId}")
    public ResponseEntity<Map<String, Object>> resendOfferLetter(@PathVariable Long guarantorId) {
        Map<String, Object> response = offerLetterService.generateAndSendOfferLetter(guarantorId);

        if (Boolean.TRUE.equals(response.get("success"))) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
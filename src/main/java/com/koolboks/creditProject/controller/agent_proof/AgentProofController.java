package com.koolboks.creditProject.controller.agent_proof;



import com.koolboks.creditProject.dto.agent_proof.AgentProofSubmissionRequest;
import com.koolboks.creditProject.service.agent_proof.AgentProofService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;




@RestController
@RequestMapping("/v1/api")
//@CrossOrigin(origins = "*")
public class AgentProofController {

    @Autowired
    private AgentProofService agentProofService;

    @GetMapping("/agent-proof-data/{applicationReference}")
    public ResponseEntity<?> getAgentProofData(@PathVariable String applicationReference) {
        try {
            Map<String, Object> response = agentProofService.getAgentProofData(applicationReference);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping(value = "/submit-proof/{applicationReference}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitProof(
            @PathVariable String applicationReference,
            @RequestParam("store_name") String storeName,
            @RequestParam("store_location") String storeLocation,
            @RequestParam("confirmation_date") String confirmationDate,
            @RequestParam("receipt_image") MultipartFile receiptImage,
            // Agent data from frontend
            @RequestParam("agent_name") String agentName,
            @RequestParam("agent_email") String agentEmail,
            @RequestParam("agent_id") String agentId,
            @RequestParam("agent_mobile") String agentMobile) {

        try {
            AgentProofSubmissionRequest request = new AgentProofSubmissionRequest();

            // Store information
            request.setStoreName(storeName);
            request.setStoreLocation(storeLocation);
            request.setConfirmationDate(LocalDate.parse(confirmationDate));
            request.setReceiptImage(receiptImage);

            // Agent information
            request.setAgentName(agentName);
            request.setAgentEmail(agentEmail);
            request.setAgentId(agentId);
            request.setAgentMobile(agentMobile);

            agentProofService.submitProof(applicationReference, request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Proof submitted successfully");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/confirm-paygo-configuration/{disbursementId}")
    public ResponseEntity<?> confirmPaygoConfiguration(@PathVariable Long disbursementId) {
        try {
            agentProofService.confirmPaygoConfiguration(disbursementId);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Paygo configuration confirmed successfully");
            response.put("success", true);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}











//@RestController
//@RequestMapping("/v1/api")
////@CrossOrigin(origins = "*")
//public class AgentProofController {
//
//    @Autowired
//    private AgentProofService agentProofService;
//
//    @GetMapping("/agent-proof-data/{paymentId}")
//    public ResponseEntity<?> getAgentProofData(@PathVariable String paymentId) {
//        try {
//            Map<String, Object> response = agentProofService.getAgentProofData(paymentId);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//
//    @PostMapping(value = "/submit-proof/{paymentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> submitProof(
//            @PathVariable String paymentId,
//            @RequestParam("store_name") String storeName,
//            @RequestParam("store_location") String storeLocation,
//            @RequestParam("confirmation_date") String confirmationDate,
//            @RequestParam("receipt_image") MultipartFile receiptImage) {
//
//        try {
//            AgentProofSubmissionRequest request = new AgentProofSubmissionRequest();
//            request.setStoreName(storeName);
//            request.setStoreLocation(storeLocation);
//            request.setConfirmationDate(LocalDate.parse(confirmationDate));
//            request.setReceiptImage(receiptImage);
//
//            agentProofService.submitProof(paymentId, request);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "Proof submitted successfully");
//            response.put("success", true);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("message", e.getMessage());
//            errorResponse.put("success", false);
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//
//    @PostMapping("/confirm-paygo-configuration/{disbursementId}")
//    public ResponseEntity<?> confirmPaygoConfiguration(@PathVariable Long disbursementId) {
//        try {
//            agentProofService.confirmPaygoConfiguration(disbursementId);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "Paygo configuration confirmed successfully");
//            response.put("success", true);
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("message", e.getMessage());
//            errorResponse.put("success", false);
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//}
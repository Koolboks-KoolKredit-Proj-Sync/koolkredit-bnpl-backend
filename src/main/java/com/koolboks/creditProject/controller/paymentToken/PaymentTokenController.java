package com.koolboks.creditProject.controller.paymentToken;

import com.koolboks.creditProject.entity.paymentToken.PaymentToken;
import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.repository.PaymentTokenRepository;
import com.koolboks.creditProject.service.loan_repayment_service.LoanRepaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/payment-tokens")
//@CrossOrigin(origins = "*")
public class PaymentTokenController {

    @Autowired
    private PaymentTokenRepository paymentTokenRepository;

    @Autowired
    private LoanRepaymentService loanRepaymentService;

    /**
     * Validate payment token (called by React frontend)
     * Returns token details including late fee if applicable
     */
    @GetMapping("/validate/{token}")
    public ResponseEntity<?> validateToken(@PathVariable String token) {
        try {
            PaymentToken paymentToken = paymentTokenRepository.findByToken(token)
                .orElse(null);

            if (paymentToken == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("valid", false);
                error.put("error", "invalid");
                error.put("message", "Invalid payment link");
                return ResponseEntity.status(404).body(error);
            }

            // Check if used
            if (paymentToken.getIsUsed()) {
                Map<String, Object> error = new HashMap<>();
                error.put("valid", false);
                error.put("error", "used");
                error.put("message", "This payment link has already been used");
                return ResponseEntity.status(400).body(error);
            }

            // Check if expired
            if (paymentToken.isExpired()) {
                Map<String, Object> error = new HashMap<>();
                error.put("valid", false);
                error.put("error", "expired");
                error.put("message", "This payment link has expired. A new link with late fees will be sent.");
                return ResponseEntity.status(400).body(error);
            }

            // Get loan details
            LoanRepayment loan = loanRepaymentService.getLoanRepaymentByReference(
                paymentToken.getLoanReference()
            );

            // Return valid token data (including late fee info)
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("token", paymentToken.getToken());
            response.put("loanReference", paymentToken.getLoanReference());
            response.put("instalmentNumber", paymentToken.getInstalmentNumber());
            response.put("amount", paymentToken.getAmount());  // Current amount (with late fee if applicable)
            response.put("originalAmount", paymentToken.getOriginalAmount());  // Original amount
            response.put("lateFee", paymentToken.getLateFee());  // Late fee amount
            response.put("hasLateFee", paymentToken.getHasLateFee());  // Boolean flag
            response.put("expiresAt", paymentToken.getExpiresAt());
            response.put("customerName", loan.getFullName());
            response.put("email", loan.getEmail());
            response.put("phone", loan.getPhone());
            response.put("amountPaid", loan.getAmountPaid());
            response.put("remainingBalance", loan.getRemainingBalance());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("valid", false);
            error.put("error", "server_error");
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Mark token as used (called after successful payment)
     */
    @PostMapping("/mark-used/{token}")
    public ResponseEntity<?> markTokenUsed(@PathVariable String token, @RequestBody Map<String, Object> payload) {
        try {
            PaymentToken paymentToken = paymentTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

            paymentToken.setIsUsed(true);
            paymentToken.setUsedAt(LocalDateTime.now());
            paymentTokenRepository.save(paymentToken);

            System.out.println("✅ Token marked as used: " + token);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token marked as used");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}























//package com.koolboks.creditProject.controller.paymentToken;
//
//
//
//import com.koolboks.creditProject.entity.paymentToken.PaymentToken;
//import com.koolboks.creditProject.entity.LoanRepayment;
//import com.koolboks.creditProject.repository.PaymentTokenRepository;
//import com.koolboks.creditProject.service.loan_repayment_service.LoanRepaymentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/v1/api/payment-tokens")
////@CrossOrigin(origins = "*")
//public class PaymentTokenController {
//
//    @Autowired
//    private PaymentTokenRepository paymentTokenRepository;
//
//    @Autowired
//    private LoanRepaymentService loanRepaymentService;
//
//    /**
//     * Validate payment token (called by React frontend)
//     */
//    @GetMapping("/validate/{token}")
//    public ResponseEntity<?> validateToken(@PathVariable String token) {
//        try {
//            PaymentToken paymentToken = paymentTokenRepository.findByToken(token)
//                .orElse(null);
//
//            if (paymentToken == null) {
//                Map<String, Object> error = new HashMap<>();
//                error.put("valid", false);
//                error.put("error", "invalid");
//                error.put("message", "Invalid payment link");
//                return ResponseEntity.status(404).body(error);
//            }
//
//            // Check if used
//            if (paymentToken.getIsUsed()) {
//                Map<String, Object> error = new HashMap<>();
//                error.put("valid", false);
//                error.put("error", "used");
//                error.put("message", "This payment link has already been used");
//                return ResponseEntity.status(400).body(error);
//            }
//
//            // Check if expired
//            if (paymentToken.isExpired()) {
//                Map<String, Object> error = new HashMap<>();
//                error.put("valid", false);
//                error.put("error", "expired");
//                error.put("message", "This payment link has expired");
//                return ResponseEntity.status(400).body(error);
//            }
//
//            // Get loan details
//            LoanRepayment loan = loanRepaymentService.getLoanRepaymentByReference(
//                paymentToken.getLoanReference()
//            );
//
//            // Return valid token data
//            Map<String, Object> response = new HashMap<>();
//            response.put("valid", true);
//            response.put("token", paymentToken.getToken());
//            response.put("loanReference", paymentToken.getLoanReference());
//            response.put("instalmentNumber", paymentToken.getInstalmentNumber());
//            response.put("amount", paymentToken.getAmount());
//            response.put("expiresAt", paymentToken.getExpiresAt());
//            response.put("customerName", loan.getFullName());
//            response.put("email", loan.getEmail());
//            response.put("phone", loan.getPhone());
//            response.put("amountPaid", loan.getAmountPaid());
//            response.put("remainingBalance", loan.getRemainingBalance());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> error = new HashMap<>();
//            error.put("valid", false);
//            error.put("error", "server_error");
//            error.put("message", e.getMessage());
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//
//    /**
//     * Mark token as used (called after successful payment)
//     */
//    @PostMapping("/mark-used/{token}")
//    public ResponseEntity<?> markTokenUsed(@PathVariable String token, @RequestBody Map<String, Object> payload) {
//        try {
//            PaymentToken paymentToken = paymentTokenRepository.findByToken(token)
//                .orElseThrow(() -> new RuntimeException("Token not found"));
//
//            paymentToken.setIsUsed(true);
//            paymentToken.setUsedAt(LocalDateTime.now());
//            paymentTokenRepository.save(paymentToken);
//
//            System.out.println("✅ Token marked as used: " + token);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Token marked as used");
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            Map<String, Object> error = new HashMap<>();
//            error.put("success", false);
//            error.put("message", e.getMessage());
//            return ResponseEntity.status(500).body(error);
//        }
//    }
//}
//

package com.koolboks.creditProject.controller.loan;



import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.service.loan.LoanActivationEmailService;
import com.koolboks.creditProject.service.loan_repayment_service.LoanRepaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
//@CrossOrigin(origins = "*")
public class LoanActivationController {


    private final LoanActivationEmailService loanActivationEmailService;
    private final LoanRepaymentService loanRepaymentService;

    @Autowired
    public LoanActivationController(
            LoanActivationEmailService loanActivationEmailService,
            LoanRepaymentService loanRepaymentService
    ) {
        this.loanActivationEmailService = loanActivationEmailService;
        this.loanRepaymentService = loanRepaymentService;
    }

//    @Autowired
//    private LoanActivationEmailService loanActivationEmailService;

//

    @PostMapping("/activate-loan")
    public ResponseEntity<?> activateLoan(@RequestBody Map<String, Object> activationData) {
        try {
            System.out.println("=== ACTIVATING LOAN ===");
            System.out.println("Order ID: " + activationData.get("orderId"));
            System.out.println("Loan Ref: " + activationData.get("customerLoanRef"));

            loanActivationEmailService.sendLoanActivationEmail(activationData);

            System.out.println("=== LOAN ACTIVATION COMPLETED ===");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Loan activated successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error activating loan: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Failed to activate loan: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/loan-repayment/{loanReference}")
    public ResponseEntity<?> getLoanRepayment(@PathVariable String loanReference) {
        try {
            // This endpoint is called by Django to get loan details
            LoanRepayment loan = loanRepaymentService.getLoanRepaymentByReference(loanReference);

            Map<String, Object> response = new HashMap<>();
            response.put("fullName", loan.getFullName());
            response.put("email", loan.getEmail());
            response.put("phone", loan.getPhone());
            response.put("monthlyRepayment", loan.getMonthlyRepayment());
            response.put("amountPaid", loan.getAmountPaid());
            response.put("remainingBalance", loan.getRemainingBalance());
            response.put("numberOfMonths", loan.getNumberOfMonths());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
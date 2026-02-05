package com.koolboks.creditProject.controller.loan;




import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.service.loan_repayment_service.LoanRepaymentService;
import com.koolboks.creditProject.service.loan.PaymentConfirmationEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
//@CrossOrigin(origins = "*")
public class LoanPaymentController {

    @Autowired
    private LoanRepaymentService loanRepaymentService;

    @Autowired
    private PaymentConfirmationEmailService paymentConfirmationEmailService;

    /**
     * Update loan repayment after successful payment
     * BUSINESS LOGIC:
     * 1. amount_paid += payment_amount
     * 2. remaining_balance -= payment_amount
     * 3. number_of_months -= 1
     * 4. If late fee: remaining_balance += late_fee
     * 5. Send confirmation emails to customer, guarantor, agent
     */
    @PostMapping("/update-loan-payment")
    public ResponseEntity<?> updateLoanPayment(@RequestBody Map<String, Object> paymentData) {
        try {
            String loanReference = (String) paymentData.get("loanReference");
            int instalmentNumber = (Integer) paymentData.get("instalmentNumber");

            // Get payment amount and late fee
            BigDecimal paymentAmount = new BigDecimal(paymentData.get("paymentAmount").toString());
            BigDecimal lateFee = paymentData.containsKey("lateFee")
                ? new BigDecimal(paymentData.get("lateFee").toString())
                : BigDecimal.ZERO;

            String paymentReference = (String) paymentData.get("paymentReference");

            System.out.println("=== UPDATING LOAN PAYMENT ===");
            System.out.println("Loan Reference: " + loanReference);
            System.out.println("Instalment: " + instalmentNumber);
            System.out.println("Payment Amount: ₦" + paymentAmount);
            System.out.println("Late Fee: ₦" + lateFee);

            // Get loan repayment
            LoanRepayment loan = loanRepaymentService.getLoanRepaymentByReference(loanReference);

            // BUSINESS LOGIC IMPLEMENTATION

            // 1. Update amount_paid
            BigDecimal newAmountPaid = loan.getAmountPaid().add(paymentAmount);
            loan.setAmountPaid(newAmountPaid);

            // 2. Update remaining_balance
            BigDecimal newRemainingBalance = loan.getRemainingBalance().subtract(paymentAmount);

            // 3. If there's a late fee, ADD it to remaining balance
            if (lateFee.compareTo(BigDecimal.ZERO) > 0) {
                newRemainingBalance = newRemainingBalance.add(lateFee);
                System.out.println("⚠️ Late fee of ₦" + lateFee + " added to remaining balance");
            }

            loan.setRemainingBalance(newRemainingBalance);

            // 4. Decrease number_of_months by 1
            int remainingMonths = loan.getNumberOfMonths() - 1;
            loan.setNumberOfMonths(remainingMonths);

            System.out.println("📊 Updated Values:");
            System.out.println("   Amount Paid: ₦" + newAmountPaid);
            System.out.println("   Remaining Balance: ₦" + newRemainingBalance);
            System.out.println("   Remaining Months: " + remainingMonths);

            // 5. Update status
            if (newRemainingBalance.compareTo(BigDecimal.ZERO) <= 0 || remainingMonths <= 0) {
                loan.setRepaymentStatus(LoanRepayment.RepaymentStatus.COMPLETED);
                loan.setRemainingBalance(BigDecimal.ZERO);
                loan.setNumberOfMonths(0);
                System.out.println("✅ Loan marked as COMPLETED");
            } else if (loan.getRepaymentStatus() == LoanRepayment.RepaymentStatus.PENDING) {
                loan.setRepaymentStatus(LoanRepayment.RepaymentStatus.ACTIVE);
                System.out.println("Loan status changed to ACTIVE");
            }

            // Save changes
            LoanRepayment updatedLoan = loanRepaymentService.saveLoanRepayment(loan);

            // 6. Send payment confirmation emails (customer, guarantor, agent)
            Map<String, Object> confirmationData = new HashMap<>();
            confirmationData.put("loanReference", loanReference);
            confirmationData.put("instalmentNumber", instalmentNumber);
            confirmationData.put("paymentAmount", paymentAmount);
            confirmationData.put("lateFee", lateFee);
            confirmationData.put("paymentReference", paymentReference);
            confirmationData.put("customerName", updatedLoan.getFullName());
            confirmationData.put("customerEmail", updatedLoan.getEmail());
            confirmationData.put("guarantorEmail", updatedLoan.getGuarantorContact());
            confirmationData.put("amountPaid", updatedLoan.getAmountPaid());
            confirmationData.put("remainingBalance", updatedLoan.getRemainingBalance());
            confirmationData.put("remainingMonths", updatedLoan.getNumberOfMonths());
            confirmationData.put("totalInstalments", instalmentNumber); // How many paid so far
            confirmationData.put("isCompleted", updatedLoan.getRepaymentStatus() == LoanRepayment.RepaymentStatus.COMPLETED);

            paymentConfirmationEmailService.sendPaymentConfirmationEmails(confirmationData);

            System.out.println("=== LOAN PAYMENT UPDATE COMPLETED ===");

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment updated successfully");
            response.put("amountPaid", updatedLoan.getAmountPaid());
            response.put("remainingBalance", updatedLoan.getRemainingBalance());
            response.put("remainingMonths", updatedLoan.getNumberOfMonths());
            response.put("status", updatedLoan.getRepaymentStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error updating loan payment: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("success", "false");
            error.put("message", "Failed to update payment: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}

















//import com.koolboks.creditProject.entity.LoanRepayment;
//import com.koolboks.creditProject.service.loan_repayment_service.LoanRepaymentService;
//import com.koolboks.creditProject.service.loan.PaymentConfirmationEmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/v1/api")
////@CrossOrigin(origins = "*")
//public class LoanPaymentController {
//
//    @Autowired
//    private LoanRepaymentService loanRepaymentService;
//
//    @Autowired
//    private PaymentConfirmationEmailService paymentConfirmationEmailService;
//
//    @PostMapping("/update-loan-payment")
//    public ResponseEntity<?> updateLoanPayment(@RequestBody Map<String, Object> paymentData) {
//        try {
//            String loanReference = (String) paymentData.get("loanReference");
//            int instalmentNumber = (Integer) paymentData.get("instalmentNumber");
//            BigDecimal paymentAmount = new BigDecimal(paymentData.get("paymentAmount").toString());
//            String paymentReference = (String) paymentData.get("paymentReference");
//
//            System.out.println("=== UPDATING LOAN PAYMENT ===");
//            System.out.println("Loan Reference: " + loanReference);
//            System.out.println("Instalment: " + instalmentNumber);
//            System.out.println("Amount: " + paymentAmount);
//
//            // Update loan repayment
//            LoanRepayment loan = loanRepaymentService.updatePayment(loanReference, paymentAmount);
//
//            // Calculate remaining months
//            BigDecimal paidInstalments = loan.getAmountPaid().divide(loan.getMonthlyRepayment(), BigDecimal.ROUND_DOWN);
//            int remainingMonths = loan.getNumberOfMonths() - paidInstalments.intValue();
//
//            // Send payment confirmation emails (customer, guarantor, agent)
//            Map<String, Object> confirmationData = new HashMap<>();
//            confirmationData.put("loanReference", loanReference);
//            confirmationData.put("instalmentNumber", instalmentNumber);
//            confirmationData.put("paymentAmount", paymentAmount);
//            confirmationData.put("paymentReference", paymentReference);
//            confirmationData.put("customerName", loan.getFullName());
//            confirmationData.put("customerEmail", loan.getEmail());
//            confirmationData.put("guarantorEmail", loan.getGuarantorContact());
//            confirmationData.put("amountPaid", loan.getAmountPaid());
//            confirmationData.put("remainingBalance", loan.getRemainingBalance());
//            confirmationData.put("remainingMonths", remainingMonths);
//            confirmationData.put("totalInstalments", loan.getNumberOfMonths());
//
//            paymentConfirmationEmailService.sendPaymentConfirmationEmails(confirmationData);
//
//            System.out.println("=== LOAN PAYMENT UPDATED SUCCESSFULLY ===");
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("message", "Payment updated successfully");
//            response.put("remainingBalance", loan.getRemainingBalance());
//            response.put("status", loan.getRepaymentStatus());
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            System.err.println("Error updating loan payment: " + e.getMessage());
//            e.printStackTrace();
//
//            Map<String, String> error = new HashMap<>();
//            error.put("success", "false");
//            error.put("message", "Failed to update payment: " + e.getMessage());
//            return ResponseEntity.internalServerError().body(error);
//        }
//    }
//}


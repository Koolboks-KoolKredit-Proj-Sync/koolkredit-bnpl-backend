package com.koolboks.creditProject.service.loan_repayment_service;




import com.koolboks.creditProject.entity.AgentFollowUp;
import com.koolboks.creditProject.entity.Guarantor;
import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.repository.AgentFollowUpRepository;
import com.koolboks.creditProject.repository.LoanRepaymentRepository;
import com.koolboks.creditProject.service.loan_calculator.LoanCalculatorService.LoanCalculation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class LoanRepaymentService {

    private static final Logger log = LoggerFactory.getLogger(LoanRepaymentService.class);

    private final LoanRepaymentRepository loanRepaymentRepository;
    private final AgentFollowUpRepository agentFollowUpRepository;

    public LoanRepaymentService(LoanRepaymentRepository loanRepaymentRepository,
                               AgentFollowUpRepository agentFollowUpRepository) {
        this.loanRepaymentRepository = loanRepaymentRepository;
        this.agentFollowUpRepository = agentFollowUpRepository;
    }

    /**
     * Create loan repayment record from guarantor and loan calculation data
     */
    @Transactional
    public LoanRepayment createLoanRepayment(Guarantor guarantor,
                                            LoanCalculation calculation,
                                            String applicationReference) {

        log.info("Creating loan repayment record for application reference: {}", applicationReference);

        try {
            // Get AgentFollowUp data using the guarantor's BVN (most recent record)
            AgentFollowUp agentFollowUp = agentFollowUpRepository.findTopByBvnOrderByCreatedAtDesc(guarantor.getCustomerBvn())
                .orElseThrow(() -> new RuntimeException(
                    "AgentFollowUp not found for BVN: " + guarantor.getCustomerBvn()
                ));

            log.info("Found AgentFollowUp record for BVN: {}", guarantor.getCustomerBvn());

            LoanRepayment loanRepayment = new LoanRepayment();

            // Populate from Guarantor and Calculation
            String fullName = guarantor.getCustomerFirstName() + " " + guarantor.getCustomerLastName();
            loanRepayment.setFullName(fullName);
            loanRepayment.setLoanReference(applicationReference);
            loanRepayment.setEmail(guarantor.getCustomerEmail());
            loanRepayment.setPhone(guarantor.getMobileNumber());

            // Duration from calculation
            String loanDuration = "Omolope".equals(calculation.getPlan())
                ? calculation.getDays() + " days"
                : calculation.getMonths() + " months";
            loanRepayment.setLoanDuration(loanDuration);
            loanRepayment.setNumberOfMonths(calculation.getMonths());

            // Payment details from calculation
            BigDecimal monthlyRepayment = "Omolope".equals(calculation.getPlan())
                ? calculation.getDailyPayment()
                : calculation.getMonthlyPayment();
            loanRepayment.setMonthlyRepayment(monthlyRepayment);
            loanRepayment.setTotalInstalment(calculation.getInstalmentTotal());

            // Customer identification from AgentFollowUp
            loanRepayment.setCustomerBvn(agentFollowUp.getBvn());
            loanRepayment.setCustomerNin(agentFollowUp.getNin());

            // Guarantor contact (email)
            loanRepayment.setGuarantorContact(guarantor.getGuarantorEmail());

            // Initialize repayment tracking
            loanRepayment.setRepaymentStatus(LoanRepayment.RepaymentStatus.PENDING);
            loanRepayment.setAmountPaid(BigDecimal.ZERO);
            loanRepayment.setRemainingBalance(calculation.getInstalmentTotal());

            // Save to database
            LoanRepayment savedRepayment = loanRepaymentRepository.save(loanRepayment);

            log.info("✅ Loan repayment record created successfully with ID: {}", savedRepayment.getId());
            log.info("   Reference: {}", savedRepayment.getLoanReference());
            log.info("   Customer: {}", savedRepayment.getFullName());
            log.info("   BVN: {}", savedRepayment.getCustomerBvn());
            log.info("   NIN: {}", savedRepayment.getCustomerNin());
            log.info("   Total Instalment: {}", savedRepayment.getTotalInstalment());
            log.info("   Monthly Repayment: {}", savedRepayment.getMonthlyRepayment());
            log.info("   Guarantor Contact: {}", savedRepayment.getGuarantorContact());

            return savedRepayment;

        } catch (Exception e) {
            log.error("❌ Error creating loan repayment record for reference: {}", applicationReference, e);
            throw new RuntimeException("Failed to create loan repayment record: " + e.getMessage(), e);
        }
    }

    /**
     * Update payment status and amounts
     */
    @Transactional
    public LoanRepayment updatePayment(String loanReference, BigDecimal paymentAmount) {
        log.info("Updating payment for loan reference: {}, amount: {}", loanReference, paymentAmount);

        LoanRepayment repayment = loanRepaymentRepository.findByLoanReference(loanReference)
            .orElseThrow(() -> new RuntimeException("Loan repayment not found: " + loanReference));

        BigDecimal newAmountPaid = repayment.getAmountPaid().add(paymentAmount);
        repayment.setAmountPaid(newAmountPaid);

        // Update status based on payment
        if (newAmountPaid.compareTo(repayment.getTotalInstalment()) >= 0) {
            repayment.setRepaymentStatus(LoanRepayment.RepaymentStatus.COMPLETED);
            repayment.setRemainingBalance(BigDecimal.ZERO);
            log.info("✅ Loan {} marked as COMPLETED", loanReference);
        } else if (repayment.getRepaymentStatus() == LoanRepayment.RepaymentStatus.PENDING) {
            repayment.setRepaymentStatus(LoanRepayment.RepaymentStatus.ACTIVE);
            log.info("Loan {} status changed to ACTIVE", loanReference);
        }

        log.info("Payment updated - Amount Paid: {}, Remaining: {}",
                 repayment.getAmountPaid(), repayment.getRemainingBalance());

        return loanRepaymentRepository.save(repayment);
    }

    /**
     * Mark loan as defaulted
     */
    @Transactional
    public LoanRepayment markAsDefaulted(String loanReference) {
        LoanRepayment repayment = loanRepaymentRepository.findByLoanReference(loanReference)
            .orElseThrow(() -> new RuntimeException("Loan repayment not found: " + loanReference));

        repayment.setRepaymentStatus(LoanRepayment.RepaymentStatus.DEFAULTED);
        log.warn("⚠️ Loan {} marked as DEFAULTED", loanReference);

        return loanRepaymentRepository.save(repayment);
    }

    /**
     * Get loan repayment by reference
     */
    public LoanRepayment getLoanRepaymentByReference(String loanReference) {
        return loanRepaymentRepository.findByLoanReference(loanReference)
            .orElseThrow(() -> new RuntimeException("Loan repayment not found: " + loanReference));
    }

    /**
     * Get loan repayment by customer BVN
     */
    public LoanRepayment getLoanRepaymentByBvn(String bvn) {
        return loanRepaymentRepository.findByCustomerBvn(bvn)
            .orElseThrow(() -> new RuntimeException("Loan repayment not found for BVN: " + bvn));
    }

    /**
     * Get loan repayment by email
     */
    public LoanRepayment getLoanRepaymentByEmail(String email) {
        return loanRepaymentRepository.findTopByEmailOrderByIdDesc(email)
            .orElseThrow(() -> new RuntimeException("Loan repayment not found for email: " + email));
    }

    /**
 * Save loan repayment (for updating after payment)
 */
@Transactional
public LoanRepayment saveLoanRepayment(LoanRepayment loanRepayment) {
    return loanRepaymentRepository.save(loanRepayment);
}
}


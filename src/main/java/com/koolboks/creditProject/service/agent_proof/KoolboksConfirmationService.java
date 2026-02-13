package com.koolboks.creditProject.service.agent_proof;


import com.koolboks.creditProject.entity.Guarantor;
import com.koolboks.creditProject.entity.koolboks_loan.KoolboksLoanDisbursement;
import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.repository.GuarantorRepository;
//import com.koolboks.creditProject.repository.KoolboksLoanDisbursementRepository;
import com.koolboks.creditProject.repository.KoolboksLoanDisbursementRepository;
//import com.koolboks.creditProject.repository.KoolbuyLoanDisbursementRepository;
import com.koolboks.creditProject.repository.LoanRepaymentRepository;
import com.koolboks.creditProject.service.loan.LoanActivationEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class KoolboksConfirmationService {

    private static final Logger log = LoggerFactory.getLogger(KoolboksConfirmationService.class);

    @Autowired
    private KoolboksLoanDisbursementRepository disbursementRepository;

    @Autowired
    private GuarantorRepository guarantorRepository;

    @Autowired
    private LoanRepaymentRepository loanRepaymentRepository;

    @Autowired
    private LoanActivationEmailService loanActivationEmailService;

    /**
     * Confirm initial payment and activate loan
     * Mimics the Koolbuy flow but for Koolboks
     */
    @Transactional
    public Map<String, Object> confirmInitialPayment(String customerLoanRef) throws Exception {
        log.info("=== CONFIRMING KOOLBOKS INITIAL PAYMENT ===");
        log.info("Customer Loan Reference: {}", customerLoanRef);

        try {
            // 1. Find or create KoolboksLoanDisbursement record
            KoolboksLoanDisbursement disbursement = disbursementRepository
                    .findByCustomerLoanRef(customerLoanRef)
                    .orElseGet(() -> {
                        log.info("No existing disbursement found, creating new record");
                        return createNewDisbursement(customerLoanRef);
                    });

            // 2. Check if already confirmed
            if (Boolean.TRUE.equals(disbursement.getInitialPaymentConfirmation())) {
                log.warn("Payment already confirmed for: {}", customerLoanRef);
                throw new Exception("Payment already confirmed for this loan reference");
            }

            // 3. Set initial payment confirmation to TRUE
            disbursement.setInitialPaymentConfirmation(true);
            disbursement.setPaymentDate(LocalDate.now());
            disbursementRepository.save(disbursement);
            log.info("✅ Initial payment confirmation set to TRUE");

            // 4. Update LoanRepayment status to ACTIVE
            LoanRepayment loanRepayment = loanRepaymentRepository
                    .findByLoanReference(customerLoanRef)
                    .orElseThrow(() -> new Exception("Loan repayment record not found: " + customerLoanRef));

            if (loanRepayment.getRepaymentStatus() == LoanRepayment.RepaymentStatus.PENDING) {
                loanRepayment.setRepaymentStatus(LoanRepayment.RepaymentStatus.ACTIVE);
                loanRepaymentRepository.save(loanRepayment);
                log.info("✅ Loan repayment status updated to ACTIVE");
            } else {
                log.info("ℹ️ Loan status already: {}", loanRepayment.getRepaymentStatus());
            }

            // 5. Send loan activation email
            Map<String, Object> emailData = buildActivationEmailData(disbursement, loanRepayment);
            loanActivationEmailService.sendLoanActivationEmail(emailData);
            log.info("✅ Loan activation email sent to: {}", disbursement.getCustomerEmail());

            // 6. Build response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Initial payment confirmed and loan activated successfully");
            response.put("customerLoanRef", customerLoanRef);
            response.put("customerName", disbursement.getCustomerFirstName() + " " + disbursement.getCustomerLastname());
            response.put("loanStatus", "ACTIVE");
            response.put("emailSent", true);

            log.info("=== KOOLBOKS CONFIRMATION COMPLETED ===");
            return response;

        } catch (Exception e) {
            log.error("❌ Error confirming Koolboks payment: {}", e.getMessage(), e);
            throw new Exception("Failed to confirm payment: " + e.getMessage());
        }
    }

    /**
     * Create new disbursement record from Guarantor data
     */
    private KoolboksLoanDisbursement createNewDisbursement(String customerLoanRef) {
        log.info("Creating new KoolboksLoanDisbursement from Guarantor data");

        Guarantor guarantor = guarantorRepository.findByApplicationReference(customerLoanRef)
                .orElseThrow(() -> new RuntimeException("Guarantor not found for reference: " + customerLoanRef));

        KoolboksLoanDisbursement disbursement = new KoolboksLoanDisbursement();

        // Set customer information from Guarantor
        disbursement.setCustomerLoanRef(customerLoanRef);
        disbursement.setCustomerFirstName(guarantor.getCustomerFirstName());
        disbursement.setCustomerLastname(guarantor.getCustomerLastName());
        disbursement.setCustomerEmail(guarantor.getCustomerEmail());
        disbursement.setCustomerPhoneNumber(guarantor.getCustomerPhoneNumber());

        // Set product information
        disbursement.setProductName(guarantor.getProductName());
        disbursement.setProductBrand(guarantor.getProductBrand());
        disbursement.setProductSize(guarantor.getProductSize());

        // Set loan information
        disbursement.setInitialInstalment(guarantor.getStorePrice());
        disbursement.setGuarantorEmail(guarantor.getGuarantorEmail());

        // Parse and set loan duration if available
        if (guarantor.getCustomerInstallmentDuration() != null) {
            try {
                disbursement.setCustomerLoanDuration(
                    Integer.parseInt(guarantor.getCustomerInstallmentDuration())
                );
            } catch (NumberFormatException e) {
                log.warn("Could not parse installment duration: {}", guarantor.getCustomerInstallmentDuration());
            }
        }

        // Note: Agent fields will remain null until agent proof is submitted
        // This is just for initial payment confirmation

        log.info("New disbursement record created for: {}", customerLoanRef);
        return disbursementRepository.save(disbursement);
    }

    /**
     * Build email data for loan activation
     */
    private Map<String, Object> buildActivationEmailData(
            KoolboksLoanDisbursement disbursement,
            LoanRepayment loanRepayment) {

        Map<String, Object> emailData = new HashMap<>();

        // Customer information
        emailData.put("customerFirstName", disbursement.getCustomerFirstName());
        emailData.put("customerLastName", disbursement.getCustomerLastname());
        emailData.put("customerEmail", disbursement.getCustomerEmail());
        emailData.put("customerPhoneNumber", disbursement.getCustomerPhoneNumber());
        emailData.put("customerLoanRef", disbursement.getCustomerLoanRef());
        emailData.put("customerLoanDuration", loanRepayment.getLoanDuration());

        // Product information
        emailData.put("productName", disbursement.getProductName());
        emailData.put("productBrand", disbursement.getProductBrand());
        emailData.put("productSize", disbursement.getProductSize());

        // Payment information
        emailData.put("initialInstalment", disbursement.getInitialInstalment());
        emailData.put("installationDate", LocalDate.now().toString());
        emailData.put("paymentDate", disbursement.getPaymentDate() != null
            ? disbursement.getPaymentDate().toString()
            : LocalDate.now().toString());

        // Agent information (may be null if not yet submitted)
        emailData.put("agentName", disbursement.getAgentName() != null
            ? disbursement.getAgentName()
            : "N/A");
        emailData.put("agentId", disbursement.getAgentId() != null
            ? disbursement.getAgentId()
            : "N/A");
        emailData.put("agentEmail", disbursement.getAgentEmail() != null
            ? disbursement.getAgentEmail()
            : "N/A");
        emailData.put("agentNumber", disbursement.getAgentNumber() != null
            ? disbursement.getAgentNumber()
            : "N/A");

        // Installation details (using current date as installation date)
        emailData.put("installerName", "Koolboks Technician");
        emailData.put("orderId", disbursement.getCustomerLoanRef());

        return emailData;
    }
}

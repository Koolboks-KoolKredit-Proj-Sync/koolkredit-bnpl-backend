package com.koolboks.creditProject.service.installation;



import com.koolboks.creditProject.dto.installation.InstallationVerificationDTO;
import com.koolboks.creditProject.dto.mono.MonoCreateMandateResponse;
import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
import com.koolboks.creditProject.repository.DebitMandateRepository;
import com.koolboks.creditProject.service.debitMandate.CreateMandateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.repository.LoanRepaymentRepository;
import com.koolboks.creditProject.service.loan.LoanActivationEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class InstallationVerificationService {

    private static final Logger logger = LoggerFactory.getLogger(InstallationVerificationService.class);

    @Autowired
    private DebitMandateRepository debitMandateRepository;

    @Autowired
    private CreateMandateService createMandateService;

    @Autowired
    private InstallationPinService installationPinService;

    @Autowired
    private MandatePollingService mandatePollingService;

    @Autowired
    private LoanRepaymentRepository loanRepaymentRepository;

    @Autowired
    private LoanActivationEmailService loanActivationEmailService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mono.api.key}")
    private String monoSecretKey;

    /**
     * Verify customer details and trigger mandate creation + PIN sending
     */
    public Map<String, Object> verifyAndProcessInstallation(InstallationVerificationDTO dto) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("=== STARTING INSTALLATION VERIFICATION ===");
            logger.info("Verification Type: {}", dto.getVerificationType());
            logger.info("Verification Value: {}", dto.getVerificationValue());
            logger.info("Order ID: {}", dto.getOrderId());

            // Step 1: Find matching DebitMandate
            Optional<DebitMandate> mandateOpt = findDebitMandateByVerification(dto);

            if (mandateOpt.isEmpty()) {
                logger.warn("❌ No matching DebitMandate found for: {} = {}",
                           dto.getVerificationType(), dto.getVerificationValue());
                response.put("success", false);
                response.put("message", "No matching record found. Please check your details.");
                return response;
            }

            DebitMandate debitMandate = mandateOpt.get();
            logger.info("✅ Found matching DebitMandate ID: {}", debitMandate.getId());
            logger.info("Customer: {}", debitMandate.getCustomer_name());

            // Step 2: Check if mandate already exists
            if (debitMandate.getMandate_id() != null && !debitMandate.getMandate_id().isEmpty()) {
                logger.info("⚠️ Mandate already exists for this customer. Mandate ID: {}",
                           debitMandate.getMandate_id());

                // Check if debit_account is already true
                if (debitMandate.isDebit_account()) {
                    logger.info("✅ Mandate is already approved and ready. Sending PIN...");
                    sendInstallationPin(debitMandate, dto.getOrderId());

                    response.put("success", true);
                    response.put("message", "Verification successful! Installation PIN has been sent to your email and phone.");
                    response.put("customerName", debitMandate.getCustomer_name());
                    response.put("customerEmail", debitMandate.getCustomer_email());
                    return response;
                } else {
                    logger.warn("⚠️ Mandate exists but not yet approved. Please complete mandate verification.");
                    response.put("success", false);
                    response.put("message", "Your mandate is pending approval. Please complete the verification process first.");
                    response.put("requiresVerification", true);
                    return response;
                }
            }

            // Step 3: Create mandate via Mono API
            logger.info("Creating mandate for DebitMandate ID: {}", debitMandate.getId());
            MonoCreateMandateResponse mandateResponse = createMandateService.createMandate(debitMandate.getId());

            if (mandateResponse == null || mandateResponse.getData() == null) {
                throw new RuntimeException("Failed to create mandate - no response from Mono API");
            }

            logger.info("✅ Mandate created successfully. Mandate ID: {}",
                       mandateResponse.getData().getMandateId());

            // Step 4: Check if mandate requires verification


            // Step 4: Check if mandate requires verification
if (!mandateResponse.getData().isApproved() || !mandateResponse.getData().isReadyToDebit()) {
    logger.warn("⚠️ Mandate created but requires customer verification");

    // Map transfer destinations to camelCase for frontend
    List<Map<String, Object>> destinations = new ArrayList<>();
    if (mandateResponse.getData().getTransferDestinations() != null) {
        for (MonoCreateMandateResponse.TransferDestination dest : mandateResponse.getData().getTransferDestinations()) {
            Map<String, Object> destMap = new HashMap<>();
            destMap.put("bankName", dest.getBankName());
            destMap.put("accountNumber", String.valueOf(dest.getAccountNumber()));
            destMap.put("icon", dest.getIcon());
            destMap.put("primaryColor", dest.getPrimaryColor());
            destinations.add(destMap);
        }
    }

    response.put("success", true);
    response.put("message", mandateResponse.getMessage());
    response.put("requiresVerification", true);
    response.put("mandateId", mandateResponse.getData().getMandateId());
    response.put("status", mandateResponse.getData().getStatus());
    response.put("transferDestinations", destinations);  // ← now camelCase ✅
    response.put("customerName", debitMandate.getCustomer_name());

    return response;
}




//            if (!mandateResponse.getData().isApproved() || !mandateResponse.getData().isReadyToDebit()) {
//                logger.warn("⚠️ Mandate created but requires customer verification");
//
//                response.put("success", true);
//                response.put("message", mandateResponse.getMessage());
//                response.put("requiresVerification", true);
//                response.put("mandateId", mandateResponse.getData().getMandateId());
//                response.put("status", mandateResponse.getData().getStatus());
//                response.put("transferDestinations", mandateResponse.getData().getTransferDestinations());
//                response.put("customerName", debitMandate.getCustomer_name());
//
//                return response;
//            }
//
            // Step 5: If mandate is approved and ready, send PIN
            logger.info("✅ Mandate is approved and ready. Sending installation PIN...");
            sendInstallationPin(debitMandate, dto.getOrderId());

            response.put("success", true);
            response.put("message", "Verification successful! Installation PIN has been sent to your email and phone.");
            response.put("mandateId", mandateResponse.getData().getMandateId());
            response.put("customerName", debitMandate.getCustomer_name());
            response.put("customerEmail", debitMandate.getCustomer_email());

            logger.info("=== INSTALLATION VERIFICATION COMPLETED SUCCESSFULLY ===");
            return response;

        } catch (Exception e) {
            logger.error("❌ Error in installation verification process", e);
            response.put("success", false);
            response.put("message", "An error occurred: " + e.getMessage());
            return response;
        }
    }

    /**
     * Find DebitMandate by verification type and value
     */
    private Optional<DebitMandate> findDebitMandateByVerification(InstallationVerificationDTO dto) {
        String type = dto.getVerificationType().toLowerCase();
        String value = dto.getVerificationValue().trim();

        logger.info("Searching DebitMandate by {} = {}", type, value);

        switch (type) {
            case "bvn":
                return debitMandateRepository.findByBvn(value);

            case "email":
                return debitMandateRepository.findByCustomerEmail(value);

            case "phone":
                // Normalize phone number (remove spaces, dashes, etc.)
                String normalizedPhone = normalizePhoneNumber(value);
                return debitMandateRepository.findByCustomerPhone(normalizedPhone);

            default:
                logger.warn("Invalid verification type: {}", type);
                return Optional.empty();
        }
    }

    /**
     * Normalize phone number by removing non-digit characters
     */
    private String normalizePhoneNumber(String phone) {
        // Remove all non-digit characters
        String normalized = phone.replaceAll("[^0-9]", "");

        // If it starts with country code, keep it as is
        // Otherwise, just return the normalized version
        return normalized;
    }

    /**
     * Send installation PIN to customer
     */
    private void sendInstallationPin(DebitMandate debitMandate, String orderId) {
        logger.info("Generating and sending installation PIN...");

        try {
            // Generate PIN
            String pin = installationPinService.generatePin(orderId);
            logger.info("Generated PIN for order: {}", orderId);

            // Send PIN via email
            if (debitMandate.getCustomer_email() != null && !debitMandate.getCustomer_email().isEmpty()) {
                installationPinService.sendPinByEmail(
                    debitMandate.getCustomer_email(),
                    pin,
                    orderId,
                    debitMandate.getCustomer_name()
                );
                logger.info("✅ PIN sent to email: {}", debitMandate.getCustomer_email());
            }

            // Send PIN via SMS
            if (debitMandate.getCustomer_phone() != null && !debitMandate.getCustomer_phone().isEmpty()) {
                installationPinService.sendPinBySms(
                    debitMandate.getCustomer_phone(),
                    pin,
                    orderId
                );
                logger.info("✅ PIN sent to phone: {}", debitMandate.getCustomer_phone());
            }

            logger.info("✅ Installation PIN sent successfully");

        } catch (Exception e) {
            logger.error("❌ Error sending installation PIN", e);
            throw new RuntimeException("Failed to send installation PIN: " + e.getMessage(), e);
        }
    }


        /**
     * Start async mandate approval polling after customer confirms transfer
     */
    public void startMandateApprovalPolling(String mandateId, String orderId) {
        logger.info("Starting mandate approval polling for mandateId: {}, orderId: {}",
            mandateId, orderId);

        // Find the DebitMandate so we can pass customer details to polling
        debitMandateRepository.findByMandateId(mandateId).ifPresentOrElse(
            debitMandate -> {
                // Start polling asynchronously
                mandatePollingService.pollUntilApprovedWithCustomer(
                    mandateId,
                    orderId,
                    debitMandate
                );
            },
            () -> {
                logger.error("❌ DebitMandate not found for mandateId: {}", mandateId);
                throw new RuntimeException("Mandate not found: " + mandateId);
            }
        );
    }

    /**
     * Activate loan after installer confirms installation with PIN + photo
     */
    public void activateLoan(Map<String, Object> data) {
        String orderId = String.valueOf(data.get("orderId"));
        logger.info("=== ACTIVATING LOAN FOR ORDER: {} ===", orderId);

        try {
            // Find LoanRepayment by BVN or email - try both
            String customerEmail = String.valueOf(data.get("customerEmail"));
            //Optional<LoanRepayment> loanOpt = loanRepaymentRepository.findByEmail(customerEmail);
            Optional<LoanRepayment> loanOpt = loanRepaymentRepository.findTopByEmailOrderByIdDesc(customerEmail);

            if (loanOpt.isPresent()) {
                LoanRepayment loan = loanOpt.get();

                if (loan.getRepaymentStatus() == LoanRepayment.RepaymentStatus.PENDING) {
                    loan.setRepaymentStatus(LoanRepayment.RepaymentStatus.ACTIVE);
                    loanRepaymentRepository.save(loan);
                    logger.info("✅ Loan activated for customer: {}", customerEmail);
                } else {
                    logger.info("ℹ️ Loan already has status: {}", loan.getRepaymentStatus());
                }

                // ✅ FIX: inject the actual loanReference from the found loan into data
                //data.put("loanReference", loan.getLoanReference()); // ← add this line

                data.put("customerLoanRef", loan.getLoanReference());

                // Send loan activation email
                loanActivationEmailService.sendLoanActivationEmail(data);
                logger.info("✅ Loan activation email sent");

            } else {
                logger.warn("⚠️ No LoanRepayment found for email: {}", customerEmail);
                // Still send activation email with available data
                loanActivationEmailService.sendLoanActivationEmail(data);
            }

        } catch (Exception e) {
            logger.error("❌ Error activating loan for order: {}", orderId, e);
            throw new RuntimeException("Failed to activate loan: " + e.getMessage(), e);
        }
    }
}
//package com.koolboks.creditProject.service.agent_proof;
//
//import com.koolboks.creditProject.dto.agent_proof.AgentProofSubmissionRequest;
//import com.koolboks.creditProject.entity.koolbuy_loan.KoolbuyLoanDisbursement;
//import com.koolboks.creditProject.entity.AgentFollowUp;
//import com.koolboks.creditProject.entity.Guarantor;
//import com.koolboks.creditProject.repository.KoolbuyLoanDisbursementRepository;
//import com.koolboks.creditProject.repository.AgentFollowUpRepository;
//import com.koolboks.creditProject.repository.GuarantorRepository;
//import com.koolboks.creditProject.service.email_paygo_config.PaygoEmailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//@Service
//public class AgentProofService {
//
//    @Autowired
//    private KoolbuyLoanDisbursementRepository disbursementRepository;
//
//    @Autowired
//    private GuarantorRepository guarantorRepository;
//
//    @Autowired
//    private AgentFollowUpRepository agentFollowUpRepository;
//
//    @Autowired
//    private PaygoEmailService emailService;
//
//    @Value("${file.upload.directory:uploads/receipts}")
//    private String uploadDirectory;
//
//    /**
//     * Get agent proof data for the form
//     * This returns the data that's displayed in the React form
//     */
//    public Map<String, Object> getAgentProofData(String applicationReference) {
//        // Fetch guarantor data using application reference
//        Guarantor guarantor = guarantorRepository.findByApplicationReference(applicationReference)
//                .orElseThrow(() -> new RuntimeException("Application reference not found: " + applicationReference));
//
//        // Fetch agent follow-up data
//        AgentFollowUp agentFollowUp = agentFollowUpRepository.findByGuarantorEmail(guarantor.getGuarantorEmail())
//                .orElse(agentFollowUpRepository.findByMobileNumber(guarantor.getCustomerPhoneNumber())
//                        .orElse(null));
//
//        Map<String, Object> data = new HashMap<>();
//
//        // Customer/Loan data
//        data.put("loan_ref", guarantor.getApplicationReference());
//        data.put("amount", guarantor.getStorePrice());
//        data.put("payment_date", java.time.LocalDate.now().toString());
//
//        // Agent data - you'll need to implement how you track/store this
//        // For now, returning placeholder - you should replace with actual agent tracking
//        data.put("agent_name", "Agent Name"); // TODO: Fetch from your agent tracking system
//        data.put("agent_id", "AGENT-ID"); // TODO: Fetch from your agent tracking system
//        data.put("agent_email", "agent@email.com"); // TODO: Fetch from your agent tracking system
//        data.put("agent_mobile", "1234567890"); // TODO: Fetch from your agent tracking system
//
//        return data;
//    }
//
//    /**
//     * Submit proof and populate KoolbuyLoanDisbursement table
//     * Agent data comes from the React frontend
//     */
//    @Transactional
//    public void submitProof(String applicationReference, AgentProofSubmissionRequest request) throws Exception {
//
//        // 1. Find customer data from Guarantor entity using application reference
//        Guarantor guarantor = guarantorRepository.findByApplicationReference(applicationReference)
//                .orElseThrow(() -> new Exception("Application reference not found: " + applicationReference));
//
//        // 2. Find installment duration from AgentFollowUp using guarantor email or phone
//        AgentFollowUp agentFollowUp = agentFollowUpRepository.findByGuarantorEmail(guarantor.getGuarantorEmail())
//                .orElseGet(() -> agentFollowUpRepository.findByMobileNumber(guarantor.getCustomerPhoneNumber())
//                        .orElse(null));
//
//        // 3. Save receipt image
//        String receiptPath = saveReceiptImage(request.getReceiptImage(), applicationReference);
//
//        // 4. Create and populate KoolbuyLoanDisbursement entity
//        KoolbuyLoanDisbursement disbursement = new KoolbuyLoanDisbursement();
//
//        // Agent Information (from React frontend - passed in request)
//        disbursement.setAgentName(request.getAgentName());
//        disbursement.setAgentEmail(request.getAgentEmail());
//        disbursement.setAgentId(request.getAgentId());
//        disbursement.setAgentNumber(request.getAgentMobile());
//
//        // Customer Information (from Guarantor entity)
//        disbursement.setCustomerLoanRef(guarantor.getApplicationReference());
//        disbursement.setCustomerFirstName(guarantor.getCustomerFirstName());
//        disbursement.setCustomerLastname(guarantor.getCustomerLastName());
//        disbursement.setCustomerEmail(guarantor.getCustomerEmail());
//        disbursement.setCustomerPhoneNumber(guarantor.getCustomerPhoneNumber());
//
//        // Loan Duration from AgentFollowUp or Guarantor
//        if (agentFollowUp != null && agentFollowUp.getInstallmentOption() != null) {
//            try {
//                disbursement.setCustomerLoanDuration(Integer.parseInt(agentFollowUp.getInstallmentOption()));
//            } catch (NumberFormatException e) {
//                // Fallback to guarantor's installment duration if available
//                if (guarantor.getCustomerInstallmentDuration() != null) {
//                    try {
//                        disbursement.setCustomerLoanDuration(Integer.parseInt(guarantor.getCustomerInstallmentDuration()));
//                    } catch (NumberFormatException ex) {
//                        disbursement.setCustomerLoanDuration(null);
//                    }
//                }
//            }
//        } else if (guarantor.getCustomerInstallmentDuration() != null) {
//            try {
//                disbursement.setCustomerLoanDuration(Integer.parseInt(guarantor.getCustomerInstallmentDuration()));
//            } catch (NumberFormatException e) {
//                disbursement.setCustomerLoanDuration(null);
//            }
//        }
//
//        // Product Information (from Guarantor entity)
//        disbursement.setProductName(guarantor.getProductName());
//        disbursement.setProductBrand(guarantor.getProductBrand());
//        disbursement.setProductSize(guarantor.getProductSize());
//
//        // Store Information (from form submission)
//        disbursement.setStoreName(request.getStoreName());
//        disbursement.setStoreLocation(request.getStoreLocation());
//        disbursement.setStorePaymentConfirmation(true); // ✅ Set to TRUE when proof is submitted
//
//        // Payment Information
//        disbursement.setPaymentDate(request.getConfirmationDate());
//        disbursement.setInitialInstalment(guarantor.getStorePrice());
//
//        // Receipt Image Path
//        disbursement.setReceiptImagePath(receiptPath);
//
//        // Guarantor Email
//        disbursement.setGuarantorEmail(guarantor.getGuarantorEmail());
//
//        // 5. Save to database
//        KoolbuyLoanDisbursement savedDisbursement = disbursementRepository.save(disbursement);
//
//        // 6. Send email to After Sales Team
//        sendPaygoConfigurationEmail(savedDisbursement);
//    }
//
//    /**
//     * Save receipt image to file system
//     */
//    private String saveReceiptImage(org.springframework.web.multipart.MultipartFile file, String applicationReference) throws IOException {
//        // Create upload directory if it doesn't exist
//        Path uploadPath = Paths.get(uploadDirectory);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        // Generate unique filename
//        String originalFilename = file.getOriginalFilename();
//        String extension = originalFilename != null ?
//            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
//        String filename = applicationReference + "_" + UUID.randomUUID().toString() + extension;
//
//        // Save file
//        Path filePath = uploadPath.resolve(filename);
//        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
//
//        return filePath.toString();
//    }
//
//    /**
//     * Send email to After Sales Team
//     */
//    private void sendPaygoConfigurationEmail(KoolbuyLoanDisbursement disbursement) {
//        Map<String, Object> emailData = new HashMap<>();
//
//        // Customer Information
//        emailData.put("customerFirstName", disbursement.getCustomerFirstName());
//        emailData.put("customerLastName", disbursement.getCustomerLastname());
//        emailData.put("customerEmail", disbursement.getCustomerEmail());
//        emailData.put("customerPhoneNumber", disbursement.getCustomerPhoneNumber());
//        emailData.put("customerLoanRef", disbursement.getCustomerLoanRef());
//        emailData.put("customerLoanDuration", disbursement.getCustomerLoanDuration());
//
//        // Agent Information
//        emailData.put("agentName", disbursement.getAgentName());
//        emailData.put("agentId", disbursement.getAgentId());
//        emailData.put("agentEmail", disbursement.getAgentEmail());
//        emailData.put("agentNumber", disbursement.getAgentNumber());
//
//        // Store Information
//        emailData.put("storeName", disbursement.getStoreName());
//        emailData.put("storeLocation", disbursement.getStoreLocation());
//
//        // Payment Information
//        emailData.put("initialInstalment", disbursement.getInitialInstalment());
//        emailData.put("paymentDate", disbursement.getPaymentDate());
//
//        // Product Information
//        emailData.put("productName", disbursement.getProductName());
//        emailData.put("productBrand", disbursement.getProductBrand());
//        emailData.put("productSize", disbursement.getProductSize());
//
//        // Confirmation URL and status
//        emailData.put("confirmationUrl",
//            "http://your-domain.com/api/v1/confirm-paygo/" + disbursement.getId());
//        emailData.put("disbursementId", disbursement.getId());
//        emailData.put("isConfigured", true); // Already confirmed since storePaymentConfirmation is true
//
//        emailService.sendPaygoConfigurationEmail(emailData);
//    }
//
//    /**
//     * Confirm Paygo Configuration (This is now just for tracking/logging purposes)
//     */
//    @Transactional
//    public void confirmPaygoConfiguration(Long disbursementId) throws Exception {
//        KoolbuyLoanDisbursement disbursement = disbursementRepository.findById(disbursementId)
//                .orElseThrow(() -> new Exception("Disbursement not found with ID: " + disbursementId));
//
//        if (Boolean.TRUE.equals(disbursement.getStorePaymentConfirmation())) {
//            // Payment is already confirmed - this is just for logging
//            System.out.println("Paygo configuration acknowledged for disbursement ID: " + disbursementId);
//        } else {
//            throw new Exception("Payment proof not yet submitted");
//        }
//
//        // TODO: Add any additional logic needed after confirmation
//        // For example, notify other systems, update customer status, etc.
//    }
//}





package com.koolboks.creditProject.service.agent_proof;

import com.koolboks.creditProject.dto.agent_proof.AgentProofSubmissionRequest;
import com.koolboks.creditProject.entity.koolbuy_loan.KoolbuyLoanDisbursement;
import com.koolboks.creditProject.entity.AgentFollowUp;
import com.koolboks.creditProject.entity.Guarantor;
import com.koolboks.creditProject.repository.KoolbuyLoanDisbursementRepository;
import com.koolboks.creditProject.repository.AgentFollowUpRepository;
import com.koolboks.creditProject.repository.GuarantorRepository;
import com.koolboks.creditProject.service.email_paygo_config.PaygoEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AgentProofService {

    @Autowired
    private KoolbuyLoanDisbursementRepository disbursementRepository;

    @Autowired
    private GuarantorRepository guarantorRepository;

    @Autowired
    private AgentFollowUpRepository agentFollowUpRepository;

    @Autowired
    private PaygoEmailService emailService;

    @Value("${file.upload.directory:uploads/receipts}")
    private String uploadDirectory;

    /**
     * Get agent proof data for the form
     */
    public Map<String, Object> getAgentProofData(String applicationReference) {
        Guarantor guarantor = guarantorRepository.findByApplicationReference(applicationReference)
                .orElseThrow(() -> new RuntimeException("Application reference not found: " + applicationReference));

        AgentFollowUp agentFollowUp = agentFollowUpRepository.findByGuarantorEmail(guarantor.getGuarantorEmail())
                .orElse(agentFollowUpRepository.findByMobileNumber(guarantor.getCustomerPhoneNumber())
                        .orElse(null));

        Map<String, Object> data = new HashMap<>();
        data.put("loan_ref", guarantor.getApplicationReference());
        data.put("amount", guarantor.getStorePrice());
        data.put("payment_date", java.time.LocalDate.now().toString());
        data.put("agent_name", "Agent Name");
        data.put("agent_id", "AGENT-ID");
        data.put("agent_email", "agent@email.com");
        data.put("agent_mobile", "1234567890");

        return data;
    }

    /**
     * Submit proof - UPDATED to handle both INSERT and UPDATE
     */
    @Transactional
    public void submitProof(String applicationReference, AgentProofSubmissionRequest request) throws Exception {

        // 1. Find customer data from Guarantor
        Guarantor guarantor = guarantorRepository.findByApplicationReference(applicationReference)
                .orElseThrow(() -> new Exception("Application reference not found: " + applicationReference));

        // 2. Find installment duration from AgentFollowUp
        AgentFollowUp agentFollowUp = agentFollowUpRepository.findByGuarantorEmail(guarantor.getGuarantorEmail())
                .orElseGet(() -> agentFollowUpRepository.findByMobileNumber(guarantor.getCustomerPhoneNumber())
                        .orElse(null));

        // 3. Save receipt image
        String receiptPath = saveReceiptImage(request.getReceiptImage(), applicationReference);

        // 4. CHECK IF RECORD EXISTS - THIS IS THE KEY CHANGE
        Optional<KoolbuyLoanDisbursement> existingDisbursement =
            disbursementRepository.findByCustomerLoanRef(applicationReference);

        KoolbuyLoanDisbursement disbursement;

        if (existingDisbursement.isPresent()) {
            // UPDATE existing record
            disbursement = existingDisbursement.get();
            System.out.println("Updating existing disbursement for: " + applicationReference);
        } else {
            // CREATE new record
            disbursement = new KoolbuyLoanDisbursement();
            System.out.println("Creating new disbursement for: " + applicationReference);

            // Set fields that should only be set on creation
            disbursement.setCustomerLoanRef(guarantor.getApplicationReference());
            disbursement.setCustomerFirstName(guarantor.getCustomerFirstName());
            disbursement.setCustomerLastname(guarantor.getCustomerLastName());
            disbursement.setCustomerEmail(guarantor.getCustomerEmail());
            disbursement.setCustomerPhoneNumber(guarantor.getCustomerPhoneNumber());
            disbursement.setProductName(guarantor.getProductName());
            disbursement.setProductBrand(guarantor.getProductBrand());
            disbursement.setProductSize(guarantor.getProductSize());
            disbursement.setInitialInstalment(guarantor.getStorePrice());
            disbursement.setGuarantorEmail(guarantor.getGuarantorEmail());
        }

        // 5. Set/Update Agent Information (from React frontend)
        disbursement.setAgentName(request.getAgentName());
        disbursement.setAgentEmail(request.getAgentEmail());
        disbursement.setAgentId(request.getAgentId());
        disbursement.setAgentNumber(request.getAgentMobile());

        // 6. Set/Update Loan Duration
        if (agentFollowUp != null && agentFollowUp.getInstallmentOption() != null) {
            try {
                disbursement.setCustomerLoanDuration(Integer.parseInt(agentFollowUp.getInstallmentOption()));
            } catch (NumberFormatException e) {
                if (guarantor.getCustomerInstallmentDuration() != null) {
                    try {
                        disbursement.setCustomerLoanDuration(Integer.parseInt(guarantor.getCustomerInstallmentDuration()));
                    } catch (NumberFormatException ex) {
                        disbursement.setCustomerLoanDuration(null);
                    }
                }
            }
        } else if (guarantor.getCustomerInstallmentDuration() != null) {
            try {
                disbursement.setCustomerLoanDuration(Integer.parseInt(guarantor.getCustomerInstallmentDuration()));
            } catch (NumberFormatException e) {
                disbursement.setCustomerLoanDuration(null);
            }
        }

        // 7. Set/Update Store Information (from form submission)
        disbursement.setStoreName(request.getStoreName());
        disbursement.setStoreLocation(request.getStoreLocation());
        disbursement.setStorePaymentConfirmation(true);

        // 8. Set/Update Payment Information
        disbursement.setPaymentDate(request.getConfirmationDate());
        disbursement.setReceiptImagePath(receiptPath);

        // 9. Set updated timestamp
        disbursement.setUpdatedAt(LocalDateTime.now());

        // 10. Save to database (will INSERT or UPDATE as appropriate)
        KoolbuyLoanDisbursement savedDisbursement = disbursementRepository.save(disbursement);

        // 11. Send email to After Sales Team
        sendPaygoConfigurationEmail(savedDisbursement);
    }

    private String saveReceiptImage(org.springframework.web.multipart.MultipartFile file, String applicationReference) throws IOException {
        Path uploadPath = Paths.get(uploadDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ?
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
        String filename = applicationReference + "_" + UUID.randomUUID().toString() + extension;

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    private void sendPaygoConfigurationEmail(KoolbuyLoanDisbursement disbursement) {
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("customerFirstName", disbursement.getCustomerFirstName());
        emailData.put("customerLastName", disbursement.getCustomerLastname());
        emailData.put("customerEmail", disbursement.getCustomerEmail());
        emailData.put("customerPhoneNumber", disbursement.getCustomerPhoneNumber());
        emailData.put("customerLoanRef", disbursement.getCustomerLoanRef());
        emailData.put("customerLoanDuration", disbursement.getCustomerLoanDuration());
        emailData.put("agentName", disbursement.getAgentName());
        emailData.put("agentId", disbursement.getAgentId());
        emailData.put("agentEmail", disbursement.getAgentEmail());
        emailData.put("agentNumber", disbursement.getAgentNumber());
        emailData.put("storeName", disbursement.getStoreName());
        emailData.put("storeLocation", disbursement.getStoreLocation());
        emailData.put("initialInstalment", disbursement.getInitialInstalment());
        emailData.put("paymentDate", disbursement.getPaymentDate());
        emailData.put("productName", disbursement.getProductName());
        emailData.put("productBrand", disbursement.getProductBrand());
        emailData.put("productSize", disbursement.getProductSize());
        emailData.put("confirmationUrl", "http://your-domain.com/api/v1/confirm-paygo/" + disbursement.getId());
        emailData.put("disbursementId", disbursement.getId());
        //emailData.put("isConfigured", true);

        emailData.put("isConfigured", false); // ✅ CORRECT!

        emailService.sendPaygoConfigurationEmail(emailData);
    }

    @Transactional
    public void confirmPaygoConfiguration(Long disbursementId) throws Exception {
        KoolbuyLoanDisbursement disbursement = disbursementRepository.findById(disbursementId)
                .orElseThrow(() -> new Exception("Disbursement not found with ID: " + disbursementId));

        if (Boolean.TRUE.equals(disbursement.getStorePaymentConfirmation())) {
            System.out.println("Paygo configuration acknowledged for disbursement ID: " + disbursementId);
        } else {
            throw new Exception("Payment proof not yet submitted");
        }
    }
}
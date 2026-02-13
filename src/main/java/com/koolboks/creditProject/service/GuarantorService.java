package com.koolboks.creditProject.service;

import com.koolboks.creditProject.dto.GuarantorRequest;
import com.koolboks.creditProject.dto.guarantor.MonoNinResponse;
import com.koolboks.creditProject.entity.AgentEntry;
import com.koolboks.creditProject.entity.AgentFollowUp;
import com.koolboks.creditProject.entity.Guarantor;
import com.koolboks.creditProject.repository.GuarantorRepository;
import com.koolboks.creditProject.repository.AgentEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;



import jakarta.mail.internet.MimeMessage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class GuarantorService {

    private static final Logger log = LoggerFactory.getLogger(GuarantorService.class);

    private final RestTemplate restTemplate;
    private final JavaMailSender mailSender;
    private final GuarantorRepository guarantorRepository;
    private final OtpService otpService;
    private final OfferLetterService offerLetterService;
    private final AgentEntryRepository agentEntryRepository;
    private final AgentFollowUpService agentFollowUpService;




    @Value("${mono.sec.key}")
    private String monoApiKey;

    @Value("${mono.nin.url}")
    private String monoNinUrl;

    @Value("${notification.email.to:folorunsho@koolboks.com}")
    private String adminEmail;

    @Value("${notification.email.from:hadelinet192@gmail.com}")
    private String fromEmail;

    @Value("${app.base.url:http://localhost:8080}")
    //@Value("${app.base.url:https://bde9122e7fd0.ngrok-free.app}")
    private String appBaseUrl;

    @Value("${frontend.base.url:http://localhost:5173}")
    //@Value("${frontend.base.url:https://4629bd22e20c.ngrok-free.app}")
    private String frontendBaseUrl;






    // Update the constructor
    public GuarantorService(RestTemplate restTemplate,
                            JavaMailSender mailSender,
                            GuarantorRepository guarantorRepository,
                            OtpService otpService,
                            AgentEntryRepository agentEntryRepository,
                            OfferLetterService offerLetterService, AgentFollowUpService agentFollowUpService) {
        this.restTemplate = restTemplate;
        this.mailSender = mailSender;
        this.guarantorRepository = guarantorRepository;
        this.agentEntryRepository = agentEntryRepository;
        this.otpService = otpService;
        this.offerLetterService = offerLetterService;
        this.agentFollowUpService = agentFollowUpService;
    }



    private String resolveInstallmentDuration(Guarantor guarantor) {
    // 1. Use value stored directly on guarantor
    if (guarantor.getCustomerInstallmentDuration() != null &&
        !guarantor.getCustomerInstallmentDuration().isBlank()) {
        return guarantor.getCustomerInstallmentDuration();
    }

    // 2. Fallback to AgentFollowUp.installmentOption
    AgentFollowUp followUp = guarantor.getAgentFollowUp();
    if (followUp != null &&
        followUp.getInstallmentOption() != null &&
        !followUp.getInstallmentOption().isBlank()) {
        return followUp.getInstallmentOption();
    }

    // 3. Final fallback
    return "N/A";
}




//    public GuarantorService(RestTemplate restTemplate,
//                            JavaMailSender mailSender,
//                            GuarantorRepository guarantorRepository,
//                            OtpService otpService,
//                            OfferLetterService offerLetterService) {
//        this.restTemplate = restTemplate;
//        this.mailSender = mailSender;
//        this.guarantorRepository = guarantorRepository;
//        this.otpService = otpService;
//        this.offerLetterService = offerLetterService;
//    }

//    public GuarantorService(RestTemplate restTemplate,
//                            JavaMailSender mailSender,
//                            GuarantorRepository guarantorRepository,
//                            OtpService otpService) {
//        this.restTemplate = restTemplate;
//        this.mailSender = mailSender;
//        this.guarantorRepository = guarantorRepository;
//        this.otpService = otpService;
//    }

    /**
     * Step 1: Customer requests guarantor (after OTP verification)
     * Creates guarantor record with token and sends email to guarantor
     *
     *
     */


    public Map<String, Object> requestGuarantor(String customerBvn,
                                            String customerFirstName,
                                            String customerLastName,
                                            String customerEmail,
                                            String customerPhoneNumber,
                                            String customerPlan,
                                            String installmentDuration,
                                            String guarantorEmail,
                                            BigDecimal storePrice,
                                            String productName,
                                            String productBrand,
                                            String productSize) {
    Map<String, Object> response = new HashMap<>();

    try {
        // Check if guarantor already requested for this customer
//        Optional<Guarantor> existing = guarantorRepository.findByCustomerBvnAndActiveTrue(customerBvn);
        Optional<Guarantor> existing = guarantorRepository.findTopByCustomerBvnAndActiveTrueOrderByCreatedAtDesc(customerBvn);


        if (existing.isPresent()) {
            Guarantor existingGuarantor = existing.get();

            // ✅ UPDATE PRODUCT FIELDS EVEN FOR EXISTING RECORDS
            existingGuarantor.setProductName(productName);
            existingGuarantor.setProductBrand(productBrand);
            existingGuarantor.setProductSize(productSize);
            existingGuarantor.setStorePrice(storePrice);
            existingGuarantor.setCustomerPhoneNumber(customerPhoneNumber);
            existingGuarantor.setCustomerInstallmentDuration(installmentDuration);

            // Save the updates
            guarantorRepository.save(existingGuarantor);

            // If already verified, return success
            if (existingGuarantor.getOtpVerified()) {
                response.put("success", true);
                response.put("alreadyVerified", true);
                response.put("message", "Guarantor already verified");
                return response;
            }

            // If form already submitted but not verified
            if (existingGuarantor.getGuarantorFormSubmitted()) {
                response.put("success", true);
                response.put("status", "PENDING");
                response.put("message", "Waiting for guarantor verification");
                return response;
            }

            // Use existing record
            response.put("success", true);
            response.put("status", "WAITING");
            response.put("message", "Email already sent to guarantor");
            response.put("guarantorEmail", maskEmail(existingGuarantor.getGuarantorEmail()));
            return response;
        }

        // Find the AgentFollowUp by customer BVN
        AgentFollowUp agentFollowUp = agentFollowUpService.findByCustomerBvn(customerBvn);

        // Create new guarantor record
        String token = UUID.randomUUID().toString();

        Guarantor guarantor = new Guarantor();
        // Customer info
        guarantor.setCustomerBvn(customerBvn);
        guarantor.setCustomerFirstName(customerFirstName);
        guarantor.setCustomerLastName(customerLastName);
        guarantor.setCustomerEmail(customerEmail);
        guarantor.setCustomerPhoneNumber(customerPhoneNumber);
        guarantor.setCustomerPlan(customerPlan);
        guarantor.setCustomerInstallmentDuration(installmentDuration);

        // Product info
        guarantor.setProductName(productName);
        guarantor.setProductBrand(productBrand);
        guarantor.setProductSize(productSize);
        guarantor.setStorePrice(storePrice);

        // Guarantor info
        guarantor.setGuarantorEmail(guarantorEmail);
        guarantor.setGuarantorToken(token);
        guarantor.setTokenExpiresAt(LocalDateTime.now().plusHours(24));

        // Status
        guarantor.setActive(true);
        guarantor.setGuarantorFormSubmitted(false);
        guarantor.setIsConfirmed(false);
        guarantor.setOtpVerified(false);

        guarantor.setAgentFollowUp(agentFollowUp);

        guarantor = guarantorRepository.save(guarantor);

        // Send email to guarantor with link
        sendGuarantorFormLinkEmail(guarantor);

        log.info("Guarantor link sent to: {} for customer BVN: {}", guarantorEmail, customerBvn);

        response.put("success", true);
        response.put("status", "WAITING");
        response.put("message", "Email sent to guarantor successfully");
        response.put("guarantorEmail", maskEmail(guarantorEmail));

        return response;

    } catch (Exception e) {
        log.error("Error requesting guarantor: ", e);
        response.put("success", false);
        response.put("message", "Error: " + e.getMessage());
        return response;
    }
}

//    public Map<String, Object> requestGuarantor(String customerBvn,
//                                            String customerFirstName,
//                                            String customerLastName,
//                                            String customerEmail,
//                                            String customerPhoneNumber,
//                                            String customerPlan,
//                                            String installmentDuration,
//                                            String guarantorEmail,
//                                            BigDecimal storePrice,
//                                            String productName,
//                                            String productBrand,
//                                            String productSize) {
//    Map<String, Object> response = new HashMap<>();
//
//    try {
//        // Check if guarantor already requested for this customer
//        Optional<Guarantor> existing = guarantorRepository.findByCustomerBvnAndActiveTrue(customerBvn);
//        if (existing.isPresent()) {
//            Guarantor existingGuarantor = existing.get();
//
//            // If already verified, return success
//            if (existingGuarantor.getOtpVerified()) {
//                response.put("success", true);
//                response.put("alreadyVerified", true);
//                response.put("message", "Guarantor already verified");
//                return response;
//            }
//
//            // If form already submitted but not verified
//            if (existingGuarantor.getGuarantorFormSubmitted()) {
//                response.put("success", true);
//                response.put("status", "PENDING");
//                response.put("message", "Waiting for guarantor verification");
//                return response;
//            }
//
//            // Use existing record
//            response.put("success", true);
//            response.put("status", "WAITING");
//            response.put("message", "Email already sent to guarantor");
//            response.put("guarantorEmail", maskEmail(existingGuarantor.getGuarantorEmail()));
//            return response;
//        }
//
//        // Find the AgentFollowUp by customer BVN
//        AgentFollowUp agentFollowUp = agentFollowUpService.findByCustomerBvn(customerBvn);
//
//        // Create new guarantor record
//        String token = UUID.randomUUID().toString();
//
//
//
//        Guarantor guarantor = new Guarantor();
//        // Customer info
//        guarantor.setCustomerBvn(customerBvn);
//        guarantor.setCustomerFirstName(customerFirstName);
//        guarantor.setCustomerLastName(customerLastName);
//        guarantor.setCustomerEmail(customerEmail);
//        guarantor.setCustomerPhoneNumber(customerPhoneNumber);
//        guarantor.setCustomerPlan(customerPlan);
//        guarantor.setCustomerInstallmentDuration(installmentDuration);
//
//
//        // Product info
//        guarantor.setProductName(productName);
//        guarantor.setProductBrand(productBrand);
//        guarantor.setProductSize(productSize);
//        guarantor.setStorePrice(storePrice);
//
//        // Guarantor info
//        guarantor.setGuarantorEmail(guarantorEmail);
//        guarantor.setGuarantorToken(token);
//        guarantor.setTokenExpiresAt(LocalDateTime.now().plusHours(24));
//
//        // Status
//        guarantor.setActive(true);
//        guarantor.setGuarantorFormSubmitted(false);
//        guarantor.setIsConfirmed(false);
//        guarantor.setOtpVerified(false);
//
//
//        guarantor.setAgentFollowUp(agentFollowUp);
//
//
//        guarantor = guarantorRepository.save(guarantor);
//
//        // Send email to guarantor with link
//        sendGuarantorFormLinkEmail(guarantor);
//
//        log.info("Guarantor link sent to: {} for customer BVN: {}", guarantorEmail, customerBvn);
//
//        response.put("success", true);
//        response.put("status", "WAITING");
//        response.put("message", "Email sent to guarantor successfully");
//        response.put("guarantorEmail", maskEmail(guarantorEmail));
//
//        return response;
//
//    } catch (Exception e) {
//        log.error("Error requesting guarantor: ", e);
//        response.put("success", false);
//        response.put("message", "Error: " + e.getMessage());
//        return response;
//    }
//}




    // Update requestGuarantor method to link AgentEntry

//public Map<String, Object> requestGuarantor(String customerBvn,
//                                            String customerFirstName,
//                                            String customerLastName,
//                                            String customerEmail,
//                                            String customerPlan,
//                                            String installmentDuration,
//                                            String guarantorEmail,
//                                            Long agentEntryId) {  // ← Change from BigDecimal to Long
//    Map<String, Object> response = new HashMap<>();
//
//    try {
//        // Check if guarantor already requested for this customer
//        Optional<Guarantor> existing = guarantorRepository.findByCustomerBvnAndActiveTrue(customerBvn);
//        if (existing.isPresent()) {
//            Guarantor existingGuarantor = existing.get();
//
//            // If already verified, return success
//            if (existingGuarantor.getOtpVerified()) {
//                response.put("success", true);
//                response.put("alreadyVerified", true);
//                response.put("message", "Guarantor already verified");
//                return response;
//            }
//
//            // If form already submitted but not verified
//            if (existingGuarantor.getGuarantorFormSubmitted()) {
//                response.put("success", true);
//                response.put("status", "PENDING");
//                response.put("message", "Waiting for guarantor verification");
//                return response;
//            }
//
//            // Use existing record
//            response.put("success", true);
//            response.put("status", "WAITING");
//            response.put("message", "Email already sent to guarantor");
//            response.put("guarantorEmail", maskEmail(existingGuarantor.getGuarantorEmail()));
//            return response;
//        }
//
//        // Fetch AgentEntry if ID provided
//
//        AgentEntry agentEntry = null;
//        if (agentEntryId != null) {
//            agentEntry = agentEntryRepository.findById(agentEntryId)
//                          .orElseThrow(() -> new IllegalArgumentException("AgentEntry not found with ID: " + agentEntryId));
//        }
//
//        AgentFollowUp agentFollowUp = null;
//
//
////        guarantor.setAgentEntry(agentEntry);
////        guarantor.setAgentFollowUp(agentFollowUp);
//
//
////        AgentEntry agentEntry = agentEntryRepository
////        .findById(agentEntryId)
////        .orElseThrow(() -> new RuntimeException("AgentEntry not found"));
//
//
//
//
//
////        if (agentEntryId == null) {
////            throw new IllegalArgumentException("AgentEntry ID is required");
////        }
////
////        AgentEntry agentEntry = agentEntryRepository.findById(agentEntryId)
////                .orElseThrow(() -> new IllegalArgumentException(
////                        "AgentEntry not found with ID: " + agentEntryId));
//
//
//
////        AgentEntry agentEntry = null;
////        if (agentEntryId != null) {
////            agentEntry = agentEntryRepository.findById(agentEntryId).orElse(null);
////            if (agentEntry == null) {
////                log.warn("AgentEntry not found with ID: {}", agentEntryId);
////            }
////        }
//
//        // Create new guarantor record
//        String token = UUID.randomUUID().toString();
//
//        Guarantor guarantor = new Guarantor();
//        guarantor.setCustomerBvn(customerBvn);
//        guarantor.setCustomerFirstName(customerFirstName);
//        guarantor.setCustomerLastName(customerLastName);
//        guarantor.setCustomerEmail(customerEmail);
//        guarantor.setCustomerPlan(customerPlan);
//        guarantor.setCustomerInstallmentDuration(installmentDuration);
//        guarantor.setGuarantorEmail(guarantorEmail);
//        guarantor.setGuarantorToken(token);
//        guarantor.setTokenExpiresAt(LocalDateTime.now().plusHours(24));
//        guarantor.setActive(true);
//        guarantor.setGuarantorFormSubmitted(false);
//        guarantor.setIsConfirmed(false);
//        guarantor.setOtpVerified(false);
//        guarantor.setAgentEntry(agentEntry);
//        guarantor.setAgentFollowUp(agentFollowUp);// ← Set the relationship
//
//        guarantor = guarantorRepository.save(guarantor);
//
//        // Send email to guarantor with link
//        sendGuarantorFormLinkEmail(guarantor);
//
//        log.info("Guarantor link sent to: {} for customer BVN: {}", guarantorEmail, customerBvn);
//
//        response.put("success", true);
//        response.put("status", "WAITING");
//        response.put("message", "Email sent to guarantor successfully");
//        response.put("guarantorEmail", maskEmail(guarantorEmail));
//
//        return response;
//
//    } catch (Exception e) {
//        log.error("Error requesting guarantor: ", e);
//        response.put("success", false);
//        response.put("message", "Error: " + e.getMessage());
//        return response;
//    }
//}






//    public Map<String, Object> requestGuarantor(String customerBvn, String customerFirstName,
//                                                String customerLastName, String customerEmail,
//                                                String customerPlan, String installmentDuration,
//                                                String guarantorEmail, BigDecimal storePrice) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            // Check if guarantor already requested for this customer
//            Optional<Guarantor> existing = guarantorRepository.findByCustomerBvnAndActiveTrue(customerBvn);
//            if (existing.isPresent()) {
//                Guarantor existingGuarantor = existing.get();
//
//                // If already verified, return success
//                if (existingGuarantor.getOtpVerified()) {
//                    response.put("success", true);
//                    response.put("alreadyVerified", true);
//                    response.put("message", "Guarantor already verified");
//                    return response;
//                }
//
//                // If form already submitted but not verified
//                if (existingGuarantor.getGuarantorFormSubmitted()) {
//                    response.put("success", true);
//                    response.put("status", "PENDING");
//                    response.put("message", "Waiting for guarantor verification");
//                    return response;
//                }
//
//                // Use existing record
//                response.put("success", true);
//                response.put("status", "WAITING");
//                response.put("message", "Email already sent to guarantor");
//                response.put("guarantorEmail", maskEmail(existingGuarantor.getGuarantorEmail()));
//                return response;
//            }
//
//            // Create new guarantor record
//            String token = UUID.randomUUID().toString();
//
//            Guarantor guarantor = new Guarantor();
//            guarantor.setCustomerBvn(customerBvn);
//            guarantor.setCustomerFirstName(customerFirstName);
//            guarantor.setCustomerLastName(customerLastName);
//            guarantor.setCustomerEmail(customerEmail);
//            guarantor.setCustomerPlan(customerPlan);
//            guarantor.setCustomerInstallmentDuration(installmentDuration);
//            guarantor.setGuarantorEmail(guarantorEmail);
//            guarantor.setGuarantorToken(token);
//            guarantor.setTokenExpiresAt(LocalDateTime.now().plusHours(24));
//            guarantor.setActive(true);
//            guarantor.setGuarantorFormSubmitted(false);
//            guarantor.setIsConfirmed(false);
//            guarantor.setOtpVerified(false);
//            guarantor.setStorePrice(storePrice);
//
//            guarantor = guarantorRepository.save(guarantor);
//
//            // Send email to guarantor with link
//            sendGuarantorFormLinkEmail(guarantor);
//
//            log.info("Guarantor link sent to: {} for customer BVN: {}", guarantorEmail, customerBvn);
//
//            response.put("success", true);
//            response.put("status", "WAITING");
//            response.put("message", "Email sent to guarantor successfully");
//            response.put("guarantorEmail", maskEmail(guarantorEmail));
//
//            return response;
//
//        } catch (Exception e) {
//            log.error("Error requesting guarantor: ", e);
//            response.put("success", false);
//            response.put("message", "Error: " + e.getMessage());
//            return response;
//        }
//    }

    /**
     * Step 2: Get guarantor data by token (when guarantor opens link)
     */
    public Map<String, Object> getGuarantorDataByToken(String token) {
    Map<String, Object> response = new HashMap<>();

    try {
        Optional<Guarantor> guarantorOpt = guarantorRepository.findByGuarantorToken(token);

        if (guarantorOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Invalid or expired link");
            return response;
        }

        Guarantor guarantor = guarantorOpt.get();

        // Check if token expired
        if (guarantor.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            response.put("success", false);
            response.put("message", "This link has expired");
            return response;
        }

        // Check if already submitted
        if (guarantor.getGuarantorFormSubmitted()) {
            response.put("success", false);
            response.put("message", "Form already submitted");
            response.put("alreadySubmitted", true);
            return response;
        }

        // Return customer data for display
        response.put("success", true);
        response.put("customerFirstName", guarantor.getCustomerFirstName());
        response.put("customerLastName", guarantor.getCustomerLastName());
        response.put("customerBvn", guarantor.getCustomerBvn());
        response.put("customerEmail", guarantor.getCustomerEmail());
        response.put("customerPhoneNumber", guarantor.getCustomerPhoneNumber());
        response.put("customerPlan", guarantor.getCustomerPlan());
        response.put("installmentDuration", guarantor.getCustomerInstallmentDuration());

        // Product details
        response.put("productName", guarantor.getProductName());
        response.put("productBrand", guarantor.getProductBrand());
        response.put("productSize", guarantor.getProductSize());
        response.put("price", guarantor.getStorePrice());

        return response;

    } catch (Exception e) {
        log.error("Error getting guarantor data: ", e);
        response.put("success", false);
        response.put("message", "Error: " + e.getMessage());
        return response;
    }
}








//    public Map<String, Object> getGuarantorDataByToken(String token) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            Optional<Guarantor> guarantorOpt = guarantorRepository.findByGuarantorToken(token);
//
//            if (guarantorOpt.isEmpty()) {
//                response.put("success", false);
//                response.put("message", "Invalid or expired link");
//                return response;
//            }
//
//            Guarantor guarantor = guarantorOpt.get();
//
//            // Check if token expired
//            if (guarantor.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
//                response.put("success", false);
//                response.put("message", "This link has expired");
//                return response;
//            }
//
//            // Check if already submitted
//            if (guarantor.getGuarantorFormSubmitted()) {
//                response.put("success", false);
//                response.put("message", "Form already submitted");
//                response.put("alreadySubmitted", true);
//                return response;
//            }
//
//            // Return customer data for display
//            response.put("success", true);
//            response.put("customerFirstName", guarantor.getCustomerFirstName());
//            response.put("customerLastName", guarantor.getCustomerLastName());
//            response.put("customerBvn", guarantor.getCustomerBvn());
//            response.put("customerPlan", guarantor.getCustomerPlan());
//            response.put("customerInstallmentDuration", guarantor.getCustomerInstallmentDuration());
//            response.put("customerEmail", guarantor.getCustomerEmail());
//
//            return response;
//
//        } catch (Exception e) {
//            log.error("Error getting guarantor data: ", e);
//            response.put("success", false);
//            response.put("message", "Error: " + e.getMessage());
//            return response;
//        }
//    }

    /**
     * Step 3: Guarantor submits their information
     */
    public Map<String, Object> submitGuarantorForm(String token, GuarantorRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Guarantor> guarantorOpt = guarantorRepository.findByGuarantorToken(token);

            if (guarantorOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Invalid or expired link");
                return response;
            }

            Guarantor guarantor = guarantorOpt.get();

            // Check if token expired
            if (guarantor.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
                response.put("success", false);
                response.put("message", "This link has expired");
                return response;
            }






//            //  VALIDATE IMAGE FIRST
//            if (request.getSelfieImage() == null || request.getSelfieImage().isEmpty()) {
//                response.put("success", false);
//                response.put("message", "Selfie image is required for verification");
//                return response;
//            }
//
//            //  OPTIONAL: Validate image type
//            String contentType = request.getSelfieImage().getContentType();
//            if (contentType == null ||
//                    (!contentType.equalsIgnoreCase("image/jpeg")
//                    && !contentType.equalsIgnoreCase("image/png"))) {
//                response.put("success", false);
//                response.put("message", "Only JPEG or PNG images are allowed");
//                return response;
//            }


            // Check if already submitted
            if (guarantor.getGuarantorFormSubmitted()) {
                response.put("success", false);
                response.put("message", "Form already submitted");
                return response;
            }

            // Check if guarantor BVN/Phone already used
            if (guarantorRepository.existsByGuarantorBvnAndActiveTrue(request.getGuarantorBvn())) {
                response.put("success", false);
                response.put("message", "This BVN is already used by another guarantor");
                return response;
            }

            if (guarantorRepository.existsByGuarantorPhoneNumberAndActiveTrue(request.getGuarantorPhoneNumber())) {
                response.put("success", false);
                response.put("message", "This phone number is already used by another guarantor");
                return response;
            }

            // Call Mono API for NIN verification
            MonoNinResponse ninResponse = callMonoNinLookup(request.getGuarantorNin());
            if (ninResponse == null || !"successful".equalsIgnoreCase(ninResponse.getStatus())) {
                log.error("Mono NIN lookup failed");
                response.put("success", false);
                response.put("message", "NIN verification failed. Please check your NIN and try again.");
                return response;
            }

            // Update guarantor record
            guarantor.setGuarantorBvn(request.getGuarantorBvn());
            guarantor.setGuarantorPhoneNumber(request.getGuarantorPhoneNumber());
            guarantor.setGuarantorNin(request.getGuarantorNin());

//            // ✅ SAVE IMAGE
//            guarantor.setSelfieImage(request.getSelfieImage().getBytes());
            guarantor.setGuarantorFormSubmitted(true);
            guarantor.setFormSubmittedAt(LocalDateTime.now());

            guarantor = guarantorRepository.save(guarantor);

            // Send email to admin with NIN verification details
            sendAdminVerificationEmail(guarantor, ninResponse);

            log.info("Guarantor form submitted successfully for customer BVN: {}", guarantor.getCustomerBvn());

            response.put("success", true);
            response.put("message", "Form submitted successfully. Awaiting admin verification.");
            response.put("guarantorId", guarantor.getId());

            return response;

        } catch (Exception e) {
            log.error("Error submitting guarantor form: ", e);
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }

    /**
     * Step 4: Admin confirms guarantor and sends OTP
     */
    public Map<String, Object> confirmGuarantor(Long guarantorId) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Guarantor> guarantorOpt = guarantorRepository.findById(guarantorId);

            if (guarantorOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Guarantor not found");
                return response;
            }

            Guarantor guarantor = guarantorOpt.get();

            if (guarantor.getIsConfirmed()) {
                response.put("success", false);
                response.put("message", "Guarantor already confirmed");
                response.put("alreadyConfirmed", true);
                return response;
            }

            if (!guarantor.getGuarantorFormSubmitted()) {
                response.put("success", false);
                response.put("message", "Guarantor has not submitted form yet");
                return response;
            }

            // Generate and send OTP
            String otp = otpService.sendOtp(guarantor.getGuarantorPhoneNumber());

            if (otp == null) {
                response.put("success", false);
                response.put("message", "Failed to send OTP");
                return response;
            }

            // Update guarantor record
            guarantor.setIsConfirmed(true);
            guarantor.setConfirmedAt(LocalDateTime.now());
            guarantor.setConfirmedBy(adminEmail);
            guarantor.setOtpCode(otp);
            guarantor.setOtpSentAt(LocalDateTime.now());
            guarantor.setOtpVerified(false);

            guarantorRepository.save(guarantor);

            log.info("Guarantor {} confirmed and OTP sent to {}", guarantorId, guarantor.getGuarantorPhoneNumber());

            response.put("success", true);
            response.put("message", "Guarantor confirmed and OTP sent successfully");
            response.put("phoneNumber", maskPhoneNumber(guarantor.getGuarantorPhoneNumber()));

            return response;

        } catch (Exception e) {
            log.error("Error confirming guarantor: ", e);
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }

    /**
     * Step 5: Guarantor verifies OTP
     *
     */

    /**
     * Step 5: Guarantor verifies OTP (UPDATED with offer letter generation)
     */
    public Map<String, Object> verifyGuarantorOtp(String token, String otp) {
        Map<String, Object> response = new HashMap<>();

        try {
            Optional<Guarantor> guarantorOpt = guarantorRepository.findByGuarantorToken(token);

            if (guarantorOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Invalid link");
                return response;
            }

            Guarantor guarantor = guarantorOpt.get();

            // Check if already verified - BUT STILL RETURN PAYMENT INFO
            if (guarantor.getOtpVerified()) {
                response.put("success", true);
                response.put("alreadyVerified", true);
                response.put("message", "OTP already verified");

                // Return application reference and payment link if available
                if (guarantor.getApplicationReference() != null) {
                    response.put("applicationReference", guarantor.getApplicationReference());
                    // You might want to store payment link in DB or regenerate it
                }
                return response;
            }

            // Check if OTP sent
            if (guarantor.getOtpCode() == null) {
                response.put("success", false);
                response.put("message", "OTP not sent yet. Please wait for admin confirmation.");
                return response;
            }

            // Check if OTP expired (10 minutes)
            if (guarantor.getOtpSentAt() != null) {
                Duration duration = Duration.between(guarantor.getOtpSentAt(), LocalDateTime.now());
                if (duration.toMinutes() > 10) {
                    response.put("success", false);
                    response.put("message", "OTP has expired");
                    return response;
                }
            }

            // Check OTP match
            if (otp.equals(guarantor.getOtpCode())) {
                guarantor.setOtpVerified(true);
                guarantor.setOtpVerifiedAt(LocalDateTime.now());
                guarantorRepository.save(guarantor);

                log.info("OTP verified successfully for guarantor ID: {}", guarantor.getId());

                // ✅ GENERATE AND SEND OFFER LETTER
                try {
                    log.info("Starting offer letter generation for guarantor ID: {}", guarantor.getId());
                    Map<String, Object> offerLetterResult = offerLetterService.generateAndSendOfferLetter(guarantor.getId());

                    if (Boolean.TRUE.equals(offerLetterResult.get("success"))) {
                        log.info("Offer letter sent successfully for guarantor ID: {}", guarantor.getId());
                        response.put("offerLetterSent", true);
                        response.put("applicationReference", offerLetterResult.get("applicationReference"));
                        response.put("paymentLink", offerLetterResult.get("paymentLink"));
                    } else {
                        log.error("Failed to send offer letter: {}", offerLetterResult.get("message"));
                        response.put("offerLetterSent", false);
                        response.put("offerLetterError", offerLetterResult.get("message"));
                    }
                } catch (Exception e) {
                    log.error("Error generating offer letter for guarantor ID: {}", guarantor.getId(), e);
                    response.put("offerLetterSent", false);
                    response.put("offerLetterError", e.getMessage());
                    // Don't fail the entire OTP verification if offer letter fails
                }

                response.put("success", true);
                response.put("verified", true);
                response.put("message", "OTP verified successfully. Offer letter has been sent to your email.");
                return response;
            } else {
                // Increment failed attempts
                guarantor.setOtpAttempts(guarantor.getOtpAttempts() + 1);
                guarantorRepository.save(guarantor);

                log.warn("Invalid OTP for guarantor ID: {}. Attempts: {}", guarantor.getId(), guarantor.getOtpAttempts());

                response.put("success", false);
                response.put("message", "Invalid OTP code");
                response.put("attemptsRemaining", 5 - guarantor.getOtpAttempts());
                return response;
            }

        } catch (Exception e) {
            log.error("Error verifying OTP: ", e);
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }
























//    /**
//     * Step 5: Guarantor verifies OTP (UPDATED with offer letter generation)
//     */
//    public Map<String, Object> verifyGuarantorOtp(String token, String otp) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            Optional<Guarantor> guarantorOpt = guarantorRepository.findByGuarantorToken(token);
//
//            if (guarantorOpt.isEmpty()) {
//                response.put("success", false);
//                response.put("message", "Invalid link");
//                return response;
//            }
//
//            Guarantor guarantor = guarantorOpt.get();
//
//            // Check if already verified
//            if (guarantor.getOtpVerified()) {
//                response.put("success", true);
//                response.put("alreadyVerified", true);
//                response.put("message", "OTP already verified");
//                return response;
//            }
//
//            // Check if OTP sent
//            if (guarantor.getOtpCode() == null) {
//                response.put("success", false);
//                response.put("message", "OTP not sent yet. Please wait for admin confirmation.");
//                return response;
//            }
//
//            // Check if OTP expired (10 minutes)
//            if (guarantor.getOtpSentAt() != null) {
//                Duration duration = Duration.between(guarantor.getOtpSentAt(), LocalDateTime.now());
//                if (duration.toMinutes() > 10) {
//                    response.put("success", false);
//                    response.put("message", "OTP has expired");
//                    return response;
//                }
//            }
//
//            // Check OTP match
//            if (otp.equals(guarantor.getOtpCode())) {
//                guarantor.setOtpVerified(true);
//                guarantor.setOtpVerifiedAt(LocalDateTime.now());
//                guarantorRepository.save(guarantor);
//
//                log.info("OTP verified successfully for guarantor ID: {}", guarantor.getId());
//
//                // ✅ GENERATE AND SEND OFFER LETTER
//                try {
//                    Map<String, Object> offerLetterResult = offerLetterService.generateAndSendOfferLetter(guarantor.getId());
//
//                    if (Boolean.TRUE.equals(offerLetterResult.get("success"))) {
//                        log.info("Offer letter sent successfully for guarantor ID: {}", guarantor.getId());
//                        response.put("offerLetterSent", true);
//                        response.put("applicationReference", offerLetterResult.get("applicationReference"));
//                        response.put("paymentLink", offerLetterResult.get("paymentLink"));
//                    } else {
//                        log.error("Failed to send offer letter: {}", offerLetterResult.get("message"));
//                        response.put("offerLetterSent", false);
//                        response.put("offerLetterError", offerLetterResult.get("message"));
//                    }
//                } catch (Exception e) {
//                    log.error("Error generating offer letter", e);
//                    response.put("offerLetterSent", false);
//                    response.put("offerLetterError", e.getMessage());
//                }
//
//                response.put("success", true);
//                response.put("verified", true);
//                response.put("message", "OTP verified successfully. Offer letter has been sent to your email.");
//                return response;
//            } else {
//                // Increment failed attempts
//                guarantor.setOtpAttempts(guarantor.getOtpAttempts() + 1);
//                guarantorRepository.save(guarantor);
//
//                log.warn("Invalid OTP for guarantor ID: {}. Attempts: {}", guarantor.getId(), guarantor.getOtpAttempts());
//
//                response.put("success", false);
//                response.put("message", "Invalid OTP code");
//                response.put("attemptsRemaining", 5 - guarantor.getOtpAttempts());
//                return response;
//            }
//
//        } catch (Exception e) {
//            log.error("Error verifying OTP: ", e);
//            response.put("success", false);
//            response.put("message", "Error: " + e.getMessage());
//            return response;
//        }
//    }
//    public Map<String, Object> verifyGuarantorOtp(String token, String otp) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            Optional<Guarantor> guarantorOpt = guarantorRepository.findByGuarantorToken(token);
//
//            if (guarantorOpt.isEmpty()) {
//                response.put("success", false);
//                response.put("message", "Invalid link");
//                return response;
//            }
//
//            Guarantor guarantor = guarantorOpt.get();
//
//            // Check if already verified
//            if (guarantor.getOtpVerified()) {
//                response.put("success", true);
//                response.put("alreadyVerified", true);
//                response.put("message", "OTP already verified");
//                return response;
//            }
//
//            // Check if OTP sent
//            if (guarantor.getOtpCode() == null) {
//                response.put("success", false);
//                response.put("message", "OTP not sent yet. Please wait for admin confirmation.");
//                return response;
//            }
//
//            // Check if OTP expired (10 minutes)
//            if (guarantor.getOtpSentAt() != null) {
//                Duration duration = Duration.between(guarantor.getOtpSentAt(), LocalDateTime.now());
//                if (duration.toMinutes() > 10) {
//                    response.put("success", false);
//                    response.put("message", "OTP has expired");
//                    return response;
//                }
//            }
//
//            // Check OTP match
//            if (otp.equals(guarantor.getOtpCode())) {
//                guarantor.setOtpVerified(true);
//                guarantor.setOtpVerifiedAt(LocalDateTime.now());
//                guarantorRepository.save(guarantor);
//
//                log.info("OTP verified successfully for guarantor ID: {}", guarantor.getId());
//
//                response.put("success", true);
//                response.put("verified", true);
//                response.put("message", "OTP verified successfully");
//                return response;
//            } else {
//                // Increment failed attempts
//                guarantor.setOtpAttempts(guarantor.getOtpAttempts() + 1);
//                guarantorRepository.save(guarantor);
//
//                log.warn("Invalid OTP for guarantor ID: {}. Attempts: {}", guarantor.getId(), guarantor.getOtpAttempts());
//
//                response.put("success", false);
//                response.put("message", "Invalid OTP code");
//                response.put("attemptsRemaining", 5 - guarantor.getOtpAttempts());
//                return response;
//            }
//
//        } catch (Exception e) {
//            log.error("Error verifying OTP: ", e);
//            response.put("success", false);
//            response.put("message", "Error: " + e.getMessage());
//            return response;
//        }
//    }

    /**
     * Step 6: Customer checks guarantor status
     */
    public Map<String, Object> getGuarantorStatus(String customerBvn) {
        Map<String, Object> response = new HashMap<>();

        try {
//            Optional<Guarantor> guarantorOpt = guarantorRepository.findByCustomerBvnAndActiveTrue(customerBvn);
            Optional<Guarantor> guarantorOpt = guarantorRepository.findTopByCustomerBvnAndActiveTrueOrderByCreatedAtDesc(customerBvn);


            if (guarantorOpt.isEmpty()) {
                response.put("success", true);
                response.put("status", "NOT_REQUESTED");
                response.put("message", "Guarantor not requested yet");
                return response;
            }

            Guarantor guarantor = guarantorOpt.get();

            if (guarantor.getOtpVerified()) {
                response.put("success", true);
                response.put("status", "VERIFIED");
                response.put("message", "Guarantor verified successfully");
                response.put("verifiedAt", guarantor.getOtpVerifiedAt());
                return response;
            }

            if (guarantor.getIsConfirmed()) {
                response.put("success", true);
                response.put("status", "OTP_SENT");
                response.put("message", "OTP sent to guarantor. Waiting for verification.");
                return response;
            }




            if (guarantor.getGuarantorFormSubmitted()) {
                response.put("success", true);
                response.put("status", "PENDING_ADMIN");
                response.put("message", "Guarantor form submitted. Waiting for admin confirmation.");
                return response;
            }

            response.put("success", true);
            response.put("status", "WAITING_GUARANTOR");
            response.put("message", "Waiting for guarantor to fill form");
            response.put("guarantorEmail", maskEmail(guarantor.getGuarantorEmail()));

            return response;

        } catch (Exception e) {
            log.error("Error getting guarantor status: ", e);
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }

    /**
 * Get approved application details by customer BVN
 */
@GetMapping("/application/success/{customerBvn}")
public ResponseEntity<Map<String, Object>> getApprovedApplication(@PathVariable String customerBvn) {
    Map<String, Object> response = new HashMap<>();

    try {
//        Optional<Guarantor> guarantorOpt = guarantorRepository.findByCustomerBvnAndActiveTrue(customerBvn);

        Optional<Guarantor> guarantorOpt = guarantorRepository.findTopByCustomerBvnAndActiveTrueOrderByCreatedAtDesc(customerBvn);


        if (guarantorOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Application not found");
            return ResponseEntity.status(404).body(response);
        }

        Guarantor guarantor = guarantorOpt.get();

        // Check if guarantor is verified
        if (!guarantor.getOtpVerified()) {
            response.put("success", false);
            response.put("message", "Application not yet approved");
            return ResponseEntity.status(400).body(response);
        }

        // Build customer object
        Map<String, Object> customer = new HashMap<>();
        customer.put("firstName", guarantor.getCustomerFirstName());
        customer.put("lastName", guarantor.getCustomerLastName());
        customer.put("email", guarantor.getCustomerEmail());
        customer.put("bvn", guarantor.getCustomerBvn());
        customer.put("plan", guarantor.getCustomerPlan());
        customer.put("gender", ""); // You may need to add this field to Guarantor entity

        response.put("success", true);
        response.put("customer", customer);

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        log.error("Error fetching approved application: ", e);
        response.put("success", false);
        response.put("message", "Error: " + e.getMessage());
        return ResponseEntity.status(500).body(response);
    }
}

    // Helper methods below...

    private MonoNinResponse callMonoNinLookup(String nin) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("mono-sec-key", monoApiKey);

            Map<String, String> body = new HashMap<>();
            body.put("nin", nin);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            log.info("Calling Mono NIN Lookup API for NIN {}", nin);

            ResponseEntity<MonoNinResponse> response = restTemplate.exchange(
                    monoNinUrl,
                    HttpMethod.POST,
                    entity,
                    MonoNinResponse.class
            );

            return response.getBody();

        } catch (Exception e) {
            log.error("Mono NIN Lookup API error", e);
            return null;
        }
    }

    private void sendGuarantorFormLinkEmail(Guarantor guarantor) {
        try {
            String formLink = frontendBaseUrl + "/guarantor/form/" + guarantor.getGuarantorToken();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(guarantor.getGuarantorEmail());
            helper.setSubject("You've Been Listed as a Guarantor - Action Required");

            String htmlContent = buildGuarantorFormLinkEmailBody(guarantor, formLink);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Guarantor form link sent to: {}", guarantor.getGuarantorEmail());

        } catch (Exception e) {
            log.error("Error sending guarantor form link email", e);
        }
    }

    private String buildGuarantorFormLinkEmailBody(Guarantor guarantor, String formLink) {
        return String.format("""
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #f7623b; color: white; padding: 30px 20px; text-align: center; border-radius: 5px 5px 0 0; }
        .content { background-color: #f9f9f9; padding: 30px 20px; border: 1px solid #ddd; border-radius: 0 0 5px 5px; }
        .button { display: inline-block; padding: 15px 40px; background-color: #f7623b; color: white !important; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; }
        .button:hover { background-color: #d5541f; }
        .info-box { background-color: white; padding: 20px; margin: 20px 0; border-left: 4px solid #f7623b; }
        .footer { text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #ddd; color: #777; font-size: 12px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🤝 Guarantor Request</h1>
        </div>
        
        <div class="content">
            <p>Hello,</p>
            
            <p><strong>%s %s</strong> has listed you as a guarantor for their product purchase application.</p>
            
            <div class="info-box">
                <h3 style="margin-top: 0; color: #f7623b;">Application Details:</h3>
                <p><strong>Customer:</strong> %s %s</p>
                <p><strong>Plan:</strong> %s</p>
                <p><strong>Duration:</strong> %s</p>
            </div>
            
            <p>To proceed with this application, please click the button below to fill out the guarantor form:</p>
            
            <div style="text-align: center; margin: 30px 0;">
                <a href="%s" class="button">
                    Fill Guarantor Form
                </a>
            </div>
            
            <p style="font-size: 14px; color: #666;">
                <strong>Important:</strong><br>
                • This link will expire in 24 hours<br>
                • You will need to provide your BVN, NIN, and phone number<br>
                • A verification OTP will be sent to your phone after admin confirmation
            </p>
            
            <p style="font-size: 12px; color: #999;">
                If you did not agree to be a guarantor, please ignore this email or contact us immediately.
            </p>
            
            <div class="footer">
                <p>This is an automated message from KoolKredit</p>
                <p>If the button doesn't work, copy and paste this link:<br>
                <span style="font-size: 10px; word-break: break-all;">%s</span></p>
            </div>
        </div>
    </div>
</body>
</html>
                """,
                guarantor.getCustomerFirstName(),
                guarantor.getCustomerLastName(),
                guarantor.getCustomerFirstName(),
                guarantor.getCustomerLastName(),
                guarantor.getCustomerPlan(),
                resolveInstallmentDuration(guarantor),
                //guarantor.getCustomerInstallmentDuration(),
                formLink,
                formLink
        );
    }

    private void sendAdminVerificationEmail(Guarantor guarantor, MonoNinResponse ninResponse) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(adminEmail);
            helper.setSubject("✓ Guarantor Verification Required - " + guarantor.getCustomerFirstName() + " " + guarantor.getCustomerLastName());

            String confirmUrl = appBaseUrl + "/guarantor/confirm/" + guarantor.getId();
            String htmlContent = buildAdminVerificationEmailBody(guarantor, ninResponse, confirmUrl);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Admin verification email sent");

        } catch (Exception e) {
            log.error("Error sending admin verification email", e);
        }
    }

    private String buildAdminVerificationEmailBody(Guarantor guarantor, MonoNinResponse ninResponse, String confirmUrl) {
        MonoNinResponse.NinData data = ninResponse.getData();

        return String.format("""
<!DOCTYPE html>
<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #f7623b; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
        .content { background-color: #f9f9f9; padding: 20px; border: 1px solid #ddd; }
        .section { margin-bottom: 20px; padding: 15px; background-color: white; border-left: 4px solid #f7623b; }
        .section h3 { margin-top: 0; color: #f7623b; }
        .info-row { padding: 8px 0; border-bottom: 1px solid #eee; }
        .info-label { font-weight: bold; color: #555; }
        .button { display: inline-block; padding: 15px 30px; background-color: #f7623b; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; }
        .button:hover { background-color: #d5541f; }
        .warning { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 15px 0; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🔔 Guarantor Verification Required</h1>
        </div>
        
        <div class="content">
            <div class="warning">
                <strong>⚠️ Action Required:</strong> Please verify guarantor information by phone call before clicking the confirm button.
            </div>

            <div class="section">
                <h3>👤 Customer Information</h3>
                <div class="info-row">
                    <span class="info-label">Name:</span> %s %s
                </div>
                <div class="info-row">
                    <span class="info-label">Email:</span> %s
                </div>
                <div class="info-row">
                    <span class="info-label">BVN:</span> %s
                </div>
                <div class="info-row">
                    <span class="info-label">Plan:</span> %s
                </div>
            </div>

            <div class="section">
                <h3>🤝 Submitted Guarantor Information</h3>
                <div class="info-row">
                    <span class="info-label">BVN:</span> %s
                </div>
                <div class="info-row">
                    <span class="info-label">Phone:</span> %s
                </div>
                <div class="info-row">
                    <span class="info-label">NIN:</span> %s
                </div>
            </div>

            <div class="section">
                <h3>✅ Verified NIN Details from Mono</h3>
                <div class="info-row">
                    <span class="info-label">Full Name:</span> %s %s %s
                </div>
                <div class="info-row">
                    <span class="info-label">Gender:</span> %s
                </div>
                <div class="info-row">
                    <span class="info-label">DOB:</span> %s
                </div>
                <div class="info-row">
                    <span class="info-label">Phone:</span> %s
                </div>
                <div class="info-row">
                    <span class="info-label">Address:</span> %s, %s, %s, %s
                </div>
            </div>

            <div style="text-align: center; margin: 30px 0;">
                <p><strong>After verifying via phone call, click below to confirm and send OTP to guarantor:</strong></p>
                <a href="%s" class="button">
                    ✓ Confirm Guarantor & Send OTP
                </a>
            </div>
        </div>
    </div>
</body>
</html>
                """,
                guarantor.getCustomerFirstName(),
                guarantor.getCustomerLastName(),
                guarantor.getCustomerEmail(),
                guarantor.getCustomerBvn(),
                guarantor.getCustomerPlan(),
                guarantor.getGuarantorBvn(),
                guarantor.getGuarantorPhoneNumber(),
                guarantor.getGuarantorNin(),
                safe(data.getFirstName()),
                safe(data.getMiddleName()),
                safe(data.getSurname()),
                safe(data.getGender()),
                safe(data.getBirthDate()),
                safe(data.getTelephoneNo()),
                safe(data.getResidenceAddress()),
                safe(data.getResidenceTown()),
                safe(data.getResidenceLga()),
                safe(data.getResidenceState()),
                confirmUrl
        );
    }

    private String safe(String v) {
        return (v == null || v.isBlank()) ? "N/A" : v;
    }

    private String maskPhoneNumber(String phone) {
        if (phone == null || phone.length() < 4) return phone;
        int visibleDigits = 4;
        int maskLength = phone.length() - visibleDigits;
        return "*".repeat(maskLength) + phone.substring(maskLength);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) return email;

        return username.substring(0, 2) + "****@" + domain;
    }
}























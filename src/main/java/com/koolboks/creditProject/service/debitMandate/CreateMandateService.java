package com.koolboks.creditProject.service.debitMandate;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.koolboks.creditProject.dto.mono.MonoBankListResponse;
import com.koolboks.creditProject.dto.mono.MonoCreateMandateRequest;
import com.koolboks.creditProject.dto.mono.MonoCreateMandateResponse;
import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
import com.koolboks.creditProject.repository.DebitMandateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class CreateMandateService {

    private static final Logger logger = LoggerFactory.getLogger(CreateMandateService.class);
    private static final String MONO_BANKS_URL = "https://api.withmono.com/v3/banks/list";
    private static final String MONO_MANDATE_URL = "https://api.withmono.com/v3/payments/mandates";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    private DebitMandateRepository debitMandateRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${mono.sec.key}")
    private String monoSecretKey;

    /**
     * Main method to create mandate - orchestrates the entire process
     */
    public MonoCreateMandateResponse createMandate(Long debitMandateId) {
        logger.info("=== STARTING MANDATE CREATION FOR DEBIT MANDATE ID: {} ===", debitMandateId);

        try {
            // Step 1: Get the DebitMandate record
            DebitMandate debitMandate = debitMandateRepository.findById(debitMandateId)
                    .orElseThrow(() -> new RuntimeException("DebitMandate not found with ID: " + debitMandateId));

            logger.info("Found DebitMandate for customer: {}", debitMandate.getCustomer_name());

            // Step 2: Populate missing bank_code if needed
            if (debitMandate.getBank_code() == null || debitMandate.getBank_code().isEmpty()) {
                logger.info("Bank code is missing. Fetching from Mono API using nip_code: {}",
                           debitMandate.getNip_code());
                populateBankCode(debitMandate);
            } else {
                logger.info("Bank code already exists: {}", debitMandate.getBank_code());
            }

            // Step 3: Populate missing required fields with defaults
            populateMissingFields(debitMandate);

            // Step 4: Validate all required fields
            validateDebitMandate(debitMandate);

            // Step 5: Create mandate via Mono API
            MonoCreateMandateResponse response = createMandateWithMono(debitMandate);

            // Step 6: Update DebitMandate with mandate details from response
            if (response != null && response.getData() != null) {
                updateDebitMandateFromResponse(debitMandate, response);
            }

            logger.info("=== MANDATE CREATION COMPLETED FOR DEBIT MANDATE ID: {} ===", debitMandateId);
            return response;

        } catch (Exception e) {
            logger.error("❌ Error creating mandate for DebitMandate ID: {}", debitMandateId, e);
            throw new RuntimeException("Failed to create mandate: " + e.getMessage(), e);
        }
    }

    /**
     * Populate missing fields with default or calculated values
     */
//    private void populateMissingFields(DebitMandate debitMandate) {
//        logger.info("Checking and populating missing fields...");
//
//        boolean needsSave = false;
//
//        // Populate description if missing
//        if (debitMandate.getDescription() == null || debitMandate.getDescription().isEmpty()) {
//            debitMandate.setDescription("KoolKredit Loan Repayment - " + debitMandate.getCustomer_name());
//            logger.info("✅ Set description: {}", debitMandate.getDescription());
//            needsSave = true;
//        }
//
//        // Populate start_date if missing (use today's date)
//        if (debitMandate.getStart_date() == null) {
//            debitMandate.setStart_date(LocalDate.now());
//            logger.info("✅ Set start_date to today: {}", debitMandate.getStart_date());
//            needsSave = true;
//        }
//
//        // Populate end_date if missing (1 year from start_date or today)
//        if (debitMandate.getEnd_date() == null) {
//            LocalDate startDate = debitMandate.getStart_date() != null
//                ? debitMandate.getStart_date()
//                : LocalDate.now();
//            debitMandate.setEnd_date(startDate.plusYears(1));
//            logger.info("✅ Set end_date to 1 year from start: {}", debitMandate.getEnd_date());
//            needsSave = true;
//        }
//
//        // Ensure mandate_type is set
//        if (debitMandate.getMandate_type() == null || debitMandate.getMandate_type().isEmpty()) {
//            debitMandate.setMandate_type("emandate");
//            logger.info("✅ Set mandate_type to default: emandate");
//            needsSave = true;
//        }
//
//        // Ensure debit_type is set
//        if (debitMandate.getDebit_type() == null || debitMandate.getDebit_type().isEmpty()) {
//            debitMandate.setDebit_type("variable");
//            logger.info("✅ Set debit_type to default: variable");
//            needsSave = true;
//        }
//
//        // Ensure fee_bearer is set
//        if (debitMandate.getFee_bearer() == null || debitMandate.getFee_bearer().isEmpty()) {
//            debitMandate.setFee_bearer("customer");
//            logger.info("✅ Set fee_bearer to default: customer");
//            needsSave = true;
//        }
//
//        // Ensure verification_method is set
//        if (debitMandate.getVerification_method() == null || debitMandate.getVerification_method().isEmpty()) {
//            debitMandate.setVerification_method("selfie_verification");
//            logger.info("✅ Set verification_method to default: selfie_verification");
//            needsSave = true;
//        }
//
//        if (needsSave) {
//            debitMandateRepository.save(debitMandate);
//            logger.info("✅ Missing fields populated and saved");
//        } else {
//            logger.info("All required fields already populated");
//        }
//    }



    /**
 * Populate missing fields with default or calculated values
 */
private void populateMissingFields(DebitMandate debitMandate) {
    logger.info("Checking and populating missing fields...");

    boolean needsSave = false;

    // Validate and fix reference if needed
    if (debitMandate.getReference() == null || debitMandate.getReference().isEmpty() ||
        !isValidMonoReference(debitMandate.getReference())) {

        String newReference = generateMonoCompliantReference();
        debitMandate.setReference(newReference);
        logger.info("✅ Generated new Mono-compliant reference: {}", newReference);
        needsSave = true;
    }

    // Populate description if missing
    if (debitMandate.getDescription() == null || debitMandate.getDescription().isEmpty()) {
        debitMandate.setDescription("KoolKredit Loan Repayment - " + debitMandate.getCustomer_name());
        logger.info("✅ Set description: {}", debitMandate.getDescription());
        needsSave = true;
    }

    // Populate start_date if missing (use today's date)
    if (debitMandate.getStart_date() == null) {
        debitMandate.setStart_date(LocalDate.now());
        logger.info("✅ Set start_date to today: {}", debitMandate.getStart_date());
        needsSave = true;
    }

    // Populate end_date if missing (1 year from start_date or today)
    if (debitMandate.getEnd_date() == null) {
        LocalDate startDate = debitMandate.getStart_date() != null
            ? debitMandate.getStart_date()
            : LocalDate.now();
        debitMandate.setEnd_date(startDate.plusYears(1));
        logger.info("✅ Set end_date to 1 year from start: {}", debitMandate.getEnd_date());
        needsSave = true;
    }

    // Ensure mandate_type is set
    if (debitMandate.getMandate_type() == null || debitMandate.getMandate_type().isEmpty()) {
        debitMandate.setMandate_type("emandate");
        logger.info("✅ Set mandate_type to default: emandate");
        needsSave = true;
    }

    // Ensure debit_type is set
    if (debitMandate.getDebit_type() == null || debitMandate.getDebit_type().isEmpty()) {
        debitMandate.setDebit_type("variable");
        logger.info("✅ Set debit_type to default: variable");
        needsSave = true;
    }

    // Ensure fee_bearer is set
    if (debitMandate.getFee_bearer() == null || debitMandate.getFee_bearer().isEmpty()) {
        debitMandate.setFee_bearer("customer");
        logger.info("✅ Set fee_bearer to default: customer");
        needsSave = true;
    }

    // Ensure verification_method is set
    if (debitMandate.getVerification_method() == null || debitMandate.getVerification_method().isEmpty()) {
        debitMandate.setVerification_method("selfie_verification");
        logger.info("✅ Set verification_method to default: selfie_verification");
        needsSave = true;
    }

    if (needsSave) {
        debitMandateRepository.save(debitMandate);
        logger.info("✅ Missing fields populated and saved");
    } else {
        logger.info("All required fields already populated");
    }
}

/**
 * Check if reference is valid for Mono API
 */
private boolean isValidMonoReference(String reference) {
    if (reference == null || reference.isEmpty()) {
        return false;
    }

    // Must be 24 characters or less
    if (reference.length() > 24) {
        return false;
    }

    // Must contain only letters and numbers (no special characters)
    return reference.matches("^[A-Za-z0-9]+$");
}

/**
 * Generate Mono-compliant reference (max 24 chars, alphanumeric only)
 */
private String generateMonoCompliantReference() {
    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    java.security.SecureRandom random = new java.security.SecureRandom();

    // Get timestamp (last 10 digits)
    String timestamp = String.valueOf(System.currentTimeMillis());
    timestamp = timestamp.substring(timestamp.length() - 10);

    // Generate 8 random characters
    StringBuilder randomPart = new StringBuilder(8);
    for (int i = 0; i < 8; i++) {
        randomPart.append(chars.charAt(random.nextInt(chars.length())));
    }

    // Format: KK + timestamp(10) + random(8) = 20 characters total
    return "KK" + timestamp + randomPart.toString();
}

    /**
     * Update DebitMandate entity with response data from Mono API
     */
    private void updateDebitMandateFromResponse(DebitMandate debitMandate, MonoCreateMandateResponse response) {
        logger.info("Updating DebitMandate with response data...");

        MonoCreateMandateResponse.Data data = response.getData();

        // Store the mandate ID (using 'id' field from response)
        if (data.getMandateId() != null && !data.getMandateId().isEmpty()) {
            debitMandate.setMandate_id(data.getMandateId());
            logger.info("✅ Set mandate_id: {}", data.getMandateId());
        }

        // Update debit_account flag based on ready_to_debit status
        // Only set to true if mandate is approved AND ready to debit
        if (data.isApproved() && data.isReadyToDebit()) {
            debitMandate.setDebit_account(true);
            logger.info("✅ Set debit_account to TRUE (mandate is approved and ready)");
        } else {
            debitMandate.setDebit_account(false);
            logger.info("⚠️ Set debit_account to FALSE (mandate status: {}, approved: {}, ready: {})",
                       data.getStatus(), data.isApproved(), data.isReadyToDebit());
        }

        debitMandateRepository.save(debitMandate);

        logger.info("✅ DebitMandate updated successfully");
        logger.info("   Mandate ID: {}", data.getMandateId());
        logger.info("   Status: {}", data.getStatus());
        logger.info("   Ready to Debit: {}", data.isReadyToDebit());
        logger.info("   Approved: {}", data.isApproved());
        logger.info("   NIBSS Code: {}", data.getNibssCode());

        // Log important verification info
        if ("initiated".equals(data.getStatus()) && !data.isApproved()) {
            logger.warn("⚠️ MANDATE REQUIRES VERIFICATION");
            logger.warn("⚠️ Message: {}", response.getMessage());
            logger.warn("⚠️ Customer must verify by transferring to one of the provided accounts");

            if (data.getTransferDestinations() != null && !data.getTransferDestinations().isEmpty()) {
                logger.warn("⚠️ Available transfer destinations:");
                data.getTransferDestinations().forEach(dest ->
                    logger.warn("   - {}: {}", dest.getBankName(), dest.getAccountNumber())
                );
            }
        }
    }

    /**
     * Fetch bank list from Mono API and populate bank_code by matching nip_code
     */
//    private void populateBankCode(DebitMandate debitMandate) {
//        try {
//            if (debitMandate.getNip_code() == null || debitMandate.getNip_code().isEmpty()) {
//                throw new RuntimeException("Cannot populate bank_code: nip_code is missing");
//            }
//
//            logger.info("Fetching bank list from Mono API...");
//
//            // Create headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("accept", "application/json");
//            headers.set("mono-sec-key", monoSecretKey);
//
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//
//            // Make GET request to fetch bank list
//            ResponseEntity<MonoBankListResponse> response = restTemplate.exchange(
//                    MONO_BANKS_URL,
//                    HttpMethod.GET,
//                    entity,
//                    MonoBankListResponse.class
//            );
//
//            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
//                MonoBankListResponse bankListResponse = response.getBody();
//
//                if (bankListResponse.getData() != null &&
//                    bankListResponse.getData().getBanks() != null) {
//
//                    List<MonoBankListResponse.Bank> banks = bankListResponse.getData().getBanks();
//                    logger.info("Retrieved {} banks from Mono API", banks.size());
//
//                    // Find matching bank by nip_code
//                    Optional<MonoBankListResponse.Bank> matchingBank = banks.stream()
//                            .filter(bank -> bank.getNipCode() != null &&
//                                          bank.getNipCode().equals(debitMandate.getNip_code()))
//                            .findFirst();
//
//                    if (matchingBank.isPresent()) {
//                        String bankCode = matchingBank.get().getBankCode();
//                        String bankName = matchingBank.get().getName();
//
//                        debitMandate.setBank_code(bankCode);
//
//                        // Also update bank_name if it's not set
//                        if (debitMandate.getBank_name() == null || debitMandate.getBank_name().isEmpty()) {
//                            debitMandate.setBank_name(bankName);
//                        }
//
//                        debitMandateRepository.save(debitMandate);
//
//                        logger.info("✅ Populated bank_code: {} for bank: {} (nip_code: {})",
//                                   bankCode, bankName, debitMandate.getNip_code());
//                    } else {
//                        throw new RuntimeException(
//                            "No bank found with nip_code: " + debitMandate.getNip_code()
//                        );
//                    }
//                } else {
//                    throw new RuntimeException("Invalid response from Mono bank list API");
//                }
//            } else {
//                throw new RuntimeException("Failed to fetch bank list from Mono API. Status: " +
//                                         response.getStatusCode());
//            }
//
//        } catch (Exception e) {
//            logger.error("❌ Error populating bank_code", e);
//            throw new RuntimeException("Failed to populate bank_code: " + e.getMessage(), e);
//        }
//    }




    /**
 * Fetch bank list from Mono API and populate bank_code by matching nip_code
 */
private void populateBankCode(DebitMandate debitMandate) {
    try {
        if (debitMandate.getNip_code() == null || debitMandate.getNip_code().isEmpty()) {
            throw new RuntimeException("Cannot populate bank_code: nip_code is missing");
        }

        logger.info("Fetching bank list from Mono API...");

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/json");
        headers.set("mono-sec-key", monoSecretKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make GET request to fetch bank list
        ResponseEntity<MonoBankListResponse> response = restTemplate.exchange(
                MONO_BANKS_URL,
                HttpMethod.GET,
                entity,
                MonoBankListResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            MonoBankListResponse bankListResponse = response.getBody();

            // The data field is now directly a list of banks
            if (bankListResponse.getData() != null && !bankListResponse.getData().isEmpty()) {

                List<MonoBankListResponse.Bank> banks = bankListResponse.getData();
                logger.info("Retrieved {} banks from Mono API", banks.size());

                // Find matching bank by nip_code
                Optional<MonoBankListResponse.Bank> matchingBank = banks.stream()
                        .filter(bank -> bank.getNipCode() != null &&
                                      bank.getNipCode().equals(debitMandate.getNip_code()))
                        .findFirst();

                if (matchingBank.isPresent()) {
                    String bankCode = matchingBank.get().getBankCode();
                    String bankName = matchingBank.get().getName();

                    debitMandate.setBank_code(bankCode);

                    // Also update bank_name if it's not set
                    if (debitMandate.getBank_name() == null || debitMandate.getBank_name().isEmpty()) {
                        debitMandate.setBank_name(bankName);
                    }

                    debitMandateRepository.save(debitMandate);

                    logger.info("✅ Populated bank_code: {} for bank: {} (nip_code: {})",
                               bankCode, bankName, debitMandate.getNip_code());
                } else {
                    throw new RuntimeException(
                        "No bank found with nip_code: " + debitMandate.getNip_code()
                    );
                }
            } else {
                throw new RuntimeException("Invalid response from Mono bank list API - no banks returned");
            }
        } else {
            throw new RuntimeException("Failed to fetch bank list from Mono API. Status: " +
                                     response.getStatusCode());
        }

    } catch (Exception e) {
        logger.error("❌ Error populating bank_code", e);
        throw new RuntimeException("Failed to populate bank_code: " + e.getMessage(), e);
    }
}

    /**
     * Validate that all required fields are present in DebitMandate
     */
    private void validateDebitMandate(DebitMandate debitMandate) {
        logger.info("Validating DebitMandate fields...");

        StringBuilder errors = new StringBuilder();

        if (debitMandate.getCustomer_id() == null || debitMandate.getCustomer_id().isEmpty()) {
            errors.append("customer_id is required; ");
        }
        if (debitMandate.getMandate_type() == null || debitMandate.getMandate_type().isEmpty()) {
            errors.append("mandate_type is required; ");
        }
        if (debitMandate.getDebit_type() == null || debitMandate.getDebit_type().isEmpty()) {
            errors.append("debit_type is required; ");
        }
        if (debitMandate.getAmount() <= 0) {
            errors.append("amount must be greater than 0; ");
        }
        if (debitMandate.getReference() == null || debitMandate.getReference().isEmpty()) {
            errors.append("reference is required; ");
        }
        if (debitMandate.getAccount_number() == null || debitMandate.getAccount_number().isEmpty()) {
            errors.append("account_number is required; ");
        }
        if (debitMandate.getBank_code() == null || debitMandate.getBank_code().isEmpty()) {
            errors.append("bank_code is required; ");
        }
        if (debitMandate.getDescription() == null || debitMandate.getDescription().isEmpty()) {
            errors.append("description is required; ");
        }
        if (debitMandate.getStart_date() == null) {
            errors.append("start_date is required; ");
        }
        if (debitMandate.getEnd_date() == null) {
            errors.append("end_date is required; ");
        }

        if (errors.length() > 0) {
            String errorMessage = "DebitMandate validation failed: " + errors.toString();
            logger.error("❌ {}", errorMessage);
            throw new RuntimeException(errorMessage);
        }

        logger.info("✅ DebitMandate validation passed");
    }

    /**
     * Create mandate via Mono API
     */
    private MonoCreateMandateResponse createMandateWithMono(DebitMandate debitMandate) {
        try {
            logger.info("Creating mandate with Mono API...");

            // Build request payload
            MonoCreateMandateRequest request = new MonoCreateMandateRequest();
            request.setCustomer(debitMandate.getCustomer_id());
            request.setMandateType(debitMandate.getMandate_type());
            request.setDebitType(debitMandate.getDebit_type());
            request.setAmount(debitMandate.getAmount());
            request.setReference(debitMandate.getReference());
            request.setFeeBearer(debitMandate.getFee_bearer());
            request.setAccountNumber(debitMandate.getAccount_number());
            request.setBankCode(debitMandate.getBank_code());
            request.setDescription(debitMandate.getDescription());
            request.setStartDate(formatDate(debitMandate.getStart_date()));
            request.setEndDate(formatDate(debitMandate.getEnd_date()));
            request.setVerificationMethod(debitMandate.getVerification_method());

            logger.info("Request payload:");
            logger.info("  customer: {}", request.getCustomer());
            logger.info("  mandate_type: {}", request.getMandateType());
            logger.info("  debit_type: {}", request.getDebitType());
            logger.info("  amount: {}", request.getAmount());
            logger.info("  reference: {}", request.getReference());
            logger.info("  account_number: {}", request.getAccountNumber());
            logger.info("  bank_code: {}", request.getBankCode());

            // Create headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("accept", "application/json");
            headers.set("mono-sec-key", monoSecretKey);

            HttpEntity<MonoCreateMandateRequest> entity = new HttpEntity<>(request, headers);

            // Make POST request
            ResponseEntity<MonoCreateMandateResponse> response = restTemplate.exchange(
                    MONO_MANDATE_URL,
                    HttpMethod.POST,
                    entity,
                    MonoCreateMandateResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK ||
                response.getStatusCode() == HttpStatus.CREATED) {

                MonoCreateMandateResponse mandateResponse = response.getBody();

                if (mandateResponse != null && "successful".equalsIgnoreCase(mandateResponse.getStatus())) {
                    logger.info("✅ Mandate created successfully via Mono API");
                    logger.info("   Response Message: {}", mandateResponse.getMessage());
                    return mandateResponse;
                } else {
                    throw new RuntimeException("Mono API returned unsuccessful status: " +
                                             (mandateResponse != null ? mandateResponse.getMessage() : "Unknown error"));
                }
            } else {
                throw new RuntimeException("Failed to create mandate. HTTP Status: " +
                                         response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("❌ Error creating mandate with Mono API", e);
            throw new RuntimeException("Failed to create mandate with Mono: " + e.getMessage(), e);
        }
    }

    /**
     * Format LocalDate to dd-MM-yyyy string
     */
    private String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Batch process: populate bank_code for all DebitMandates missing it
     */
    public void populateAllMissingBankCodes() {
        logger.info("=== BATCH POPULATING MISSING BANK CODES ===");

        try {
            // Find all records with nip_code but without bank_code
            List<DebitMandate> mandates = debitMandateRepository.findAll().stream()
                    .filter(dm -> dm.getNip_code() != null && !dm.getNip_code().isEmpty())
                    .filter(dm -> dm.getBank_code() == null || dm.getBank_code().isEmpty())
                    .toList();

            logger.info("Found {} DebitMandate records with missing bank_code", mandates.size());

            int successCount = 0;
            int failCount = 0;

            for (DebitMandate mandate : mandates) {
                try {
                    populateBankCode(mandate);
                    successCount++;
                } catch (Exception e) {
                    logger.error("Failed to populate bank_code for DebitMandate ID: {}",
                               mandate.getId(), e);
                    failCount++;
                }
            }

            logger.info("=== BATCH PROCESS COMPLETE ===");
            logger.info("Success: {}, Failed: {}", successCount, failCount);

        } catch (Exception e) {
            logger.error("❌ Error in batch population process", e);
            throw new RuntimeException("Batch population failed: " + e.getMessage(), e);
        }
    }
}
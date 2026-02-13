package com.koolboks.creditProject.service.debitMandate;



import com.koolboks.creditProject.dto.mono.AutoDebitResult;
import com.koolboks.creditProject.dto.mono.BalanceInquiryResponse;
import com.koolboks.creditProject.dto.mono.DebitRequest;
import com.koolboks.creditProject.dto.mono.DebitResponse;
import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
import com.koolboks.creditProject.exceptions.DebitOperationException;
import com.koolboks.creditProject.exceptions.InsufficientBalanceException;
import com.koolboks.creditProject.exceptions.MandateNotFoundException;
import com.koolboks.creditProject.repository.DebitMandateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class AutoDebitService {

    private static final Logger logger = LoggerFactory.getLogger(AutoDebitService.class);

    private final DebitMandateRepository debitMandateRepository;
    private final RestTemplate restTemplate;

    @Value("${mono.api.base-url}")
    private String monoBaseUrl;

    @Value("${mono.api.secret-key}")
    private String monoSecretKey;

    public AutoDebitService(DebitMandateRepository debitMandateRepository, RestTemplate restTemplate) {
        this.debitMandateRepository = debitMandateRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Check account balance and debit if sufficient funds are available
     *
     * @param mandateReference The mandate reference (UUID) from DebitMandate entity
     * @param amount The amount to debit
     * @param narration The narration for the transaction
     * @return AutoDebitResult containing the result of the operation
     * @throws MandateNotFoundException if mandate is not found
     * @throws InsufficientBalanceException if balance is insufficient
     * @throws DebitOperationException if debit operation fails
     */
    public AutoDebitResult checkBalanceAndDebit(String mandateReference, int amount, String narration) {
        logger.info("Starting auto-debit process for mandate reference: {}, amount: {}", mandateReference, amount);

        // 1. Fetch the DebitMandate from database
        DebitMandate mandate = debitMandateRepository.findByReference(mandateReference)
                .orElseThrow(() -> new MandateNotFoundException(mandateReference));

        // Validate that mandate has mandate_id (required for API calls)
        if (mandate.getMandate_id() == null || mandate.getMandate_id().isEmpty()) {
            throw new DebitOperationException(
                "Mandate ID is missing for reference: " + mandateReference,
                mandateReference
            );
        }

        logger.info("Found mandate with ID: {} for customer: {}",
                   mandate.getMandate_id(), mandate.getCustomer_name());

        // 2. Check balance
        BalanceInquiryResponse balanceResponse = checkBalance(mandate.getMandate_id(), amount);

        // 3. Verify sufficient balance
        if (!balanceResponse.getData().isHasSufficientBalance()) {
            logger.warn("Insufficient balance for mandate: {}, requested amount: {}",
                       mandate.getMandate_id(), amount);
            throw new InsufficientBalanceException(mandate.getMandate_id(), amount);
        }

        logger.info("Balance check passed for mandate: {}", mandate.getMandate_id());

        // 4. Generate unique transaction reference
        String transactionReference = generateTransactionReference();

        // 5. Perform debit
        DebitResponse debitResponse = performDebit(
            mandate.getMandate_id(),
            amount,
            transactionReference,
            narration
        );

        // 6. Update mandate entity if needed (mark as debited)
        if (debitResponse.getData() != null && debitResponse.getData().isSuccess()) {
            mandate.setDebit_account(true);
            debitMandateRepository.save(mandate);
            logger.info("Updated mandate {} - marked as debited", mandate.getMandate_id());
        }

        // 7. Build and return result
        return AutoDebitResult.builder()
                .success(true)
                .message("Account debited successfully")
                .mandateId(mandate.getMandate_id())
                .debitedAmount(amount)
                .referenceNumber(debitResponse.getData().getReferenceNumber())
                .sessionId(debitResponse.getData().getSessionId())
                .fee(debitResponse.getData().getFee())
                .feeBearer(debitResponse.getData().getFeeBearer())
                .accountDetails(balanceResponse.getData().getAccountDetails())
                .fullDebitData(debitResponse.getData())
                .build();
    }

    /**
     * Check account balance for a specific mandate
     *
     * @param mandateId The Mono mandate ID
     * @param amount The amount to check
     * @return BalanceInquiryResponse
     * @throws DebitOperationException if API call fails
     */
    private BalanceInquiryResponse checkBalance(String mandateId, int amount) {
        String url = String.format("%s/v3/payments/mandates/%s/balance-inquiry?amount=%d",
                                   monoBaseUrl, mandateId, amount);

        logger.debug("Checking balance at URL: {}", url);

        try {
            HttpHeaders headers = createHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<BalanceInquiryResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                BalanceInquiryResponse.class
            );

            if (response.getBody() == null) {
                throw new DebitOperationException(
                    "Empty response from balance inquiry API",
                    mandateId
                );
            }

            logger.info("Balance inquiry successful for mandate: {}", mandateId);
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("HTTP error during balance inquiry for mandate: {}. Status: {}, Body: {}",
                        mandateId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new DebitOperationException(
                "Failed to check balance: " + e.getMessage(),
                mandateId,
                e
            );
        } catch (Exception e) {
            logger.error("Unexpected error during balance inquiry for mandate: {}", mandateId, e);
            throw new DebitOperationException(
                "Unexpected error during balance inquiry: " + e.getMessage(),
                mandateId,
                e
            );
        }
    }

    /**
     * Perform debit operation
     *
     * @param mandateId The Mono mandate ID
     * @param amount The amount to debit
     * @param reference The transaction reference
     * @param narration The transaction narration
     * @return DebitResponse
     * @throws DebitOperationException if API call fails
     */
    private DebitResponse performDebit(String mandateId, int amount, String reference, String narration) {
        String url = String.format("%s/v3/payments/mandates/%s/debit", monoBaseUrl, mandateId);

        logger.debug("Performing debit at URL: {}", url);

        try {
            HttpHeaders headers = createHeaders();
            DebitRequest debitRequest = new DebitRequest(amount, reference, narration);
            HttpEntity<DebitRequest> entity = new HttpEntity<>(debitRequest, headers);

            ResponseEntity<DebitResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                DebitResponse.class
            );

            if (response.getBody() == null) {
                throw new DebitOperationException(
                    "Empty response from debit API",
                    mandateId
                );
            }

            DebitResponse debitResponse = response.getBody();

            // Validate response
            if (debitResponse.getData() == null || !debitResponse.getData().isSuccess()) {
                String errorMsg = debitResponse.getMessage() != null ?
                    debitResponse.getMessage() : "Debit operation failed";
                throw new DebitOperationException(
                    errorMsg,
                    mandateId,
                    debitResponse.getResponseCode()
                );
            }

            logger.info("Debit successful for mandate: {}, amount: {}, reference: {}",
                       mandateId, amount, debitResponse.getData().getReferenceNumber());
            return debitResponse;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("HTTP error during debit for mandate: {}. Status: {}, Body: {}",
                        mandateId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new DebitOperationException(
                "Failed to perform debit: " + e.getMessage(),
                mandateId,
                e
            );
        } catch (Exception e) {
            logger.error("Unexpected error during debit for mandate: {}", mandateId, e);
            throw new DebitOperationException(
                "Unexpected error during debit: " + e.getMessage(),
                mandateId,
                e
            );
        }
    }

    /**
     * Create HTTP headers with required authentication
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("accept", "application/json");
        headers.set("mono-sec-key", monoSecretKey);
        return headers;
    }

    /**
     * Generate a unique transaction reference
     */
    private String generateTransactionReference() {
        return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}

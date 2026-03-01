package com.koolboks.creditProject.service.installation;

import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
import com.koolboks.creditProject.repository.DebitMandateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class MandatePollingService {

    private static final Logger logger = LoggerFactory.getLogger(MandatePollingService.class);
    private static final String MONO_MANDATE_URL = "https://api.withmono.com/v3/payments/mandates/";
    private static final int MAX_ATTEMPTS = 144; // 144 x 10 mins = 24 hours max
    private static final long POLL_INTERVAL_MS = 10 * 60 * 1000L; // 10 minutes

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DebitMandateRepository debitMandateRepository;

    @Autowired
    private InstallationPinService installationPinService;

    @Value("${mono.api.key}")
    private String monoSecretKey;

    @Async
public void pollUntilApprovedWithCustomer(String mandateId, String orderId, DebitMandate debitMandate) {
    logger.info("=== STARTING MANDATE APPROVAL POLLING ===");
    logger.info("Mandate ID: {}, Order ID: {}, Customer: {}",
        mandateId, orderId, debitMandate.getCustomer_name());

    int attempt = 0;

    while (attempt < MAX_ATTEMPTS) {
        attempt++;
        logger.info("Polling attempt {} for mandate: {}", attempt, mandateId);

        try {
            boolean approved = checkMandateApproval(mandateId);

            if (approved) {
                logger.info("✅ Mandate APPROVED! Sending PIN to customer: {}",
                    debitMandate.getCustomer_name());

                // Generate and send PIN
                String pin = installationPinService.generatePin(orderId);

                if (debitMandate.getCustomer_email() != null) {
                    installationPinService.sendPinByEmail(
                        debitMandate.getCustomer_email(),
                        pin,
                        orderId,
                        debitMandate.getCustomer_name()
                    );
                    logger.info("✅ PIN sent to email: {}", debitMandate.getCustomer_email());
                }

                if (debitMandate.getCustomer_phone() != null) {
                    installationPinService.sendPinBySms(
                        debitMandate.getCustomer_phone(),
                        pin,
                        orderId
                    );
                    logger.info("✅ PIN sent to SMS: {}", debitMandate.getCustomer_phone());
                }

                // Update DB
                updateMandateApprovalStatus(mandateId);
                return;
            }

            logger.info("⏳ Not approved yet. Next check in 10 minutes. (attempt {}/{})",
                attempt, MAX_ATTEMPTS);
            Thread.sleep(POLL_INTERVAL_MS);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("❌ Polling interrupted for mandate: {}", mandateId);
            return;
        } catch (Exception e) {
            logger.error("❌ Error on polling attempt {}: {}", attempt, e.getMessage());
            try { Thread.sleep(POLL_INTERVAL_MS); }
            catch (InterruptedException ie) { Thread.currentThread().interrupt(); return; }
        }
    }

    logger.warn("⚠️ Polling timed out after 24 hours for mandate: {}", mandateId);
}

private boolean checkMandateApproval(String mandateId) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("accept", "application/json");
    headers.set("mono-sec-key", monoSecretKey);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<Map> response = restTemplate.exchange(
        MONO_MANDATE_URL + mandateId,
        HttpMethod.GET,
        entity,
        Map.class
    );

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
        if (data != null) {
            boolean approved = Boolean.TRUE.equals(data.get("approved"));
            logger.info("Mandate {} - status: {}, approved: {}", mandateId,
                data.get("status"), approved);
            return approved;
        }
    }
    return false;
}

private void updateMandateApprovalStatus(String mandateId) {
    debitMandateRepository.findByMandateId(mandateId).ifPresent(mandate -> {
        mandate.setDebit_account(true);
        debitMandateRepository.save(mandate);
        logger.info("✅ DebitMandate updated: debit_account=true for {}", mandateId);
    });
}
    /**
     * Find the DebitMandate and send PIN to customer
     */
    private void sendPinToCustomer(String orderId) {
        try {
            // Find DebitMandate linked to this order
            // orderId maps to the order, we need customer details
            // We'll look it up by mandate_id stored during polling start
            String pin = installationPinService.generatePin(orderId);
            logger.info("✅ PIN generated for order: {}", orderId);

            // The actual send will be handled by InstallationVerificationService
            // which has access to customer details - we store the pin and notify
            logger.info("PIN ready for order {}. Customer will be notified.", orderId);

        } catch (Exception e) {
            logger.error("❌ Error sending PIN after approval: {}", e.getMessage());
        }
    }
}
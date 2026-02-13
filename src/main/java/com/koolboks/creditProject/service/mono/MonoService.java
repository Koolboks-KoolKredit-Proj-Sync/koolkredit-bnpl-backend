package com.koolboks.creditProject.service.mono;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koolboks.creditProject.dto.monoCustomer.MonoCustomerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class MonoService {

    private static final Logger logger = LoggerFactory.getLogger(MonoService.class);

    @Value("${mono.api.base-url:https://api.withmono.com}")
    private String monoBaseUrl;

    @Value("${mono.sec.key}")
    private String monoSecretKey;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MonoService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Create a customer in Mono
     *
     * @param firstName Customer's first name
     * @param lastName Customer's last name
     * @param email Customer's email
     * @param phone Customer's phone number
     * @param address Customer's address (max 100 characters)
     * @param bvn Customer's BVN
     * @return Mono customer ID
     * @throws Exception if the API call fails
     */
    public String createCustomer(String firstName, String lastName, String email,
                                 String phone, String address, String bvn) throws Exception {

        logger.info("Creating Mono customer for email: {}", email);

        // Validate required fields
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone is required");
        }
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }
        if (bvn == null || bvn.trim().isEmpty()) {
            throw new IllegalArgumentException("BVN is required");
        }

        // Truncate address to 100 characters if needed
        String truncatedAddress = address.length() > 100 ? address.substring(0, 100) : address;

        // Build request body manually to avoid including response fields
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email.trim());
        requestBody.put("type", "individual");
        requestBody.put("first_name", firstName.trim());
        requestBody.put("last_name", lastName.trim());
        requestBody.put("address", truncatedAddress.trim());
        requestBody.put("phone", phone.trim());

        // Set identity (BVN)
        Map<String, String> identity = new HashMap<>();
        identity.put("type", "bvn");
        identity.put("number", bvn.trim());
        requestBody.put("identity", identity);

        // Convert to JSON
        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        logger.debug("Mono API Request: {}", requestBodyJson);

        // Build HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(monoBaseUrl + "/v2/customers"))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("mono-sec-key", monoSecretKey)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                .build();

        // Send request
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        logger.info("Mono API Response Status: {}", response.statusCode());
        logger.debug("Mono API Response Body: {}", response.body());

        // Parse response
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            MonoCustomerDTO responseDto = objectMapper.readValue(response.body(), MonoCustomerDTO.class);

            if (responseDto.getData() != null && responseDto.getData().getId() != null) {
                String customerId = responseDto.getData().getId();
                logger.info("Successfully created Mono customer with ID: {}", customerId);
                return customerId;
            } else {
                throw new Exception("Mono API returned success but no customer ID found");
            }
        } else {
            // Handle error response
            String errorMessage = "Mono API error: " + response.statusCode() + " - " + response.body();
            logger.error(errorMessage);
            throw new Exception(errorMessage);
        }
    }

    /**
     * Create a customer in Mono with error handling
     * Returns null if creation fails instead of throwing exception
     */
    public String createCustomerSafe(String firstName, String lastName, String email,
                                     String phone, String address, String bvn) {
        try {
            return createCustomer(firstName, lastName, email, phone, address, bvn);
        } catch (Exception e) {
            logger.error("Failed to create Mono customer: {}", e.getMessage(), e);
            return null;
        }
    }
}
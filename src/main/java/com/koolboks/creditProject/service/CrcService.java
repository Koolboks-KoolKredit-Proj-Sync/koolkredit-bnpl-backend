package com.koolboks.creditProject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class CrcService {

    @Value("${mono.api.url}")
    private String monoUrl;

    @Value("${mono.sec.key}")
    private String monoKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CrcService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Call Mono CRC endpoint and return the parsed JSON as JsonNode.
     * Throws runtime exception on network or parsing errors.
     */
    public JsonNode fetchCrcByBvn(String bvn) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("mono-sec-key", monoKey);

        String payload = String.format("{\"bvn\":\"%s\"}", bvn);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<String> resp = restTemplate.postForEntity(monoUrl, entity, String.class);

        if (resp.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("Mono CRC call failed: " + resp.getStatusCode());
        }

        try {
            return objectMapper.readTree(resp.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CRC response", e);
        }
    }
}

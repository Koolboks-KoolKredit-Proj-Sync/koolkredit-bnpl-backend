package com.koolboks.creditProject.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MonoClientService {

    @Value("${mono.sec.key}")
    private String monoKey;

    @Value("${mono.api.url}")
    private String monoEndpoint;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public JsonNode fetchCrcByBvn(String bvn) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("mono-sec-key", monoKey);

            String body = String.format("{\"bvn\":\"%s\"}", bvn);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> resp = rest.exchange(monoEndpoint, HttpMethod.POST, entity, String.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return mapper.readTree(resp.getBody());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mapper.createObjectNode(); // empty JSON node
    }
}












//package com.koolboks.creditProject.service;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.*;
//
//@Service
//public class MonoClientService {
//
//    @Value("${mono.secret.key}")
//    private String monoSecretKey;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    public Map<String, Object> fetchCreditReportByBvn(String bvn) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("mono-sec-key", monoSecretKey);
//
//        Map<String, String> body = new HashMap<>();
//        body.put("bvn", bvn);
//
//        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
//
//        // Use Map.class so we can parse flexible response
//        Map result = restTemplate.postForObject(
//                "https://api.withmono.com/v3/lookup/credit-history/crc",
//                entity,
//                Map.class
//        );
//
//        if (result == null) return Collections.emptyMap();
//        return result;
//    }
//}

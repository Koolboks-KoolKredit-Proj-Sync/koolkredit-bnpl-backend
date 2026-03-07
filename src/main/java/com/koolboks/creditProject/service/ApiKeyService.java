package com.koolboks.creditProject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApiKeyService {

    @Value("${mono.bank.api.key}")
    private String monoBankApiKey;

    @Value("${paystack.public.key}")
    private String paystackPublicKey;

    public Map<String, String> getPublicKeys() {
        Map<String, String> keys = new HashMap<>();
        keys.put("monoBankApiKey", monoBankApiKey);
        keys.put("paystackPublicKey", paystackPublicKey);
        return keys;
    }
}
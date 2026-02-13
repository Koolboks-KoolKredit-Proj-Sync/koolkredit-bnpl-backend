package com.koolboks.creditProject.service;

import com.koolboks.creditProject.dto.AgentEntryRequest;
import com.koolboks.creditProject.dto.MonoApiResponse;
import com.koolboks.creditProject.dto.VerificationResult;
import com.koolboks.creditProject.entity.AgentEntry;
import com.koolboks.creditProject.repository.AgentEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VerificationService {

    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);

    private final RestTemplate restTemplate;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;
    private final AgentEntryRepository agentEntryRepository;

    @Value("${mono.mashup.url}")
    private String monoApiUrl;

    //@Value("${mono.api.key:test_sk_h6nby3di44q76dhb0uha}")
    @Value("${mono.sec.key}")
    private String monoApiKey;

    public VerificationService(RestTemplate restTemplate, EmailService emailService,
                                FileStorageService fileStorageService,
                                AgentEntryRepository agentEntryRepository) {
        this.restTemplate = restTemplate;
        this.emailService = emailService;
        this.fileStorageService = fileStorageService;
        this.agentEntryRepository = agentEntryRepository;
    }

    public VerificationResult verifyAndNotify(AgentEntryRequest request) {
        AgentEntry agentEntry = null;

        try {
            // Store passport photo
            String passportFileName = fileStorageService.storeFile(request.getPassport(), request.getBvn());

            // Create and save initial entry
            agentEntry = createAgentEntry(request, passportFileName);
            agentEntry = agentEntryRepository.save(agentEntry);

            // Call Mono API
            MonoApiResponse monoResponse = callMonoApi(request.getBvn(), request.getNin(), request.getDateOfBirth());

            if (monoResponse == null) {
                String errorMsg = "API call failed - No response received";
                log.error(errorMsg);
                updateVerificationStatus(agentEntry, false, errorMsg);
                emailService.sendVerificationEmail(request, false, errorMsg);
                return new VerificationResult(false, false, errorMsg);
            }

            if (!"successful".equalsIgnoreCase(monoResponse.getStatus())) {
                String errorMsg = String.format("Verification failed: %s. Please use valid test credentials from Mono sandbox.",
                    monoResponse.getMessage());
                log.error(errorMsg);
                updateVerificationStatus(agentEntry, false, errorMsg);
                emailService.sendVerificationEmail(request, false, errorMsg);
                return new VerificationResult(false, false, errorMsg);
            }

            // Perform verification based on gender and marital status
            boolean isVerified = performVerification(request, monoResponse);

            // Update verification status
            String message = isVerified ? "Documents authentic and verified successfully" : "Documents do not match";
            updateVerificationStatus(agentEntry, isVerified, message);

            // Send email notification
            emailService.sendVerificationEmail(request, isVerified, message);

            return new VerificationResult(true, isVerified, message);

        } catch (Exception e) {
            String errorMsg = "Error: " + e.getMessage();
            log.error("Error during verification: ", e);
            if (agentEntry != null) {
                updateVerificationStatus(agentEntry, false, errorMsg);
            }
            emailService.sendVerificationEmail(request, false, errorMsg);
            return new VerificationResult(false, false, errorMsg);
        }
    }


    private AgentEntry createAgentEntry(AgentEntryRequest request, String passportFileName) {
    AgentEntry entry = new AgentEntry();

    // ADD THIS LINE - Business Type
    entry.setBusinessType(request.getBusinessType());

    // Product details
    entry.setProductName(request.getProductName());
    entry.setBrand(request.getBrand());
    entry.setSize(request.getSize());
    entry.setPrice(request.getPrice());

    // ADD THIS LINE TOO - Total Price (it's also missing)
    entry.setTotalPrice(request.getTotalPrice());

    // Personal information
    entry.setFirstName(request.getFirstName());
    entry.setMiddleName(request.getMiddleName());
    entry.setLastName(request.getLastName());
    entry.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
    entry.setMaritalStatus(request.getMaritalStatus());
    entry.setGender(request.getGender());

    // Verification details
    entry.setBvn(request.getBvn());
    entry.setNin(request.getNin());
    entry.setMobileNumber(request.getMobileNumber());
    entry.setPassportPhotoPath(passportFileName);

    // Payment plan
    entry.setPlan(request.getPlan());
    entry.setInstallmentDuration(request.getInstallmentDuration());
    entry.setOmolopeDays(request.getOmolopeDays());
    entry.setBankStatementMethod(request.getBankStatementMethod());

    // Default verification status
    entry.setIsVerified(false);

    return entry;
}






//    private AgentEntry createAgentEntry(AgentEntryRequest request, String passportFileName) {
//        AgentEntry entry = new AgentEntry();
//
//        // Product details
//        entry.setProductName(request.getProductName());
//        entry.setBrand(request.getBrand());
//        entry.setSize(request.getSize());
//        entry.setPrice(request.getPrice());
//
//        // Personal information
//        entry.setFirstName(request.getFirstName());
//        entry.setMiddleName(request.getMiddleName());
//        entry.setLastName(request.getLastName());
//        entry.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
//        entry.setMaritalStatus(request.getMaritalStatus());
//        entry.setGender(request.getGender());
//
//        // Verification details
//        entry.setBvn(request.getBvn());
//        entry.setNin(request.getNin());
//        entry.setMobileNumber(request.getMobileNumber());
//        entry.setPassportPhotoPath(passportFileName);
//
//        // Payment plan
//        entry.setPlan(request.getPlan());
//        entry.setInstallmentDuration(request.getInstallmentDuration());
//        entry.setOmolopeDays(request.getOmolopeDays());
//        entry.setBankStatementMethod(request.getBankStatementMethod());
//
//        // Default verification status
//        entry.setIsVerified(false);
//
//        return entry;
//    }

    private void updateVerificationStatus(AgentEntry entry, boolean isVerified, String message) {
        entry.setIsVerified(isVerified);
        entry.setVerificationMessage(message);
        entry.setVerificationDate(LocalDateTime.now());
        agentEntryRepository.save(entry);
    }

    private MonoApiResponse callMonoApi(String bvn, String nin, String dateOfBirth) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // FIXED: Use correct header name "mono-sec-key"
            if (monoApiKey != null && !monoApiKey.isEmpty()) {
                headers.set("mono-sec-key", monoApiKey);
                log.info("Using API key: {}...{}",
                    monoApiKey.substring(0, Math.min(15, monoApiKey.length())),
                    monoApiKey.length() > 15 ? monoApiKey.substring(monoApiKey.length() - 4) : "");
            } else {
                log.error("Mono API key is null or empty!");
            }

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("nin", nin);
            requestBody.put("bvn", bvn);
            // FIXED: API expects YYYY-MM-DD format, don't convert it
            requestBody.put("date_of_birth", dateOfBirth);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

            log.info("Calling Mono API with URL: {}", monoApiUrl);
            log.info("Request headers: {}", headers.keySet());
            log.info("Request body: nin={}, bvn={}, date_of_birth={}", nin, bvn, dateOfBirth);

            ResponseEntity<MonoApiResponse> response = restTemplate.exchange(
                    monoApiUrl,
                    HttpMethod.POST,
                    entity,
                    MonoApiResponse.class
            );

            log.info("Mono API response status: {}", response.getStatusCode());
            MonoApiResponse body = response.getBody();

            if (body != null) {
                log.info("Mono API response status field: {}", body.getStatus());
                log.info("Mono API response message: {}", body.getMessage());
            } else {
                log.error("Mono API response body is null");
            }

            return body;

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("Mono API HTTP Client Error - Status: {}, Response: {}",
                e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (org.springframework.web.client.HttpServerErrorException e) {
            log.error("Mono API HTTP Server Error - Status: {}, Response: {}",
                e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            log.error("Error calling Mono API: ", e);
            return null;
        }
    }

    private boolean performVerification(AgentEntryRequest request, MonoApiResponse monoResponse) {
        MonoApiResponse.PersonalInformation personalInfo = monoResponse.getData().getPersonalInformation();
        MonoApiResponse.IdentificationNumbers idNumbers = monoResponse.getData().getIdentificationNumbers();

        // Log raw API response data
        log.info("Raw API DOB value: '{}'", personalInfo.getDob());

        // Normalize submitted data
        String submittedFirstName = normalizeString(request.getFirstName());
        String submittedLastName = normalizeString(request.getLastName());
        String submittedBvn = request.getBvn().trim();
        String submittedNin = request.getNin().trim();
        String submittedDob = formatDateForComparison(request.getDateOfBirth());

        // Normalize API response data
        String apiFirstName = normalizeString(personalInfo.getFirstName());
        String apiSurname = normalizeString(personalInfo.getSurname());
        String apiBvn = idNumbers.getBvn().trim();
        String apiNin = idNumbers.getNin().trim();
        String apiDob = formatDateForComparison(personalInfo.getDob());

        // Check if female and married/widowed - only check firstName, DOB, BVN, NIN
        boolean isFemaleMarriedOrWidowed = "Female".equalsIgnoreCase(request.getGender()) &&
                ("Married".equalsIgnoreCase(request.getMaritalStatus()) ||
                 "Widowed".equalsIgnoreCase(request.getMaritalStatus()));

        log.info("Verification Details:");
        log.info("Submitted - First Name: {}, Last Name: {}, DOB: {}, BVN: {}, NIN: {}",
                submittedFirstName, submittedLastName, submittedDob, submittedBvn, submittedNin);
        log.info("API Response - First Name: {}, Surname: {}, DOB: {} (raw: {}), BVN: {}, NIN: {}",
                apiFirstName, apiSurname, apiDob, personalInfo.getDob(), apiBvn, apiNin);
        log.info("Female Married/Widowed: {}", isFemaleMarriedOrWidowed);

        // Perform validation
        boolean firstNameMatch = submittedFirstName.equals(apiFirstName);
        boolean lastNameMatch = submittedLastName.equals(apiSurname);
        boolean dobMatch = submittedDob.equals(apiDob);
        boolean bvnMatch = submittedBvn.equals(apiBvn);
        boolean ninMatch = submittedNin.equals(apiNin);

        log.info("Match Results - FirstName: {}, LastName: {}, DOB: {}, BVN: {}, NIN: {}",
                firstNameMatch, lastNameMatch, dobMatch, bvnMatch, ninMatch);

        if (isFemaleMarriedOrWidowed) {
            // For female married/widowed: check firstName, DOB, BVN, NIN only
            return firstNameMatch && dobMatch && bvnMatch && ninMatch;
        } else {
            // For others: check firstName, lastName, DOB, BVN, NIN
            return firstNameMatch && lastNameMatch && dobMatch && bvnMatch && ninMatch;
        }
    }

    private String normalizeString(String str) {
        if (str == null) return "";
        // Convert to uppercase and trim
        return str.trim().toUpperCase();
    }

    private String formatDateForComparison(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return "";
        }

        // Normalize dates to dd-MM-yyyy format for comparison
        // The Mono API returns dates in dd-MM-yyyy format (e.g., "01-09-1991")
        try {
            SimpleDateFormat[] possibleFormats = {
                new SimpleDateFormat("dd-MM-yyyy"),  // Mono API format (PRIORITY)
                new SimpleDateFormat("yyyy-MM-dd"),  // Our input format
                new SimpleDateFormat("dd/MM/yyyy"),
                new SimpleDateFormat("MM/dd/yyyy"),
                new SimpleDateFormat("yyyy/MM/dd")
            };

            Date date = null;
            for (SimpleDateFormat format : possibleFormats) {
                format.setLenient(false); // Strict parsing
                try {
                    date = format.parse(dateStr);
                    break;
                } catch (ParseException ignored) {
                }
            }

            if (date != null) {
                // Always output in dd-MM-yyyy format to match Mono API
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = outputFormat.format(date);
                log.debug("Formatted date '{}' to '{}'", dateStr, formattedDate);
                return formattedDate;
            }
        } catch (Exception e) {
            log.error("Error formatting date: {}", dateStr, e);
        }

        log.warn("Could not parse date: '{}', returning original", dateStr);
        return dateStr;
    }
}




















//package com.koolboks.creditProject.service;
//
//import com.koolboks.creditProject.dto.AgentEntryRequest;
//import com.koolboks.creditProject.dto.MonoApiResponse;
//import com.koolboks.creditProject.dto.VerificationResult;
//import com.koolboks.creditProject.entity.AgentEntry;
//import com.koolboks.creditProject.repository.AgentEntryRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Service
//public class VerificationService {
//
//    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);
//
//    private final RestTemplate restTemplate;
//    private final EmailService emailService;
//    private final FileStorageService fileStorageService;
//    private final AgentEntryRepository agentEntryRepository;
//
//    @Value("${mono.api.url:https://api.withmono.com/v3/lookup/mashup}")
//    private String monoApiUrl;
//
//    @Value("${mono.api.key:test_sk_h6nby3di44q76dhb0uha}")
//    private String monoApiKey;
//
//    public VerificationService(RestTemplate restTemplate, EmailService emailService,
//                                FileStorageService fileStorageService,
//                                AgentEntryRepository agentEntryRepository) {
//        this.restTemplate = restTemplate;
//        this.emailService = emailService;
//        this.fileStorageService = fileStorageService;
//        this.agentEntryRepository = agentEntryRepository;
//    }
//
//    public VerificationResult verifyAndNotify(AgentEntryRequest request) {
//        AgentEntry agentEntry = null;
//
//        try {
//            // Store passport photo
//            String passportFileName = fileStorageService.storeFile(request.getPassport(), request.getBvn());
//
//            // Create and save initial entry
//            agentEntry = createAgentEntry(request, passportFileName);
//            agentEntry = agentEntryRepository.save(agentEntry);
//
//            // Call Mono API
//            MonoApiResponse monoResponse = callMonoApi(request.getBvn(), request.getNin(), request.getDateOfBirth());
//
//            if (monoResponse == null) {
//                String errorMsg = "API call failed - No response received";
//                log.error(errorMsg);
//                updateVerificationStatus(agentEntry, false, errorMsg);
//                emailService.sendVerificationEmail(request, false, errorMsg);
//                return new VerificationResult(false, false, errorMsg);
//            }
//
//            if (!"successful".equalsIgnoreCase(monoResponse.getStatus())) {
//                String errorMsg = String.format("Verification failed: %s. Please use valid test credentials from Mono sandbox.",
//                    monoResponse.getMessage());
//                log.error(errorMsg);
//                updateVerificationStatus(agentEntry, false, errorMsg);
//                emailService.sendVerificationEmail(request, false, errorMsg);
//                return new VerificationResult(false, false, errorMsg);
//            }
//
//            // Perform verification based on gender and marital status
//            boolean isVerified = performVerification(request, monoResponse);
//
//            // Update verification status
//            String message = isVerified ? "Documents authentic and verified successfully" : "Documents do not match";
//            updateVerificationStatus(agentEntry, isVerified, message);
//
//            // Send email notification
//            emailService.sendVerificationEmail(request, isVerified, message);
//
//            return new VerificationResult(true, isVerified, message);
//
//        } catch (Exception e) {
//            String errorMsg = "Error: " + e.getMessage();
//            log.error("Error during verification: ", e);
//            if (agentEntry != null) {
//                updateVerificationStatus(agentEntry, false, errorMsg);
//            }
//            emailService.sendVerificationEmail(request, false, errorMsg);
//            return new VerificationResult(false, false, errorMsg);
//        }
//    }
//
//    private AgentEntry createAgentEntry(AgentEntryRequest request, String passportFileName) {
//        AgentEntry entry = new AgentEntry();
//
//        // Product details
//        entry.setProductName(request.getProductName());
//        entry.setBrand(request.getBrand());
//        entry.setSize(request.getSize());
//        entry.setPrice(request.getPrice());
//
//        // Personal information
//        entry.setFirstName(request.getFirstName());
//        entry.setMiddleName(request.getMiddleName());
//        entry.setLastName(request.getLastName());
//        entry.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
//        entry.setMaritalStatus(request.getMaritalStatus());
//        entry.setGender(request.getGender());
//
//        // Verification details
//        entry.setBvn(request.getBvn());
//        entry.setNin(request.getNin());
//        entry.setMobileNumber(request.getMobileNumber());
//        entry.setPassportPhotoPath(passportFileName);
//
//        // Payment plan
//        entry.setPlan(request.getPlan());
//        entry.setInstallmentDuration(request.getInstallmentDuration());
//        entry.setOmolopeDays(request.getOmolopeDays());
//        entry.setBankStatementMethod(request.getBankStatementMethod());
//
//        // Default verification status
//        entry.setIsVerified(false);
//
//        return entry;
//    }
//
//    private void updateVerificationStatus(AgentEntry entry, boolean isVerified, String message) {
//        entry.setIsVerified(isVerified);
//        entry.setVerificationMessage(message);
//        entry.setVerificationDate(LocalDateTime.now());
//        agentEntryRepository.save(entry);
//    }
//
//    private MonoApiResponse callMonoApi(String bvn, String nin, String dateOfBirth) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//
//            // FIXED: Use correct header name "mono-sec-key"
//            if (monoApiKey != null && !monoApiKey.isEmpty()) {
//                headers.set("mono-sec-key", monoApiKey);
//                log.info("Using API key: {}...{}",
//                    monoApiKey.substring(0, Math.min(15, monoApiKey.length())),
//                    monoApiKey.length() > 15 ? monoApiKey.substring(monoApiKey.length() - 4) : "");
//            } else {
//                log.error("Mono API key is null or empty!");
//            }
//
//            Map<String, String> requestBody = new HashMap<>();
//            requestBody.put("nin", nin);
//            requestBody.put("bvn", bvn);
//            // FIXED: API expects YYYY-MM-DD format, don't convert it
//            requestBody.put("date_of_birth", dateOfBirth);
//
//            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
//
//            log.info("Calling Mono API with URL: {}", monoApiUrl);
//            log.info("Request headers: {}", headers.keySet());
//            log.info("Request body: nin={}, bvn={}, date_of_birth={}", nin, bvn, dateOfBirth);
//
//            ResponseEntity<MonoApiResponse> response = restTemplate.exchange(
//                    monoApiUrl,
//                    HttpMethod.POST,
//                    entity,
//                    MonoApiResponse.class
//            );
//
//            log.info("Mono API response status: {}", response.getStatusCode());
//            MonoApiResponse body = response.getBody();
//
//            if (body != null) {
//                log.info("Mono API response status field: {}", body.getStatus());
//                log.info("Mono API response message: {}", body.getMessage());
//            } else {
//                log.error("Mono API response body is null");
//            }
//
//            return body;
//
//        } catch (org.springframework.web.client.HttpClientErrorException e) {
//            log.error("Mono API HTTP Client Error - Status: {}, Response: {}",
//                e.getStatusCode(), e.getResponseBodyAsString());
//            return null;
//        } catch (org.springframework.web.client.HttpServerErrorException e) {
//            log.error("Mono API HTTP Server Error - Status: {}, Response: {}",
//                e.getStatusCode(), e.getResponseBodyAsString());
//            return null;
//        } catch (Exception e) {
//            log.error("Error calling Mono API: ", e);
//            return null;
//        }
//    }
//
//    private boolean performVerification(AgentEntryRequest request, MonoApiResponse monoResponse) {
//        MonoApiResponse.PersonalInformation personalInfo = monoResponse.getData().getPersonalInformation();
//        MonoApiResponse.IdentificationNumbers idNumbers = monoResponse.getData().getIdentificationNumbers();
//
//        // Normalize submitted data
//        String submittedFirstName = normalizeString(request.getFirstName());
//        String submittedLastName = normalizeString(request.getLastName());
//        String submittedBvn = request.getBvn().trim();
//        String submittedNin = request.getNin().trim();
//        String submittedDob = formatDateForComparison(request.getDateOfBirth());
//
//        // Normalize API response data
//        String apiFirstName = normalizeString(personalInfo.getFirstName());
//        String apiSurname = normalizeString(personalInfo.getSurname());
//        String apiBvn = idNumbers.getBvn().trim();
//        String apiNin = idNumbers.getNin().trim();
//        String apiDob = formatDateForComparison(personalInfo.getDob());
//
//        // Check if female and married/widowed - only check firstName, DOB, BVN, NIN
//        boolean isFemaleMarriedOrWidowed = "Female".equalsIgnoreCase(request.getGender()) &&
//                ("Married".equalsIgnoreCase(request.getMaritalStatus()) ||
//                 "Widowed".equalsIgnoreCase(request.getMaritalStatus()));
//
//        log.info("Verification Details:");
//        log.info("Submitted - First Name: {}, Last Name: {}, DOB: {}, BVN: {}, NIN: {}",
//                submittedFirstName, submittedLastName, submittedDob, submittedBvn, submittedNin);
//        log.info("API Response - First Name: {}, Surname: {}, DOB: {}, BVN: {}, NIN: {}",
//                apiFirstName, apiSurname, apiDob, apiBvn, apiNin);
//        log.info("Female Married/Widowed: {}", isFemaleMarriedOrWidowed);
//
//        // Perform validation
//        boolean firstNameMatch = submittedFirstName.equals(apiFirstName);
//        boolean lastNameMatch = submittedLastName.equals(apiSurname);
//        boolean dobMatch = submittedDob.equals(apiDob);
//        boolean bvnMatch = submittedBvn.equals(apiBvn);
//        boolean ninMatch = submittedNin.equals(apiNin);
//
//        log.info("Match Results - FirstName: {}, LastName: {}, DOB: {}, BVN: {}, NIN: {}",
//                firstNameMatch, lastNameMatch, dobMatch, bvnMatch, ninMatch);
//
//        if (isFemaleMarriedOrWidowed) {
//            // For female married/widowed: check firstName, DOB, BVN, NIN only
//            return firstNameMatch && dobMatch && bvnMatch && ninMatch;
//        } else {
//            // For others: check firstName, lastName, DOB, BVN, NIN
//            return firstNameMatch && lastNameMatch && dobMatch && bvnMatch && ninMatch;
//        }
//    }
//
//    private String normalizeString(String str) {
//        if (str == null) return "";
//        // Convert to uppercase and trim
//        return str.trim().toUpperCase();
//    }
//
//    private String formatDateForComparison(String dateStr) {
//        // Normalize dates to YYYY-MM-DD format for comparison
//        try {
//            SimpleDateFormat[] possibleFormats = {
//                new SimpleDateFormat("yyyy-MM-dd"),
//                new SimpleDateFormat("dd-MM-yyyy"),
//                new SimpleDateFormat("dd/MM/yyyy"),
//                new SimpleDateFormat("MM/dd/yyyy")
//            };
//
//            Date date = null;
//            for (SimpleDateFormat format : possibleFormats) {
//                try {
//                    date = format.parse(dateStr);
//                    break;
//                } catch (ParseException ignored) {
//                }
//            }
//
//            if (date != null) {
//                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                return outputFormat.format(date);
//            }
//        } catch (Exception e) {
//            log.error("Error formatting date: {}", dateStr, e);
//        }
//        return dateStr;
//    }
//}
//
//
//
//
//
//
//
//
//
//








//package com.koolboks.creditProject.service;
//
//import com.koolboks.creditProject.dto.AgentEntryRequest;
//import com.koolboks.creditProject.dto.MonoApiResponse;
//import com.koolboks.creditProject.entity.AgentEntry;
//import com.koolboks.creditProject.repository.AgentEntryRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Service
//public class VerificationService {
//
//    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);
//
//    private final RestTemplate restTemplate;
//    private final EmailService emailService;
//    private final FileStorageService fileStorageService;
//    private final AgentEntryRepository agentEntryRepository;
//
//    @Value("${mono.api.url:https://api.withmono.com/v3/lookup/mashup}")
//    private String monoApiUrl;
//
//    @Value("${mono.api.key:test_sk_h6nby3di44q76dhb0uha}")
//    private String monoApiKey;
//
//    public VerificationService(RestTemplate restTemplate, EmailService emailService,
//                                FileStorageService fileStorageService,
//                                AgentEntryRepository agentEntryRepository) {
//        this.restTemplate = restTemplate;
//        this.emailService = emailService;
//        this.fileStorageService = fileStorageService;
//        this.agentEntryRepository = agentEntryRepository;
//    }
//
//    public boolean verifyAndNotify(AgentEntryRequest request) {
//        AgentEntry agentEntry = null;
//
//        try {
//            // Store passport photo
//            String passportFileName = fileStorageService.storeFile(request.getPassport(), request.getBvn());
//
//            // Create and save initial entry
//            agentEntry = createAgentEntry(request, passportFileName);
//            agentEntry = agentEntryRepository.save(agentEntry);
//
//            // Call Mono API
//            MonoApiResponse monoResponse = callMonoApi(request.getBvn(), request.getNin(), request.getDateOfBirth());
//
//            if (monoResponse == null || !"successful".equalsIgnoreCase(monoResponse.getStatus())) {
//                log.error("Mono API call failed or returned unsuccessful status");
//                updateVerificationStatus(agentEntry, false, "API call failed");
//                emailService.sendVerificationEmail(request, false, "API call failed");
//                return false;
//            }
//
//            // Perform verification based on gender and marital status
//            boolean isVerified = performVerification(request, monoResponse);
//
//            // Update verification status
//            String message = isVerified ? "All documents match" : "Documents do not match";
//            updateVerificationStatus(agentEntry, isVerified, message);
//
//            // Send email notification
//            emailService.sendVerificationEmail(request, isVerified, message);
//
//            return isVerified;
//
//        } catch (Exception e) {
//            log.error("Error during verification: ", e);
//            if (agentEntry != null) {
//                updateVerificationStatus(agentEntry, false, "Error: " + e.getMessage());
//            }
//            emailService.sendVerificationEmail(request, false, "Error: " + e.getMessage());
//            return false;
//        }
//    }
//
//    private AgentEntry createAgentEntry(AgentEntryRequest request, String passportFileName) {
//        AgentEntry entry = new AgentEntry();
//
//        // Product details
//        entry.setProductName(request.getProductName());
//        entry.setBrand(request.getBrand());
//        entry.setSize(request.getSize());
//        entry.setPrice(request.getPrice());
//
//        // Personal information
//        entry.setFirstName(request.getFirstName());
//        entry.setMiddleName(request.getMiddleName());
//        entry.setLastName(request.getLastName());
//        entry.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
//        entry.setMaritalStatus(request.getMaritalStatus());
//        entry.setGender(request.getGender());
//
//        // Verification details
//        entry.setBvn(request.getBvn());
//        entry.setNin(request.getNin());
//        entry.setMobileNumber(request.getMobileNumber());
//        entry.setPassportPhotoPath(passportFileName);
//
//        // Payment plan
//        entry.setPlan(request.getPlan());
//        entry.setInstallmentDuration(request.getInstallmentDuration());
//        entry.setOmolopeDays(request.getOmolopeDays());
//        entry.setBankStatementMethod(request.getBankStatementMethod());
//
//        // Default verification status
//        entry.setIsVerified(false);
//
//        return entry;
//    }
//
//    private void updateVerificationStatus(AgentEntry entry, boolean isVerified, String message) {
//        entry.setIsVerified(isVerified);
//        entry.setVerificationMessage(message);
//        entry.setVerificationDate(LocalDateTime.now());
//        agentEntryRepository.save(entry);
//    }
//
//    private MonoApiResponse callMonoApi(String bvn, String nin, String dateOfBirth) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//
//            // FIXED: Use correct header name "mono-sec-key"
//            if (monoApiKey != null && !monoApiKey.isEmpty()) {
//                headers.set("mono-sec-key", monoApiKey);
//                log.info("Using API key: {}...{}",
//                    monoApiKey.substring(0, Math.min(15, monoApiKey.length())),
//                    monoApiKey.length() > 15 ? monoApiKey.substring(monoApiKey.length() - 4) : "");
//            } else {
//                log.error("Mono API key is null or empty!");
//            }
//
//            Map<String, String> requestBody = new HashMap<>();
//            requestBody.put("nin", nin);
//            requestBody.put("bvn", bvn);
//            // FIXED: API expects YYYY-MM-DD format, don't convert it
//            requestBody.put("date_of_birth", dateOfBirth);
//
//            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
//
//            log.info("Calling Mono API with URL: {}", monoApiUrl);
//            log.info("Request headers: {}", headers.keySet());
//            log.info("Request body: nin={}, bvn={}, date_of_birth={}", nin, bvn, dateOfBirth);
//
//            ResponseEntity<MonoApiResponse> response = restTemplate.exchange(
//                    monoApiUrl,
//                    HttpMethod.POST,
//                    entity,
//                    MonoApiResponse.class
//            );
//
//            log.info("Mono API response status: {}", response.getStatusCode());
//            MonoApiResponse body = response.getBody();
//
//            if (body != null) {
//                log.info("Mono API response status field: {}", body.getStatus());
//                log.info("Mono API response message: {}", body.getMessage());
//            } else {
//                log.error("Mono API response body is null");
//            }
//
//            return body;
//
//        } catch (org.springframework.web.client.HttpClientErrorException e) {
//            log.error("Mono API HTTP Client Error - Status: {}, Response: {}",
//                e.getStatusCode(), e.getResponseBodyAsString());
//            return null;
//        } catch (org.springframework.web.client.HttpServerErrorException e) {
//            log.error("Mono API HTTP Server Error - Status: {}, Response: {}",
//                e.getStatusCode(), e.getResponseBodyAsString());
//            return null;
//        } catch (Exception e) {
//            log.error("Error calling Mono API: ", e);
//            return null;
//        }
//    }
//
//    private boolean performVerification(AgentEntryRequest request, MonoApiResponse monoResponse) {
//        MonoApiResponse.PersonalInformation personalInfo = monoResponse.getData().getPersonalInformation();
//        MonoApiResponse.IdentificationNumbers idNumbers = monoResponse.getData().getIdentificationNumbers();
//
//        // Normalize submitted data
//        String submittedFirstName = normalizeString(request.getFirstName());
//        String submittedLastName = normalizeString(request.getLastName());
//        String submittedBvn = request.getBvn().trim();
//        String submittedNin = request.getNin().trim();
//        String submittedDob = formatDateForComparison(request.getDateOfBirth());
//
//        // Normalize API response data
//        String apiFirstName = normalizeString(personalInfo.getFirstName());
//        String apiSurname = normalizeString(personalInfo.getSurname());
//        String apiBvn = idNumbers.getBvn().trim();
//        String apiNin = idNumbers.getNin().trim();
//        String apiDob = formatDateForComparison(personalInfo.getDob());
//
//        // Check if female and married/widowed - only check firstName, DOB, BVN, NIN
//        boolean isFemaleMarriedOrWidowed = "Female".equalsIgnoreCase(request.getGender()) &&
//                ("Married".equalsIgnoreCase(request.getMaritalStatus()) ||
//                 "Widowed".equalsIgnoreCase(request.getMaritalStatus()));
//
//        log.info("Verification Details:");
//        log.info("Submitted - First Name: {}, Last Name: {}, DOB: {}, BVN: {}, NIN: {}",
//                submittedFirstName, submittedLastName, submittedDob, submittedBvn, submittedNin);
//        log.info("API Response - First Name: {}, Surname: {}, DOB: {}, BVN: {}, NIN: {}",
//                apiFirstName, apiSurname, apiDob, apiBvn, apiNin);
//        log.info("Female Married/Widowed: {}", isFemaleMarriedOrWidowed);
//
//        // Perform validation
//        boolean firstNameMatch = submittedFirstName.equals(apiFirstName);
//        boolean lastNameMatch = submittedLastName.equals(apiSurname);
//        boolean dobMatch = submittedDob.equals(apiDob);
//        boolean bvnMatch = submittedBvn.equals(apiBvn);
//        boolean ninMatch = submittedNin.equals(apiNin);
//
//        log.info("Match Results - FirstName: {}, LastName: {}, DOB: {}, BVN: {}, NIN: {}",
//                firstNameMatch, lastNameMatch, dobMatch, bvnMatch, ninMatch);
//
//        if (isFemaleMarriedOrWidowed) {
//            // For female married/widowed: check firstName, DOB, BVN, NIN only
//            return firstNameMatch && dobMatch && bvnMatch && ninMatch;
//        } else {
//            // For others: check firstName, lastName, DOB, BVN, NIN
//            return firstNameMatch && lastNameMatch && dobMatch && bvnMatch && ninMatch;
//        }
//    }
//
//    private String normalizeString(String str) {
//        if (str == null) return "";
//        // Convert to uppercase and trim
//        return str.trim().toUpperCase();
//    }
//
//    private String formatDateForComparison(String dateStr) {
//        // Normalize dates to YYYY-MM-DD format for comparison
//        try {
//            SimpleDateFormat[] possibleFormats = {
//                new SimpleDateFormat("yyyy-MM-dd"),
//                new SimpleDateFormat("dd-MM-yyyy"),
//                new SimpleDateFormat("dd/MM/yyyy"),
//                new SimpleDateFormat("MM/dd/yyyy")
//            };
//
//            Date date = null;
//            for (SimpleDateFormat format : possibleFormats) {
//                try {
//                    date = format.parse(dateStr);
//                    break;
//                } catch (ParseException ignored) {
//                }
//            }
//
//            if (date != null) {
//                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
//                return outputFormat.format(date);
//            }
//        } catch (Exception e) {
//            log.error("Error formatting date: {}", dateStr, e);
//        }
//        return dateStr;
//    }
//}







//package com.koolboks.creditProject.service;
//
//
//
//import com.koolboks.creditProject.dto.AgentEntryRequest;
//import com.koolboks.creditProject.dto.MonoApiResponse;
//import com.koolboks.creditProject.entity.AgentEntry;
//import com.koolboks.creditProject.repository.AgentEntryRepository;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
//@Service
//public class VerificationService {
//
//    private static final Logger log = LoggerFactory.getLogger(VerificationService.class);
//
//    private final RestTemplate restTemplate;
//    private final EmailService emailService;
//    private final FileStorageService fileStorageService;
//    private final AgentEntryRepository agentEntryRepository;
//
//
//    @Value("${mono.api.url:https://api.withmono.com/v3/lookup/mashup}")
//    private String monoApiUrl;
//
//    @Value("${mono.api.key:test_pk_psee490olw7y4688czk2}")
//    private String monoApiKey;
//
//    public VerificationService(RestTemplate restTemplate,EmailService emailService,
//                                FileStorageService fileStorageService,
//                                AgentEntryRepository agentEntryRepository) {
//        this.restTemplate = restTemplate;
//        this.emailService = emailService;
//        this.fileStorageService = fileStorageService;
//        this.agentEntryRepository = agentEntryRepository;
//    }
//
//    public boolean verifyAndNotify(AgentEntryRequest request) {
//        AgentEntry agentEntry = null;
//
//        try {
//            // Store passport photo
//            String passportFileName = fileStorageService.storeFile(request.getPassport(), request.getBvn());
//
//            // Create and save initial entry
//            agentEntry = createAgentEntry(request, passportFileName);
//            agentEntry = agentEntryRepository.save(agentEntry);
//
//            // Call Mono API
//            MonoApiResponse monoResponse = callMonoApi(request.getBvn(), request.getNin(), request.getDateOfBirth());
//
//            if (monoResponse == null || !"successful".equalsIgnoreCase(monoResponse.getStatus())) {
//                log.error("Mono API call failed or returned unsuccessful status");
//                updateVerificationStatus(agentEntry, false, "API call failed");
//                emailService.sendVerificationEmail(request, false, "API call failed");
//                return false;
//            }
//
//            // Perform verification based on gender and marital status
//            boolean isVerified = performVerification(request, monoResponse);
//
//            // Update verification status
//            String message = isVerified ? "All documents match" : "Documents do not match";
//            updateVerificationStatus(agentEntry, isVerified, message);
//
//            // Send email notification
//            emailService.sendVerificationEmail(request, isVerified, message);
//
//            return isVerified;
//
//        } catch (Exception e) {
//            log.error("Error during verification: ", e);
//            if (agentEntry != null) {
//                updateVerificationStatus(agentEntry, false, "Error: " + e.getMessage());
//            }
//            emailService.sendVerificationEmail(request, false, "Error: " + e.getMessage());
//            return false;
//        }
//    }
//
//    private AgentEntry createAgentEntry(AgentEntryRequest request, String passportFileName) {
//        AgentEntry entry = new AgentEntry();
//
//        // Product details
//        entry.setProductName(request.getProductName());
//        entry.setBrand(request.getBrand());
//        entry.setSize(request.getSize());
//        entry.setPrice(request.getPrice());
//
//        // Personal information
//        entry.setFirstName(request.getFirstName());
//        entry.setMiddleName(request.getMiddleName());
//        entry.setLastName(request.getLastName());
//        entry.setDateOfBirth(LocalDate.parse(request.getDateOfBirth()));
//        entry.setMaritalStatus(request.getMaritalStatus());
//        entry.setGender(request.getGender());
//
//        // Verification details
//        entry.setBvn(request.getBvn());
//        entry.setNin(request.getNin());
//        entry.setMobileNumber(request.getMobileNumber());
//        entry.setPassportPhotoPath(passportFileName);
//
//        // Payment plan
//        entry.setPlan(request.getPlan());
//        entry.setInstallmentDuration(request.getInstallmentDuration());
//        entry.setOmolopeDays(request.getOmolopeDays());
//        entry.setBankStatementMethod(request.getBankStatementMethod());
//
//        // Default verification status
//        entry.setIsVerified(false);
//
//        return entry;
//    }
//
//    private void updateVerificationStatus(AgentEntry entry, boolean isVerified, String message) {
//        entry.setIsVerified(isVerified);
//        entry.setVerificationMessage(message);
//        entry.setVerificationDate(LocalDateTime.now());
//        agentEntryRepository.save(entry);
//    }
//
//    private MonoApiResponse callMonoApi(String bvn, String nin, String dateOfBirth) {
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
//
//            if (monoApiKey != null && !monoApiKey.isEmpty()) {
//                headers.set("test_sk_h6nby3di44q76dhb0uha", monoApiKey);
//            }
//
//            Map<String, String> requestBody = new HashMap<>();
//            requestBody.put("nin", nin);
//            requestBody.put("bvn", bvn);
//            requestBody.put("date_of_birth", formatDateForApi(dateOfBirth));
//
//            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
//
//            ResponseEntity<MonoApiResponse> response = restTemplate.exchange(
//                    monoApiUrl,
//                    HttpMethod.POST,
//                    entity,
//                    MonoApiResponse.class
//            );
//
//            return response.getBody();
//
//        } catch (Exception e) {
//            log.error("Error calling Mono API: ", e);
//            return null;
//        }
//    }
//
//    private boolean performVerification(AgentEntryRequest request, MonoApiResponse monoResponse) {
//        MonoApiResponse.PersonalInformation personalInfo = monoResponse.getData().getPersonalInformation();
//        MonoApiResponse.IdentificationNumbers idNumbers = monoResponse.getData().getIdentificationNumbers();
//
//        // Normalize submitted data
//        String submittedFirstName = normalizeString(request.getFirstName());
//        String submittedLastName = normalizeString(request.getLastName());
//        String submittedBvn = request.getBvn().trim();
//        String submittedNin = request.getNin().trim();
//        String submittedDob = formatDateForComparison(request.getDateOfBirth());
//
//        // Normalize API response data
//        String apiFirstName = normalizeString(personalInfo.getFirstName());
//        String apiSurname = normalizeString(personalInfo.getSurname());
//        String apiBvn = idNumbers.getBvn().trim();
//        String apiNin = idNumbers.getNin().trim();
//        String apiDob = formatDateForComparison(personalInfo.getDob());
//
//        // Check if female and married/widowed - only check firstName, DOB, BVN, NIN
//        boolean isFemaleMarriedOrWidowed = "Female".equalsIgnoreCase(request.getGender()) &&
//                ("Married".equalsIgnoreCase(request.getMaritalStatus()) ||
//                 "Widowed".equalsIgnoreCase(request.getMaritalStatus()));
//
//        log.info("Verification Details:");
//        log.info("Submitted - First Name: {}, Last Name: {}, DOB: {}, BVN: {}, NIN: {}",
//                submittedFirstName, submittedLastName, submittedDob, submittedBvn, submittedNin);
//        log.info("API Response - First Name: {}, Surname: {}, DOB: {}, BVN: {}, NIN: {}",
//                apiFirstName, apiSurname, apiDob, apiBvn, apiNin);
//        log.info("Female Married/Widowed: {}", isFemaleMarriedOrWidowed);
//
//        // Perform validation
//        boolean firstNameMatch = submittedFirstName.equals(apiFirstName);
//        boolean lastNameMatch = submittedLastName.equals(apiSurname);
//        boolean dobMatch = submittedDob.equals(apiDob);
//        boolean bvnMatch = submittedBvn.equals(apiBvn);
//        boolean ninMatch = submittedNin.equals(apiNin);
//
//        log.info("Match Results - FirstName: {}, LastName: {}, DOB: {}, BVN: {}, NIN: {}",
//                firstNameMatch, lastNameMatch, dobMatch, bvnMatch, ninMatch);
//
//        if (isFemaleMarriedOrWidowed) {
//            // For female married/widowed: check firstName, DOB, BVN, NIN only
//            return firstNameMatch && dobMatch && bvnMatch && ninMatch;
//        } else {
//            // For others: check firstName, lastName, DOB, BVN, NIN
//            return firstNameMatch && lastNameMatch && dobMatch && bvnMatch && ninMatch;
//        }
//    }
//
//    private String normalizeString(String str) {
//        if (str == null) return "";
//        // Convert to uppercase and trim
//        return str.trim().toUpperCase();
//    }
//
//    private String formatDateForApi(String dateOfBirth) {
//        // Convert from yyyy-MM-dd to dd-MM-yyyy for API call
//        try {
//            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
//            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
//            Date date = inputFormat.parse(dateOfBirth);
//            return outputFormat.format(date);
//        } catch (ParseException e) {
//            log.error("Error parsing date: {}", dateOfBirth, e);
//            return dateOfBirth;
//        }
//    }
//
//    private String formatDateForComparison(String dateStr) {
//        // Normalize dates to dd-MM-yyyy format for comparison
//        try {
//            SimpleDateFormat[] possibleFormats = {
//                new SimpleDateFormat("dd-MM-yyyy"),
//                new SimpleDateFormat("yyyy-MM-dd"),
//                new SimpleDateFormat("dd/MM/yyyy"),
//                new SimpleDateFormat("MM/dd/yyyy")
//            };
//
//            Date date = null;
//            for (SimpleDateFormat format : possibleFormats) {
//                try {
//                    date = format.parse(dateStr);
//                    break;
//                } catch (ParseException ignored) {
//                }
//            }
//
//            if (date != null) {
//                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
//                return outputFormat.format(date);
//            }
//        } catch (Exception e) {
//            log.error("Error formatting date: {}", dateStr, e);
//        }
//        return dateStr;
//    }
//}

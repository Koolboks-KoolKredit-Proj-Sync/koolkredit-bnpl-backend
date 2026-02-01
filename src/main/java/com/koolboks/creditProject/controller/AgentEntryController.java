package com.koolboks.creditProject.controller;

import com.koolboks.creditProject.dto.AgentEntryRequest;
import com.koolboks.creditProject.dto.VerificationResult;
import com.koolboks.creditProject.service.VerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/agent-entry")
public class AgentEntryController {

    private final VerificationService verificationService;

    public AgentEntryController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Object>> submitAgentEntry(
            @ModelAttribute AgentEntryRequest request,                  // ← Spring auto-binds all text fields
            @RequestParam("passport") MultipartFile passport            // ← Handle file separately
    ) {
        // Manually set the file (since MultipartFile fields don't auto-bind reliably in some Spring versions)
        request.setPassport(passport);

        // Optional: log everything to debug
        System.out.println("Received businessType: " + request.getBusinessType());
        System.out.println("Received brand: " + request.getBrand());
        System.out.println("Received productName: " + request.getProductName());
        // ... add more if needed

        try {
            VerificationResult result = verificationService.verifyAndNotify(request);

            Map<String, Object> response = new HashMap<>();
            response.put("success", result.isSuccess());
            response.put("verified", result.isVerified());
            response.put("message", result.getMessage());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("verified", false);
            errorResponse.put("message", "Error processing request: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}













//package com.koolboks.creditProject.controller;
//
//import com.koolboks.creditProject.dto.AgentEntryRequest;
//import com.koolboks.creditProject.dto.VerificationResult;
//import com.koolboks.creditProject.service.VerificationService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/agent-entry")
////@CrossOrigin(origins = "*")
//public class AgentEntryController {
//
//    private final VerificationService verificationService;
//
//    public AgentEntryController(VerificationService verificationService) {
//        this.verificationService = verificationService;
//    }
//
//    @PostMapping(consumes = "multipart/form-data")
//    public ResponseEntity<Map<String, Object>> submitAgentEntry(
//            @RequestParam("businessType") String businessType,
//            @RequestParam("productName") String productName,
//            @RequestParam("brand") String brand,
//            @RequestParam(value = "size", required = false) String size,
//            @RequestParam(value = "price", required = false) String price,
//            @RequestParam("totalPrice") String totalPrice,
//            @RequestParam("firstName") String firstName,
//            @RequestParam(value = "middleName", required = false) String middleName,
//            @RequestParam("lastName") String lastName,
//            @RequestParam("dateOfBirth") String dateOfBirth,
//            @RequestParam("maritalStatus") String maritalStatus,
//            @RequestParam("gender") String gender,
//            @RequestParam(value = "spouseName", required = false) String spouseName,
//            @RequestParam(value = "spousePhone", required = false) String spousePhone,
//            @RequestParam(value = "nextOfKinName", required = false) String nextOfKinName,
//            @RequestParam(value = "nextOfKinPhone", required = false) String nextOfKinPhone,
//            @RequestParam(value = "nextOfKinRelationship", required = false) String nextOfKinRelationship,
//            @RequestParam("bvn") String bvn,
//            @RequestParam("nin") String nin,
//            @RequestParam("mobileNumber") String mobileNumber,
//            @RequestParam("passport") MultipartFile passport,
//            @RequestParam("plan") String plan,
//            @RequestParam(value = "installmentDuration", required = false) String installmentDuration,
//            @RequestParam(value = "omolopeDays", required = false) String omolopeDays,
//            @RequestParam(value = "bankStatementMethod", required = false) String bankStatementMethod
//    ) {
//        try {
//            AgentEntryRequest request = new AgentEntryRequest();
//            request.setBusinessType(businessType);
//            request.setProductName(productName);
//            request.setBrand(brand);
//            request.setSize(size);
//            request.setPrice(price);
//            request.setTotalPrice(totalPrice);
//            request.setFirstName(firstName);
//            request.setMiddleName(middleName);
//            request.setLastName(lastName);
//            request.setDateOfBirth(dateOfBirth);
//            request.setMaritalStatus(maritalStatus);
//            request.setGender(gender);
//            request.setSpouseName(spouseName);
//            request.setSpousePhone(spousePhone);
//            request.setNextOfKinName(nextOfKinName);
//            request.setNextOfKinPhone(nextOfKinPhone);
//            request.setNextOfKinRelationship(nextOfKinRelationship);
//            request.setBvn(bvn);
//            request.setNin(nin);
//            request.setMobileNumber(mobileNumber);
//            request.setPassport(passport);
//            request.setPlan(plan);
//            request.setInstallmentDuration(installmentDuration);
//            request.setOmolopeDays(omolopeDays);
//            request.setBankStatementMethod(bankStatementMethod);
//
//            VerificationResult result = verificationService.verifyAndNotify(request);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", result.isSuccess());
//            response.put("verified", result.isVerified());
//            response.put("message", result.getMessage());
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("verified", false);
//            errorResponse.put("message", "Error processing request: " + e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//}
//
//












//package com.koolboks.creditProject.controller;
//
//import com.koolboks.creditProject.dto.AgentEntryRequest;
//import com.koolboks.creditProject.dto.VerificationResult;
//import com.koolboks.creditProject.service.VerificationService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/agent-entry")
////@CrossOrigin(origins = "*")
//public class AgentEntryController {
//
//    private final VerificationService verificationService;
//
//    public AgentEntryController(VerificationService verificationService) {
//        this.verificationService = verificationService;
//    }
//
//    @PostMapping(consumes = "multipart/form-data")
//    public ResponseEntity<Map<String, Object>> submitAgentEntry(
//            @RequestParam("productName") String productName,
//            @RequestParam("brand") String brand,
//            @RequestParam("size") String size,
//            @RequestParam("price") String price,
//            @RequestParam("firstName") String firstName,
//            @RequestParam(value = "middleName", required = false) String middleName,
//            @RequestParam("lastName") String lastName,
//            @RequestParam("dateOfBirth") String dateOfBirth,
//            @RequestParam("maritalStatus") String maritalStatus,
//            @RequestParam("gender") String gender,
//            @RequestParam("bvn") String bvn,
//            @RequestParam("nin") String nin,
//            @RequestParam("mobileNumber") String mobileNumber,
//            @RequestParam("passport") MultipartFile passport,
//            @RequestParam("plan") String plan,
//            @RequestParam(value = "installmentDuration", required = false) String installmentDuration,
//            @RequestParam(value = "omolopeDays", required = false) String omolopeDays,
//            @RequestParam(value = "bankStatementMethod", required = false) String bankStatementMethod
//    ) {
//        try {
//            AgentEntryRequest request = new AgentEntryRequest();
//            request.setProductName(productName);
//            request.setBrand(brand);
//            request.setSize(size);
//            request.setPrice(price);
//            request.setFirstName(firstName);
//            request.setMiddleName(middleName);
//            request.setLastName(lastName);
//            request.setDateOfBirth(dateOfBirth);
//            request.setMaritalStatus(maritalStatus);
//            request.setGender(gender);
//            request.setBvn(bvn);
//            request.setNin(nin);
//            request.setMobileNumber(mobileNumber);
//            request.setPassport(passport);
//            request.setPlan(plan);
//            request.setInstallmentDuration(installmentDuration);
//            request.setOmolopeDays(omolopeDays);
//            request.setBankStatementMethod(bankStatementMethod);
//
//            VerificationResult result = verificationService.verifyAndNotify(request);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", result.isSuccess());
//            response.put("verified", result.isVerified());
//            response.put("message", result.getMessage());
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("verified", false);
//            errorResponse.put("message", "Error processing request: " + e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//}







//package com.koolboks.creditProject.controller;
//
//
//import com.koolboks.creditProject.dto.AgentEntryRequest;
//import com.koolboks.creditProject.service.VerificationService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/agent-entry")
////@CrossOrigin(origins = "*")
//public class AgentEntryController {
//
//    private final VerificationService verificationService;
//
//    public AgentEntryController(VerificationService verificationService) {
//        this.verificationService = verificationService;
//    }
//
//    @PostMapping(consumes = "multipart/form-data")
//    public ResponseEntity<Map<String, Object>> submitAgentEntry(
//            @RequestParam("productName") String productName,
//            @RequestParam("brand") String brand,
//            @RequestParam("size") String size,
//            @RequestParam("price") String price,
//            @RequestParam("firstName") String firstName,
//            @RequestParam(value = "middleName", required = false) String middleName,
//            @RequestParam("lastName") String lastName,
//            @RequestParam("dateOfBirth") String dateOfBirth,
//            @RequestParam("maritalStatus") String maritalStatus,
//            @RequestParam("gender") String gender,
//            @RequestParam("bvn") String bvn,
//            @RequestParam("nin") String nin,
//            @RequestParam("mobileNumber") String mobileNumber,
//            @RequestParam("passport") MultipartFile passport,
//            @RequestParam("plan") String plan,
//            @RequestParam(value = "installmentDuration", required = false) String installmentDuration,
//            @RequestParam(value = "omolopeDays", required = false) String omolopeDays,
//            @RequestParam(value = "bankStatementMethod", required = false) String bankStatementMethod
//    ) {
//        try {
//            AgentEntryRequest request = new AgentEntryRequest();
//            request.setProductName(productName);
//            request.setBrand(brand);
//            request.setSize(size);
//            request.setPrice(price);
//            request.setFirstName(firstName);
//            request.setMiddleName(middleName);
//            request.setLastName(lastName);
//            request.setDateOfBirth(dateOfBirth);
//            request.setMaritalStatus(maritalStatus);
//            request.setGender(gender);
//            request.setBvn(bvn);
//            request.setNin(nin);
//            request.setMobileNumber(mobileNumber);
//            request.setPassport(passport);
//            request.setPlan(plan);
//            request.setInstallmentDuration(installmentDuration);
//            request.setOmolopeDays(omolopeDays);
//            request.setBankStatementMethod(bankStatementMethod);
//
//            boolean isVerified = verificationService.verifyAndNotify(request);
//
//            Map<String, Object> response = new HashMap<>();
//            response.put("success", true);
//            response.put("verified", isVerified);
//            response.put("message", isVerified
//                ? "Documents authentic and verified successfully"
//                : "Documents do not match");
//
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", "Error processing request: " + e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
//}
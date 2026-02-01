package com.koolboks.creditProject.dto.agent_proof;



import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;



import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

public class AgentProofSubmissionRequest {

    // Store Information (from form)
    private String storeName;
    private String storeLocation;
    private LocalDate confirmationDate;
    private MultipartFile receiptImage;

    // Agent Information (from React frontend)
    private String agentName;
    private String agentEmail;
    private String agentId;
    private String agentMobile;

    public AgentProofSubmissionRequest() {}

    // Store Information Getters/Setters
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public LocalDate getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(LocalDate confirmationDate) {
        this.confirmationDate = confirmationDate;
    }

    public MultipartFile getReceiptImage() {
        return receiptImage;
    }

    public void setReceiptImage(MultipartFile receiptImage) {
        this.receiptImage = receiptImage;
    }

    // Agent Information Getters/Setters
    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentMobile() {
        return agentMobile;
    }

    public void setAgentMobile(String agentMobile) {
        this.agentMobile = agentMobile;
    }
}
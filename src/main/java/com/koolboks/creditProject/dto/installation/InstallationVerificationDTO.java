package com.koolboks.creditProject.dto.installation;



public class InstallationVerificationDTO {
    private String verificationType; // "bvn", "email", or "phone"
    private String verificationValue;
    private String orderId;

    public InstallationVerificationDTO() {
    }

    public InstallationVerificationDTO(String verificationType, String verificationValue, String orderId) {
        this.verificationType = verificationType;
        this.verificationValue = verificationValue;
        this.orderId = orderId;
    }

    public String getVerificationType() {
        return verificationType;
    }

    public void setVerificationType(String verificationType) {
        this.verificationType = verificationType;
    }

    public String getVerificationValue() {
        return verificationValue;
    }

    public void setVerificationValue(String verificationValue) {
        this.verificationValue = verificationValue;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

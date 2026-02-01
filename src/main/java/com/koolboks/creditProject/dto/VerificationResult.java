package com.koolboks.creditProject.dto;

public class VerificationResult {
    private boolean success;
    private boolean verified;
    private String message;

    public VerificationResult(boolean success, boolean verified, String message) {
        this.success = success;
        this.verified = verified;
        this.message = message;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
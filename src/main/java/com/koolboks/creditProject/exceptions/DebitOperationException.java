package com.koolboks.creditProject.exceptions;



public class DebitOperationException extends RuntimeException {

    private final String mandateId;
    private final String responseCode;

    public DebitOperationException(String message, String mandateId) {
        super(message);
        this.mandateId = mandateId;
        this.responseCode = null;
    }

    public DebitOperationException(String message, String mandateId, String responseCode) {
        super(message);
        this.mandateId = mandateId;
        this.responseCode = responseCode;
    }

    public DebitOperationException(String message, String mandateId, Throwable cause) {
        super(message, cause);
        this.mandateId = mandateId;
        this.responseCode = null;
    }

    public String getMandateId() {
        return mandateId;
    }

    public String getResponseCode() {
        return responseCode;
    }
}
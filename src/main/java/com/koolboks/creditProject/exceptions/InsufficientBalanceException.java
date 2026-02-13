package com.koolboks.creditProject.exceptions;



public class InsufficientBalanceException extends RuntimeException {

    private final String mandateId;
    private final int requestedAmount;

    public InsufficientBalanceException(String mandateId, int requestedAmount) {
        super(String.format("Insufficient balance for mandate %s. Requested amount: %d", mandateId, requestedAmount));
        this.mandateId = mandateId;
        this.requestedAmount = requestedAmount;
    }

    public InsufficientBalanceException(String message, String mandateId, int requestedAmount) {
        super(message);
        this.mandateId = mandateId;
        this.requestedAmount = requestedAmount;
    }

    public String getMandateId() {
        return mandateId;
    }

    public int getRequestedAmount() {
        return requestedAmount;
    }
}
package com.koolboks.creditProject.dto.mono;



import com.fasterxml.jackson.annotation.JsonProperty;

public class MonoCreateMandateRequest {

    @JsonProperty("customer")
    private String customer;

    @JsonProperty("mandate_type")
    private String mandateType;

    @JsonProperty("debit_type")
    private String debitType;

    @JsonProperty("amount")
    private int amount;

    @JsonProperty("reference")
    private String reference;

    @JsonProperty("fee_bearer")
    private String feeBearer;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("bank_code")
    private String bankCode;

    @JsonProperty("description")
    private String description;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

//    @JsonProperty("verification_method")
//    private String verificationMethod;

    // Constructors
    public MonoCreateMandateRequest() {
    }

    // Getters and Setters
    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getMandateType() {
        return mandateType;
    }

    public void setMandateType(String mandateType) {
        this.mandateType = mandateType;
    }

    public String getDebitType() {
        return debitType;
    }

    public void setDebitType(String debitType) {
        this.debitType = debitType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getFeeBearer() {
        return feeBearer;
    }

    public void setFeeBearer(String feeBearer) {
        this.feeBearer = feeBearer;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

//    public String getVerificationMethod() {
//        return verificationMethod;
//    }
//
//    public void setVerificationMethod(String verificationMethod) {
//        this.verificationMethod = verificationMethod;
//    }
}
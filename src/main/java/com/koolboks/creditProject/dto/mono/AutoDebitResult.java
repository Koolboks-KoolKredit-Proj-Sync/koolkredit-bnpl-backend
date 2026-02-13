package com.koolboks.creditProject.dto.mono;



import com.koolboks.creditProject.dto.mono.DebitResponse;
import com.koolboks.creditProject.dto.mono.BalanceInquiryResponse;

public class AutoDebitResult {

    private boolean success;
    private String message;
    private String mandateId;
    private int debitedAmount;
    private String referenceNumber;
    private String sessionId;
    private int fee;
    private String feeBearer;
    private BalanceInquiryResponse.AccountDetails accountDetails;
    private DebitResponse.DebitData fullDebitData;

    public AutoDebitResult() {
    }

    public AutoDebitResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Builder pattern for easier construction
    public static Builder builder() {
        return new Builder();
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMandateId() {
        return mandateId;
    }

    public void setMandateId(String mandateId) {
        this.mandateId = mandateId;
    }

    public int getDebitedAmount() {
        return debitedAmount;
    }

    public void setDebitedAmount(int debitedAmount) {
        this.debitedAmount = debitedAmount;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getFeeBearer() {
        return feeBearer;
    }

    public void setFeeBearer(String feeBearer) {
        this.feeBearer = feeBearer;
    }

    public BalanceInquiryResponse.AccountDetails getAccountDetails() {
        return accountDetails;
    }

    public void setAccountDetails(BalanceInquiryResponse.AccountDetails accountDetails) {
        this.accountDetails = accountDetails;
    }

    public DebitResponse.DebitData getFullDebitData() {
        return fullDebitData;
    }

    public void setFullDebitData(DebitResponse.DebitData fullDebitData) {
        this.fullDebitData = fullDebitData;
    }

    public static class Builder {
        private final AutoDebitResult result;

        public Builder() {
            this.result = new AutoDebitResult();
        }

        public Builder success(boolean success) {
            result.setSuccess(success);
            return this;
        }

        public Builder message(String message) {
            result.setMessage(message);
            return this;
        }

        public Builder mandateId(String mandateId) {
            result.setMandateId(mandateId);
            return this;
        }

        public Builder debitedAmount(int debitedAmount) {
            result.setDebitedAmount(debitedAmount);
            return this;
        }

        public Builder referenceNumber(String referenceNumber) {
            result.setReferenceNumber(referenceNumber);
            return this;
        }

        public Builder sessionId(String sessionId) {
            result.setSessionId(sessionId);
            return this;
        }

        public Builder fee(int fee) {
            result.setFee(fee);
            return this;
        }

        public Builder feeBearer(String feeBearer) {
            result.setFeeBearer(feeBearer);
            return this;
        }

        public Builder accountDetails(BalanceInquiryResponse.AccountDetails accountDetails) {
            result.setAccountDetails(accountDetails);
            return this;
        }

        public Builder fullDebitData(DebitResponse.DebitData fullDebitData) {
            result.setFullDebitData(fullDebitData);
            return this;
        }

        public AutoDebitResult build() {
            return result;
        }
    }

    @Override
    public String toString() {
        return "AutoDebitResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", mandateId='" + mandateId + '\'' +
                ", debitedAmount=" + debitedAmount +
                ", referenceNumber='" + referenceNumber + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", fee=" + fee +
                ", feeBearer='" + feeBearer + '\'' +
                '}';
    }
}

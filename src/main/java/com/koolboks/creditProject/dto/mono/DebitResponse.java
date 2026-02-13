package com.koolboks.creditProject.dto.mono;



import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import java.util.Map;

public class DebitResponse {

    private String status;
    private String message;

    @JsonProperty("response_code")
    private String responseCode;

    private DebitData data;

    public DebitResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public DebitData getData() {
        return data;
    }

    public void setData(DebitData data) {
        this.data = data;
    }

    public static class DebitData {
        private boolean success;
        private String status;
        private String event;
        private int amount;
        private String mandate;

        @JsonProperty("reference_number")
        private String referenceNumber;

        private ZonedDateTime date;

        @JsonProperty("live_mode")
        private boolean liveMode;

        private int fee;

        @JsonProperty("fee_bearer")
        private String feeBearer;

        private String narration;

        @JsonProperty("session_id")
        private String sessionId;

        @JsonProperty("account_details")
        private AccountDetails accountDetails;

        private Beneficiary beneficiary;

        private Map<String, Object> meta;

        public DebitData() {
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getMandate() {
            return mandate;
        }

        public void setMandate(String mandate) {
            this.mandate = mandate;
        }

        public String getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        public ZonedDateTime getDate() {
            return date;
        }

        public void setDate(ZonedDateTime date) {
            this.date = date;
        }

        public boolean isLiveMode() {
            return liveMode;
        }

        public void setLiveMode(boolean liveMode) {
            this.liveMode = liveMode;
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

        public String getNarration() {
            return narration;
        }

        public void setNarration(String narration) {
            this.narration = narration;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public AccountDetails getAccountDetails() {
            return accountDetails;
        }

        public void setAccountDetails(AccountDetails accountDetails) {
            this.accountDetails = accountDetails;
        }

        public Beneficiary getBeneficiary() {
            return beneficiary;
        }

        public void setBeneficiary(Beneficiary beneficiary) {
            this.beneficiary = beneficiary;
        }

        public Map<String, Object> getMeta() {
            return meta;
        }

        public void setMeta(Map<String, Object> meta) {
            this.meta = meta;
        }
    }

    public static class AccountDetails {
        @JsonProperty("bank_code")
        private String bankCode;

        @JsonProperty("account_name")
        private String accountName;

        @JsonProperty("account_number")
        private String accountNumber;

        @JsonProperty("bank_name")
        private String bankName;

        public AccountDetails() {
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }
    }

    public static class Beneficiary {
        @JsonProperty("bank_code")
        private String bankCode;

        @JsonProperty("account_name")
        private String accountName;

        @JsonProperty("account_number")
        private String accountNumber;

        @JsonProperty("bank_name")
        private String bankName;

        public Beneficiary() {
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }
    }
}

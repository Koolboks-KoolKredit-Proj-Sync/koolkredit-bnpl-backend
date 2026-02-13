package com.koolboks.creditProject.dto.mono;



import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class MonoCreateMandateResponse {
    private String status;
    private String message;
    private Data data;

    public static class Data {
        private Map<String, Object> meta;

        private String id;

        private String status;

        @JsonProperty("mandate_type")
        private String mandateType;

        @JsonProperty("debit_type")
        private String debitType;

        @JsonProperty("ready_to_debit")
        private boolean readyToDebit;

        @JsonProperty("nibss_code")
        private String nibssCode;

        private boolean approved;

        private String reference;

        @JsonProperty("account_name")
        private String accountName;

        @JsonProperty("account_number")
        private String accountNumber;

        private String bank;

        @JsonProperty("bank_code")
        private String bankCode;

        private String customer;

        @JsonProperty("fee_bearer")
        private String feeBearer;

        @JsonProperty("verification_method")
        private String verificationMethod;

        private String description;

        @JsonProperty("live_mode")
        private boolean liveMode;

        @JsonProperty("start_date")
        private String startDate;

        @JsonProperty("end_date")
        private String endDate;

        private String date;

        @JsonProperty("initial_debit_date")
        private String initialDebitDate;

        @JsonProperty("transfer_destinations")
        private List<TransferDestination> transferDestinations;

        private long amount;

        @JsonProperty("initial_debit_amount")
        private Long initialDebitAmount;

        // Getters and Setters
        public Map<String, Object> getMeta() {
            return meta;
        }

        public void setMeta(Map<String, Object> meta) {
            this.meta = meta;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
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

        public boolean isReadyToDebit() {
            return readyToDebit;
        }

        public void setReadyToDebit(boolean readyToDebit) {
            this.readyToDebit = readyToDebit;
        }

        public String getNibssCode() {
            return nibssCode;
        }

        public void setNibssCode(String nibssCode) {
            this.nibssCode = nibssCode;
        }

        public boolean isApproved() {
            return approved;
        }

        public void setApproved(boolean approved) {
            this.approved = approved;
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
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

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getCustomer() {
            return customer;
        }

        public void setCustomer(String customer) {
            this.customer = customer;
        }

        public String getFeeBearer() {
            return feeBearer;
        }

        public void setFeeBearer(String feeBearer) {
            this.feeBearer = feeBearer;
        }

        public String getVerificationMethod() {
            return verificationMethod;
        }

        public void setVerificationMethod(String verificationMethod) {
            this.verificationMethod = verificationMethod;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isLiveMode() {
            return liveMode;
        }

        public void setLiveMode(boolean liveMode) {
            this.liveMode = liveMode;
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

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getInitialDebitDate() {
            return initialDebitDate;
        }

        public void setInitialDebitDate(String initialDebitDate) {
            this.initialDebitDate = initialDebitDate;
        }

        public List<TransferDestination> getTransferDestinations() {
            return transferDestinations;
        }

        public void setTransferDestinations(List<TransferDestination> transferDestinations) {
            this.transferDestinations = transferDestinations;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public Long getInitialDebitAmount() {
            return initialDebitAmount;
        }

        public void setInitialDebitAmount(Long initialDebitAmount) {
            this.initialDebitAmount = initialDebitAmount;
        }

        // Convenience method to get mandate_id (using 'id' field)
        public String getMandateId() {
            return this.id;
        }
    }

    public static class TransferDestination {
        @JsonProperty("bank_name")
        private String bankName;

        @JsonProperty("account_number")
        private long accountNumber;

        private String icon;

        @JsonProperty("primary_color")
        private String primaryColor;

        // Getters and Setters
        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public long getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(long accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getPrimaryColor() {
            return primaryColor;
        }

        public void setPrimaryColor(String primaryColor) {
            this.primaryColor = primaryColor;
        }
    }

    // Getters and Setters
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
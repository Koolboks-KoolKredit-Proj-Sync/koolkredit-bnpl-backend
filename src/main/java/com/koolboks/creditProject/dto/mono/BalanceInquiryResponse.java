package com.koolboks.creditProject.dto.mono;


import com.fasterxml.jackson.annotation.JsonProperty;

public class BalanceInquiryResponse {

    private String status;
    private String message;
    private BalanceData data;

    public BalanceInquiryResponse() {
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

    public BalanceData getData() {
        return data;
    }

    public void setData(BalanceData data) {
        this.data = data;
    }

    public static class BalanceData {
        private String id;

        @JsonProperty("has_sufficient_balance")
        private boolean hasSufficientBalance;

        @JsonProperty("account_details")
        private AccountDetails accountDetails;

        public BalanceData() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public boolean isHasSufficientBalance() {
            return hasSufficientBalance;
        }

        public void setHasSufficientBalance(boolean hasSufficientBalance) {
            this.hasSufficientBalance = hasSufficientBalance;
        }

        public AccountDetails getAccountDetails() {
            return accountDetails;
        }

        public void setAccountDetails(AccountDetails accountDetails) {
            this.accountDetails = accountDetails;
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
}

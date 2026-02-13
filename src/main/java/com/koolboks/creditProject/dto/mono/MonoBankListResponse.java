//package com.koolboks.creditProject.dto.mono;
//
//
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import java.util.List;
//
//public class MonoBankListResponse {
//    private String status;
//    private String message;
//    private Data data;
//
//    public static class Data {
//        private List<Bank> banks;
//
//        public List<Bank> getBanks() {
//            return banks;
//        }
//
//        public void setBanks(List<Bank> banks) {
//            this.banks = banks;
//        }
//    }
//
//    public static class Bank {
//        private String name;
//
//        @JsonProperty("bank_code")
//        private String bankCode;
//
//        @JsonProperty("nip_code")
//        private String nipCode;
//
//        @JsonProperty("direct_debit")
//        private boolean directDebit;
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getBankCode() {
//            return bankCode;
//        }
//
//        public void setBankCode(String bankCode) {
//            this.bankCode = bankCode;
//        }
//
//        public String getNipCode() {
//            return nipCode;
//        }
//
//        public void setNipCode(String nipCode) {
//            this.nipCode = nipCode;
//        }
//
//        public boolean isDirectDebit() {
//            return directDebit;
//        }
//
//        public void setDirectDebit(boolean directDebit) {
//            this.directDebit = directDebit;
//        }
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    public Data getData() {
//        return data;
//    }
//
//    public void setData(Data data) {
//        this.data = data;
//    }
//}



package com.koolboks.creditProject.dto.mono;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MonoBankListResponse {
    private String status;
    private String message;

    // The actual response is just an array of banks directly under "data"
    private List<Bank> data;

    public static class Bank {
        private String name;

        @JsonProperty("bank_code")
        private String bankCode;

        @JsonProperty("nip_code")
        private String nipCode;

        @JsonProperty("direct_debit")
        private boolean directDebit;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getNipCode() {
            return nipCode;
        }

        public void setNipCode(String nipCode) {
            this.nipCode = nipCode;
        }

        public boolean isDirectDebit() {
            return directDebit;
        }

        public void setDirectDebit(boolean directDebit) {
            this.directDebit = directDebit;
        }
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

    public List<Bank> getData() {
        return data;
    }

    public void setData(List<Bank> data) {
        this.data = data;
    }
}
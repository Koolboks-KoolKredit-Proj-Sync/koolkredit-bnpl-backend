package com.koolboks.creditProject.dto.accountVerification;
//
//
//
//public class AccountVerificationDTO {
//
//    private String accountNumber;
//    private String bankName;
//    private String nipCode;
//    private String customerName;
//
//    // Constructors
//    public AccountVerificationDTO() {
//    }
//
//    public AccountVerificationDTO(String accountNumber, String bankName, String nipCode, String customerName) {
//        this.accountNumber = accountNumber;
//        this.bankName = bankName;
//        this.nipCode = nipCode;
//        this.customerName = customerName;
//    }
//
//    // Getters and Setters
//    public String getAccountNumber() {
//        return accountNumber;
//    }
//
//    public void setAccountNumber(String accountNumber) {
//        this.accountNumber = accountNumber;
//    }
//
//    public String getBankName() {
//        return bankName;
//    }
//
//    public void setBankName(String bankName) {
//        this.bankName = bankName;
//    }
//
//    public String getNipCode() {
//        return nipCode;
//    }
//
//    public void setNipCode(String nipCode) {
//        this.nipCode = nipCode;
//    }
//
//    public String getCustomerName() {
//        return customerName;
//    }
//
//    public void setCustomerName(String customerName) {
//        this.customerName = customerName;
//    }
//}





public class AccountVerificationDTO {

    private String accountNumber;
    private String bankName;
    private String nipCode;
    private String customerName;
    private String bvn;
    private String customerAddress;
    private String customerEmail;
    private String customerPhone;



    // Name fields for Mono API
    private String firstName;

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    private String lastName;

    // Constructors
    public AccountVerificationDTO() {
    }

    public AccountVerificationDTO(String accountNumber, String bankName, String nipCode, String customerName) {
        this.accountNumber = accountNumber;
        this.bankName = bankName;
        this.nipCode = nipCode;
        this.customerName = customerName;
    }

    // Getters and Setters
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

    public String getNipCode() {
        return nipCode;
    }

    public void setNipCode(String nipCode) {
        this.nipCode = nipCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
}
//package com.koolboks.creditProject.dto;
//
//
//
//import org.springframework.web.multipart.MultipartFile;
//
//public class AgentEntryRequest {
//    private String productName;
//    private String brand;
//    private String size;
//    private String price;
//    private String firstName;
//    private String middleName;
//    private String lastName;
//    private String dateOfBirth;
//    private String maritalStatus;
//    private String gender;
//    private String bvn;
//    private String nin;
//    private String mobileNumber;
//    private MultipartFile passport;
//    private String plan;
//    private String installmentDuration;
//    private String omolopeDays;
//    private String bankStatementMethod;
//
//    public AgentEntryRequest() {
//    }
//
//    // Getters
//    public String getProductName() {
//        return productName;
//    }
//
//    public String getBrand() {
//        return brand;
//    }
//
//    public String getSize() {
//        return size;
//    }
//
//    public String getPrice() {
//        return price;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public String getMiddleName() {
//        return middleName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public String getDateOfBirth() {
//        return dateOfBirth;
//    }
//
//    public String getMaritalStatus() {
//        return maritalStatus;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public String getBvn() {
//        return bvn;
//    }
//
//    public String getNin() {
//        return nin;
//    }
//
//    public String getMobileNumber() {
//        return mobileNumber;
//    }
//
//    public MultipartFile getPassport() {
//        return passport;
//    }
//
//    public String getPlan() {
//        return plan;
//    }
//
//    public String getInstallmentDuration() {
//        return installmentDuration;
//    }
//
//    public String getOmolopeDays() {
//        return omolopeDays;
//    }
//
//    public String getBankStatementMethod() {
//        return bankStatementMethod;
//    }
//
//    // Setters
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public void setBrand(String brand) {
//        this.brand = brand;
//    }
//
//    public void setSize(String size) {
//        this.size = size;
//    }
//
//    public void setPrice(String price) {
//        this.price = price;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public void setMiddleName(String middleName) {
//        this.middleName = middleName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public void setDateOfBirth(String dateOfBirth) {
//        this.dateOfBirth = dateOfBirth;
//    }
//
//    public void setMaritalStatus(String maritalStatus) {
//        this.maritalStatus = maritalStatus;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public void setBvn(String bvn) {
//        this.bvn = bvn;
//    }
//
//    public void setNin(String nin) {
//        this.nin = nin;
//    }
//
//    public void setMobileNumber(String mobileNumber) {
//        this.mobileNumber = mobileNumber;
//    }
//
//    public void setPassport(MultipartFile passport) {
//        this.passport = passport;
//    }
//
//    public void setPlan(String plan) {
//        this.plan = plan;
//    }
//
//    public void setInstallmentDuration(String installmentDuration) {
//        this.installmentDuration = installmentDuration;
//    }
//
//    public void setOmolopeDays(String omolopeDays) {
//        this.omolopeDays = omolopeDays;
//    }
//
//    public void setBankStatementMethod(String bankStatementMethod) {
//        this.bankStatementMethod = bankStatementMethod;
//    }
//}





















package com.koolboks.creditProject.dto;

import org.springframework.web.multipart.MultipartFile;

public class AgentEntryRequest {
    private String businessType;
    private String productName;  // This will be the concatenated product names
    private String brand;
    private String size;
    private String price;
    private String totalPrice;
    private String firstName;
    private String middleName;
    private String lastName;
    private String dateOfBirth;
    private String maritalStatus;
    private String gender;
    private String spouseName;
    private String spousePhone;
    private String nextOfKinName;
    private String nextOfKinPhone;
    private String nextOfKinRelationship;
    private String bvn;
    private String nin;
    private String mobileNumber;
    private MultipartFile passport;
    private String plan;
    private String installmentDuration;
    private String omolopeDays;
    private String bankStatementMethod;

    public AgentEntryRequest() {
    }

    // Getters
    public String getBusinessType() {
        return businessType;
    }

    public String getProductName() {
        return productName;
    }

    public String getBrand() {
        return brand;
    }

    public String getSize() {
        return size;
    }

    public String getPrice() {
        return price;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public String getGender() {
        return gender;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public String getSpousePhone() {
        return spousePhone;
    }

    public String getNextOfKinName() {
        return nextOfKinName;
    }

    public String getNextOfKinPhone() {
        return nextOfKinPhone;
    }

    public String getNextOfKinRelationship() {
        return nextOfKinRelationship;
    }

    public String getBvn() {
        return bvn;
    }

    public String getNin() {
        return nin;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public MultipartFile getPassport() {
        return passport;
    }

    public String getPlan() {
        return plan;
    }

    public String getInstallmentDuration() {
        return installmentDuration;
    }

    public String getOmolopeDays() {
        return omolopeDays;
    }

    public String getBankStatementMethod() {
        return bankStatementMethod;
    }

    // Setters
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public void setSpousePhone(String spousePhone) {
        this.spousePhone = spousePhone;
    }

    public void setNextOfKinName(String nextOfKinName) {
        this.nextOfKinName = nextOfKinName;
    }

    public void setNextOfKinPhone(String nextOfKinPhone) {
        this.nextOfKinPhone = nextOfKinPhone;
    }

    public void setNextOfKinRelationship(String nextOfKinRelationship) {
        this.nextOfKinRelationship = nextOfKinRelationship;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setPassport(MultipartFile passport) {
        this.passport = passport;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public void setInstallmentDuration(String installmentDuration) {
        this.installmentDuration = installmentDuration;
    }

    public void setOmolopeDays(String omolopeDays) {
        this.omolopeDays = omolopeDays;
    }

    public void setBankStatementMethod(String bankStatementMethod) {
        this.bankStatementMethod = bankStatementMethod;
    }
}
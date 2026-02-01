package com.koolboks.creditProject.dto;

import jakarta.persistence.Column;
import org.springframework.web.multipart.MultipartFile;

public class AgentFollowUpRequest {

    private String bvn;
    private String nin;
    private String mobileNumber;

    // usageType is your real field
    private String usageType;   // "personal" or "commercial"

    // Addresses
    private String homeAddress;
    private String workAddress;
    private String storeAddress;

    //Emails
    private String customerEmail;
    private String guarantorEmail;

    // Income fields
    private Double monthlyIncome;  // for personal
    private Double monthlySales;   // for commercial

    private String plan;
    private String installmentOption;

    // File upload
    private MultipartFile utilityBill;  // IMPORTANT

    public AgentFollowUpRequest() {}

    // -------------------------------------------------------------------------
    // STANDARD GETTERS/SETTERS (your original ones)
    // -------------------------------------------------------------------------

    public String getBvn() { return bvn; }
    public void setBvn(String bvn) { this.bvn = bvn; }

    public String getNin() { return nin; }
    public void setNin(String nin) { this.nin = nin; }

    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public String getUsageType() { return usageType; }
    public void setUsageType(String usageType) { this.usageType = usageType; }

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    public String getWorkAddress() { return workAddress; }
    public void setWorkAddress(String workAddress) { this.workAddress = workAddress; }

    public String getStoreAddress() { return storeAddress; }
    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }

    public Double getMonthlyIncome() { return monthlyIncome; }
    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }

    public Double getMonthlySales() { return monthlySales; }
    public void setMonthlySales(Double monthlySales) { this.monthlySales = monthlySales; }

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }

    public String getInstallmentOption() { return installmentOption; }
    public void setInstallmentOption(String installmentOption) { this.installmentOption = installmentOption; }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;


    }

    public String getGuarantorEmail() {
        return guarantorEmail;
    }

    public void setGuarantorEmail(String guarantorEmail) {
        this.guarantorEmail = guarantorEmail;
    }



    public MultipartFile getUtilityBill() { return utilityBill; }
    public void setUtilityBill(MultipartFile utilityBill) { this.utilityBill = utilityBill; }

//    public MultipartFile getUtilityBill() { return utilityBill; }
//    public void setUtilityBill(MultipartFile utilityBill) { this.utilityBill = utilityBill; }

    // -------------------------------------------------------------------------
    // COMPATIBILITY METHODS (so your existing service compiles)
    // -------------------------------------------------------------------------

    public String getUsage() {
        return this.usageType;
    }

    public void setUsage(String usage) {
        this.usageType = usage;
    }

    public Double getMonthlyGrossIncome() {
        return this.monthlyIncome;
    }

    public void setMonthlyGrossIncome(Double income) {
        this.monthlyIncome = income;
    }
}







//package com.koolboks.creditProject.dto;
//
//public class AgentFollowUpRequest {
//
//    private String bvn;
//    private String nin;
//    private String mobileNumber;
//    private String usageType; // Personal or Commercial
//    private String homeAddress;
//    private String workAddress;
//    private String storeAddress;
//    private Double monthlyIncome;
//    private Double monthlySales;
//    private String plan;
//    private String installmentOption;
//    private String utilityBillFilePath; // optional (path or filename)
//
//    public AgentFollowUpRequest() {}
//
//    // getters & setters
//    public String getBvn() { return bvn; }
//    public void setBvn(String bvn) { this.bvn = bvn; }
//
//    public String getNin() { return nin; }
//    public void setNin(String nin) { this.nin = nin; }
//
//    public String getMobileNumber() { return mobileNumber; }
//    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
//
//    public String getUsageType() { return usageType; }
//    public void setUsageType(String usageType) { this.usageType = usageType; }
//
//    public String getHomeAddress() { return homeAddress; }
//    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }
//
//    public String getWorkAddress() { return workAddress; }
//    public void setWorkAddress(String workAddress) { this.workAddress = workAddress; }
//
//    public String getStoreAddress() { return storeAddress; }
//    public void setStoreAddress(String storeAddress) { this.storeAddress = storeAddress; }
//
//    public Double getMonthlyIncome() { return monthlyIncome; }
//    public void setMonthlyIncome(Double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
//
//    public Double getMonthlySales() { return monthlySales; }
//    public void setMonthlySales(Double monthlySales) { this.monthlySales = monthlySales; }
//
//    public String getPlan() { return plan; }
//    public void setPlan(String plan) { this.plan = plan; }
//
//    public String getInstallmentOption() { return installmentOption; }
//    public void setInstallmentOption(String installmentOption) { this.installmentOption = installmentOption; }
//
//    public String getUtilityBillFilePath() { return utilityBillFilePath; }
//    public void setUtilityBillFilePath(String utilityBillFilePath) { this.utilityBillFilePath = utilityBillFilePath; }
//}

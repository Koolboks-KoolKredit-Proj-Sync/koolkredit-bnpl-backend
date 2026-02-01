package com.koolboks.creditProject.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_follow_up")
public class AgentFollowUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="bvn", nullable=false, length=20, unique = true)
    private String bvn;

    @Column(name="nin", length=20, unique = true)
    private String nin;

    @Column(name="mobile_number", unique = true)
    private String mobileNumber;

    @Column(name="usage_type")
    private String usageType; // Personal or Commercial

    @Column(name="home_address", length=1000)
    private String homeAddress;

    @Column(name="work_address", length=1000)
    private String workAddress;

    @Column(name="store_address", length=1000)
    private String storeAddress;

    @Column(name="monthly_income")
    private Double monthlyIncome;

    @Column(name="monthly_sales")
    private Double monthlySales;

    @Column(name = "customer_email", unique = true)
    private String customerEmail;

    @Column(name = "guarantor_email", unique = true)
    private String guarantorEmail;

    @Column(name="plan")
    private String plan;

    @Column(name="installment_option")
    private String installmentOption;

    @Column(name="utility_bill_path")
    private String utilityBillFilePath;

    @Column(name="credit_status")
    private String creditStatus; // GREEN / AMBER / RED

    @Column(name="approval_pin")
    private String approvalPin;

//    @Lob
//    @Column(name="mono_full_response", columnDefinition = "TEXT")
//    private String monoFullResponse;

    // ✅ CHANGE TO THIS (remove @Lob)
    @Column(name="mono_full_response", columnDefinition = "TEXT")
    private String monoFullResponse;

    @Column(name="dti")
    private Double dti;

    @Column(name="otp_sent")
    private Boolean otpSent = false;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt;


    @Column(name = "otp_verified")
    private boolean otpVerified = false;


//    @OneToOne(mappedBy = "agentFollowUp")
//    private Guarantor guarantor;

    // Inverse 1:1 relationship

    @OneToOne(mappedBy = "agentFollowUp", fetch = FetchType.LAZY)
    private Guarantor guarantor;

//    @OneToOne(mappedBy = "agentFollowUp", fetch = FetchType.LAZY)
//    private Guarantor guarantorByMobile;

//    @OneToOne(mappedBy = "agentFollowUpByMobile", fetch = FetchType.LAZY)
//    private Guarantor guarantorByMobile;




    public AgentFollowUp() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getUtilityBillFilePath() { return utilityBillFilePath; }
    public void setUtilityBillFilePath(String utilityBillFilePath) { this.utilityBillFilePath = utilityBillFilePath; }

    public String getCreditStatus() { return creditStatus; }
    public void setCreditStatus(String creditStatus) { this.creditStatus = creditStatus; }

    public String getApprovalPin() { return approvalPin; }
    public void setApprovalPin(String approvalPin) { this.approvalPin = approvalPin; }

    public String getMonoFullResponse() { return monoFullResponse; }
    public void setMonoFullResponse(String monoFullResponse) { this.monoFullResponse = monoFullResponse; }

    public Double getDti() { return dti; }
    public void setDti(Double dti) { this.dti = dti; }

    public Boolean getOtpSent() { return otpSent; }
    public void setOtpSent(Boolean otpSent) { this.otpSent = otpSent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }


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

    public boolean isOtpVerified() {
        return otpVerified;
    }

    public void setOtpVerified(boolean otpVerified) {
        this.otpVerified = otpVerified;
    }


    public boolean isOtpSent() {
    return otpSent;
    }

    public void setOtpSent(boolean otpSent) {
        this.otpSent = otpSent;
    }

    public Guarantor getGuarantor() {
    return guarantor;
    }

    public void setGuarantor(Guarantor guarantor) {
        this.guarantor = guarantor;
    }




//    public Guarantor getGuarantorByMobile() {
//        return guarantorByMobile;
//    }

//    public void setGuarantor(Guarantor guarantor) {
//    this.guarantor = guarantor;
//    if (guarantor != null && guarantor.getAgentFollowUp() != this) {
//        guarantor.setAgentFollowUp(this);
//    }



//    public void setGuarantorByMobile(Guarantor guarantorByMobile) {
//        this.guarantorByMobile = guarantorByMobile;
//    }


//    public Guarantor getGuarantor() {
//    return guarantor;
//}
//
//    public void setGuarantor(Guarantor guarantor) {
//    this.guarantor = guarantor;
//    if (guarantor != null && guarantor.getAgentFollowUp() != this) {
//        guarantor.setAgentFollowUp(this);
//    }
//}

}

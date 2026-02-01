package com.koolboks.creditProject.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_entries")
public class AgentEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "product_name", columnDefinition = "TEXT", nullable = false)
    private String productName;

    @Column(name = "size", columnDefinition = "TEXT")
    private String size;

//    @Column(name = "price", columnDefinition = "TEXT", nullable = false)
//    private String price;
//
//    @Column(name = "total_price", columnDefinition = "TEXT", nullable = false)
//    private String totalPrice;


    // Business Type
    @Column(name = "business_type", nullable = false)
    private String businessType;

    // Product Details (concatenated)
//    @Column(name = "product_name", nullable = false, length = 1000)
//    private String productName;

    @Column(name = "brand", nullable = false)
    private String brand;

//    @Column(name = "size", length = 500)
//    private String size;

    @Column(name = "price", nullable = false)
    private String price;

    @Column(name = "total_price", nullable = false)
    private String totalPrice;

    // Personal Information
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "marital_status", nullable = false)
    private String maritalStatus;

    @Column(name = "gender", nullable = false)
    private String gender;

    // Spouse Information
    @Column(name = "spouse_name")
    private String spouseName;

    @Column(name = "spouse_phone")
    private String spousePhone;

    // Next of Kin Information
    @Column(name = "next_of_kin_name")
    private String nextOfKinName;

    @Column(name = "next_of_kin_phone")
    private String nextOfKinPhone;

    @Column(name = "next_of_kin_relationship")
    private String nextOfKinRelationship;

    // Verification Details
    @Column(name = "bvn", nullable = false, length = 11)
    private String bvn;

    @Column(name = "nin", nullable = false, length = 11)
    private String nin;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "passport_photo_path")
    private String passportPhotoPath;

    // Payment Plan
    @Column(name = "plan", nullable = false)
    private String plan;

    @Column(name = "installment_duration")
    private String installmentDuration;

    @Column(name = "omolope_days")
    private String omolopeDays;

    @Column(name = "bank_statement_method")
    private String bankStatementMethod;

    // Verification Status
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "verification_message")
    private String verificationMessage;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    // Audit Fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public AgentEntry() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSpouseName() {
        return spouseName;
    }

    public void setSpouseName(String spouseName) {
        this.spouseName = spouseName;
    }

    public String getSpousePhone() {
        return spousePhone;
    }

    public void setSpousePhone(String spousePhone) {
        this.spousePhone = spousePhone;
    }

    public String getNextOfKinName() {
        return nextOfKinName;
    }

    public void setNextOfKinName(String nextOfKinName) {
        this.nextOfKinName = nextOfKinName;
    }

    public String getNextOfKinPhone() {
        return nextOfKinPhone;
    }

    public void setNextOfKinPhone(String nextOfKinPhone) {
        this.nextOfKinPhone = nextOfKinPhone;
    }

    public String getNextOfKinRelationship() {
        return nextOfKinRelationship;
    }

    public void setNextOfKinRelationship(String nextOfKinRelationship) {
        this.nextOfKinRelationship = nextOfKinRelationship;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getNin() {
        return nin;
    }

    public void setNin(String nin) {
        this.nin = nin;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassportPhotoPath() {
        return passportPhotoPath;
    }

    public void setPassportPhotoPath(String passportPhotoPath) {
        this.passportPhotoPath = passportPhotoPath;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getInstallmentDuration() {
        return installmentDuration;
    }

    public void setInstallmentDuration(String installmentDuration) {
        this.installmentDuration = installmentDuration;
    }

    public String getOmolopeDays() {
        return omolopeDays;
    }

    public void setOmolopeDays(String omolopeDays) {
        this.omolopeDays = omolopeDays;
    }

    public String getBankStatementMethod() {
        return bankStatementMethod;
    }

    public void setBankStatementMethod(String bankStatementMethod) {
        this.bankStatementMethod = bankStatementMethod;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }

    public String getVerificationMessage() {
        return verificationMessage;
    }

    public void setVerificationMessage(String verificationMessage) {
        this.verificationMessage = verificationMessage;
    }

    public LocalDateTime getVerificationDate() {
        return verificationDate;
    }

    public void setVerificationDate(LocalDateTime verificationDate) {
        this.verificationDate = verificationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}




















//package com.koolboks.creditProject.entity;
//
//
//
//import jakarta.persistence.*;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "agent_entries")
//public class AgentEntry {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // Product Details
//    @Column(name = "product_name", nullable = false)
//    private String productName;
//
//    @Column(name = "brand", nullable = false)
//    private String brand;
//
//    @Column(name = "size", nullable = false)
//    private String size;
//
//    @Column(name = "price", nullable = false)
//    private String price;
//
//
//    // Inverse 1:1 relationship
//
////    @OneToOne(mappedBy = "agentEntry", fetch = FetchType.LAZY)
////    private Guarantor guarantor;
//
////    @OneToOne(mappedBy = "agentEntry", fetch = FetchType.LAZY)
////    private Guarantor guarantorByPrice;
//
//
////    @OneToOne(mappedBy = "agentEntryByPrice", fetch = FetchType.LAZY)
////    private Guarantor guarantorByPrice;
//
//    // Personal Information
//    @Column(name = "first_name", nullable = false)
//    private String firstName;
//
//    @Column(name = "middle_name")
//    private String middleName;
//
//    @Column(name = "last_name", nullable = false)
//    private String lastName;
//
//    @Column(name = "date_of_birth", nullable = false)
//    private LocalDate dateOfBirth;
//
//    @Column(name = "marital_status", nullable = false)
//    private String maritalStatus;
//
//    @Column(name = "gender", nullable = false)
//    private String gender;
//
//    // Verification Details
//    @Column(name = "bvn", nullable = false, length = 11)
//    private String bvn;
//
//    @Column(name = "nin", nullable = false, length = 11)
//    private String nin;
//
//    @Column(name = "mobile_number", nullable = false)
//    private String mobileNumber;
//
//    @Column(name = "passport_photo_path")
//    private String passportPhotoPath;
//
//    // Payment Plan
//    @Column(name = "plan", nullable = false)
//    private String plan;
//
//    @Column(name = "installment_duration")
//    private String installmentDuration;
//
//    @Column(name = "omolope_days")
//    private String omolopeDays;
//
//    @Column(name = "bank_statement_method")
//    private String bankStatementMethod;
//
//    // Verification Status
//    @Column(name = "is_verified", nullable = false)
//    private Boolean isVerified = false;
//
//    @Column(name = "verification_message")
//    private String verificationMessage;
//
//    @Column(name = "verification_date")
//    private LocalDateTime verificationDate;
//
//    // Audit Fields
//    @Column(name = "created_at", nullable = false, updatable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    @PrePersist
//    protected void onCreate() {
//        createdAt = LocalDateTime.now();
//        updatedAt = LocalDateTime.now();
//    }
//
//    @PreUpdate
//    protected void onUpdate() {
//        updatedAt = LocalDateTime.now();
//    }
//
//    // Constructors
//    public AgentEntry() {
//    }
//
//    // Getters and Setters
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getProductName() {
//        return productName;
//    }
//
//    public void setProductName(String productName) {
//        this.productName = productName;
//    }
//
//    public String getBrand() {
//        return brand;
//    }
//
//    public void setBrand(String brand) {
//        this.brand = brand;
//    }
//
//    public String getSize() {
//        return size;
//    }
//
//    public void setSize(String size) {
//        this.size = size;
//    }
//
//    public String getPrice() {
//        return price;
//    }
//
//    public void setPrice(String price) {
//        this.price = price;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getMiddleName() {
//        return middleName;
//    }
//
//    public void setMiddleName(String middleName) {
//        this.middleName = middleName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public LocalDate getDateOfBirth() {
//        return dateOfBirth;
//    }
//
//    public void setDateOfBirth(LocalDate dateOfBirth) {
//        this.dateOfBirth = dateOfBirth;
//    }
//
//    public String getMaritalStatus() {
//        return maritalStatus;
//    }
//
//    public void setMaritalStatus(String maritalStatus) {
//        this.maritalStatus = maritalStatus;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public void setGender(String gender) {
//        this.gender = gender;
//    }
//
//    public String getBvn() {
//        return bvn;
//    }
//
//    public void setBvn(String bvn) {
//        this.bvn = bvn;
//    }
//
//    public String getNin() {
//        return nin;
//    }
//
//    public void setNin(String nin) {
//        this.nin = nin;
//    }
//
//    public String getMobileNumber() {
//        return mobileNumber;
//    }
//
//    public void setMobileNumber(String mobileNumber) {
//        this.mobileNumber = mobileNumber;
//    }
//
//    public String getPassportPhotoPath() {
//        return passportPhotoPath;
//    }
//
//    public void setPassportPhotoPath(String passportPhotoPath) {
//        this.passportPhotoPath = passportPhotoPath;
//    }
//
//    public String getPlan() {
//        return plan;
//    }
//
//    public void setPlan(String plan) {
//        this.plan = plan;
//    }
//
//    public String getInstallmentDuration() {
//        return installmentDuration;
//    }
//
//    public void setInstallmentDuration(String installmentDuration) {
//        this.installmentDuration = installmentDuration;
//    }
//
//    public String getOmolopeDays() {
//        return omolopeDays;
//    }
//
//    public void setOmolopeDays(String omolopeDays) {
//        this.omolopeDays = omolopeDays;
//    }
//
//    public String getBankStatementMethod() {
//        return bankStatementMethod;
//    }
//
//    public void setBankStatementMethod(String bankStatementMethod) {
//        this.bankStatementMethod = bankStatementMethod;
//    }
//
//    public Boolean getIsVerified() {
//        return isVerified;
//    }
//
//    public void setIsVerified(Boolean isVerified) {
//        this.isVerified = isVerified;
//    }
//
//    public String getVerificationMessage() {
//        return verificationMessage;
//    }
//
//    public void setVerificationMessage(String verificationMessage) {
//        this.verificationMessage = verificationMessage;
//    }
//
//    public LocalDateTime getVerificationDate() {
//        return verificationDate;
//    }
//
//    public void setVerificationDate(LocalDateTime verificationDate) {
//        this.verificationDate = verificationDate;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//














//    public Guarantor getGuarantor() {
//    return guarantor;
//    }
//
//    public void setGuarantor(Guarantor guarantor) {
//        this.guarantor = guarantor;
//    }



//    public Guarantor getGuarantorByPrice() {
//        return guarantorByPrice;
//    }

//    public void setGuarantor(Guarantor guarantor) {
//    this.guarantor = guarantor;
//    if (guarantor != null && guarantor.getAgentEntry() != this) {
//        guarantor.setAgentEntry(this);
//    }



//    public void setGuarantorByPrice(Guarantor guarantorByPrice) {
//        this.guarantorByPrice = guarantorByPrice;
//    }


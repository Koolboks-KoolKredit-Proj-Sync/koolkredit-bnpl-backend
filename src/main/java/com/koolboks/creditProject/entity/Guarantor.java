package com.koolboks.creditProject.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "guarantors")
public class Guarantor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Customer Information (from AgentEntryForm + AgentFollowUpForm)
    @Column(name = "customer_bvn", nullable = true)
    private String customerBvn;

    @Column(name = "customer_first_name")
    private String customerFirstName;

    @Column(name = "customer_last_name")
    private String customerLastName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_phone_number")
    private String customerPhoneNumber;

    @Column(name = "customer_plan")
    private String customerPlan;

    @Column(name = "customer_installment_duration")
    private String customerInstallmentDuration;

    // Product Details (from AgentEntryForm)
    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_brand")
    private String productBrand;

    @Column(name = "product_size")
    private String productSize;

    @Column(name = "store_price", precision = 15, scale = 2)
    private BigDecimal storePrice;

    // Guarantor Information
    @Column(name = "guarantor_bvn", nullable = true)
    private String guarantorBvn;

    @Column(name = "guarantor_phone_number", nullable = true)
    private String guarantorPhoneNumber;

    @Column(name = "guarantor_nin")
    private String guarantorNin;

    @Column(name = "guarantor_email")
    private String guarantorEmail;

    // Token for guarantor link
    @Column(name = "guarantor_token", unique = true)
    private String guarantorToken;

    @Column(name = "token_expires_at")
    private LocalDateTime tokenExpiresAt;

    @Column(name = "guarantor_form_submitted")
    private Boolean guarantorFormSubmitted = false;

    @Column(name = "form_submitted_at")
    private LocalDateTime formSubmittedAt;

    // Confirmation Status
    @Column(name = "is_confirmed")
    private Boolean isConfirmed = false;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "confirmed_by")
    private String confirmedBy;

    // OTP Information
    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "otp_sent_at")
    private LocalDateTime otpSentAt;

    @Column(name = "otp_verified")
    private Boolean otpVerified = false;

    @Column(name = "otp_verified_at")
    private LocalDateTime otpVerifiedAt;

    @Column(name = "otp_attempts")
    private Integer otpAttempts = 0;

    // Status
    @Column(name = "active")
    private Boolean active = true;

    // Audit
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Offer Letter fields
    @Column(name = "offer_letter_sent")
    private Boolean offerLetterSent = false;

    @Column(name = "offer_letter_sent_at")
    private LocalDateTime offerLetterSentAt;

    @Column(name = "application_reference", unique = true)
    private String applicationReference;

    // Optional AgentFollowUp relationship (if needed for additional data)
    @OneToOne
    @JoinColumn(name = "agent_followup_id", referencedColumnName = "id")
    private AgentFollowUp agentFollowUp;

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
    public Guarantor() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerBvn() {
        return customerBvn;
    }

    public void setCustomerBvn(String customerBvn) {
        this.customerBvn = customerBvn;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerPlan() {
        return customerPlan;
    }

    public void setCustomerPlan(String customerPlan) {
        this.customerPlan = customerPlan;
    }

    public String getCustomerInstallmentDuration() {
        return customerInstallmentDuration;
    }

    public void setCustomerInstallmentDuration(String customerInstallmentDuration) {
        this.customerInstallmentDuration = customerInstallmentDuration;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public BigDecimal getStorePrice() {
        return storePrice;
    }

    public void setStorePrice(BigDecimal storePrice) {
        this.storePrice = storePrice;
    }

    public String getGuarantorBvn() {
        return guarantorBvn;
    }

    public void setGuarantorBvn(String guarantorBvn) {
        this.guarantorBvn = guarantorBvn;
    }

    public String getGuarantorPhoneNumber() {
        return guarantorPhoneNumber;
    }

    public void setGuarantorPhoneNumber(String guarantorPhoneNumber) {
        this.guarantorPhoneNumber = guarantorPhoneNumber;
    }

    public String getGuarantorNin() {
        return guarantorNin;
    }

    public void setGuarantorNin(String guarantorNin) {
        this.guarantorNin = guarantorNin;
    }

    public String getGuarantorEmail() {
        return guarantorEmail;
    }

    public void setGuarantorEmail(String guarantorEmail) {
        this.guarantorEmail = guarantorEmail;
    }

    public String getGuarantorToken() {
        return guarantorToken;
    }

    public void setGuarantorToken(String guarantorToken) {
        this.guarantorToken = guarantorToken;
    }

    public LocalDateTime getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public Boolean getGuarantorFormSubmitted() {
        return guarantorFormSubmitted;
    }

    public void setGuarantorFormSubmitted(Boolean guarantorFormSubmitted) {
        this.guarantorFormSubmitted = guarantorFormSubmitted;
    }

    public LocalDateTime getFormSubmittedAt() {
        return formSubmittedAt;
    }

    public void setFormSubmittedAt(LocalDateTime formSubmittedAt) {
        this.formSubmittedAt = formSubmittedAt;
    }

    public Boolean getIsConfirmed() {
        return isConfirmed;
    }

    public void setIsConfirmed(Boolean isConfirmed) {
        this.isConfirmed = isConfirmed;
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(LocalDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public String getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(String confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getOtpSentAt() {
        return otpSentAt;
    }

    public void setOtpSentAt(LocalDateTime otpSentAt) {
        this.otpSentAt = otpSentAt;
    }

    public Boolean getOtpVerified() {
        return otpVerified;
    }

    public void setOtpVerified(Boolean otpVerified) {
        this.otpVerified = otpVerified;
    }

    public LocalDateTime getOtpVerifiedAt() {
        return otpVerifiedAt;
    }

    public void setOtpVerifiedAt(LocalDateTime otpVerifiedAt) {
        this.otpVerifiedAt = otpVerifiedAt;
    }

    public Integer getOtpAttempts() {
        return otpAttempts;
    }

    public void setOtpAttempts(Integer otpAttempts) {
        this.otpAttempts = otpAttempts;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public Boolean getOfferLetterSent() {
        return offerLetterSent;
    }

    public void setOfferLetterSent(Boolean offerLetterSent) {
        this.offerLetterSent = offerLetterSent;
    }

    public LocalDateTime getOfferLetterSentAt() {
        return offerLetterSentAt;
    }

    public void setOfferLetterSentAt(LocalDateTime offerLetterSentAt) {
        this.offerLetterSentAt = offerLetterSentAt;
    }

    public String getApplicationReference() {
        return applicationReference;
    }

    public void setApplicationReference(String applicationReference) {
        this.applicationReference = applicationReference;
    }

    public AgentFollowUp getAgentFollowUp() {
        return agentFollowUp;
    }

    public void setAgentFollowUp(AgentFollowUp agentFollowUp) {
        this.agentFollowUp = agentFollowUp;
    }

    // Convenience method for SMS (tries AgentFollowUp first, then direct column)
    public String getMobileNumber() {
        if (customerPhoneNumber != null && !customerPhoneNumber.isBlank()) {
            return customerPhoneNumber;
        }
        if (agentFollowUp != null) {
            return agentFollowUp.getMobileNumber();
        }
        return null;
    }
}





















//package com.koolboks.creditProject.entity;
//
//import jakarta.persistence.*;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "guarantors")
//public class Guarantor {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // Customer Information
//    @Column(name = "customer_bvn", nullable = false)
//    private String customerBvn;
//
//    @Column(name = "customer_first_name")
//    private String customerFirstName;
//
//    @Column(name = "customer_last_name")
//    private String customerLastName;
//
//    @Column(name = "customer_email")
//    private String customerEmail;
//
//    @Column(name = "customer_plan")
//    private String customerPlan;
//
//    @Column(name = "customer_installment_duration")
//    private String customerInstallmentDuration;
//
//    // Guarantor Information
//    @Column(name = "guarantor_bvn", nullable = false)
//    private String guarantorBvn;
//
//    @Column(name = "guarantor_phone_number", nullable = false)
//    private String guarantorPhoneNumber;
//
//    @Column(name = "guarantor_nin")
//    private String guarantorNin;
//
//    @Column(name = "guarantor_email")
//    private String guarantorEmail;
//
//    // Token for guarantor link
//    @Column(name = "guarantor_token", unique = true)
//    private String guarantorToken;
//
//    @Column(name = "token_expires_at")
//    private LocalDateTime tokenExpiresAt;
//
//    @Column(name = "guarantor_form_submitted")
//    private Boolean guarantorFormSubmitted = false;
//
//    @Column(name = "form_submitted_at")
//    private LocalDateTime formSubmittedAt;
//
//    // Confirmation Status
//    @Column(name = "is_confirmed")
//    private Boolean isConfirmed = false;
//
//    @Column(name = "confirmed_at")
//    private LocalDateTime confirmedAt;
//
//    @Column(name = "confirmed_by")
//    private String confirmedBy;
//
//    // OTP Information
//    @Column(name = "otp_code")
//    private String otpCode;
//
//    @Column(name = "otp_sent_at")
//    private LocalDateTime otpSentAt;
//
//    @Column(name = "otp_verified")
//    private Boolean otpVerified = false;
//
//    @Column(name = "otp_verified_at")
//    private LocalDateTime otpVerifiedAt;
//
//    @Column(name = "otp_attempts")
//    private Integer otpAttempts = 0;
//
//    // Status
//    @Column(name = "active")
//    private Boolean active = true;
//
//    // Audit
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//    // Offer Letter fields
//    @Column(name = "offer_letter_sent")
//    private Boolean offerLetterSent = false;
//
//    @Column(name = "offer_letter_sent_at")
//    private LocalDateTime offerLetterSentAt;
//
//    @Column(name = "application_reference", unique = true)
//    private String applicationReference;
//
//    // ✅ PROPER FOREIGN KEY RELATIONSHIPS
//
//    // Link to AgentFollowUp by ID (primary key)
//    //@OneToOne
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "agent_followup_id", referencedColumnName = "id")
//    private AgentFollowUp agentFollowUp;
//
//    // Link to AgentEntry by ID (primary key) - to get product price
//    //@OneToOne
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "agent_entry_id", referencedColumnName = "id")
//    private AgentEntry agentEntry;
//
//
//
//
//
//
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
//    public Guarantor() {
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
//    public String getCustomerBvn() {
//        return customerBvn;
//    }
//
//    public void setCustomerBvn(String customerBvn) {
//        this.customerBvn = customerBvn;
//    }
//
//    public String getCustomerFirstName() {
//        return customerFirstName;
//    }
//
//    public void setCustomerFirstName(String customerFirstName) {
//        this.customerFirstName = customerFirstName;
//    }
//
//    public String getCustomerLastName() {
//        return customerLastName;
//    }
//
//    public void setCustomerLastName(String customerLastName) {
//        this.customerLastName = customerLastName;
//    }
//
//    public String getCustomerEmail() {
//        return customerEmail;
//    }
//
//    public void setCustomerEmail(String customerEmail) {
//        this.customerEmail = customerEmail;
//    }
//
//    public String getCustomerPlan() {
//        return customerPlan;
//    }
//
//    public void setCustomerPlan(String customerPlan) {
//        this.customerPlan = customerPlan;
//    }
//
//    public String getCustomerInstallmentDuration() {
//        return customerInstallmentDuration;
//    }
//
//    public void setCustomerInstallmentDuration(String customerInstallmentDuration) {
//        this.customerInstallmentDuration = customerInstallmentDuration;
//    }
//
//    public String getGuarantorBvn() {
//        return guarantorBvn;
//    }
//
//    public void setGuarantorBvn(String guarantorBvn) {
//        this.guarantorBvn = guarantorBvn;
//    }
//
//    public String getGuarantorPhoneNumber() {
//        return guarantorPhoneNumber;
//    }
//
//    public void setGuarantorPhoneNumber(String guarantorPhoneNumber) {
//        this.guarantorPhoneNumber = guarantorPhoneNumber;
//    }
//
//    public String getGuarantorNin() {
//        return guarantorNin;
//    }
//
//    public void setGuarantorNin(String guarantorNin) {
//        this.guarantorNin = guarantorNin;
//    }
//
//    public String getGuarantorEmail() {
//        return guarantorEmail;
//    }
//
//    public void setGuarantorEmail(String guarantorEmail) {
//        this.guarantorEmail = guarantorEmail;
//    }
//
//    public String getGuarantorToken() {
//        return guarantorToken;
//    }
//
//    public void setGuarantorToken(String guarantorToken) {
//        this.guarantorToken = guarantorToken;
//    }
//
//    public LocalDateTime getTokenExpiresAt() {
//        return tokenExpiresAt;
//    }
//
//    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) {
//        this.tokenExpiresAt = tokenExpiresAt;
//    }
//
//    public Boolean getGuarantorFormSubmitted() {
//        return guarantorFormSubmitted;
//    }
//
//    public void setGuarantorFormSubmitted(Boolean guarantorFormSubmitted) {
//        this.guarantorFormSubmitted = guarantorFormSubmitted;
//    }
//
//    public LocalDateTime getFormSubmittedAt() {
//        return formSubmittedAt;
//    }
//
//    public void setFormSubmittedAt(LocalDateTime formSubmittedAt) {
//        this.formSubmittedAt = formSubmittedAt;
//    }
//
//    public Boolean getIsConfirmed() {
//        return isConfirmed;
//    }
//
//    public void setIsConfirmed(Boolean isConfirmed) {
//        this.isConfirmed = isConfirmed;
//    }
//
//    public LocalDateTime getConfirmedAt() {
//        return confirmedAt;
//    }
//
//    public void setConfirmedAt(LocalDateTime confirmedAt) {
//        this.confirmedAt = confirmedAt;
//    }
//
//    public String getConfirmedBy() {
//        return confirmedBy;
//    }
//
//    public void setConfirmedBy(String confirmedBy) {
//        this.confirmedBy = confirmedBy;
//    }
//
//    public String getOtpCode() {
//        return otpCode;
//    }
//
//    public void setOtpCode(String otpCode) {
//        this.otpCode = otpCode;
//    }
//
//    public LocalDateTime getOtpSentAt() {
//        return otpSentAt;
//    }
//
//    public void setOtpSentAt(LocalDateTime otpSentAt) {
//        this.otpSentAt = otpSentAt;
//    }
//
//    public Boolean getOtpVerified() {
//        return otpVerified;
//    }
//
//    public void setOtpVerified(Boolean otpVerified) {
//        this.otpVerified = otpVerified;
//    }
//
//    public LocalDateTime getOtpVerifiedAt() {
//        return otpVerifiedAt;
//    }
//
//    public void setOtpVerifiedAt(LocalDateTime otpVerifiedAt) {
//        this.otpVerifiedAt = otpVerifiedAt;
//    }
//
//    public Integer getOtpAttempts() {
//        return otpAttempts;
//    }
//
//    public void setOtpAttempts(Integer otpAttempts) {
//        this.otpAttempts = otpAttempts;
//    }
//
//    public Boolean getActive() {
//        return active;
//    }
//
//    public void setActive(Boolean active) {
//        this.active = active;
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
//    public Boolean getOfferLetterSent() {
//        return offerLetterSent;
//    }
//
//    public void setOfferLetterSent(Boolean offerLetterSent) {
//        this.offerLetterSent = offerLetterSent;
//    }
//
//    public LocalDateTime getOfferLetterSentAt() {
//        return offerLetterSentAt;
//    }
//
//    public void setOfferLetterSentAt(LocalDateTime offerLetterSentAt) {
//        this.offerLetterSentAt = offerLetterSentAt;
//    }
//
//    public String getApplicationReference() {
//        return applicationReference;
//    }
//
//    public void setApplicationReference(String applicationReference) {
//        this.applicationReference = applicationReference;
//    }
//
//    public AgentFollowUp getAgentFollowUp() {
//        return agentFollowUp;
//    }
//
//    public void setAgentFollowUp(AgentFollowUp agentFollowUp) {
//        this.agentFollowUp = agentFollowUp;
//    }
//
//    public AgentEntry getAgentEntry() {
//        return agentEntry;
//    }
//
//    public void setAgentEntry(AgentEntry agentEntry) {
//        this.agentEntry = agentEntry;
//    }
//
//    // ✅ CONVENIENCE METHODS to get values from related entities
//
//    /**
//     * Get mobile number from AgentFollowUp
//     */
//    public String getMobileNumber() {
//        if (agentFollowUp != null) {
//            return agentFollowUp.getMobileNumber();
//        }
//        return null;
//    }
//
//    /**
//     * Get store price from AgentEntry
//     */
//    public BigDecimal getStorePrice() {
//        if (agentEntry != null && agentEntry.getPrice() != null) {
//            try {
//                // AgentEntry.price is String, convert to BigDecimal
//                String priceStr = agentEntry.getPrice().replace(",", "").trim();
//                return new BigDecimal(priceStr);
//            } catch (NumberFormatException e) {
//                return null;
//            }
//        }
//        return null;
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



//package com.koolboks.creditProject.entity;
//
//import jakarta.persistence.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "guarantors")
//public class Guarantor {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // Customer Information
//    @Column(name = "customer_bvn", nullable = false)
//    private String customerBvn;
//
//    @Column(name = "customer_first_name")
//    private String customerFirstName;
//
//    @Column(name = "customer_last_name")
//    private String customerLastName;
//
//    public String getCustomerEmail() {
//        return customerEmail;
//    }
//
//    @OneToOne
//    @JoinColumn(
//        name = "mobile_number",
//        referencedColumnName = "mobile_number",
//        foreignKey = @ForeignKey(name = "fk_guarantor_agent_followup_mobile"),
//        unique = true
//    )
//    private AgentFollowUp agentFollowUpByMobile;
//
//    // One-to-One with AgentEntry (by price)
//    @OneToOne
//    @JoinColumn(
//        name = "store_price",
//        referencedColumnName = "price",
//        foreignKey = @ForeignKey(name = "fk_guarantor_agent_entry_price"),
//        unique = true
//    )
//    private AgentEntry agentEntryByPrice;
//
//        // Add field
////    @Column(name = "store_price")
////    private BigDecimal storePrice;
//
//    public void setCustomerEmail(String customerEmail) {
//        this.customerEmail = customerEmail;
//    }
//
//    // 🔥 This behaves like a field but DOES NOT create a new column
////    @Transient
////    private String customerEmail;
//    @Column(name = "customer_email")
//    private String customerEmail;
//
//    @Column(name = "customer_plan")
//    private String customerPlan;
//
//    @Column(name = "customer_installment_duration")
//    private String customerInstallmentDuration;
//
//    // Guarantor Information
//    @Column(name = "guarantor_bvn", nullable = false)
//    private String guarantorBvn;
//
//    @Column(name = "guarantor_phone_number", nullable = false)
//    private String guarantorPhoneNumber;
//
//    @Column(name = "guarantor_nin")
//    private String guarantorNin;
//
//    @Column(name = "guarantor_email")
//    private String guarantorEmail;
//
//    // Token for guarantor link
//    @Column(name = "guarantor_token", unique = true)
//    private String guarantorToken;
//
//    @Column(name = "token_expires_at")
//    private LocalDateTime tokenExpiresAt;
//
//    @Column(name = "guarantor_form_submitted")
//    private Boolean guarantorFormSubmitted = false;
//
//    @Column(name = "form_submitted_at")
//    private LocalDateTime formSubmittedAt;
//
//    // Confirmation Status
//    @Column(name = "is_confirmed")
//    private Boolean isConfirmed = false;
//
//    @Column(name = "confirmed_at")
//    private LocalDateTime confirmedAt;
//
//    @Column(name = "confirmed_by")
//    private String confirmedBy; // Admin email or ID
//
//    // OTP Information
//    @Column(name = "otp_code")
//    private String otpCode;
//
//    @Column(name = "otp_sent_at")
//    private LocalDateTime otpSentAt;
//
//    @Column(name = "otp_verified")
//    private Boolean otpVerified = false;
//
//    @Column(name = "otp_verified_at")
//    private LocalDateTime otpVerifiedAt;
//
//    @Column(name = "otp_attempts")
//    private Integer otpAttempts = 0;
//
//    // Status
//    @Column(name = "active")
//    private Boolean active = true;
//
//
//
//
////    @Lob
////    @Column(name = "selfie_image", nullable = true)
////    private byte[] selfieImage;
//
//    // Audit
//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;
//
//
//    // Add these fields to your Guarantor entity class
//
//    // Offer Letter fields
//    @Column(name = "offer_letter_sent")
//    private Boolean offerLetterSent = false;
//
//    @Column(name = "offer_letter_sent_at")
//    private LocalDateTime offerLetterSentAt;
//
//    @Column(name = "application_reference", unique = true)
//    private String applicationReference;
//
//
//    // Make AgentFollowUp optional
//    @OneToOne
//    @JoinColumn(name = "agent_followup_id", referencedColumnName = "id", unique = true)
//    private AgentFollowUp agentFollowUp;
//
//
////    @OneToOne(optional = false)
////    @JoinColumn(name = "agent_followup_id", nullable = false, unique = true)
////    private AgentFollowUp agentFollowUp;
//
//
////    @OneToOne
////    @JoinColumn(name = "agent_followup_id", referencedColumnName = "id", unique = true)
////    private AgentFollowUp agentFollowUp;
//
//
//    // 🔥 This behaves like a field but DOES NOT create a new column
////    @Transient
////    private String mobileNumber;
//
//    // Update getter to be safe
////    public String getMobileNumber() {
////        // Try AgentFollowUp first if it exists
////        if (agentFollowUp != null && agentFollowUp.getMobileNumber() != null) {
////            return agentFollowUp.getMobileNumber();
////        }
////        // Fall back to direct column
////        return mobileNumber;
////    }
////
////    public void setMobileNumber(String mobileNumber) {
////        this.mobileNumber = mobileNumber;
////    }
//
////    @Column(name = "customer_phone_number")
////    private String mobileNumber;  // Add this for SMS notifications
//
//
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
//    public Guarantor() {
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
//    public String getCustomerBvn() {
//        return customerBvn;
//    }
//
//    public void setCustomerBvn(String customerBvn) {
//        this.customerBvn = customerBvn;
//    }
//
//    public String getCustomerFirstName() {
//        return customerFirstName;
//    }
//
//    public void setCustomerFirstName(String customerFirstName) {
//        this.customerFirstName = customerFirstName;
//    }
//
//    public String getCustomerLastName() {
//        return customerLastName;
//    }
//
//    public void setCustomerLastName(String customerLastName) {
//        this.customerLastName = customerLastName;
//    }
//
////    public String getCustomerEmail() {
////        return customerEmail;
////    }
////
////    public void setCustomerEmail(String customerEmail) {
////        this.customerEmail = customerEmail;
////    }
//
//    public String getCustomerPlan() {
//        return customerPlan;
//    }
//
//    public void setCustomerPlan(String customerPlan) {
//        this.customerPlan = customerPlan;
//    }
//
//    public String getCustomerInstallmentDuration() {
//        return customerInstallmentDuration;
//    }
//
//    public void setCustomerInstallmentDuration(String customerInstallmentDuration) {
//        this.customerInstallmentDuration = customerInstallmentDuration;
//    }
//
//    public String getGuarantorBvn() {
//        return guarantorBvn;
//    }
//
//    public void setGuarantorBvn(String guarantorBvn) {
//        this.guarantorBvn = guarantorBvn;
//    }
//
//    public String getGuarantorPhoneNumber() {
//        return guarantorPhoneNumber;
//    }
//
//    public void setGuarantorPhoneNumber(String guarantorPhoneNumber) {
//        this.guarantorPhoneNumber = guarantorPhoneNumber;
//    }
//
//    public String getGuarantorNin() {
//        return guarantorNin;
//    }
//
//    public void setGuarantorNin(String guarantorNin) {
//        this.guarantorNin = guarantorNin;
//    }
//
//    public String getGuarantorEmail() {
//        return guarantorEmail;
//    }
//
//    public void setGuarantorEmail(String guarantorEmail) {
//        this.guarantorEmail = guarantorEmail;
//    }
//
//    public Boolean getIsConfirmed() {
//        return isConfirmed;
//    }
//
//    public void setIsConfirmed(Boolean isConfirmed) {
//        this.isConfirmed = isConfirmed;
//    }
//
//    public LocalDateTime getConfirmedAt() {
//        return confirmedAt;
//    }
//
//    public void setConfirmedAt(LocalDateTime confirmedAt) {
//        this.confirmedAt = confirmedAt;
//    }
//
//    public String getConfirmedBy() {
//        return confirmedBy;
//    }
//
//    public void setConfirmedBy(String confirmedBy) {
//        this.confirmedBy = confirmedBy;
//    }
//
//    public String getOtpCode() {
//        return otpCode;
//    }
//
//    public void setOtpCode(String otpCode) {
//        this.otpCode = otpCode;
//    }
//
//    public LocalDateTime getOtpSentAt() {
//        return otpSentAt;
//    }
//
//    public void setOtpSentAt(LocalDateTime otpSentAt) {
//        this.otpSentAt = otpSentAt;
//    }
//
//    public Boolean getOtpVerified() {
//        return otpVerified;
//    }
//
//    public void setOtpVerified(Boolean otpVerified) {
//        this.otpVerified = otpVerified;
//    }
//
//    public LocalDateTime getOtpVerifiedAt() {
//        return otpVerifiedAt;
//    }
//
//    public void setOtpVerifiedAt(LocalDateTime otpVerifiedAt) {
//        this.otpVerifiedAt = otpVerifiedAt;
//    }
//
//    public Integer getOtpAttempts() {
//        return otpAttempts;
//    }
//
//    public void setOtpAttempts(Integer otpAttempts) {
//        this.otpAttempts = otpAttempts;
//    }
//
//    public Boolean getActive() {
//        return active;
//    }
//
//    public void setActive(Boolean active) {
//        this.active = active;
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
//    public String getGuarantorToken() {
//        return guarantorToken;
//    }
//
//    public void setGuarantorToken(String guarantorToken) {
//        this.guarantorToken = guarantorToken;
//    }
//
//    public LocalDateTime getTokenExpiresAt() {
//        return tokenExpiresAt;
//    }
//
//    public void setTokenExpiresAt(LocalDateTime tokenExpiresAt) {
//        this.tokenExpiresAt = tokenExpiresAt;
//    }
//
//    public Boolean getGuarantorFormSubmitted() {
//        return guarantorFormSubmitted;
//    }
//
//    public void setGuarantorFormSubmitted(Boolean guarantorFormSubmitted) {
//        this.guarantorFormSubmitted = guarantorFormSubmitted;
//    }
//
//    public LocalDateTime getFormSubmittedAt() {
//        return formSubmittedAt;
//    }
//
//    public void setFormSubmittedAt(LocalDateTime formSubmittedAt) {
//        this.formSubmittedAt = formSubmittedAt;
//    }
//
//
////    public byte[] getSelfieImage() {
////    return selfieImage;
////    }
////
////    public void setSelfieImage(byte[] selfieImage) {
////    this.selfieImage = selfieImage;
////    }
//
//     public Boolean getOfferLetterSent() {
//        return offerLetterSent;
//    }
//
//    public void setOfferLetterSent(Boolean offerLetterSent) {
//        this.offerLetterSent = offerLetterSent;
//    }
//
//    public LocalDateTime getOfferLetterSentAt() {
//        return offerLetterSentAt;
//    }
//
//    public void setOfferLetterSentAt(LocalDateTime offerLetterSentAt) {
//        this.offerLetterSentAt = offerLetterSentAt;
//    }
//
//    public String getApplicationReference() {
//        return applicationReference;
//    }
//
//    public void setApplicationReference(String applicationReference) {
//        this.applicationReference = applicationReference;
//    }
//
//
//
//
//    public AgentFollowUp getAgentFollowUp() {
//        return agentFollowUp;
//    }
//
//    public void setAgentFollowUp(AgentFollowUp agentFollowUp) {
//        this.agentFollowUp = agentFollowUp;
//    }
//
//    // Convenience getters that return the referenced values
////    public String getMobileNumber() {
////        return agentFollowUpByMobile != null ? agentFollowUpByMobile.getMobileNumber() : null;
////    }
////
//    public BigDecimal getStorePrice() {
//        if (agentEntryByPrice != null && agentEntryByPrice.getPrice() != null) {
//            try {
//                return new BigDecimal(agentEntryByPrice.getPrice());
//            } catch (NumberFormatException e) {
//                return BigDecimal.ZERO;
//            }
//        }
//        return null;
//    }
//
//    // Setters for the relationships
//    public void setAgentFollowUpByMobile(AgentFollowUp agentFollowUpByMobile) {
//        this.agentFollowUpByMobile = agentFollowUpByMobile;
//    }
//
//    public void setAgentEntryByPrice(AgentEntry agentEntryByPrice) {
//        this.agentEntryByPrice = agentEntryByPrice;
//    }
//
//    // Getters for the relationships
//    public AgentFollowUp getAgentFollowUpByMobile() {
//        return agentFollowUpByMobile;
//    }
//
//    public AgentEntry getAgentEntryByPrice() {
//        return agentEntryByPrice;
//    }
//        // Add getters/setters
////    public BigDecimal getStorePrice() {
////        return storePrice;
////    }
////
////    public void setStorePrice(BigDecimal storePrice) {
////        this.storePrice = storePrice;
////    }
//
//
////    public String getMobileNumber() {
////        return agentFollowUp != null ? agentFollowUp.getMobileNumber() : null;
////    }
////
////    public void setMobileNumber(String mobileNumber) {
////        throw new UnsupportedOperationException("mobileNumber is inherited from AgentFollowUp and cannot be set here");
////    }
////
////
////    public String getCustomerEmail() {
////        return agentFollowUp != null ? agentFollowUp.getCustomerEmail() : null;
////    }
////
////    public void setCustomerEmail(String customerEmail) {
////        throw new UnsupportedOperationException("Email is inherited from AgentFollowUp and cannot be set here");
////    }
////
//
//}
//
//
//
//
//
//
//
//
//








//package com.koolboks.creditProject.entity;
//
//import jakarta.persistence.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "guarantors")
//public class Guarantor {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // Customer Information
//    @Column(name = "customer_bvn", nullable = false)
//    private String customerBvn;
//
//    @Column(name = "customer_first_name")
//    private String customerFirstName;
//
//    @Column(name = "customer_last_name")
//    private String customerLastName;
//
//    @Column(name = "customer_email")
//    private String customerEmail;
//
//    @Column(name = "customer_plan")
//    private String customerPlan;
//
//    @Column(name = "customer_installment_duration")
//    private String customerInstallmentDuration;
//
//    // Guarantor Information
//
//    @Column(name = "guarantor_bvn", nullable = false, unique = true)
//    private String guarantorBvn;
//
//    @Column(name = "guarantor_phone_number", nullable = false, unique = true)
//    private String guarantorPhoneNumber;
//
//    @Column(name = "guarantor_nin", unique = true)
//    private String guarantorNin;
//
//    @Column(name = "guarantor_email", unique = true)
//    private String guarantorEmail;
//
//    // Confirmation Status
//    @Column(name = "is_confirmed")
//    private Boolean isConfirmed = false;
//
//    @Column(name = "confirmed_at")
//    private LocalDateTime confirmedAt;
//
//    @Column(name = "confirmed_by")
//    private String confirmedBy; // Admin email or ID
//
//    // OTP Information
//    @Column(name = "otp_code")
//    private String otpCode;
//
//    @Column(name = "otp_sent_at")
//    private LocalDateTime otpSentAt;
//
//    @Column(name = "otp_verified")
//    private Boolean otpVerified = false;
//
//    @Column(name = "otp_verified_at")
//    private LocalDateTime otpVerifiedAt;
//
//    @Column(name = "otp_attempts")
//    private Integer otpAttempts = 0;
//
//    // Status
//    @Column(name = "active")
//    private Boolean active = true;
//
//    // Audit
//    @Column(name = "created_at")
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
//    public Guarantor() {
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
//    public String getCustomerBvn() {
//        return customerBvn;
//    }
//
//    public void setCustomerBvn(String customerBvn) {
//        this.customerBvn = customerBvn;
//    }
//
//    public String getCustomerFirstName() {
//        return customerFirstName;
//    }
//
//    public void setCustomerFirstName(String customerFirstName) {
//        this.customerFirstName = customerFirstName;
//    }
//
//    public String getCustomerLastName() {
//        return customerLastName;
//    }
//
//    public void setCustomerLastName(String customerLastName) {
//        this.customerLastName = customerLastName;
//    }
//
//    public String getCustomerEmail() {
//        return customerEmail;
//    }
//
//    public void setCustomerEmail(String customerEmail) {
//        this.customerEmail = customerEmail;
//    }
//
//    public String getCustomerPlan() {
//        return customerPlan;
//    }
//
//    public void setCustomerPlan(String customerPlan) {
//        this.customerPlan = customerPlan;
//    }
//
//    public String getCustomerInstallmentDuration() {
//        return customerInstallmentDuration;
//    }
//
//    public void setCustomerInstallmentDuration(String customerInstallmentDuration) {
//        this.customerInstallmentDuration = customerInstallmentDuration;
//    }
//
//    public String getGuarantorBvn() {
//        return guarantorBvn;
//    }
//
//    public void setGuarantorBvn(String guarantorBvn) {
//        this.guarantorBvn = guarantorBvn;
//    }
//
//    public String getGuarantorPhoneNumber() {
//        return guarantorPhoneNumber;
//    }
//
//    public void setGuarantorPhoneNumber(String guarantorPhoneNumber) {
//        this.guarantorPhoneNumber = guarantorPhoneNumber;
//    }
//
//    public String getGuarantorNin() {
//        return guarantorNin;
//    }
//
//    public void setGuarantorNin(String guarantorNin) {
//        this.guarantorNin = guarantorNin;
//    }
//
//    public String getGuarantorEmail() {
//        return guarantorEmail;
//    }
//
//    public void setGuarantorEmail(String guarantorEmail) {
//        this.guarantorEmail = guarantorEmail;
//    }
//
//    public Boolean getIsConfirmed() {
//        return isConfirmed;
//    }
//
//    public void setIsConfirmed(Boolean isConfirmed) {
//        this.isConfirmed = isConfirmed;
//    }
//
//    public LocalDateTime getConfirmedAt() {
//        return confirmedAt;
//    }
//
//    public void setConfirmedAt(LocalDateTime confirmedAt) {
//        this.confirmedAt = confirmedAt;
//    }
//
//    public String getConfirmedBy() {
//        return confirmedBy;
//    }
//
//    public void setConfirmedBy(String confirmedBy) {
//        this.confirmedBy = confirmedBy;
//    }
//
//    public String getOtpCode() {
//        return otpCode;
//    }
//
//    public void setOtpCode(String otpCode) {
//        this.otpCode = otpCode;
//    }
//
//    public LocalDateTime getOtpSentAt() {
//        return otpSentAt;
//    }
//
//    public void setOtpSentAt(LocalDateTime otpSentAt) {
//        this.otpSentAt = otpSentAt;
//    }
//
//    public Boolean getOtpVerified() {
//        return otpVerified;
//    }
//
//    public void setOtpVerified(Boolean otpVerified) {
//        this.otpVerified = otpVerified;
//    }
//
//    public LocalDateTime getOtpVerifiedAt() {
//        return otpVerifiedAt;
//    }
//
//    public void setOtpVerifiedAt(LocalDateTime otpVerifiedAt) {
//        this.otpVerifiedAt = otpVerifiedAt;
//    }
//
//    public Integer getOtpAttempts() {
//        return otpAttempts;
//    }
//
//    public void setOtpAttempts(Integer otpAttempts) {
//        this.otpAttempts = otpAttempts;
//    }
//
//    public Boolean getActive() {
//        return active;
//    }
//
//    public void setActive(Boolean active) {
//        this.active = active;
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
//}
//
//
//
//
//
//









//package com.koolboks.creditProject.entity;
//
//import jakarta.persistence.*;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "guarantors")
//public class Guarantor {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // Customer info
//    private String customerBvn;
//    private String customerFirstName;
//    private String customerLastName;
//    private String customerEmail;
//    private String customerPlan;
//    private String customerInstallmentDuration;
//
//    // Guarantor info
//    @Column(unique = true)
//    private String guarantorBvn;
//
//    @Column(unique = true)
//    private String guarantorPhoneNumber;
//
//    private String guarantorNin;
//    private String guarantorEmail;
//
//    private boolean active = true;
//
//    public Guarantor() {}
//
//    // Getters & Setters (no lombok)
//    // ----------------------------------
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getCustomerBvn() {
//        return customerBvn;
//    }
//
//    public void setCustomerBvn(String customerBvn) {
//        this.customerBvn = customerBvn;
//    }
//
//    public String getCustomerFirstName() {
//        return customerFirstName;
//    }
//
//    public void setCustomerFirstName(String customerFirstName) {
//        this.customerFirstName = customerFirstName;
//    }
//
//    public String getCustomerLastName() {
//        return customerLastName;
//    }
//
//    public void setCustomerLastName(String customerLastName) {
//        this.customerLastName = customerLastName;
//    }
//
//    public String getCustomerEmail() {
//        return customerEmail;
//    }
//
//    public void setCustomerEmail(String customerEmail) {
//        this.customerEmail = customerEmail;
//    }
//
//    public String getCustomerPlan() {
//        return customerPlan;
//    }
//
//    public void setCustomerPlan(String customerPlan) {
//        this.customerPlan = customerPlan;
//    }
//
//    public String getCustomerInstallmentDuration() {
//        return customerInstallmentDuration;
//    }
//
//    public void setCustomerInstallmentDuration(String customerInstallmentDuration) {
//        this.customerInstallmentDuration = customerInstallmentDuration;
//    }
//
//    public String getGuarantorBvn() {
//        return guarantorBvn;
//    }
//
//    public void setGuarantorBvn(String guarantorBvn) {
//        this.guarantorBvn = guarantorBvn;
//    }
//
//    public String getGuarantorPhoneNumber() {
//        return guarantorPhoneNumber;
//    }
//
//    public void setGuarantorPhoneNumber(String guarantorPhoneNumber) {
//        this.guarantorPhoneNumber = guarantorPhoneNumber;
//    }
//
//    public String getGuarantorNin() {
//        return guarantorNin;
//    }
//
//    public void setGuarantorNin(String guarantorNin) {
//        this.guarantorNin = guarantorNin;
//    }
//
//    public String getGuarantorEmail() {
//        return guarantorEmail;
//    }
//
//    public void setGuarantorEmail(String guarantorEmail) {
//        this.guarantorEmail = guarantorEmail;
//    }
//
//    public boolean isActive() {
//        return active;
//    }
//
//    public void setActive(boolean active) {
//        this.active = active;
//    }
//}

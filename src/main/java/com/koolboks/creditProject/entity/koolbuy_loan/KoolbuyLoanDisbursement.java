package com.koolboks.creditProject.entity.koolbuy_loan;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "koolbuy_loan_disbursement")
public class KoolbuyLoanDisbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_name")
    private String agentName;

    @Column(name = "agent_email")
    private String agentEmail;

    @Column(name = "agent_id")
    private String agentId;

    @Column(name = "agent_number")
    private String agentNumber;

    @Column(name = "customer_loan_ref", unique = true)
    private String customerLoanRef;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "initial_instalment")
    private BigDecimal initialInstalment;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_location")
    private String storeLocation;

    @Column(name = "store_payment_confirmation")
    private Boolean storePaymentConfirmation = true; // ✅ TRUE when proof is submitted

    @Column(name = "customer_first_name")
    private String customerFirstName;

    public void setStorePrice(BigDecimal storePrice) {
        this.storePrice = storePrice;
    }

    public BigDecimal getStorePrice() {
        return storePrice;
    }

    @Column(name = "store_price", precision = 15, scale = 2)
    private BigDecimal storePrice;


    @Column(name = "customer_last_name")
    private String customerLastname;

    @Column(name = "customer_loan_duration")
    private Integer customerLoanDuration;

    @Column(name = "customer_phone_number")
    private String customerPhoneNumber;

    @Column(name = "guarantor_phone_number")
    private String guarantorPhoneNumber;

    @Column(name = "guarantor_email")
    private String guarantorEmail;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_size")
    private String productSize;

    @Column(name = "product_brand")
    private String productBrand;

    @Column(name = "receipt_image_path")
    private String receiptImagePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "paygo_configured_at")
    private LocalDateTime paygoConfiguredAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (storePaymentConfirmation == null) {
            storePaymentConfirmation = true; // ✅ Default to TRUE
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (Boolean.TRUE.equals(storePaymentConfirmation) && paygoConfiguredAt == null) {
            paygoConfiguredAt = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAgentEmail() {
        return agentEmail;
    }

    public void setAgentEmail(String agentEmail) {
        this.agentEmail = agentEmail;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAgentNumber() {
        return agentNumber;
    }

    public void setAgentNumber(String agentNumber) {
        this.agentNumber = agentNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastname() {
        return customerLastname;
    }

    public void setCustomerLastname(String customerLastname) {
        this.customerLastname = customerLastname;
    }

    public Integer getCustomerLoanDuration() {
        return customerLoanDuration;
    }

    public void setCustomerLoanDuration(Integer customerLoanDuration) {
        this.customerLoanDuration = customerLoanDuration;
    }

    public String getCustomerLoanRef() {
        return customerLoanRef;
    }

    public void setCustomerLoanRef(String customerLoanRef) {
        this.customerLoanRef = customerLoanRef;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getGuarantorEmail() {
        return guarantorEmail;
    }

    public void setGuarantorEmail(String guarantorEmail) {
        this.guarantorEmail = guarantorEmail;
    }

    public BigDecimal getInitialInstalment() {
        return initialInstalment;
    }

    public void setInitialInstalment(BigDecimal initialInstalment) {
        this.initialInstalment = initialInstalment;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(String storeLocation) {
        this.storeLocation = storeLocation;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Boolean getStorePaymentConfirmation() {
        return storePaymentConfirmation;
    }

    public void setStorePaymentConfirmation(Boolean storePaymentConfirmation) {
        this.storePaymentConfirmation = storePaymentConfirmation;
    }

    public String getReceiptImagePath() {
        return receiptImagePath;
    }

    public void setReceiptImagePath(String receiptImagePath) {
        this.receiptImagePath = receiptImagePath;
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

    public LocalDateTime getPaygoConfiguredAt() {
        return paygoConfiguredAt;
    }

    public void setPaygoConfiguredAt(LocalDateTime paygoConfiguredAt) {
        this.paygoConfiguredAt = paygoConfiguredAt;
    }





    public String getGuarantorPhoneNumber() {
        return guarantorPhoneNumber;
    }

    public void setGuarantorPhoneNumber(String guarantorPhoneNumber) {
        this.guarantorPhoneNumber = guarantorPhoneNumber;
    }


}
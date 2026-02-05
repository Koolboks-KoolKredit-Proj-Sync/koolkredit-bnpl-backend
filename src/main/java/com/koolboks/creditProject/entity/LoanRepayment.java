package com.koolboks.creditProject.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_repayment")
public class LoanRepayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "loan_reference", unique = true, nullable = false)
    private String loanReference;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "loan_duration")
    private String loanDuration;

    @Column(name = "number_of_months")
    private Integer numberOfMonths;

    @Column(name = "monthly_repayment", precision = 19, scale = 2)
    private BigDecimal monthlyRepayment;

    @Column(name = "total_instalment", precision = 19, scale = 2)
    private BigDecimal totalInstalment;

    @Column(name = "customer_bvn", nullable = false)
    private String customerBvn;

    @Column(name = "customer_nin")
    private String customerNin;

    @Column(name = "guarantor_contact")
    private String guarantorContact;

    @Column(name = "repayment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RepaymentStatus repaymentStatus = RepaymentStatus.PENDING;

    @Column(name = "amount_paid", precision = 19, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "remaining_balance", precision = 19, scale = 2)
    private BigDecimal remainingBalance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Enum for repayment status
    public enum RepaymentStatus {
        PENDING,
        ACTIVE,
        COMPLETED,
        DEFAULTED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLoanReference() {
        return loanReference;
    }

    public void setLoanReference(String loanReference) {
        this.loanReference = loanReference;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLoanDuration() {
        return loanDuration;
    }

    public void setLoanDuration(String loanDuration) {
        this.loanDuration = loanDuration;
    }

    public Integer getNumberOfMonths() {
        return numberOfMonths;
    }

    public void setNumberOfMonths(Integer numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
    }

    public BigDecimal getMonthlyRepayment() {
        return monthlyRepayment;
    }

    public void setMonthlyRepayment(BigDecimal monthlyRepayment) {
        this.monthlyRepayment = monthlyRepayment;
    }

    public BigDecimal getTotalInstalment() {
        return totalInstalment;
    }

    public void setTotalInstalment(BigDecimal totalInstalment) {
        this.totalInstalment = totalInstalment;
        // Auto-calculate remaining balance if amount paid exists
        if (this.amountPaid != null) {
            this.remainingBalance = totalInstalment.subtract(this.amountPaid);
        } else {
            this.remainingBalance = totalInstalment;
        }
    }

    public String getCustomerBvn() {
        return customerBvn;
    }

    public void setCustomerBvn(String customerBvn) {
        this.customerBvn = customerBvn;
    }

    public String getCustomerNin() {
        return customerNin;
    }

    public void setCustomerNin(String customerNin) {
        this.customerNin = customerNin;
    }

    public String getGuarantorContact() {
        return guarantorContact;
    }

    public void setGuarantorContact(String guarantorContact) {
        this.guarantorContact = guarantorContact;
    }

    public RepaymentStatus getRepaymentStatus() {
        return repaymentStatus;
    }

    public void setRepaymentStatus(RepaymentStatus repaymentStatus) {
        this.repaymentStatus = repaymentStatus;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
        // Auto-calculate remaining balance
        if (this.totalInstalment != null) {
            this.remainingBalance = this.totalInstalment.subtract(amountPaid);
        }
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
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
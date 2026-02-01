package com.koolboks.creditProject.entity.koolboks_loan;



import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "koolboks_loan_disbursement")
public class KoolboksLoanDisbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "agent_name")
    private String agentName;

    @Column(name = "agent_email", unique = true)
    private String agentEmail;

    @Column(name = "agent_id", unique = true)
    private String agentId;

    @Column(name = "agent_number", unique = true)
    private String agentNumber;

    @Column(name = "customer_loan_ref", unique = true)
    private String customerLoanRef;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "initial_instalment")
    private BigDecimal initialInstalment;




    @Column(name = "initial_payment_confirmation")
    private Boolean initialPaymentConfirmation;


    @Column(name = "customer_first_name")
    private String customerFirstName;

    @Column(name = "customer_last_name")
    private String customerLastname;

    @Column(name = "customer_loan_duration")
    private Integer customerLoanDuration;

    @Column(name = "customer_phone_number", unique = true)
    private String customerPhoneNumber;

    @Column(name = "guarantor_email")
    private String guarantorEmail;

    @Column(name = "customer_email", unique = true)
    private String customerEmail;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_size")
    private String productSize;

    @Column(name = "product_brand")
    private String productBrand;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public Boolean getInitialPaymentConfirmation() {
        return initialPaymentConfirmation;
    }

    public void setInitialPaymentConfirmation(Boolean initialPaymentConfirmation) {
        this.initialPaymentConfirmation = initialPaymentConfirmation;
    }


}

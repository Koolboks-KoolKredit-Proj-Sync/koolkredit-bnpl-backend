package com.koolboks.creditProject.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class GuarantorRequest {

    // ===================================
    // CUSTOMER INFORMATION
    // ===================================

    private String customerBvn;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String customerPlan;
    private String customerInstallmentDuration;
    private String customerPhoneNumber;

    // ===================================
    // PRODUCT INFORMATION
    // ===================================

    private String productName;
    private String productBrand;
    private String productSize;
    private BigDecimal storePrice;

    // ===================================
    // GUARANTOR INFORMATION
    // ===================================

    private String guarantorBvn;
    private String guarantorPhoneNumber;
    private String guarantorNin;
    private String guarantorEmail;

    // ===================================
    // GETTERS AND SETTERS
    // ===================================

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

    // ✅ ACCEPT BOTH "customerPhone" AND "customer_phone_number"
    @JsonProperty("customerPhone")
    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    @JsonProperty("customer_phone_number")
    public void setCustomerPhoneNumberSnakeCase(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    // ✅ ACCEPT BOTH "brand" AND "product_brand"
    @JsonProperty("brand")
    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    @JsonProperty("product_brand")
    public void setProductBrandSnakeCase(String productBrand) {
        this.productBrand = productBrand;
    }

    // ✅ ACCEPT BOTH "size" AND "product_size"
    @JsonProperty("size")
    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    @JsonProperty("product_size")
    public void setProductSizeSnakeCase(String productSize) {
        this.productSize = productSize;
    }

    // ✅ ACCEPT BOTH "price" AND "store_price"
    @JsonProperty("price")
    public BigDecimal getStorePrice() {
        return storePrice;
    }

    public void setStorePrice(BigDecimal storePrice) {
        this.storePrice = storePrice;
    }

    @JsonProperty("store_price")
    public void setStorePriceSnakeCase(BigDecimal storePrice) {
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

    @Override
    public String toString() {
        return "GuarantorRequest{" +
                "customerBvn='" + customerBvn + '\'' +
                ", customerFirstName='" + customerFirstName + '\'' +
                ", customerLastName='" + customerLastName + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                ", customerPhoneNumber='" + customerPhoneNumber + '\'' +
                ", customerPlan='" + customerPlan + '\'' +
                ", customerInstallmentDuration='" + customerInstallmentDuration + '\'' +
                ", productName='" + productName + '\'' +
                ", productBrand='" + productBrand + '\'' +
                ", productSize='" + productSize + '\'' +
                ", storePrice=" + storePrice +
                ", guarantorBvn='" + guarantorBvn + '\'' +
                ", guarantorPhoneNumber='" + guarantorPhoneNumber + '\'' +
                ", guarantorNin='" + guarantorNin + '\'' +
                ", guarantorEmail='" + guarantorEmail + '\'' +
                '}';
    }
}
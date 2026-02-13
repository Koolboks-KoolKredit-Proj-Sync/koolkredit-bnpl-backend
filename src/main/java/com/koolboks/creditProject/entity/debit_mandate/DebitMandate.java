//package com.koolboks.creditProject.entity.debit_mandate;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Column;
//
//import java.time.LocalDate;
//import java.util.UUID;
//
//
//
//@Entity
//public class DebitMandate {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "customer_id")
//    private String customer_id;
//
//
//
//    @Column(name="bvn")
//    private String bvn;
//
//    @Column(name = "customer_name")
//    private String customer_name;
//
//    @Column(name = "mandate_type")
//    private String mandate_type = "emandate";
//
//    @Column(name = "debit_type")
//    private String debit_type = "variable";
//
//    @Column(name = "amount")
//    private int amount;
//
//    @Column(name = "reference", unique = true, nullable = false)
//    private String reference = UUID.randomUUID().toString();
//
//    @Column(name = "fee_bearer")
//    private String fee_bearer = "customer";
//
//    @Column(name = "account_number")
//    private String account_number;
//
//    @Column(name = "bank_name")
//    private String bank_name;
//
//    @Column(name = "bank_code")
//    private String bank_code;
//
//    @Column(name = "nip_code")
//    private String nip_code;
//
//    @Column(name = "description")
//    private String description;
//
//    @Column(name = "start_date")
//    private LocalDate start_date;
//
//    @Column(name = "end_date")
//    private LocalDate end_date;
//
//
//
//
//    @Column(name = "customer_email")
//    private String customer_email;
//
//    @Column(name = "customer_phone")
//    private String customer_phone;
//
//
//    @Column(name = "verification_method")
//    private String verification_method = "selfie_verification";
//
//    @Column(name = "mandate_id")
//    private String mandate_id;
//
//    @Column(name = "debit_account")
//    private boolean debit_account = false;
//
//
//
//    @Column(name = "customer_address")
//    private String customer_address;
//
//    public DebitMandate() {
//        // required by JPA
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public String getCustomer_id() {
//        return customer_id;
//    }
//
//    public void setCustomer_id(String customer_id) {
//        this.customer_id = customer_id;
//    }
//
//    public String getCustomer_name() {
//        return customer_name;
//    }
//
//    public void setCustomer_name(String customer_name) {
//        this.customer_name = customer_name;
//    }
//
//    public String getMandate_type() {
//        return mandate_type;
//    }
//
//    public void setMandate_type(String mandate_type) {
//        this.mandate_type = mandate_type;
//    }
//
//    public String getDebit_type() {
//        return debit_type;
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
//    public void setDebit_type(String debit_type) {
//        this.debit_type = debit_type;
//    }
//
//    public int getAmount() {
//        return amount;
//    }
//
//    public void setAmount(int amount) {
//        this.amount = amount;
//    }
//
//    public String getReference() {
//        return reference;
//    }
//
//    public void setReference(String reference) {
//        this.reference = reference;
//    }
//
//    public String getFee_bearer() {
//        return fee_bearer;
//    }
//
//    public void setFee_bearer(String fee_bearer) {
//        this.fee_bearer = fee_bearer;
//    }
//
//    public String getAccount_number() {
//        return account_number;
//    }
//
//    public void setAccount_number(String account_number) {
//        this.account_number = account_number;
//    }
//
//    public String getBank_name() {
//        return bank_name;
//    }
//
//    public void setBank_name(String bank_name) {
//        this.bank_name = bank_name;
//    }
//
//    public String getBank_code() {
//        return bank_code;
//    }
//
//    public void setBank_code(String bank_code) {
//        this.bank_code = bank_code;
//    }
//
//    public String getNip_code() {
//        return nip_code;
//    }
//
//    public void setNip_code(String nip_code) {
//        this.nip_code = nip_code;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public LocalDate getStart_date() {
//        return start_date;
//    }
//
//    public void setStart_date(LocalDate start_date) {
//        this.start_date = start_date;
//    }
//
//    public LocalDate getEnd_date() {
//        return end_date;
//    }
//
//    public void setEnd_date(LocalDate end_date) {
//        this.end_date = end_date;
//    }
//
//    public String getVerification_method() {
//        return verification_method;
//    }
//
//    public void setVerification_method(String verification_method) {
//        this.verification_method = verification_method;
//    }
//
//    public String getMandate_id() {
//        return mandate_id;
//    }
//
//    public void setMandate_id(String mandate_id) {
//        this.mandate_id = mandate_id;
//    }
//
//    public boolean isDebit_account() {
//        return debit_account;
//    }
//
//    public void setDebit_account(boolean debit_account) {
//        this.debit_account = debit_account;
//    }
//
//
//    public String getCustomer_address() {
//        return customer_address;
//    }
//
//    public void setCustomer_address(String customer_address) {
//        this.customer_address = customer_address;
//    }
//
//
//    public String getCustomer_email() {
//        return customer_email;
//    }
//
//    public void setCustomer_email(String customer_email) {
//        this.customer_email = customer_email;
//    }
//
//    public String getCustomer_phone() {
//        return customer_phone;
//    }
//
//    public void setCustomer_phone(String customer_phone) {
//        this.customer_phone = customer_phone;
//    }
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
////Quick tip (very useful in APIs)
////
////If this entity is exposed via JSON (Spring Boot), use:
////
////@JsonFormat(pattern = "dd-MM-yyyy")
//
//
////on the LocalDate fields so clients can still send 20-12-2025.
//


package com.koolboks.creditProject.entity.debit_mandate;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;

import java.time.LocalDate;
import java.security.SecureRandom;

@Entity
public class DebitMandate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id")
    private String customer_id;

    @Column(name="bvn")
    private String bvn;

    @Column(name = "customer_name")
    private String customer_name;

    @Column(name = "mandate_type")
    private String mandate_type = "emandate";

    @Column(name = "debit_type")
    private String debit_type = "variable";

    @Column(name = "amount")
    private int amount;

    @Column(name = "reference", unique = true, nullable = false)
    private String reference;

    @Column(name = "fee_bearer")
    private String fee_bearer = "customer";

    @Column(name = "account_number")
    private String account_number;

    @Column(name = "bank_name")
    private String bank_name;

    @Column(name = "bank_code")
    private String bank_code;

    @Column(name = "nip_code")
    private String nip_code;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private LocalDate start_date;

    @Column(name = "end_date")
    private LocalDate end_date;


//    @Column(name = "start_date")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//    private LocalDate start_date;
//
//    @Column(name = "end_date")
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
//    private LocalDate end_date;

    @Column(name = "customer_email")
    private String customer_email;

    @Column(name = "customer_phone")
    private String customer_phone;

    @Column(name = "verification_method")
    private String verification_method = "selfie_verification";

    @Column(name = "mandate_id")
    private String mandate_id;

    @Column(name = "debit_account")
    private boolean debit_account = false;

    @Column(name = "customer_address")
    private String customer_address;

    public DebitMandate() {
        // Generate reference on creation if not set
    }

    @PrePersist
    public void generateReference() {
        if (this.reference == null || this.reference.isEmpty()) {
            this.reference = generateShortAlphanumericReference();
        }
    }

    /**
     * Generate a 20-character alphanumeric reference
     * Format: KK + timestamp(10 digits) + random(8 chars) = 20 chars total
     */
    private String generateShortAlphanumericReference() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();

        // Get timestamp (last 10 digits of current time millis)
        String timestamp = String.valueOf(System.currentTimeMillis());
        timestamp = timestamp.substring(timestamp.length() - 10);

        // Generate 8 random alphanumeric characters
        StringBuilder randomPart = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            randomPart.append(chars.charAt(random.nextInt(chars.length())));
        }

        // Combine: KK + timestamp + random = 2 + 10 + 8 = 20 characters
        return "KK" + timestamp + randomPart.toString();
    }

    // All getters and setters remain the same...

    public Long getId() {
        return id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getMandate_type() {
        return mandate_type;
    }

    public void setMandate_type(String mandate_type) {
        this.mandate_type = mandate_type;
    }

    public String getDebit_type() {
        return debit_type;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public void setDebit_type(String debit_type) {
        this.debit_type = debit_type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getFee_bearer() {
        return fee_bearer;
    }

    public void setFee_bearer(String fee_bearer) {
        this.fee_bearer = fee_bearer;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_code() {
        return bank_code;
    }

    public void setBank_code(String bank_code) {
        this.bank_code = bank_code;
    }

    public String getNip_code() {
        return nip_code;
    }

    public void setNip_code(String nip_code) {
        this.nip_code = nip_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    public String getVerification_method() {
        return verification_method;
    }

    public void setVerification_method(String verification_method) {
        this.verification_method = verification_method;
    }

    public String getMandate_id() {
        return mandate_id;
    }

    public void setMandate_id(String mandate_id) {
        this.mandate_id = mandate_id;
    }

    public boolean isDebit_account() {
        return debit_account;
    }

    public void setDebit_account(boolean debit_account) {
        this.debit_account = debit_account;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public void setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
    }
}
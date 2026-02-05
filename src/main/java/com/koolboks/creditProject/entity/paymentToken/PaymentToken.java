package com.koolboks.creditProject.entity.paymentToken;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payment_tokens")
public class PaymentToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String token;

    @Column(name = "loan_reference", nullable = false, length = 100)
    private String loanReference;

    @Column(name = "instalment_number", nullable = false)
    private Integer instalmentNumber;

    // UPDATED: Track original and current amounts
    @Column(name = "original_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal originalAmount;  // Base monthly repayment

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;  // Current amount (may include late fees)

    @Column(name = "late_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal lateFee = BigDecimal.ZERO;

    @Column(name = "has_late_fee", nullable = false)
    private Boolean hasLateFee = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    // Constructors
    public PaymentToken() {
        this.createdAt = LocalDateTime.now();
        this.isUsed = false;
        this.hasLateFee = false;
        this.lateFee = BigDecimal.ZERO;
    }

    public PaymentToken(String loanReference, Integer instalmentNumber, BigDecimal amount, LocalDateTime expiresAt) {
        this();
        this.token = generateToken();
        this.loanReference = loanReference;
        this.instalmentNumber = instalmentNumber;
        this.originalAmount = amount;
        this.amount = amount;
        this.expiresAt = expiresAt;
    }

    // Generate secure random token
    public static String generateToken() {
        return UUID.randomUUID().toString().replace("-", "") +
               UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    // Check if token is expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // Check if token is valid (not used and not expired)
    public boolean isValid() {
        return !isUsed && !isExpired();
    }

    /**
     * Apply 2.5% late fee
     */
    public void applyLateFee() {
        if (!hasLateFee) {
            // Calculate 2.5% late fee
            BigDecimal feeRate = new BigDecimal("0.025");
            this.lateFee = originalAmount.multiply(feeRate).setScale(2, RoundingMode.HALF_UP);
            this.amount = originalAmount.add(lateFee);
            this.hasLateFee = true;

            System.out.println("💰 Late fee applied: ₦" + lateFee);
            System.out.println("   Original: ₦" + originalAmount);
            System.out.println("   New total: ₦" + amount);
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLoanReference() {
        return loanReference;
    }

    public void setLoanReference(String loanReference) {
        this.loanReference = loanReference;
    }

    public Integer getInstalmentNumber() {
        return instalmentNumber;
    }

    public void setInstalmentNumber(Integer instalmentNumber) {
        this.instalmentNumber = instalmentNumber;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getLateFee() {
        return lateFee;
    }

    public void setLateFee(BigDecimal lateFee) {
        this.lateFee = lateFee;
    }

    public Boolean getHasLateFee() {
        return hasLateFee;
    }

    public void setHasLateFee(Boolean hasLateFee) {
        this.hasLateFee = hasLateFee;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Boolean getIsUsed() {
        return isUsed;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
}



















//package com.koolboks.creditProject.entity.paymentToken;
//
//import jakarta.persistence.*;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//@Entity
//@Table(name = "payment_tokens")
//public class PaymentToken {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false, length = 64)
//    private String token;
//
//    @Column(name = "loan_reference", nullable = false, length = 100)
//    private String loanReference;
//
//    @Column(name = "instalment_number", nullable = false)
//    private Integer instalmentNumber;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal amount;
//
//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "expires_at", nullable = false)
//    private LocalDateTime expiresAt;
//
//    @Column(name = "is_used", nullable = false)
//    private Boolean isUsed = false;
//
//    @Column(name = "used_at")
//    private LocalDateTime usedAt;
//
//    // Constructors
//    public PaymentToken() {
//        this.createdAt = LocalDateTime.now();
//        this.isUsed = false;
//    }
//
//    public PaymentToken(String loanReference, Integer instalmentNumber, BigDecimal amount, LocalDateTime expiresAt) {
//        this();
//        this.token = generateToken();
//        this.loanReference = loanReference;
//        this.instalmentNumber = instalmentNumber;
//        this.amount = amount;
//        this.expiresAt = expiresAt;
//    }
//
//    // Generate secure random token
//    public static String generateToken() {
//        return UUID.randomUUID().toString().replace("-", "") +
//               UUID.randomUUID().toString().replace("-", "").substring(0, 10);
//    }
//
//    // Check if token is expired
//    public boolean isExpired() {
//        return LocalDateTime.now().isAfter(expiresAt);
//    }
//
//    // Check if token is valid (not used and not expired)
//    public boolean isValid() {
//        return !isUsed && !isExpired();
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
//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//
//    public String getLoanReference() {
//        return loanReference;
//    }
//
//    public void setLoanReference(String loanReference) {
//        this.loanReference = loanReference;
//    }
//
//    public Integer getInstalmentNumber() {
//        return instalmentNumber;
//    }
//
//    public void setInstalmentNumber(Integer instalmentNumber) {
//        this.instalmentNumber = instalmentNumber;
//    }
//
//    public BigDecimal getAmount() {
//        return amount;
//    }
//
//    public void setAmount(BigDecimal amount) {
//        this.amount = amount;
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
//    public LocalDateTime getExpiresAt() {
//        return expiresAt;
//    }
//
//    public void setExpiresAt(LocalDateTime expiresAt) {
//        this.expiresAt = expiresAt;
//    }
//
//    public Boolean getIsUsed() {
//        return isUsed;
//    }
//
//    public void setIsUsed(Boolean isUsed) {
//        this.isUsed = isUsed;
//    }
//
//    public LocalDateTime getUsedAt() {
//        return usedAt;
//    }
//
//    public void setUsedAt(LocalDateTime usedAt) {
//        this.usedAt = usedAt;
//    }
//}

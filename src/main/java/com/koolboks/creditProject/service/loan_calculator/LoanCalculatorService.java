package com.koolboks.creditProject.service.loan_calculator;



import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class LoanCalculatorService {

    // Management fee rate (10%)
    private static final BigDecimal MANAGEMENT_FEE = new BigDecimal("0.10");

    // Insurance rate
    private static final BigDecimal INSURANCE_RATE = new BigDecimal("0.01363636");

    // Monthly interest rate (4%)
    private static final BigDecimal MONTHLY_INTEREST = new BigDecimal("0.04");

    public LoanCalculation calculate(BigDecimal storePrice, String plan, Integer duration) {
        LoanCalculation calc = new LoanCalculation();

        // Determine downpayment rate based on plan
        BigDecimal downpaymentRate = getDownpaymentRate(plan);

        // Calculate number of months and days
        Integer months = getMonths(plan, duration);
        Integer days = getDays(plan, duration, months);

        // 1. Total Outright Price: T = P(1 + m)
        BigDecimal outrightPrice = storePrice.multiply(BigDecimal.ONE.add(MANAGEMENT_FEE));

        // 2. Upfront Payment: Upfront = T(d + ins)
        BigDecimal upfront = outrightPrice.multiply(downpaymentRate.add(INSURANCE_RATE));

        // 3. Loan Amount: L = T(1 - d)
        BigDecimal loanAmount = outrightPrice.multiply(BigDecimal.ONE.subtract(downpaymentRate));

        // 4. Total Installment Interest: I = L(r*n)
        BigDecimal totalInterest = loanAmount.multiply(MONTHLY_INTEREST.multiply(new BigDecimal(months)));

        // 5. Unlock Price: Upfront + L(1 + r*n)
        BigDecimal instalmentTotal = loanAmount.add(totalInterest);
        BigDecimal unlockPrice = upfront.add(instalmentTotal);

        // 6. Daily Payment (for Omolope plan): L(1 + r*n) / D
        BigDecimal dailyPayment = BigDecimal.ZERO;
        if ("Omolope".equals(plan) && days > 0) {
            dailyPayment = instalmentTotal.divide(new BigDecimal(days), 2, RoundingMode.HALF_UP);
        }

        // Monthly Payment (for Easy plans)
        BigDecimal monthlyPayment = BigDecimal.ZERO;
        if (months > 0 && !"Omolope".equals(plan)) {
            monthlyPayment = instalmentTotal.divide(new BigDecimal(months), 2, RoundingMode.HALF_UP);
        }

        // Set all calculated values
        calc.setStorePrice(storePrice);
        calc.setOutrightPrice(outrightPrice.setScale(2, RoundingMode.HALF_UP));
        calc.setUpfront(upfront.setScale(2, RoundingMode.HALF_UP));
        calc.setLoanAmount(loanAmount.setScale(2, RoundingMode.HALF_UP));
        calc.setTotalInterest(totalInterest.setScale(2, RoundingMode.HALF_UP));
        calc.setInstalmentTotal(instalmentTotal.setScale(2, RoundingMode.HALF_UP));
        calc.setUnlockPrice(unlockPrice.setScale(2, RoundingMode.HALF_UP));
        calc.setDailyPayment(dailyPayment);
        calc.setMonthlyPayment(monthlyPayment);
        calc.setDownpaymentRate(downpaymentRate);
        calc.setMonths(months);
        calc.setDays(days);
        calc.setPlan(plan);

        return calc;
    }

    private BigDecimal getDownpaymentRate(String plan) {
        switch (plan) {
            case "Easy 35":
                return new BigDecimal("0.35");
            case "Easy 25":
                return new BigDecimal("0.25");
            case "Omolope":
                return new BigDecimal("0.25");
            case "OutrightFlex":
                return BigDecimal.ZERO;
            default:
                return new BigDecimal("0.25");
        }
    }

    private Integer getMonths(String plan, Integer duration) {
        if ("Omolope".equals(plan)) {
            // Omolope uses days, so calculate months for interest
            return duration / 30;
        }
        return duration != null ? duration : 0;
    }

    private Integer getDays(String plan, Integer duration, Integer months) {
        if ("Omolope".equals(plan)) {
            return duration != null ? duration : 0;
        }
        // For other plans, calculate total days (30 or 31 days per month)
        // Using 30 days as standard
        return months * 30;
    }

    // Inner class for calculation results
    public static class LoanCalculation {
        private BigDecimal storePrice;
        private BigDecimal outrightPrice;
        private BigDecimal upfront;
        private BigDecimal loanAmount;
        private BigDecimal totalInterest;
        private BigDecimal instalmentTotal;
        private BigDecimal unlockPrice;
        private BigDecimal dailyPayment;
        private BigDecimal monthlyPayment;
        private BigDecimal downpaymentRate;
        private Integer months;
        private Integer days;
        private String plan;

        // Getters and Setters
        public BigDecimal getStorePrice() { return storePrice; }
        public void setStorePrice(BigDecimal storePrice) { this.storePrice = storePrice; }

        public BigDecimal getOutrightPrice() { return outrightPrice; }
        public void setOutrightPrice(BigDecimal outrightPrice) { this.outrightPrice = outrightPrice; }

        public BigDecimal getUpfront() { return upfront; }
        public void setUpfront(BigDecimal upfront) { this.upfront = upfront; }

        public BigDecimal getLoanAmount() { return loanAmount; }
        public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }

        public BigDecimal getTotalInterest() { return totalInterest; }
        public void setTotalInterest(BigDecimal totalInterest) { this.totalInterest = totalInterest; }

        public BigDecimal getInstalmentTotal() { return instalmentTotal; }
        public void setInstalmentTotal(BigDecimal instalmentTotal) { this.instalmentTotal = instalmentTotal; }

        public BigDecimal getUnlockPrice() { return unlockPrice; }
        public void setUnlockPrice(BigDecimal unlockPrice) { this.unlockPrice = unlockPrice; }

        public BigDecimal getDailyPayment() { return dailyPayment; }
        public void setDailyPayment(BigDecimal dailyPayment) { this.dailyPayment = dailyPayment; }

        public BigDecimal getMonthlyPayment() { return monthlyPayment; }
        public void setMonthlyPayment(BigDecimal monthlyPayment) { this.monthlyPayment = monthlyPayment; }

        public BigDecimal getDownpaymentRate() { return downpaymentRate; }
        public void setDownpaymentRate(BigDecimal downpaymentRate) { this.downpaymentRate = downpaymentRate; }

        public Integer getMonths() { return months; }
        public void setMonths(Integer months) { this.months = months; }

        public Integer getDays() { return days; }
        public void setDays(Integer days) { this.days = days; }

        public String getPlan() { return plan; }
        public void setPlan(String plan) { this.plan = plan; }
    }
}
package com.koolboks.creditProject.scheduler;


import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.entity.paymentToken.PaymentToken;
import com.koolboks.creditProject.repository.LoanRepaymentRepository;
import com.koolboks.creditProject.repository.PaymentTokenRepository;
import com.koolboks.creditProject.service.loan.PaymentReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class PaymentReminderScheduler {

    @Autowired
    private LoanRepaymentRepository loanRepaymentRepository;

    @Autowired
    private PaymentTokenRepository paymentTokenRepository;

    @Autowired
    private PaymentReminderService paymentReminderService;

    /**
     * Run daily at 9:00 AM to:
     * 1. Check for expired tokens
     * 2. Apply late fees (2.5%)
     * 3. Send new payment reminders
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void checkAndSendPaymentReminders() {
        System.out.println("=== CHECKING FOR PAYMENT REMINDERS ===");
        System.out.println("Current date: " + LocalDate.now());

        // Get all ACTIVE loans
        List<LoanRepayment> activeLoans = loanRepaymentRepository.findByRepaymentStatus(
            LoanRepayment.RepaymentStatus.ACTIVE
        );

        System.out.println("Active loans found: " + activeLoans.size());

        for (LoanRepayment loan : activeLoans) {
            try {
                // Calculate which instalment is next based on amount paid
                BigDecimal paidInstalments = loan.getAmountPaid()
                    .divide(loan.getMonthlyRepayment(), BigDecimal.ROUND_DOWN);

                int nextInstalmentNumber = paidInstalments.intValue() + 1;

                // Check if all instalments are paid
                if (nextInstalmentNumber > loan.getNumberOfMonths()) {
                    System.out.println("Loan " + loan.getLoanReference() + " is fully paid");
                    continue;
                }

                // Check if there's an existing token for this instalment
                Optional<PaymentToken> existingToken = paymentTokenRepository
                    .findByLoanReferenceAndInstalmentNumber(
                        loan.getLoanReference(),
                        nextInstalmentNumber
                    );

                if (existingToken.isPresent()) {
                    PaymentToken token = existingToken.get();

                    // Check if token is expired and not used
                    if (token.isExpired() && !token.getIsUsed()) {
                        System.out.println("⚠️ Token expired for " + loan.getLoanReference() + " - Instalment " + nextInstalmentNumber);

                        // Apply 2.5% late fee if not already applied
                        if (!token.getHasLateFee()) {
                            token.applyLateFee();
                            paymentTokenRepository.save(token);

                            // Update remaining balance in LoanRepayment
                            BigDecimal newBalance = loan.getRemainingBalance().add(token.getLateFee());
                            loan.setRemainingBalance(newBalance);
                            loanRepaymentRepository.save(loan);

                            System.out.println("💰 Late fee of ₦" + token.getLateFee() + " applied");
                            System.out.println("   New remaining balance: ₦" + newBalance);
                        }

                        // Send new reminder with late fee
                        LocalDate nextDueDate = LocalDate.now().plusDays(7);
                        paymentReminderService.sendPaymentReminders(loan, nextInstalmentNumber, nextDueDate);
                    } else if (!token.getIsUsed() && !token.isExpired()) {
                        // Token is still valid, check if we need to send reminder (7 days before due)
                        LocalDate reminderDate = token.getExpiresAt().toLocalDate().minusDays(7);
                        if (LocalDate.now().equals(reminderDate)) {
                            System.out.println("Sending scheduled reminder for instalment " + nextInstalmentNumber);
                            LocalDate dueDate = token.getExpiresAt().toLocalDate();
                            paymentReminderService.sendPaymentReminders(loan, nextInstalmentNumber, dueDate);
                        }
                    }
                } else {
                    // No token exists - create first token
                    System.out.println("Creating first payment reminder for loan: " + loan.getLoanReference());
                    LocalDate nextDueDate = LocalDate.now().plusDays(7);
                    paymentReminderService.sendPaymentReminders(loan, nextInstalmentNumber, nextDueDate);
                }

            } catch (Exception e) {
                System.err.println("Error processing reminder for loan: " + loan.getLoanReference());
                e.printStackTrace();
            }
        }

        System.out.println("=== PAYMENT REMINDER CHECK COMPLETE ===");
    }
}









//import com.koolboks.creditProject.entity.LoanRepayment;
//import com.koolboks.creditProject.repository.LoanRepaymentRepository;
//import com.koolboks.creditProject.service.loan.PaymentReminderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//
//@Component
//public class PaymentReminderScheduler {
//
//    @Autowired
//    private LoanRepaymentRepository loanRepaymentRepository;
//
//    @Autowired
//    private PaymentReminderService paymentReminderService;
//
//    /**
//     * Run daily at 9:00 AM to check for payment reminders
//     */
//    //@Scheduled(cron = "0 0 9 * * *")
//    @Scheduled(fixedRate = 60000) // every 60 seconds
//
//    public void checkAndSendPaymentReminders() {
//        System.out.println("=== CHECKING FOR PAYMENT REMINDERS ===");
//        System.out.println("Current date: " + LocalDate.now());
//
//        // Get all ACTIVE loans
//        List<LoanRepayment> activeLoans = loanRepaymentRepository.findByRepaymentStatus(
//            LoanRepayment.RepaymentStatus.ACTIVE
//        );
//
//        System.out.println("Active loans found: " + activeLoans.size());
//
//        for (LoanRepayment loan : activeLoans) {
//            try {
//                // Calculate which instalment is next based on amount paid
//                BigDecimal paidInstalments = loan.getAmountPaid()
//                    .divide(loan.getMonthlyRepayment(), BigDecimal.ROUND_DOWN);
//
//                int nextInstalmentNumber = paidInstalments.intValue() + 1;
//
//                // Check if all instalments are paid
//                if (nextInstalmentNumber > loan.getNumberOfMonths()) {
//                    System.out.println("Loan " + loan.getLoanReference() + " is fully paid");
//                    continue;
//                }
//
//                // Calculate next due date
//                // Assuming 30-day intervals from installation
//                // In production, you should track actual payment dates
//                LocalDate today = LocalDate.now();
//                LocalDate nextDueDate = today.plusDays(7); // 7 days from now
//
//                // Send reminder
//                System.out.println("Sending reminder for loan: " + loan.getLoanReference() +
//                                 ", instalment: " + nextInstalmentNumber);
//
//                paymentReminderService.sendPaymentReminders(loan, nextInstalmentNumber, nextDueDate);
//
//            } catch (Exception e) {
//                System.err.println("Error processing reminder for loan: " + loan.getLoanReference());
//                e.printStackTrace();
//            }
//        }
//
//        System.out.println("=== PAYMENT REMINDER CHECK COMPLETE ===");
//    }
//}

package com.koolboks.creditProject.repository;

import com.koolboks.creditProject.entity.koolbuy_loan.KoolbuyLoanDisbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KoolbuyLoanDisbursementRepository extends JpaRepository<KoolbuyLoanDisbursement, Long> {
    Optional<KoolbuyLoanDisbursement> findByCustomerLoanRef(String customerLoanRef);
    List<KoolbuyLoanDisbursement> findByAgentId(String agentId);
    List<KoolbuyLoanDisbursement> findByStorePaymentConfirmation(Boolean storePaymentConfirmation);
    List<KoolbuyLoanDisbursement> findByCustomerFirstNameAndCustomerLastname(String firstName, String lastName);
    List<KoolbuyLoanDisbursement> findByCustomerEmail(String customerEmail);
    long countByStorePaymentConfirmation(Boolean storePaymentConfirmation);
}
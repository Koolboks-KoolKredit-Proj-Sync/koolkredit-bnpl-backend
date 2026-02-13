package com.koolboks.creditProject.repository;

import com.koolboks.creditProject.entity.koolboks_loan.KoolboksLoanDisbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KoolboksLoanDisbursementRepository extends JpaRepository<KoolboksLoanDisbursement, Long> {

    Optional<KoolboksLoanDisbursement> findByCustomerLoanRef(String customerLoanRef);

    List<KoolboksLoanDisbursement> findByAgentId(String agentId);

    List<KoolboksLoanDisbursement> findByInitialPaymentConfirmation(Boolean initialPaymentConfirmation);

    List<KoolboksLoanDisbursement> findByCustomerFirstNameAndCustomerLastname(String firstName, String lastName);

    List<KoolboksLoanDisbursement> findByCustomerEmail(String customerEmail);

    long countByInitialPaymentConfirmation(Boolean initialPaymentConfirmation);
}
package com.koolboks.creditProject.repository;

import com.koolboks.creditProject.entity.paymentToken.PaymentToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTokenRepository extends JpaRepository<PaymentToken, Long> {

    Optional<PaymentToken> findByToken(String token);

    Optional<PaymentToken> findByLoanReferenceAndInstalmentNumber(String loanReference, Integer instalmentNumber);
}

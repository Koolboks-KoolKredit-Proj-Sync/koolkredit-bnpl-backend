package com.koolboks.creditProject.repository;



import com.koolboks.creditProject.entity.LoanRepayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepaymentRepository extends JpaRepository<LoanRepayment, Long> {

    Optional<LoanRepayment> findByLoanReference(String loanReference);

    Optional<LoanRepayment> findByCustomerBvn(String customerBvn);

    Optional<LoanRepayment> findByEmail(String email);



    // ✅ ADD THIS
    List<LoanRepayment> findByRepaymentStatus(
            LoanRepayment.RepaymentStatus repaymentStatus
    );
}
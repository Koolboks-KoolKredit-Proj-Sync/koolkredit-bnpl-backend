package com.koolboks.creditProject.repository;

import com.koolboks.creditProject.entity.Guarantor;
import com.koolboks.creditProject.entity.koolbuy_loan.KoolbuyLoanDisbursement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuarantorRepository extends JpaRepository<Guarantor, Long> {

    boolean existsByGuarantorBvnAndActiveTrue(String guarantorBvn);

    boolean existsByGuarantorPhoneNumberAndActiveTrue(String guarantorPhoneNumber);

    //Optional<Guarantor> findByCustomerBvnAndActiveTrue(String customerBvn);

    Optional<Guarantor> findTopByCustomerBvnAndActiveTrueOrderByCreatedAtDesc(
        String customerBvn
);


    Optional<Guarantor> findByGuarantorBvn(String guarantorBvn);

    Optional<Guarantor> findByGuarantorToken(String guarantorToken);

    Optional<Guarantor> findByApplicationReference(String applicationReference);

    Optional<Guarantor> findByCustomerEmail(String customerEmail);


}















////package com.koolboks.creditProject.repository;
////
////import com.koolboks.creditProject.entity.Guarantor;
////import org.springframework.data.jpa.repository.JpaRepository;
////
////public interface GuarantorRepository extends JpaRepository<Guarantor, Long> {
////
////    boolean existsByGuarantorBvnAndActiveTrue(String guarantorBvn);
////
////    boolean existsByGuarantorPhoneNumberAndActiveTrue(String guarantorPhoneNumber);
////}
//
//
//package com.koolboks.creditProject.repository;
//
//import com.koolboks.creditProject.entity.Guarantor;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface GuarantorRepository extends JpaRepository<Guarantor, Long> {
//
//    boolean existsByGuarantorBvnAndActiveTrue(String guarantorBvn);
//
//    boolean existsByGuarantorPhoneNumberAndActiveTrue(String guarantorPhoneNumber);
//
//    Optional<Guarantor> findByCustomerBvnAndActiveTrue(String customerBvn);
//
//    Optional<Guarantor> findByGuarantorBvn(String guarantorBvn);
//}
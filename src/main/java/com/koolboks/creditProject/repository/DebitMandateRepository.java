//package com.koolboks.creditProject.repository;
//
//import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface DebitMandateRepository extends JpaRepository<DebitMandate, Long> {
//
//    /**
//     * Find DebitMandate by reference (UUID)
//     */
//    Optional<DebitMandate> findByReference(String reference);
//
//    /**
//     * Find DebitMandate by account number
//     * Using native query because the field name contains underscore
//     */
//    @Query(value = "SELECT * FROM debit_mandate WHERE account_number = :accountNumber", nativeQuery = true)
//    Optional<DebitMandate> findByAccountNumber(@Param("accountNumber") String accountNumber);
//}




//package com.koolboks.creditProject.repository;
//
//import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface DebitMandateRepository extends JpaRepository<DebitMandate, Long> {
//
//    /**
//     * Find DebitMandate by reference (UUID)
//     */
//    Optional<DebitMandate> findByReference(String reference);
//
//    /**
//     * Find DebitMandate by account number
//     * Using native query because the field name contains underscore
//     */
//    @Query(value = "SELECT * FROM debit_mandate WHERE account_number = :accountNumber", nativeQuery = true)
//    Optional<DebitMandate> findByAccountNumber(@Param("accountNumber") String accountNumber);
//
//    /**
//     * Find DebitMandate by BVN
//     */
//    Optional<DebitMandate> findByBvn(String bvn);
//}






//package com.koolboks.creditProject.repository;
//
//import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface DebitMandateRepository extends JpaRepository<DebitMandate, Long> {
//
//    /**
//     * Find DebitMandate by reference (UUID)
//     */
//    Optional<DebitMandate> findByReference(String reference);
//
//    /**
//     * Find DebitMandate by account number
//     */
//    @Query(value = "SELECT * FROM debit_mandate WHERE account_number = :accountNumber", nativeQuery = true)
//    Optional<DebitMandate> findByAccountNumber(@Param("accountNumber") String accountNumber);
//
//    /**
//     * Find DebitMandate by BVN
//     */
//    Optional<DebitMandate> findByBvn(String bvn);
//
//    /**
//     * Find all DebitMandates with missing bank_code but have nip_code
//     */
//    @Query(value = "SELECT * FROM debit_mandate WHERE nip_code IS NOT NULL AND (bank_code IS NULL OR bank_code = '')", nativeQuery = true)
//    List<DebitMandate> findAllWithMissingBankCode();
//}






package com.koolboks.creditProject.repository;

import com.koolboks.creditProject.entity.LoanRepayment;
import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebitMandateRepository extends JpaRepository<DebitMandate, Long> {

    /**
     * Find DebitMandate by reference (UUID)
     */
    Optional<DebitMandate> findByReference(String reference);

    /**
     * Find DebitMandate by account number
     */
    @Query(value = "SELECT * FROM debit_mandate WHERE account_number = :accountNumber", nativeQuery = true)
    Optional<DebitMandate> findByAccountNumber(@Param("accountNumber") String accountNumber);

    /**
     * Find DebitMandate by BVN
     */
    Optional<DebitMandate> findByBvn(String bvn);

    /**
     * Find DebitMandate by customer email
     */
    @Query(value = "SELECT * FROM debit_mandate WHERE customer_email = :email", nativeQuery = true)
    Optional<DebitMandate> findByCustomerEmail(@Param("email") String email);

    /**
     * Find DebitMandate by customer phone
     */
    @Query(value = "SELECT * FROM debit_mandate WHERE customer_phone = :phone", nativeQuery = true)
    Optional<DebitMandate> findByCustomerPhone(@Param("phone") String phone);

    /**
     * Find all DebitMandates with missing bank_code but have nip_code
     */
    @Query(value = "SELECT * FROM debit_mandate WHERE nip_code IS NOT NULL AND (bank_code IS NULL OR bank_code = '')", nativeQuery = true)
    List<DebitMandate> findAllWithMissingBankCode();


    @Query("SELECT d FROM DebitMandate d WHERE d.mandate_id = :mandateId")
    Optional<DebitMandate> findByMandateId(@Param("mandateId") String mandateId);


}
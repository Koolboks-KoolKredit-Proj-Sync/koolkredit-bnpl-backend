package com.koolboks.creditProject.repository;



import com.koolboks.creditProject.entity.AgentEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentEntryRepository extends JpaRepository<AgentEntry, Long> {

    // Find by BVN
//    Optional<AgentEntry> findByBvn(String bvn);
    Optional<AgentEntry> findTopByBvnOrderByCreatedAtDesc(String bvn);


    // Find by NIN
    Optional<AgentEntry> findByNin(String nin);

    // Find by verification status
    List<AgentEntry> findByIsVerified(Boolean isVerified);

    // Find by mobile number
    Optional<AgentEntry> findByMobileNumber(String mobileNumber);

    // Check if BVN exists
    boolean existsByBvn(String bvn);

    // Check if NIN exists
    boolean existsByNin(String nin);
}
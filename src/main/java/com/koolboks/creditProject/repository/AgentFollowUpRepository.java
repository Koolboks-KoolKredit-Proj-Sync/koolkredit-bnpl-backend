package com.koolboks.creditProject.repository;

import com.koolboks.creditProject.entity.AgentFollowUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentFollowUpRepository extends JpaRepository<AgentFollowUp, Long> {
    //Optional<AgentFollowUp> findByBvn(String bvn);

    // Add this method to find the most recent follow-up by BVN
    Optional<AgentFollowUp> findTopByBvnOrderByCreatedAtDesc(String bvn);


    Optional<AgentFollowUp> findByGuarantorEmail(String guarantorEmail);
    Optional<AgentFollowUp> findByMobileNumber(String mobileNumber);

    Optional<AgentFollowUp> findByReviewToken(String reviewToken);
    
}







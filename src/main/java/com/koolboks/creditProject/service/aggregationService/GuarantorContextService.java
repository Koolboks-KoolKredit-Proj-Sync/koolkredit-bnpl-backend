package com.koolboks.creditProject.service.aggregationService;

import com.koolboks.creditProject.dto.guarantorAggregation.GuarantorContextResponse;
import com.koolboks.creditProject.repository.AgentEntryRepository;
import com.koolboks.creditProject.repository.AgentFollowUpRepository;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
public class GuarantorContextService {

    private final AgentEntryRepository agentEntryRepository;
    private final AgentFollowUpRepository followUpRepository;

    public GuarantorContextService(
            AgentEntryRepository agentEntryRepository,
            AgentFollowUpRepository followUpRepository) {
        this.agentEntryRepository = agentEntryRepository;
        this.followUpRepository = followUpRepository;
    }

    @Transactional(readOnly = true)
    public GuarantorContextResponse getContextByBvn(String bvn) {

//        var entry = agentEntryRepository.findByBvn(bvn)
//                .orElseThrow(() -> new RuntimeException("Agent entry not found"));
//
//        var followUp = followUpRepository.findByBvn(bvn)
//                .orElse(null); // follow-up may not exist yet

        var entry = agentEntryRepository
        .findTopByBvnOrderByCreatedAtDesc(bvn)
        .orElseThrow(() -> new RuntimeException("Agent entry not found"));

        var followUp = followUpRepository
                .findTopByBvnOrderByCreatedAtDesc(bvn)
                .orElse(null);


        GuarantorContextResponse dto = new GuarantorContextResponse();

        // Customer
        dto.setFirstName(entry.getFirstName());
        dto.setLastName(entry.getLastName());
        dto.setBvn(entry.getBvn());
        dto.setMobileNumber(entry.getMobileNumber());
        dto.setCustomerEmail(followUp != null ? followUp.getCustomerEmail() : null);

        // Product
        dto.setProductName(entry.getProductName());
        dto.setBrand(entry.getBrand());
        dto.setSize(entry.getSize());
        //dto.setPrice(entry.getPrice());
        dto.setPrice(entry.getTotalPrice());

        // Plan
        dto.setPlan(entry.getPlan());
        dto.setInstallmentDuration(entry != null ? entry.getInstallmentDuration() : followUp.getInstallmentOption());
        //dto.setInstallmentDuration(followUp.getInstallmentOption());

        // Follow-up (optional)
        if (followUp != null) {
            dto.setClassification(followUp.getCreditStatus());
            dto.setDti(followUp.getDti());
        }

        return dto;
    }
}



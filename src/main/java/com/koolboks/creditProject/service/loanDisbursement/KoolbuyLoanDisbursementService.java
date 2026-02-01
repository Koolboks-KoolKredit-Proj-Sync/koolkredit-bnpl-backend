package com.koolboks.creditProject.service.loanDisbursement;



import com.koolboks.creditProject.entity.koolbuy_loan.KoolbuyLoanDisbursement;
import com.koolboks.creditProject.repository.KoolbuyLoanDisbursementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class KoolbuyLoanDisbursementService {

    @Autowired
    private KoolbuyLoanDisbursementRepository disbursementRepository;

    /**
     * Get all disbursements with pagination
     */
    public Page<KoolbuyLoanDisbursement> getAllDisbursements(Pageable pageable) {
        return disbursementRepository.findAll(pageable);
    }

    /**
     * Get all disbursements without pagination
     */
    public List<KoolbuyLoanDisbursement> getAllDisbursements() {
        return disbursementRepository.findAll();
    }

    /**
     * Get disbursement by ID
     */
    public Optional<KoolbuyLoanDisbursement> getDisbursementById(Long id) {
        return disbursementRepository.findById(id);
    }

    /**
     * Get disbursement by customer loan reference
     */
    public Optional<KoolbuyLoanDisbursement> getDisbursementByLoanRef(String loanRef) {
        return disbursementRepository.findByCustomerLoanRef(loanRef);
    }

    /**
     * Get all disbursements by agent ID
     */
    public List<KoolbuyLoanDisbursement> getDisbursementsByAgentId(String agentId) {
        return disbursementRepository.findByAgentId(agentId);
    }

    /**
     * Get all disbursements by store payment confirmation status
     */
    public List<KoolbuyLoanDisbursement> getDisbursementsByConfirmationStatus(Boolean status) {
        return disbursementRepository.findByStorePaymentConfirmation(status);
    }

    /**
     * Get all confirmed disbursements
     */
    public List<KoolbuyLoanDisbursement> getConfirmedDisbursements() {
        return disbursementRepository.findByStorePaymentConfirmation(true);
    }

    /**
     * Get all pending disbursements
     */
    public List<KoolbuyLoanDisbursement> getPendingDisbursements() {
        return disbursementRepository.findByStorePaymentConfirmation(false);
    }

    /**
     * Create new disbursement
     */
    @Transactional
    public KoolbuyLoanDisbursement createDisbursement(KoolbuyLoanDisbursement disbursement) {
        return disbursementRepository.save(disbursement);
    }

    /**
     * Update existing disbursement
     */
    @Transactional
    public KoolbuyLoanDisbursement updateDisbursement(Long id, KoolbuyLoanDisbursement disbursementDetails)
            throws Exception {
        KoolbuyLoanDisbursement disbursement = disbursementRepository.findById(id)
                .orElseThrow(() -> new Exception("Disbursement not found with ID: " + id));

        // Update fields
        if (disbursementDetails.getAgentName() != null) {
            disbursement.setAgentName(disbursementDetails.getAgentName());
        }
        if (disbursementDetails.getAgentEmail() != null) {
            disbursement.setAgentEmail(disbursementDetails.getAgentEmail());
        }
        if (disbursementDetails.getAgentId() != null) {
            disbursement.setAgentId(disbursementDetails.getAgentId());
        }
        if (disbursementDetails.getAgentNumber() != null) {
            disbursement.setAgentNumber(disbursementDetails.getAgentNumber());
        }
        if (disbursementDetails.getStoreName() != null) {
            disbursement.setStoreName(disbursementDetails.getStoreName());
        }
        if (disbursementDetails.getStoreLocation() != null) {
            disbursement.setStoreLocation(disbursementDetails.getStoreLocation());
        }
        if (disbursementDetails.getStorePaymentConfirmation() != null) {
            disbursement.setStorePaymentConfirmation(disbursementDetails.getStorePaymentConfirmation());
        }
        if (disbursementDetails.getInitialInstalment() != null) {
            disbursement.setInitialInstalment(disbursementDetails.getInitialInstalment());
        }
        if (disbursementDetails.getPaymentDate() != null) {
            disbursement.setPaymentDate(disbursementDetails.getPaymentDate());
        }

        return disbursementRepository.save(disbursement);
    }

    /**
     * Delete disbursement by ID
     */
    @Transactional
    public void deleteDisbursement(Long id) throws Exception {
        KoolbuyLoanDisbursement disbursement = disbursementRepository.findById(id)
                .orElseThrow(() -> new Exception("Disbursement not found with ID: " + id));
        disbursementRepository.delete(disbursement);
    }

    /**
     * Search disbursements by customer name
     */
    public List<KoolbuyLoanDisbursement> searchByCustomerName(String firstName, String lastName) {
        return disbursementRepository.findByCustomerFirstNameAndCustomerLastname(firstName, lastName);
    }

    /**
     * Search disbursements by customer email
     */
    public List<KoolbuyLoanDisbursement> searchByCustomerEmail(String email) {
        return disbursementRepository.findByCustomerEmail(email);
    }

    /**
     * Count total disbursements
     */
    public long countAllDisbursements() {
        return disbursementRepository.count();
    }

    /**
     * Count confirmed disbursements
     */
    public long countConfirmedDisbursements() {
        return disbursementRepository.countByStorePaymentConfirmation(true);
    }

    /**
     * Count pending disbursements
     */
    public long countPendingDisbursements() {
        return disbursementRepository.countByStorePaymentConfirmation(false);
    }
}

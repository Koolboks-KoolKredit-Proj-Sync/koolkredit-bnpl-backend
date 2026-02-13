
//import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
//import com.koolboks.creditProject.repository.DebitMandateRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class DebitMandateService {
//
//    @Autowired
//    private DebitMandateRepository debitMandateRepository;
//
//    /**
//     * Create or update a DebitMandate with account verification data
//     * This allows partial updates - other fields can be added later from other forms
//     */
//    public DebitMandate saveAccountVerification(AccountVerificationDTO dto) {
//        DebitMandate debitMandate = new DebitMandate();
//
//        // Set the account verification fields
//        debitMandate.setAccount_number(dto.getAccountNumber());
//        debitMandate.setBank_name(dto.getBankName());
//        debitMandate.setNip_code(dto.getNipCode());
//        debitMandate.setCustomer_name(dto.getCustomerName());
//
//        // Save and return
//        return debitMandateRepository.save(debitMandate);
//    }
//
//    /**
//     * Update an existing DebitMandate with account verification data
//     */
//    public DebitMandate updateAccountVerification(Long id, AccountVerificationDTO dto) {
//        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);
//
//        if (existingMandate.isPresent()) {
//            DebitMandate debitMandate = existingMandate.get();
//
//            // Update the account verification fields
//            debitMandate.setAccount_number(dto.getAccountNumber());
//            debitMandate.setBank_name(dto.getBankName());
//            debitMandate.setNip_code(dto.getNipCode());
//            debitMandate.setCustomer_name(dto.getCustomerName());
//
//            return debitMandateRepository.save(debitMandate);
//        } else {
//            throw new RuntimeException("DebitMandate not found with id: " + id);
//        }
//    }
//
//    /**
//     * Get a DebitMandate by ID
//     */
//    public Optional<DebitMandate> getDebitMandateById(Long id) {
//        return debitMandateRepository.findById(id);
//    }
//
//    /**
//     * Get a DebitMandate by reference (UUID)
//     */
//    public Optional<DebitMandate> getDebitMandateByReference(String reference) {
//        return debitMandateRepository.findByReference(reference);
//    }
//}







//package com.koolboks.creditProject.service.debitMandate;
//
//import com.koolboks.creditProject.dto.accountVerification.AccountVerificationDTO;
//import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
//import com.koolboks.creditProject.repository.DebitMandateRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class DebitMandateService {
//
//    @Autowired
//    private DebitMandateRepository debitMandateRepository;
//
//    /**
//     * Create or update a DebitMandate with account verification data
//     * This allows partial updates - other fields can be added later from other forms
//     */
//    public DebitMandate saveAccountVerification(AccountVerificationDTO dto) {
//        DebitMandate debitMandate = new DebitMandate();
//
//        // Set the account verification fields
//        debitMandate.setAccount_number(dto.getAccountNumber());
//        debitMandate.setBank_name(dto.getBankName());
//        debitMandate.setNip_code(dto.getNipCode());
//        debitMandate.setCustomer_name(dto.getCustomerName());
//
//        // Save and return
//        return debitMandateRepository.save(debitMandate);
//    }
//
//    /**
//     * Update an existing DebitMandate with account verification data
//     */
//    public DebitMandate updateAccountVerification(Long id, AccountVerificationDTO dto) {
//        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);
//
//        if (existingMandate.isPresent()) {
//            DebitMandate debitMandate = existingMandate.get();
//
//            // Update the account verification fields
//            debitMandate.setAccount_number(dto.getAccountNumber());
//            debitMandate.setBank_name(dto.getBankName());
//            debitMandate.setNip_code(dto.getNipCode());
//            debitMandate.setCustomer_name(dto.getCustomerName());
//
//            return debitMandateRepository.save(debitMandate);
//        } else {
//            throw new RuntimeException("DebitMandate not found with id: " + id);
//        }
//    }
//
//    /**
//     * Update DebitMandate with additional customer details from follow-up form
//     */
//    public DebitMandate updateCustomerDetails(Long id, String bvn, String customerAddress,
//                                               String customerEmail, String customerPhone) {
//        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);
//
//        if (existingMandate.isPresent()) {
//            DebitMandate debitMandate = existingMandate.get();
//
//            // Update customer details
//            if (bvn != null && !bvn.isEmpty()) {
//                debitMandate.setBvn(bvn);
//            }
//            if (customerAddress != null && !customerAddress.isEmpty()) {
//                debitMandate.setCustomer_address(customerAddress);
//            }
//            if (customerEmail != null && !customerEmail.isEmpty()) {
//                debitMandate.setCustomer_email(customerEmail);
//            }
//            if (customerPhone != null && !customerPhone.isEmpty()) {
//                debitMandate.setCustomer_phone(customerPhone);
//            }
//
//            return debitMandateRepository.save(debitMandate);
//        } else {
//            throw new RuntimeException("DebitMandate not found with id: " + id);
//        }
//    }
//
//    /**
//     * Update DebitMandate with complete customer details (alternative method)
//     */
//    public DebitMandate updateWithFollowUpData(Long id, AccountVerificationDTO dto) {
//        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);
//
//        if (existingMandate.isPresent()) {
//            DebitMandate debitMandate = existingMandate.get();
//
//            // Update all available fields
//            if (dto.getBvn() != null && !dto.getBvn().isEmpty()) {
//                debitMandate.setBvn(dto.getBvn());
//            }
//            if (dto.getCustomerAddress() != null && !dto.getCustomerAddress().isEmpty()) {
//                debitMandate.setCustomer_address(dto.getCustomerAddress());
//            }
//            if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().isEmpty()) {
//                debitMandate.setCustomer_email(dto.getCustomerEmail());
//            }
//            if (dto.getCustomerPhone() != null && !dto.getCustomerPhone().isEmpty()) {
//                debitMandate.setCustomer_phone(dto.getCustomerPhone());
//            }
//
//            return debitMandateRepository.save(debitMandate);
//        } else {
//            throw new RuntimeException("DebitMandate not found with id: " + id);
//        }
//    }
//
//    /**
//     * Get a DebitMandate by ID
//     */
//    public Optional<DebitMandate> getDebitMandateById(Long id) {
//        return debitMandateRepository.findById(id);
//    }
//
//    /**
//     * Get a DebitMandate by reference (UUID)
//     */
//    public Optional<DebitMandate> getDebitMandateByReference(String reference) {
//        return debitMandateRepository.findByReference(reference);
//    }
//}











//package com.koolboks.creditProject.service.debitMandate;
//
//import com.koolboks.creditProject.dto.accountVerification.AccountVerificationDTO;
//import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
//import com.koolboks.creditProject.repository.DebitMandateRepository;
//import com.koolboks.creditProject.service.mono.MonoService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Optional;
//
//@Service
//public class DebitMandateService {
//
//    private static final Logger logger = LoggerFactory.getLogger(DebitMandateService.class);
//
//    @Autowired
//    private DebitMandateRepository debitMandateRepository;
//
//    @Autowired
//    private MonoService monoService;
//
//    /**
//     * Create or update a DebitMandate with account verification data
//     * This allows partial updates - other fields can be added later from other forms
//     */
//    public DebitMandate saveAccountVerification(AccountVerificationDTO dto) {
//        DebitMandate debitMandate = new DebitMandate();
//
//        // Set the account verification fields
//        debitMandate.setAccount_number(dto.getAccountNumber());
//        debitMandate.setBank_name(dto.getBankName());
//        debitMandate.setNip_code(dto.getNipCode());
//        debitMandate.setCustomer_name(dto.getCustomerName());
//
//        // Save and return
//        return debitMandateRepository.save(debitMandate);
//    }
//
//    /**
//     * Update an existing DebitMandate with account verification data
//     */
//    public DebitMandate updateAccountVerification(Long id, AccountVerificationDTO dto) {
//        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);
//
//        if (existingMandate.isPresent()) {
//            DebitMandate debitMandate = existingMandate.get();
//
//            // Update the account verification fields
//            debitMandate.setAccount_number(dto.getAccountNumber());
//            debitMandate.setBank_name(dto.getBankName());
//            debitMandate.setNip_code(dto.getNipCode());
//            debitMandate.setCustomer_name(dto.getCustomerName());
//
//            return debitMandateRepository.save(debitMandate);
//        } else {
//            throw new RuntimeException("DebitMandate not found with id: " + id);
//        }
//    }
//
//    /**
//     * Update DebitMandate with additional customer details from follow-up form
//     */
//    public DebitMandate updateCustomerDetails(Long id, String bvn, String customerAddress,
//                                               String customerEmail, String customerPhone) {
//        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);
//
//        if (existingMandate.isPresent()) {
//            DebitMandate debitMandate = existingMandate.get();
//
//            // Update customer details
//            if (bvn != null && !bvn.isEmpty()) {
//                debitMandate.setBvn(bvn);
//            }
//            if (customerAddress != null && !customerAddress.isEmpty()) {
//                debitMandate.setCustomer_address(customerAddress);
//            }
//            if (customerEmail != null && !customerEmail.isEmpty()) {
//                debitMandate.setCustomer_email(customerEmail);
//            }
//            if (customerPhone != null && !customerPhone.isEmpty()) {
//                debitMandate.setCustomer_phone(customerPhone);
//            }
//
//            return debitMandateRepository.save(debitMandate);
//        } else {
//            throw new RuntimeException("DebitMandate not found with id: " + id);
//        }
//    }
//
//    /**
//     * Update DebitMandate with complete customer details and create Mono customer
//     */
//    public DebitMandate updateWithFollowUpData(Long id, AccountVerificationDTO dto) {
//        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);
//
//        if (existingMandate.isPresent()) {
//            DebitMandate debitMandate = existingMandate.get();
//
//            // Update all available fields
//            if (dto.getBvn() != null && !dto.getBvn().isEmpty()) {
//                debitMandate.setBvn(dto.getBvn());
//            }
//            if (dto.getCustomerAddress() != null && !dto.getCustomerAddress().isEmpty()) {
//                debitMandate.setCustomer_address(dto.getCustomerAddress());
//            }
//            if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().isEmpty()) {
//                debitMandate.setCustomer_email(dto.getCustomerEmail());
//            }
//            if (dto.getCustomerPhone() != null && !dto.getCustomerPhone().isEmpty()) {
//                debitMandate.setCustomer_phone(dto.getCustomerPhone());
//            }
//
//            return debitMandateRepository.save(debitMandate);
//        } else {
//            throw new RuntimeException("DebitMandate not found with id: " + id);
//        }
//    }
//
//    /**
//     * Update DebitMandate with customer details and create Mono customer
//     * This version integrates with Mono API
//     */
//    public DebitMandate updateWithFollowUpDataAndMono(Long id, AccountVerificationDTO dto,
//                                                      String firstName, String lastName) {
//        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);
//
//        if (existingMandate.isPresent()) {
//            DebitMandate debitMandate = existingMandate.get();
//
//            // Update customer details first
//            if (dto.getBvn() != null && !dto.getBvn().isEmpty()) {
//                debitMandate.setBvn(dto.getBvn());
//            }
//            if (dto.getCustomerAddress() != null && !dto.getCustomerAddress().isEmpty()) {
//                debitMandate.setCustomer_address(dto.getCustomerAddress());
//            }
//            if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().isEmpty()) {
//                debitMandate.setCustomer_email(dto.getCustomerEmail());
//            }
//            if (dto.getCustomerPhone() != null && !dto.getCustomerPhone().isEmpty()) {
//                debitMandate.setCustomer_phone(dto.getCustomerPhone());
//            }
//
//            // Create Mono customer if we have all required fields
//            if (firstName != null && !firstName.isEmpty() &&
//                lastName != null && !lastName.isEmpty() &&
//                debitMandate.getCustomer_email() != null &&
//                debitMandate.getCustomer_phone() != null &&
//                debitMandate.getCustomer_address() != null &&
//                debitMandate.getBvn() != null) {
//
//                logger.info("Creating Mono customer for mandate ID: {}", id);
//
//                try {
//                    String monoCustomerId = monoService.createCustomer(
//                        firstName,
//                        lastName,
//                        debitMandate.getCustomer_email(),
//                        debitMandate.getCustomer_phone(),
//                        debitMandate.getCustomer_address(),
//                        debitMandate.getBvn()
//                    );
//
//                    if (monoCustomerId != null) {
//                        debitMandate.setCustomer_id(monoCustomerId);
//                        logger.info("Successfully set Mono customer ID: {} for mandate ID: {}",
//                                  monoCustomerId, id);
//                    } else {
//                        logger.warn("Mono customer creation returned null for mandate ID: {}", id);
//                    }
//                } catch (Exception e) {
//                    // Log error but don't fail the entire operation
//                    logger.error("Failed to create Mono customer for mandate ID: {}", id, e);
//                    // Continue without setting customer_id
//                }
//            } else {
//                logger.warn("Missing required fields for Mono customer creation for mandate ID: {}", id);
//            }
//
//            return debitMandateRepository.save(debitMandate);
//        } else {
//            throw new RuntimeException("DebitMandate not found with id: " + id);
//        }
//    }
//
//    /**
//     * Get a DebitMandate by ID
//     */
//    public Optional<DebitMandate> getDebitMandateById(Long id) {
//        return debitMandateRepository.findById(id);
//    }
//
//    /**
//     * Get a DebitMandate by reference (UUID)
//     */
//    public Optional<DebitMandate> getDebitMandateByReference(String reference) {
//        return debitMandateRepository.findByReference(reference);
//    }
//}





package com.koolboks.creditProject.service.debitMandate;

import com.koolboks.creditProject.dto.accountVerification.AccountVerificationDTO;
import com.koolboks.creditProject.entity.debit_mandate.DebitMandate;
import com.koolboks.creditProject.repository.DebitMandateRepository;
import com.koolboks.creditProject.service.mono.MonoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DebitMandateService {

    private static final Logger logger = LoggerFactory.getLogger(DebitMandateService.class);

    @Autowired
    private DebitMandateRepository debitMandateRepository;

    @Autowired
    private MonoService monoService;

    /**
     * Create or update a DebitMandate with account verification data
     * This allows partial updates - other fields can be added later from other forms
     */
    public DebitMandate saveAccountVerification(AccountVerificationDTO dto) {
        DebitMandate debitMandate = new DebitMandate();

        // Set the account verification fields
        debitMandate.setAccount_number(dto.getAccountNumber());
        debitMandate.setBank_name(dto.getBankName());
        debitMandate.setNip_code(dto.getNipCode());
        debitMandate.setCustomer_name(dto.getCustomerName());

        // Save and return
        return debitMandateRepository.save(debitMandate);
    }

    /**
     * Update an existing DebitMandate with account verification data
     */
    public DebitMandate updateAccountVerification(Long id, AccountVerificationDTO dto) {
        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);

        if (existingMandate.isPresent()) {
            DebitMandate debitMandate = existingMandate.get();

            // Update the account verification fields
            debitMandate.setAccount_number(dto.getAccountNumber());
            debitMandate.setBank_name(dto.getBankName());
            debitMandate.setNip_code(dto.getNipCode());
            debitMandate.setCustomer_name(dto.getCustomerName());

            return debitMandateRepository.save(debitMandate);
        } else {
            throw new RuntimeException("DebitMandate not found with id: " + id);
        }
    }

    /**
     * Update DebitMandate with additional customer details from follow-up form
     */
    public DebitMandate updateCustomerDetails(Long id, String bvn, String customerAddress,
                                               String customerEmail, String customerPhone) {
        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);

        if (existingMandate.isPresent()) {
            DebitMandate debitMandate = existingMandate.get();

            // Update customer details
            if (bvn != null && !bvn.isEmpty()) {
                debitMandate.setBvn(bvn);
            }
            if (customerAddress != null && !customerAddress.isEmpty()) {
                debitMandate.setCustomer_address(customerAddress);
            }
            if (customerEmail != null && !customerEmail.isEmpty()) {
                debitMandate.setCustomer_email(customerEmail);
            }
            if (customerPhone != null && !customerPhone.isEmpty()) {
                debitMandate.setCustomer_phone(customerPhone);
            }

            return debitMandateRepository.save(debitMandate);
        } else {
            throw new RuntimeException("DebitMandate not found with id: " + id);
        }
    }

    /**
     * Update DebitMandate with complete customer details and create Mono customer
     */
    public DebitMandate updateWithFollowUpData(Long id, AccountVerificationDTO dto) {
        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);

        if (existingMandate.isPresent()) {
            DebitMandate debitMandate = existingMandate.get();

            // Update all available fields
            if (dto.getBvn() != null && !dto.getBvn().isEmpty()) {
                debitMandate.setBvn(dto.getBvn());
            }
            if (dto.getCustomerAddress() != null && !dto.getCustomerAddress().isEmpty()) {
                debitMandate.setCustomer_address(dto.getCustomerAddress());
            }
            if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().isEmpty()) {
                debitMandate.setCustomer_email(dto.getCustomerEmail());
            }
            if (dto.getCustomerPhone() != null && !dto.getCustomerPhone().isEmpty()) {
                debitMandate.setCustomer_phone(dto.getCustomerPhone());
            }

            return debitMandateRepository.save(debitMandate);
        } else {
            throw new RuntimeException("DebitMandate not found with id: " + id);
        }
    }

    /**
     * Update DebitMandate with customer details and create Mono customer
     * This version integrates with Mono API
     */
    public DebitMandate updateWithFollowUpDataAndMono(Long id, AccountVerificationDTO dto,
                                                      String firstName, String lastName) {
        Optional<DebitMandate> existingMandate = debitMandateRepository.findById(id);

        if (existingMandate.isPresent()) {
            DebitMandate debitMandate = existingMandate.get();

            // Update customer details first
            if (dto.getBvn() != null && !dto.getBvn().isEmpty()) {
                debitMandate.setBvn(dto.getBvn());
            }
            if (dto.getCustomerAddress() != null && !dto.getCustomerAddress().isEmpty()) {
                debitMandate.setCustomer_address(dto.getCustomerAddress());
            }
            if (dto.getCustomerEmail() != null && !dto.getCustomerEmail().isEmpty()) {
                debitMandate.setCustomer_email(dto.getCustomerEmail());
            }
            if (dto.getCustomerPhone() != null && !dto.getCustomerPhone().isEmpty()) {
                debitMandate.setCustomer_phone(dto.getCustomerPhone());
            }

            // Create Mono customer if we have all required fields
            if (firstName != null && !firstName.isEmpty() &&
                lastName != null && !lastName.isEmpty() &&
                debitMandate.getCustomer_email() != null &&
                debitMandate.getCustomer_phone() != null &&
                debitMandate.getCustomer_address() != null &&
                debitMandate.getBvn() != null) {

                logger.info("Creating Mono customer for mandate ID: {}", id);

                try {
                    String monoCustomerId = monoService.createCustomer(
                        firstName,
                        lastName,
                        debitMandate.getCustomer_email(),
                        debitMandate.getCustomer_phone(),
                        debitMandate.getCustomer_address(),
                        debitMandate.getBvn()
                    );

                    if (monoCustomerId != null) {
                        debitMandate.setCustomer_id(monoCustomerId);
                        logger.info("Successfully set Mono customer ID: {} for mandate ID: {}",
                                  monoCustomerId, id);
                    } else {
                        logger.warn("Mono customer creation returned null for mandate ID: {}", id);
                    }
                } catch (Exception e) {
                    // Log error but don't fail the entire operation
                    logger.error("Failed to create Mono customer for mandate ID: {}", id, e);
                    // Continue without setting customer_id
                }
            } else {
                logger.warn("Missing required fields for Mono customer creation for mandate ID: {}", id);
            }

            return debitMandateRepository.save(debitMandate);
        } else {
            throw new RuntimeException("DebitMandate not found with id: " + id);
        }
    }

    /**
     * Update DebitMandate amount field (for total installment from offer letter)
     */
    public DebitMandate updateAmount(String bvn, int amount) {
        Optional<DebitMandate> existingMandate = debitMandateRepository.findByBvn(bvn);

        if (existingMandate.isPresent()) {
            DebitMandate debitMandate = existingMandate.get();
            debitMandate.setAmount(amount);

            logger.info("Updated DebitMandate amount to {} for BVN: {}", amount, bvn);
            return debitMandateRepository.save(debitMandate);
        } else {
            logger.warn("No DebitMandate found for BVN: {}. Creating new record with amount.", bvn);

            // Create new DebitMandate if it doesn't exist
            DebitMandate newMandate = new DebitMandate();
            newMandate.setBvn(bvn);
            newMandate.setAmount(amount);

            return debitMandateRepository.save(newMandate);
        }
    }

    /**
     * Get a DebitMandate by ID
     */
    public Optional<DebitMandate> getDebitMandateById(Long id) {
        return debitMandateRepository.findById(id);
    }

    /**
     * Get a DebitMandate by reference (UUID)
     */
    public Optional<DebitMandate> getDebitMandateByReference(String reference) {
        return debitMandateRepository.findByReference(reference);
    }
}
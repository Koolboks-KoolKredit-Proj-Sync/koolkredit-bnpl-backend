package com.koolboks.creditProject.service;


import com.koolboks.creditProject.dto.AgentEntryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    //@Value("${notification.email.to:admin@example.com}")
    @Value("${notification.email.to:foltim256@gmail.com}")
    private String notificationEmail;

    @Value("${notification.email.from:foltim256@gmail.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(AgentEntryRequest request, boolean isVerified, String details) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(notificationEmail);

            if (isVerified) {
                message.setSubject(" Agent Entry Verification - SUCCESS");
                message.setText(buildSuccessEmailBody(request));
            } else {
                message.setSubject(" Agent Entry Verification - FAILED");
                message.setText(buildFailureEmailBody(request, details));
            }

            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", notificationEmail);

        } catch (Exception e) {
            log.error("Error sending email: ", e);
        }
    }

    private String buildSuccessEmailBody(AgentEntryRequest request) {
        return String.format("""
                AGENT ENTRY VERIFICATION - SUCCESS
                =====================================

                Documents have been authenticated and verified successfully!

                CUSTOMER DETAILS:
                -----------------
                Name: %s %s %s
                Date of Birth: %s
                Gender: %s
                Marital Status: %s
                Mobile Number: %s

                IDENTIFICATION:
                ---------------
                BVN: %s
                NIN: %s

                PRODUCT DETAILS:
                ----------------
                Product: %s
                Brand: %s
                Size: %s
                Price: ₦%s

                PAYMENT PLAN:
                -------------
                Plan: %s
                %s

                STATUS:  VERIFIED

                All submitted documents match the records from the verification API.
                You can proceed with this application.

                ---
                This is an automated message from the Agent Entry System.
                """,
                request.getFirstName(),
                request.getMiddleName() != null ? request.getMiddleName() : "",
                request.getLastName(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getMaritalStatus(),
                request.getMobileNumber(),
                request.getBvn(),
                request.getNin(),
                request.getProductName(),
                request.getBrand(),
                request.getSize(),
                request.getTotalPrice(),
                request.getPlan(),
                getPaymentPlanDetails(request)
        );
    }

    private String buildFailureEmailBody(AgentEntryRequest request, String details) {
        return String.format("""
                AGENT ENTRY VERIFICATION - FAILED
                ==================================

                ⚠️ Documents DO NOT match! Verification failed.

                CUSTOMER DETAILS:
                -----------------
                Name: %s %s %s
                Date of Birth: %s
                Gender: %s
                Marital Status: %s
                Mobile Number: %s

                IDENTIFICATION:
                ---------------
                BVN: %s
                NIN: %s

                PRODUCT DETAILS:
                ----------------
                Product: %s
                Brand: %s
                Size: %s
                Price: ₦%s

                STATUS: ❌ VERIFICATION FAILED

                Reason: %s

                The submitted information does not match the records from the verification API.
                Please review the application and contact the customer for clarification.

                ---
                This is an automated message from the Agent Entry System.
                """,
                request.getFirstName(),
                request.getMiddleName() != null ? request.getMiddleName() : "",
                request.getLastName(),
                request.getDateOfBirth(),
                request.getGender(),
                request.getMaritalStatus(),
                request.getMobileNumber(),
                request.getBvn(),
                request.getNin(),
                request.getProductName(),
                request.getBrand(),
                request.getSize(),
                request.getTotalPrice(),
                details
        );
    }

    private String getPaymentPlanDetails(AgentEntryRequest request) {
        if ("Easy 35".equals(request.getPlan()) || "Easy 25".equals(request.getPlan())) {
            return "Installment Duration: " + request.getInstallmentDuration() + " months";
        } else if ("Omolope".equals(request.getPlan())) {
            return "Duration: " + request.getOmolopeDays() + " days";
        }
        return "Full Payment";
    }
}

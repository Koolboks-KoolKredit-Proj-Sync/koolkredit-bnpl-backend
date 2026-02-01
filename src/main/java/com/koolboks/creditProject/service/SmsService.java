package com.koolboks.creditProject.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.from.number}")
    private String fromNumber;

    private void initTwilio() {
        if (accountSid == null || accountSid.isBlank()) {
            log.warn("Twilio credentials not configured");
            return;
        }
        Twilio.init(accountSid, authToken);
    }

    /**
     * Send a general SMS message
     */
    public boolean sendSms(String toPhoneNumber, String messageBody) {
        try {
            initTwilio();

            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(toPhoneNumber),
                    new com.twilio.type.PhoneNumber(fromNumber),
                    messageBody
            ).create();

            log.info("SMS sent successfully to {} with SID: {}", toPhoneNumber, message.getSid());
            return true;

        } catch (Exception e) {
            log.error("Error sending SMS to {}: {}", toPhoneNumber, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Send SMS with retry logic
     */
    public boolean sendSmsWithRetry(String toPhoneNumber, String messageBody, int maxRetries) {
        int attempts = 0;
        while (attempts < maxRetries) {
            try {
                attempts++;
                initTwilio();

                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber(toPhoneNumber),
                        new com.twilio.type.PhoneNumber(fromNumber),
                        messageBody
                ).create();

                log.info("SMS sent on attempt {} to {} with SID: {}", attempts, toPhoneNumber, message.getSid());
                return true;

            } catch (Exception e) {
                log.error("SMS attempt {} failed for {}: {}", attempts, toPhoneNumber, e.getMessage());

                if (attempts >= maxRetries) {
                    log.error("All SMS attempts failed for {}", toPhoneNumber);
                    return false;
                }

                // Wait before retry (exponential backoff)
                try {
                    Thread.sleep(1000 * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }
}





















//package com.koolboks.creditProject.service;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//@Service
//public class SMSService {
//    private static final Logger log = LoggerFactory.getLogger(SMSService.class);
//
//    /**
//     * Replace the body with a real SMS provider integration (Twilio, Infobip, Africa's Talking, etc.)
//     */
//    public void sendPin(String mobileNumber, String pin) {
//        // 1) sanitize mobile number if needed
//        // 2) call your sms provider
//        log.info("SMS PIN to {} : {}", mobileNumber, pin);
//        // TODO: integrate real provider here and handle failures.
//    }
//}

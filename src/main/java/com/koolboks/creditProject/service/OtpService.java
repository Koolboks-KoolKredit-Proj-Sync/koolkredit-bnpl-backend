//package com.koolboks.creditProject.service;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.Random;
//
//@Service
//public class OtpService {
//
//    @Value("${twilio.account.sid}")
//    private String accountSid;
//
//    @Value("${twilio.auth.token}")
//    private String authToken;
//
//    @Value("${twilio.from.number}") // e.g. "+1234567890"
//    private String fromNumber;
//
//    private void initTwilio() {
//        if (accountSid == null || accountSid.isBlank()) return;
//        Twilio.init(accountSid, authToken);
//    }
//
//    /**
//     * Generates a 6-digit pin, sends SMS using Twilio Messages API, and returns the pin when sent.
//     * Returns null on failure.
//     */
//    public String sendOtp(String toPhoneNumber) {
//        try {
//            initTwilio();
//            int pin = new Random().nextInt(900000) + 100000;
//            String pinStr = String.valueOf(pin);
//            String body = String.format("Your Confirmation code is: %s. Do not share this with anyone.", pinStr);
//
//            Message.creator(
//                    new com.twilio.type.PhoneNumber(toPhoneNumber),
//                    new com.twilio.type.PhoneNumber(fromNumber),
//                    body
//            ).create();
//
//            return pinStr;
//        } catch (Exception ex) {
//            // log error in real app
//            ex.printStackTrace();
//            return null;
//        }
//    }
//}
//



package com.koolboks.creditProject.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.from.number}") // e.g. "+1234567890"
    private String fromNumber;

    private void initTwilio() {
        if (accountSid == null || accountSid.isBlank()) {
            log.warn("⚠️ Twilio credentials not configured");
            return;
        }
        Twilio.init(accountSid, authToken);
    }

    /**
     * Generates a 6-digit OTP and attempts to send via Twilio.
     * ALWAYS returns the OTP code (even if SMS fails) so it can be saved to database.
     * Logs whether SMS was sent successfully or not.
     */
    public String sendOtp(String toPhoneNumber) {
        // ✅ ALWAYS generate OTP first
        int pin = new Random().nextInt(900000) + 100000;
        String pinStr = String.valueOf(pin);

        log.info("📱 Generated OTP: {} for phone: {}", pinStr, toPhoneNumber);

        try {
            initTwilio();

            // ✅ Check if Twilio is configured
            if (accountSid == null || accountSid.isBlank()) {
                log.error("❌ Twilio not configured. OTP generated but SMS not sent.");
                log.info("💡 OTP for manual sending: {}", pinStr);
                return pinStr; // ✅ Return OTP anyway
            }

            // ✅ Format phone number to international format
            String formattedPhone = formatPhoneNumber(toPhoneNumber);
            log.info("📞 Formatted phone number: {}", formattedPhone);

            String body = String.format("Your Confirmation code is: %s. Do not share this with anyone.", pinStr);

            Message message = Message.creator(
                    new com.twilio.type.PhoneNumber(formattedPhone),
                    new com.twilio.type.PhoneNumber(fromNumber),
                    body
            ).create();

            log.info("✅ OTP SMS sent successfully. Message SID: {}", message.getSid());
            return pinStr;

        } catch (Exception ex) {
            log.error("❌ Failed to send OTP SMS to {}: {}", toPhoneNumber, ex.getMessage());
            log.error("Stack trace:", ex);
            log.info("💡 OTP generated for manual sending: {}", pinStr);
            return pinStr; // ✅ Still return OTP even if SMS fails
        }
    }

    /**
     * Format phone number to international format for Twilio
     * Converts 09012486698 to +2349012486698 (Nigeria)
     */
    private String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            return phoneNumber;
        }

        // Remove any spaces or special characters
        String cleaned = phoneNumber.replaceAll("[^0-9+]", "");

        // If already in international format, return as is
        if (cleaned.startsWith("+")) {
            return cleaned;
        }

        // If starts with 0, replace with +234 (Nigeria country code)
        if (cleaned.startsWith("0")) {
            return "+234" + cleaned.substring(1);
        }

        // If starts with 234, add +
        if (cleaned.startsWith("234")) {
            return "+" + cleaned;
        }

        // Otherwise assume it needs +234
        return "+234" + cleaned;
    }
}








//package com.koolboks.creditProject.service;
//
//import com.twilio.Twilio;
//import com.twilio.rest.verify.v2.service.Verification;
//import com.twilio.rest.verify.v2.service.VerificationCheck;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OtpService {
//
//    @Value("${twilio.account.sid}")
//    private String accountSid;
//
//    @Value("${twilio.auth.token}")
//    private String authToken;
//
//    @Value("${twilio.verify.sid}")
//    private String verifySid;
//
//    private void initTwilio() {
//        Twilio.init(accountSid, authToken);
//    }
//
//    public String sendOtp(String phoneNumber) {
//        initTwilio();
//        Verification verification = Verification.creator(verifySid, phoneNumber, "sms").create();
//        return verification.getStatus(); // typically "pending"
//    }
//
//    public boolean verifyOtp(String phoneNumber, String code) {
//        initTwilio();
//        VerificationCheck verificationCheck = VerificationCheck.creator(verifySid)
//                .setTo(phoneNumber)
//                .setCode(code)
//                .create();
//        return "approved".equalsIgnoreCase(verificationCheck.getStatus());
//    }
//}

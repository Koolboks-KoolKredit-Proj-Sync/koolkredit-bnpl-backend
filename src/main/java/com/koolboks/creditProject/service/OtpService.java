package com.koolboks.creditProject.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class OtpService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.from.number}") // e.g. "+1234567890"
    private String fromNumber;

    private void initTwilio() {
        if (accountSid == null || accountSid.isBlank()) return;
        Twilio.init(accountSid, authToken);
    }

    /**
     * Generates a 6-digit pin, sends SMS using Twilio Messages API, and returns the pin when sent.
     * Returns null on failure.
     */
    public String sendOtp(String toPhoneNumber) {
        try {
            initTwilio();
            int pin = new Random().nextInt(900000) + 100000;
            String pinStr = String.valueOf(pin);
            String body = String.format("Your Confirmation code is: %s. Do not share this with anyone.", pinStr);

            Message.creator(
                    new com.twilio.type.PhoneNumber(toPhoneNumber),
                    new com.twilio.type.PhoneNumber(fromNumber),
                    body
            ).create();

            return pinStr;
        } catch (Exception ex) {
            // log error in real app
            ex.printStackTrace();
            return null;
        }
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

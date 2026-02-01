package com.koolboks.creditProject.dto;

public class OtpVerificationRequest {
    private String bvn;
    private String otp;

    public OtpVerificationRequest() {}

    public String getBvn() { return bvn; }
    public void setBvn(String bvn) { this.bvn = bvn; }

    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }
}
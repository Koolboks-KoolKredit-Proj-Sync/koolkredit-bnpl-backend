package com.koolboks.creditProject.dto;

public class AgentFollowUpResponse {
    private String status;
    private String creditStatus;
    private Double dti;
    private String pin; // present only when approved

    public AgentFollowUpResponse() {}

    public AgentFollowUpResponse(String status, String creditStatus, Double dti, String pin) {
        this.status = status;
        this.creditStatus = creditStatus;
        this.dti = dti;
        this.pin = pin;
    }

    // getters & setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreditStatus() { return creditStatus; }
    public void setCreditStatus(String creditStatus) { this.creditStatus = creditStatus; }

    public Double getDti() { return dti; }
    public void setDti(Double dti) { this.dti = dti; }

    public String getPin() { return pin; }
    public void setPin(String pin) { this.pin = pin; }
}

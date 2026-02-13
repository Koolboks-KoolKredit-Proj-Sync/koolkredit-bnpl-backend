package com.koolboks.creditProject.dto.mono;



import com.fasterxml.jackson.annotation.JsonProperty;

public class DebitRequest {

    private int amount;
    private String reference;
    private String narration;

    public DebitRequest() {
    }

    public DebitRequest(int amount, String reference, String narration) {
        this.amount = amount;
        this.reference = reference;
        this.narration = narration;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}
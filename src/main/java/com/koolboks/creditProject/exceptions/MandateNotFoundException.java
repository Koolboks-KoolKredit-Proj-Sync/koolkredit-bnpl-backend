package com.koolboks.creditProject.exceptions;



public class MandateNotFoundException extends RuntimeException {

    private final String identifier;

    public MandateNotFoundException(String identifier) {
        super(String.format("Mandate not found with identifier: %s", identifier));
        this.identifier = identifier;
    }

    public MandateNotFoundException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}

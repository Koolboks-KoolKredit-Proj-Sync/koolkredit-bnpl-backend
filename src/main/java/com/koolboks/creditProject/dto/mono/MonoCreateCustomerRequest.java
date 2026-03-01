package com.koolboks.creditProject.dto.mono;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MonoCreateCustomerRequest {

    @JsonProperty("email")
    private String email;

    @JsonProperty("firstName")
    private String firstName;

    @JsonProperty("lastName")
    private String lastName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("address")
    private String address;

    @JsonProperty("identity")
    private Identity identity;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Identity getIdentity() { return identity; }
    public void setIdentity(Identity identity) { this.identity = identity; }

    public static class Identity {
        @JsonProperty("type")
        private String type;

        @JsonProperty("number")
        private String number;

        public Identity() {}

        public Identity(String type, String number) {
            this.type = type;
            this.number = number;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public String getNumber() { return number; }
        public void setNumber(String number) { this.number = number; }
    }
}
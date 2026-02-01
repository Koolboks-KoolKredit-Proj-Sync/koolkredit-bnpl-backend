package com.koolboks.creditProject.dto;



import com.fasterxml.jackson.annotation.JsonProperty;

public class MonoApiResponse {
    private String status;
    private String message;
    private String timestamp;
    private ResponseData data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }

    public static class ResponseData {
        @JsonProperty("personal_information")
        private PersonalInformation personalInformation;

        @JsonProperty("identification_numbers")
        private IdentificationNumbers identificationNumbers;

        @JsonProperty("residence_information")
        private ResidenceInformation residenceInformation;

        private Biometrics biometrics;

        public PersonalInformation getPersonalInformation() {
            return personalInformation;
        }

        public void setPersonalInformation(PersonalInformation personalInformation) {
            this.personalInformation = personalInformation;
        }

        public IdentificationNumbers getIdentificationNumbers() {
            return identificationNumbers;
        }

        public void setIdentificationNumbers(IdentificationNumbers identificationNumbers) {
            this.identificationNumbers = identificationNumbers;
        }

        public ResidenceInformation getResidenceInformation() {
            return residenceInformation;
        }

        public void setResidenceInformation(ResidenceInformation residenceInformation) {
            this.residenceInformation = residenceInformation;
        }

        public Biometrics getBiometrics() {
            return biometrics;
        }

        public void setBiometrics(Biometrics biometrics) {
            this.biometrics = biometrics;
        }
    }

    public static class PersonalInformation {
        private String title;

        @JsonProperty("first_name")
        private String firstName;

        @JsonProperty("middle_name")
        private String middleName;

        private String surname;
        private String gender;
        private String dob;

        @JsonProperty("birth_country")
        private String birthCountry;

        @JsonProperty("birth_state")
        private String birthState;

        @JsonProperty("birth_lga")
        private String birthLga;

        @JsonProperty("marital_status")
        private String maritalStatus;

        private String email;

        @JsonProperty("telephone_no")
        private String telephoneNo;

        private String occupation;

        @JsonProperty("lga_of_origin")
        private String lgaOfOrigin;

        @JsonProperty("state_of_origin")
        private String stateOfOrigin;

        @JsonProperty("watch_listed")
        private String watchListed;

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public String getBirthCountry() {
            return birthCountry;
        }

        public void setBirthCountry(String birthCountry) {
            this.birthCountry = birthCountry;
        }

        public String getBirthState() {
            return birthState;
        }

        public void setBirthState(String birthState) {
            this.birthState = birthState;
        }

        public String getBirthLga() {
            return birthLga;
        }

        public void setBirthLga(String birthLga) {
            this.birthLga = birthLga;
        }

        public String getMaritalStatus() {
            return maritalStatus;
        }

        public void setMaritalStatus(String maritalStatus) {
            this.maritalStatus = maritalStatus;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelephoneNo() {
            return telephoneNo;
        }

        public void setTelephoneNo(String telephoneNo) {
            this.telephoneNo = telephoneNo;
        }

        public String getOccupation() {
            return occupation;
        }

        public void setOccupation(String occupation) {
            this.occupation = occupation;
        }

        public String getLgaOfOrigin() {
            return lgaOfOrigin;
        }

        public void setLgaOfOrigin(String lgaOfOrigin) {
            this.lgaOfOrigin = lgaOfOrigin;
        }

        public String getStateOfOrigin() {
            return stateOfOrigin;
        }

        public void setStateOfOrigin(String stateOfOrigin) {
            this.stateOfOrigin = stateOfOrigin;
        }

        public String getWatchListed() {
            return watchListed;
        }

        public void setWatchListed(String watchListed) {
            this.watchListed = watchListed;
        }
    }

    public static class IdentificationNumbers {
        private String nin;
        private String bvn;

        public String getNin() {
            return nin;
        }

        public void setNin(String nin) {
            this.nin = nin;
        }

        public String getBvn() {
            return bvn;
        }

        public void setBvn(String bvn) {
            this.bvn = bvn;
        }
    }

    public static class ResidenceInformation {
        private String address;
        private String town;
        private String lga;
        private String state;

        @JsonProperty("residence_status")
        private String residenceStatus;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTown() {
            return town;
        }

        public void setTown(String town) {
            this.town = town;
        }

        public String getLga() {
            return lga;
        }

        public void setLga(String lga) {
            this.lga = lga;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getResidenceStatus() {
            return residenceStatus;
        }

        public void setResidenceStatus(String residenceStatus) {
            this.residenceStatus = residenceStatus;
        }
    }

    public static class Biometrics {
        private String photo;

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }
    }
}
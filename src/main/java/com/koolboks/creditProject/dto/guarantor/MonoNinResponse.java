package com.koolboks.creditProject.dto.guarantor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MonoNinResponse {
    private String status;
    private String message;
    private String timestamp;
    private NinData data;

    public static class NinData {
        @JsonProperty("birthcountry")
        private String birthCountry;

        @JsonProperty("birthdate")
        private String birthDate;

        @JsonProperty("birthlga")
        private String birthLga;

        @JsonProperty("birthstate")
        private String birthState;

        @JsonProperty("educationallevel")
        private String educationalLevel;

        private String email;

        @JsonProperty("employmentstatus")
        private String employmentStatus;

        @JsonProperty("firstname")
        private String firstName;

        private String gender;

        @JsonProperty("heigth")
        private String height;

        @JsonProperty("maritalstatus")
        private String maritalStatus;

        @JsonProperty("middlename")
        private String middleName;

        private String nin;

        @JsonProperty("nok_address1")
        private String nokAddress1;

        @JsonProperty("nok_address2")
        private String nokAddress2;

        @JsonProperty("nok_firstname")
        private String nokFirstName;

        @JsonProperty("nok_lga")
        private String nokLga;

        @JsonProperty("nok_middlename")
        private String nokMiddleName;

        @JsonProperty("nok_state")
        private String nokState;

        @JsonProperty("nok_surname")
        private String nokSurname;

        @JsonProperty("nok_town")
        private String nokTown;

        private String profession;
        private String religion;

        @JsonProperty("residence_address")
        private String residenceAddress;

        @JsonProperty("residence_lga")
        private String residenceLga;

        @JsonProperty("residence_state")
        private String residenceState;

        @JsonProperty("residence_town")
        private String residenceTown;

        @JsonProperty("spoken_language")
        private String spokenLanguage;

        private String surname;

        @JsonProperty("telephoneno")
        private String telephoneNo;

        private String title;

        @JsonProperty("tracking_id")
        private String trackingId;

        // Getters and Setters
        public String getBirthCountry() { return birthCountry; }
        public void setBirthCountry(String birthCountry) { this.birthCountry = birthCountry; }

        public String getBirthDate() { return birthDate; }
        public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

        public String getBirthLga() { return birthLga; }
        public void setBirthLga(String birthLga) { this.birthLga = birthLga; }

        public String getBirthState() { return birthState; }
        public void setBirthState(String birthState) { this.birthState = birthState; }

        public String getEducationalLevel() { return educationalLevel; }
        public void setEducationalLevel(String educationalLevel) { this.educationalLevel = educationalLevel; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getEmploymentStatus() { return employmentStatus; }
        public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }

        public String getHeight() { return height; }
        public void setHeight(String height) { this.height = height; }

        public String getMaritalStatus() { return maritalStatus; }
        public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

        public String getMiddleName() { return middleName; }
        public void setMiddleName(String middleName) { this.middleName = middleName; }

        public String getNin() { return nin; }
        public void setNin(String nin) { this.nin = nin; }

        public String getNokAddress1() { return nokAddress1; }
        public void setNokAddress1(String nokAddress1) { this.nokAddress1 = nokAddress1; }

        public String getNokAddress2() { return nokAddress2; }
        public void setNokAddress2(String nokAddress2) { this.nokAddress2 = nokAddress2; }

        public String getNokFirstName() { return nokFirstName; }
        public void setNokFirstName(String nokFirstName) { this.nokFirstName = nokFirstName; }

        public String getNokLga() { return nokLga; }
        public void setNokLga(String nokLga) { this.nokLga = nokLga; }

        public String getNokMiddleName() { return nokMiddleName; }
        public void setNokMiddleName(String nokMiddleName) { this.nokMiddleName = nokMiddleName; }

        public String getNokState() { return nokState; }
        public void setNokState(String nokState) { this.nokState = nokState; }

        public String getNokSurname() { return nokSurname; }
        public void setNokSurname(String nokSurname) { this.nokSurname = nokSurname; }

        public String getNokTown() { return nokTown; }
        public void setNokTown(String nokTown) { this.nokTown = nokTown; }

        public String getProfession() { return profession; }
        public void setProfession(String profession) { this.profession = profession; }

        public String getReligion() { return religion; }
        public void setReligion(String religion) { this.religion = religion; }

        public String getResidenceAddress() { return residenceAddress; }
        public void setResidenceAddress(String residenceAddress) { this.residenceAddress = residenceAddress; }

        public String getResidenceLga() { return residenceLga; }
        public void setResidenceLga(String residenceLga) { this.residenceLga = residenceLga; }

        public String getResidenceState() { return residenceState; }
        public void setResidenceState(String residenceState) { this.residenceState = residenceState; }

        public String getResidenceTown() { return residenceTown; }
        public void setResidenceTown(String residenceTown) { this.residenceTown = residenceTown; }

        public String getSpokenLanguage() { return spokenLanguage; }
        public void setSpokenLanguage(String spokenLanguage) { this.spokenLanguage = spokenLanguage; }

        public String getSurname() { return surname; }
        public void setSurname(String surname) { this.surname = surname; }

        public String getTelephoneNo() { return telephoneNo; }
        public void setTelephoneNo(String telephoneNo) { this.telephoneNo = telephoneNo; }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getTrackingId() { return trackingId; }
        public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    }

    // Getters and Setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public NinData getData() { return data; }
    public void setData(NinData data) { this.data = data; }
}
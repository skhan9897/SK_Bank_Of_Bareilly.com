package com.skbankofbareilly.mobile.model;

public class Customer {
    private Long customerId;
    private Long userId;
    private String customerNumber;
    private String fullName;
    private String dateOfBirth;
    private String gender;
    private String mobile;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String aadhaarNumber;
    private String panNumber;
    private String profileImage;
    private String kycStatus;
    private String status;

    public Customer() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMaskedAadhaar() {
        if (aadhaarNumber != null && aadhaarNumber.length() >= 4) {
            return "XXXX XXXX " + aadhaarNumber.substring(aadhaarNumber.length() - 4);
        }
        return "XXXX XXXX XXXX";
    }

    public String getMaskedPan() {
        if (panNumber != null && panNumber.length() == 10) {
            return panNumber.substring(0, 2) + "*****" + panNumber.substring(7);
        }
        return "*****";
    }
}

package com.example.ZAuth.DatabaseHelper;


public class AddUserWithMobNumData {
    public String MobNumber;
    public String Role;
    public String sessionTime;  //unit->minutes
    public String clientId;
    public String clintApi;
    public String clientApikey;
    public String senderToken;
    public String otp;
    public String platform;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }


    public String getMobNumber() {
        return MobNumber;
    }

    public void setMobNumber(String mobNumber) {
        MobNumber = mobNumber;
    }


    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(String sessionTime) {
        this.sessionTime = sessionTime;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClintApi() {
        return clintApi;
    }

    public void setClintApi(String clintApi) {
        this.clintApi = clintApi;
    }

    public String getClientApikey() {
        return clientApikey;
    }

    public void setClientApikey(String clientApikey) {
        this.clientApikey = clientApikey;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public void setSenderToken(String senderToken) {
        this.senderToken = senderToken;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}

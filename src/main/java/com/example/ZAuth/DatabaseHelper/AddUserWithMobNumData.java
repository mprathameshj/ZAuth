package com.example.ZAuth.DatabaseHelper;

import lombok.Data;


public class AddUserWithMobNumData {
    public String AuthMethod;
    public String MobNumber;
    public String AuthToken;
    public String AvailableSessions;
    public String Role;
    public String CurrLogin;
    public String sessionTime;  //unit->minutes
    public String clientId;
    public String clintApi;
    public String clientApikey;
    public String ipAdd;
    public String deviceInfo;
    public String timeStamp;
    public String senderToken;
    public String otp;

    public String getAuthMethod() {
        return AuthMethod;
    }

    public void setAuthMethod(String authMethod) {
        AuthMethod = authMethod;
    }

    public String getMobNumber() {
        return MobNumber;
    }

    public void setMobNumber(String mobNumber) {
        MobNumber = mobNumber;
    }

    public String getAuthToken() {
        return AuthToken;
    }

    public void setAuthToken(String authToken) {
        AuthToken = authToken;
    }

    public String getAvailableSessions() {
        return AvailableSessions;
    }

    public void setAvailableSessions(String availableSessions) {
        AvailableSessions = availableSessions;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getCurrLogin() {
        return CurrLogin;
    }

    public void setCurrLogin(String currLogin) {
        CurrLogin = currLogin;
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

    public String getIpAdd() {
        return ipAdd;
    }

    public void setIpAdd(String ipAdd) {
        this.ipAdd = ipAdd;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
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

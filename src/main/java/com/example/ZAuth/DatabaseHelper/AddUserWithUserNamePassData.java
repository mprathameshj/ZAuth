package com.example.ZAuth.DatabaseHelper;

public class AddUserWithUserNamePassData {
    private String userName;
    private String password;
    private String role;
    private String sessionTime;  //unit->minutes
    private String clientId;
    private String clintApi;
    private String clientApikey;
    private String recoveryMail;
    private String recoveryMob;
    private String platform;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getRecoveryMail() {
        return recoveryMail;
    }

    public void setRecoveryMail(String recoveryMail) {
        this.recoveryMail = recoveryMail;
    }

    public String getRecoveryMob() {
        return recoveryMob;
    }

    public void setRecoveryMob(String recoveryMob) {
        this.recoveryMob = recoveryMob;
    }

    public String getRole() {
        return role;
    }
}

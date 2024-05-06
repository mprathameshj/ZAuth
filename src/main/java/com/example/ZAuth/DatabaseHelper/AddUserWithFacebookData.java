package com.example.ZAuth.DatabaseHelper;

public class AddUserWithFacebookData {
    private String facebookId;
    private String role;
    private String sessionTime;  //unit->minutes
    private String clientId;
    private String clintApi;
    private String clientApikey;
    private String platform;



    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }


    public String getRole() {
        return role;
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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}

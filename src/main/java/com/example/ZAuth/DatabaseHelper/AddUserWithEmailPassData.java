package com.example.ZAuth.DatabaseHelper;

public class AddUserWithEmailPassData {
    private String email;
    private String password;
    private String role;
    private String sessionTime;  //unit->minutes
    private String clientId;
    private String clintApi;
    private String clientApikey;
    private String platform;

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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


    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}

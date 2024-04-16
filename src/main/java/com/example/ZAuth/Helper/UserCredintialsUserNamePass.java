package com.example.ZAuth.Helper;

public class UserCredintialsUserNamePass {
    private String userName;
    private String password;
    private String clientId;
    private String clientApiKey;
    private String clientApiPass;
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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientApiKey() {
        return clientApiKey;
    }

    public void setClientApiKey(String clientApiKey) {
        this.clientApiKey = clientApiKey;
    }

    public String getClientApiPass() {
        return clientApiPass;
    }

    public void setClientApiPass(String clientApiPass) {
        this.clientApiPass = clientApiPass;
    }
}

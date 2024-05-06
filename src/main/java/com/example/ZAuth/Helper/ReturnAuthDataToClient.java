package com.example.ZAuth.Helper;

public class ReturnAuthDataToClient {
    public String userId;
    public String authToken;

    public ReturnAuthDataToClient(String userId,String authToken){
        this.userId=userId;
        this.authToken=authToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}

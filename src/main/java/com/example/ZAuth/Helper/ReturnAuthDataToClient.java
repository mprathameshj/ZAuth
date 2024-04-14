package com.example.ZAuth.Helper;

public class ReturnAuthDataToClient {
    public String userId;
    public String authToken;

    public ReturnAuthDataToClient(String userId,String authToken){
        this.userId=userId;
        this.authToken=authToken;
    }
}

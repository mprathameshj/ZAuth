package com.example.ZAuth.Helper;

public class SMSData {
    private String mobileNumber;
    private String senderToken;
    private String clientId;
    private String clientApiKey;
    private String clientApiPass;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public void setSenderToken(String senderToken) {
        this.senderToken = senderToken;
    }


    public String getClientId() {
        return clientId;
    }

    public String getClientApiKey() {
        return clientApiKey;
    }

    public String getClientApiPass() {
        return clientApiPass;
    }
}

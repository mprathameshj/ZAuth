package com.example.ZAuth.Helper;


public class SMSCrediantials{
    public String timeStamp;
    public String generatedOtp;
    public String senderToken;

    public SMSCrediantials(String timeStamp, String generatedOtp, String senderToken) {
        this.timeStamp = timeStamp;
        this.generatedOtp = generatedOtp;
        this.senderToken = senderToken;
    }
}

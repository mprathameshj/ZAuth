package com.example.ZAuth.Helper;

public class EmailOTPCred {
    public String otp;
    public String timeStamp;

    public EmailOTPCred(String otp, String timeStamp) {
        this.otp = otp;
        this.timeStamp = timeStamp;
    }
}

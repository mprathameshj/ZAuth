package com.example.ZAuth.Helper;

public class VerifyOtpData {
    private String otp;
    private String mobNumber;
    private String senderToken;

    public VerifyOtpData(String otp,String mobNumber,String senderToken){
        this.otp=otp;
        this.mobNumber=mobNumber;
        this.senderToken=senderToken;
    }


    public String getOtp() {
        return otp;
    }


    public String getMobNumber() {
        return mobNumber;
    }


    public String getSenderToken() {
        return senderToken;
    }

}

package com.example.ZAuth.VerificationCache;

import com.example.ZAuth.Helper.EmailOTPCred;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class EmailOtpVerifySatus {
    static HashMap<String, EmailOTPCred> emailOtpMap=new HashMap<>();


    public void addEmailOtp(String email,EmailOTPCred cred){
        emailOtpMap.put(email,cred);
    }


    public boolean containsEmail(String email){
        return  emailOtpMap.containsKey(email);
    }

    public boolean verifyOtp(String email,String otp){
       EmailOTPCred preCred=emailOtpMap.get(email);
        if(preCred==null) return false;

        if (preCred.otp.equals(otp))
            return true;

        return false;
    }

    public static HashMap<String, EmailOTPCred> getEmailmap() {
        return emailOtpMap;
    }

}

package com.example.ZAuth.VerificationCache;
import com.example.ZAuth.Helper.SMSCrediantials;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Component
public class SMSVerificationStatus {
private static  HashMap<String,SMSCrediantials> SMSmap=new HashMap<>();


public void addSMSCrediantilas(String number,SMSCrediantials smsCrediantials){
    SMSmap.put(number,smsCrediantials);
}

public boolean containsNumber(String number){
    return  SMSmap.containsKey(number);
}

public boolean verifyOtp(String number,String senderToken,String otp){
    SMSCrediantials previousCred=SMSmap.get(number);
    if(previousCred==null) return false;

    if(previousCred.senderToken==senderToken&&
       previousCred.generatedOtp==otp) return true;

    return false;
}

    public static Map<String, SMSCrediantials> getSMSmap() {
        return SMSmap;
    }

}


package com.example.ZAuth.VerificationCache;
import com.example.ZAuth.Helper.SMSCrediantials;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class SMSVerificationStatus {
 static  HashMap<String,SMSCrediantials> SMSmap=new HashMap<>();


public void addSMSCrediantilas(String number,SMSCrediantials smsCrediantials){
    SMSmap.put(number,smsCrediantials);
}

public int size(){
    return SMSmap.size();
}

public boolean containsNumber(String number){
    System.out.println("hashmap" +  String.valueOf(SMSmap.get(number)));
    return  SMSmap.containsKey(number);
}

public boolean verifyOtp(String number,String senderToken,String otp){
    SMSCrediantials previousCred=SMSmap.get(number);
    if(previousCred==null) return false;

    if (previousCred.senderToken.equals(senderToken) &&
            previousCred.generatedOtp.equals(otp))
        return true;

    return false;
}

    public static Map<String, SMSCrediantials> getSMSmap() {
        return SMSmap;
    }

}


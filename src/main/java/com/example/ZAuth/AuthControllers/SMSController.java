package com.example.ZAuth.AuthControllers;

import com.example.ZAuth.Helper.SMSCrediantials;
import com.example.ZAuth.Helper.SMSData;
import com.example.ZAuth.SMSServices.TwilioConfig;
import com.example.ZAuth.VerificationCache.SMSVerificationStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class SMSController {

    @Autowired
    TwilioConfig twilioConfig;

    @Autowired
    SMSVerificationStatus smsVerificationStatus;

    @PostMapping("/sendotptonumber")
    public ResponseEntity<String> sendSms(@RequestBody SMSData smsData){
       if(smsData.getMobileNumber()==null||smsData.getSenderToken()==null|| smsData.getMobileNumber().isEmpty()||
          smsData.getSenderToken().isEmpty())
           return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body("Mobile number or sender token is missing");

       String otp=generateOTP();

      twilioConfig.sendSms(smsData.getMobileNumber(),"Your One time password (OTP) is "+otp);
      String timeStAmp= String.valueOf(System.currentTimeMillis());
      smsVerificationStatus.addSMSCrediantilas(smsData.getMobileNumber(),
              new SMSCrediantials(timeStAmp,otp,smsData.getSenderToken()));

       return  ResponseEntity.ok("OTP sent succesfull");
    }


    // Method to generate a random OTP (example: 6-digit number)
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit OTP
        return String.valueOf(otp);
    }

}

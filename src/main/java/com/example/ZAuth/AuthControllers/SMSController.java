package com.example.ZAuth.AuthControllers;

import com.example.ZAuth.Cache.ClientIdCache;
import com.example.ZAuth.DataEncryptor.BcryptEncrypt;
import com.example.ZAuth.DatabaseHelper.AddUserWithMobNumData;
import com.example.ZAuth.FirebaseClasses.MyFirebase;
import com.example.ZAuth.Helper.ReturnAuthDataToClient;
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
import java.util.UUID;

@RestController
public class SMSController {

    @Autowired
    TwilioConfig twilioConfig;

    @Autowired
    SMSVerificationStatus smsVerificationStatus;

    @Autowired
    ClientIdCache clientIdCache;

    @Autowired
    MyFirebase firebase;



    @PostMapping("/sendotptonumber")
    public ResponseEntity<String> sendSms(@RequestBody SMSData smsData){

        if(!clientIdCache.validateClient(smsData.getClientId()
                , smsData.getClientApiKey()
                , smsData.getClientApiPass())){
            return ResponseEntity.badRequest().body("Invalid Credintials");
        }

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


    //Verify the OTP
    @PostMapping("/verifyotpAndAddUser")
    public ResponseEntity<?> verifyOtp(@RequestBody AddUserWithMobNumData data){

        if(!smsVerificationStatus.containsNumber(data.getMobNumber()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("OTP expired");

        if(verifyOtpHelper(data.getMobNumber(), data.getOtp(), data.getSenderToken())){

             String token= String.valueOf(UUID.randomUUID());
             String encryptedToken=BcryptEncrypt.encrypt(token);

            //User may exist in database
             String result=firebase.findUserByMobileAndUpdateAuthToken(data.getMobNumber(),
                     data.getClientId(),
                     encryptedToken,data.getPlatform());

             if(result.equals("0")) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("The user is blocked");//blocked user
             else if(result.equals("3")) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");//exception
             else if (result.equals("2")) {
                 //User is new
               String userId=  firebase.createUserWithMobCredintials(data, encryptedToken);
                 return ResponseEntity.ok(new ReturnAuthDataToClient(userId,token));
             }else{
                 return ResponseEntity.ok(new ReturnAuthDataToClient(result,token));
             }
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
    }



    // Method to generate a random OTP (example: 6-digit number)
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit OTP
        return String.valueOf(otp);
    }

    //Method to verify otp
    public  boolean verifyOtpHelper(String mobNum,String otp,String senderToken){
        return smsVerificationStatus.verifyOtp(mobNum,
                senderToken,
                otp);
    }


}

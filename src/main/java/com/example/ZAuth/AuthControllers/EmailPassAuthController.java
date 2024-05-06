package com.example.ZAuth.AuthControllers;

import com.example.ZAuth.Cache.ClientIdCache;
import com.example.ZAuth.DataEncryptor.BcryptEncrypt;
import com.example.ZAuth.DatabaseHelper.AddUserWithEmailOtp;
import com.example.ZAuth.DatabaseHelper.AddUserWithEmailPassData;
import com.example.ZAuth.FirebaseClasses.MyFirebaseTwo;
import com.example.ZAuth.Helper.EmailOTPCred;
import com.example.ZAuth.Helper.OTPEmailData;
import com.example.ZAuth.Helper.ReturnAuthDataToClient;
import com.example.ZAuth.SMSServices.EmailSender;
import com.example.ZAuth.VerificationCache.EmailOtpVerifySatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.UUID;

@RestController
public class EmailPassAuthController {

    @Autowired
    ClientIdCache clientIdCache;

    @Autowired
    MyFirebaseTwo myFirebaseTwo;

    @Autowired
    EmailSender emailSender;

    @Autowired
    EmailOtpVerifySatus emailOtpVerifySatus;

    @PostMapping("/sendOTPToEmail")
    public ResponseEntity<?> emailOTP(@RequestBody OTPEmailData data){
        if(!clientIdCache.validateClient(data.getClientId(),
                data.getClientApi(), data.getClientApiPass())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid_client_credintials");
        }
        String otp=generateOTP();
        emailSender.sendEmail(data.getEmail(),"One Time Password",
                "Your one time password (OTP) is "+otp);

        emailOtpVerifySatus.addEmailOtp(data.getEmail(),
                new EmailOTPCred(otp,String.valueOf(System.currentTimeMillis())));

        return ResponseEntity.ok("Email Sent Successfully");
    }


    @PostMapping("/verifyOtpAndAddUserWithEmail")
    public ResponseEntity<?> verifyAndAdd(@RequestBody AddUserWithEmailOtp data){
        if(!clientIdCache.validateClient(data.getClientId(),
                data.getClintApi(), data.getClientApikey())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid_client_credintials");
        }

        if(!emailOtpVerifySatus.containsEmail(data.getEmail()))
            return ResponseEntity.status(HttpStatus.GONE).body("The otp has been expired");

        if(!emailOtpVerifySatus.verifyOtp(data.getEmail(), data.getOtp()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The otp is wrong");

        String token= String.valueOf(UUID.randomUUID());
        String encrptedToken=BcryptEncrypt.encrypt(token);

        String result=myFirebaseTwo.findUserByEmailAndUpdateAuthToken(data.getEmail(),
                data.getClientId(),encrptedToken, data.getPlatform());

        if(result.startsWith("ERROR")) return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        else if (result.equals("BLOCKED")) return ResponseEntity.status(HttpStatus.LOCKED).body("user_is_blocked");
        else if(result.equals("NEWUSER")){
            String userId=myFirebaseTwo.createUserWithEmailCredintials(data,encrptedToken);
            return ResponseEntity.ok(new ReturnAuthDataToClient(userId,token));
        }else{
            return ResponseEntity.ok(new ReturnAuthDataToClient(result,token));
        }
    }


    @PostMapping("/createUserWithEmailPass")
    public ResponseEntity<?> createUser(@RequestBody AddUserWithEmailPassData data){

        if(!clientIdCache.validateClient(data.getClientId(),
                data.getClintApi(), data.getClientApikey())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid_client_credintials");
        }

        String token= String.valueOf(UUID.randomUUID());
        String encryptedToken= BcryptEncrypt.encrypt(token);

        String result= myFirebaseTwo.findUserByEmailPassAndUpdateAuthToken(data.getEmail(),
                data.getClientId(),encryptedToken,data.getPlatform(),data.getPassword());

        if(result.equals("BLOCKED")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user_is_blocked");
        else if(result.equals("WRONG")) return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("wrong_password");
        else if(result.startsWith("ERROR")) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        else if(result.equals("NEWUSER")){
            String userId= myFirebaseTwo.createUserWithEmailPassCredintials(data,encryptedToken);
            return ResponseEntity.ok(new ReturnAuthDataToClient(userId,token));
        }else{
            return ResponseEntity.ok(new ReturnAuthDataToClient(result,token));
        }
    }




    // Method to generate a random OTP (example: 6-digit number)
    private String generateOTP() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // Generate 6-digit OTP
        return String.valueOf(otp);
    }
}

package com.example.ZAuth.AuthControllers;

import com.example.ZAuth.Cache.ClientIdCache;
import com.example.ZAuth.DataEncryptor.BcryptEncrypt;
import com.example.ZAuth.DatabaseHelper.AddUserWithGoogleData;
import com.example.ZAuth.FirebaseClasses.MyFirebaseTwo;
import com.example.ZAuth.Helper.ReturnAuthDataToClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class GoogleEmailAuthController {

    @Autowired
    ClientIdCache clientIdCache;

    @Autowired
    MyFirebaseTwo myFirebaseTwo;


    @PostMapping("/createUserWithTheGoogle")
    public ResponseEntity<?> createUser(@RequestBody AddUserWithGoogleData data){
        try{
            if(!clientIdCache.validateClient(data.getClientId(),
                    data.getClintApi(),
                    data.getClientApikey())){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid_client_credintials");
            }

            String token= String.valueOf(UUID.randomUUID());
            String encryptedToken= BcryptEncrypt.encrypt(token);

            String result=myFirebaseTwo.findUserByEmailAndUpdateAuthToken(data.getEmail(),
                    data.getClientId(),encryptedToken, data.getPlatform());

            if(result.startsWith("ERROR")) return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            else if (result.equals("BLOCKED")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("user_is_blocked");
            else if(result.equals("NEWUSER")){
                String userId=myFirebaseTwo.createUserWithEmailCredintials(data,encryptedToken);
                return ResponseEntity.ok(new ReturnAuthDataToClient(userId,token));
            }else{
                return ResponseEntity.ok(new ReturnAuthDataToClient(result,token));
            }

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("server_error");
        }
    }

}

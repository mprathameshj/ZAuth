package com.example.ZAuth.AuthControllers;

import com.example.ZAuth.Cache.ClientIdCache;
import com.example.ZAuth.DataEncryptor.BcryptEncrypt;
import com.example.ZAuth.DatabaseHelper.AddUserWithFacebookData;
import com.example.ZAuth.FirebaseClasses.MyFirebaseThree;
import com.example.ZAuth.Helper.ReturnAuthDataToClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class FacebookAuthControllerTwo {

    @Autowired
    ClientIdCache clientIdCache;

    @Autowired
    MyFirebaseThree myFirebaseThree;

    @PostMapping("/addUserWithFacebook")
    public ResponseEntity<?> addUser(@RequestBody AddUserWithFacebookData data){
        if(!clientIdCache.validateClient(data.getClientId(),
                data.getClintApi(), data.getClientApikey())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid_client_credintials");
        }

        String token= String.valueOf(UUID.randomUUID());
        String encryptedToken= BcryptEncrypt.encrypt(token);

        String result= myFirebaseThree.findUserByFacebookIdAndUpdateToken(data.getFacebookId(),encryptedToken,
                data.getClientId(),data.getPlatform());

        if(result.equals("BLOCKED")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user_is_blocked");
        else if(result.startsWith("ERROR")) return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(result);
        else if(result.equals("NEWUSER")){
            String userId= myFirebaseThree.createUserWithFacebookCredintials(data,encryptedToken);
            return ResponseEntity.ok(new ReturnAuthDataToClient(userId,token));
        }else return ResponseEntity.ok(new ReturnAuthDataToClient(result,token));

    }
}

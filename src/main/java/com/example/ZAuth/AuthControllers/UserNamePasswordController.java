package com.example.ZAuth.AuthControllers;

import com.example.ZAuth.Cache.ClientIdCache;
import com.example.ZAuth.DataEncryptor.BcryptEncrypt;
import com.example.ZAuth.DatabaseHelper.AddUserWithUserNamePassData;
import com.example.ZAuth.FirebaseClasses.MyFirebase;
import com.example.ZAuth.Helper.ReturnAuthDataToClient;
import com.example.ZAuth.Helper.ReturnStringData;
import com.example.ZAuth.Helper.UserCredintialsUserNamePass;
import com.google.firebase.auth.hash.Bcrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserNamePasswordController {

    @Autowired
    ClientIdCache clientIdCache;

    @Autowired
    MyFirebase myFirebase;

    @PostMapping("/createUserWithUsenameAndPassword")
    public ResponseEntity<?> createUser(@RequestBody AddUserWithUserNamePassData data){

        if (!clientIdCache.validateClient(data.getClientId()
                                       , data.getClintApi(),
                                         data.getClientApikey())){
          return   ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Client");
        }


        String token= String.valueOf(UUID.randomUUID());
        String encryptedToken=BcryptEncrypt.encrypt(token);

        String userId= myFirebase.addUserWithUseNamePass(data,encryptedToken);

        if(userId.equals("DUPLICATEUSER")) return ResponseEntity.status(HttpStatus.CONFLICT).body("Username is already registred");

        return ResponseEntity.ok(new ReturnAuthDataToClient(userId,token));
    }

    @PostMapping("/validateUserNamePassword")
    public ResponseEntity<?> checkUser(@RequestBody UserCredintialsUserNamePass credintials){
        if(!clientIdCache.validateClient(credintials.getClientId()
                                        ,credintials.getClientApiKey(),
                                         credintials.getClientApiPass())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Client is invalid");
        }

        String token= String.valueOf(UUID.randomUUID());
        String encryptedToken=BcryptEncrypt.encrypt(token);

        String result=myFirebase.validateUsernamePass(credintials,encryptedToken);

        if (result.startsWith("ERROR")){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server is experiancing crashesh");
        }else if (result.equals("NEWUSER")){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User is new");
        }else if(result.equals("INVALID")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("wrong password");
        }else{
            return ResponseEntity.ok(new ReturnAuthDataToClient(result,token));
        }
    }
}

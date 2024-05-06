package com.example.ZAuth.AuthControllers;

import com.example.ZAuth.Cache.AuthTokenCache;
import com.example.ZAuth.DatabaseHelper.TokenVerifyData;
import com.example.ZAuth.FirebaseClasses.MyFirebaseOne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenValidityController {

    @Autowired
    MyFirebaseOne myFirebaseOne;

    @Autowired
    AuthTokenCache authTokenCache;

    @PostMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestBody TokenVerifyData data){

        String cacheResult= authTokenCache.validateToken(data);

        if (cacheResult.equals("NO")) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        else if (cacheResult.equals("OK")) {
         myFirebaseOne.updateLastLogin(data.getClientId(),data.getUserId());
         return ResponseEntity.ok("Authorosed");
        }

        if(data.getPlatform().equals("WEB"))
            return validateTokenHelper(data);

        String result= myFirebaseOne.validateToken(data);

        if(result.equals("INVALID")||result.equals("UNKNOWN")){
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        } else if (result.equals("OK")) {
            return ResponseEntity.ok("AuthenTicated");
        } else if (result.equals("EXPIRED")) {
            return ResponseEntity.status(HttpStatus.GONE).body("The login has been expired , please login again");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
    }

    public ResponseEntity<?> validateTokenHelper(TokenVerifyData data){
        String result= myFirebaseOne.validateTokenWeb(data);

        if(result.equals("INVALID")||result.equals("UNKNOWN")){
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not logged in");
        } else if (result.equals("OK")) {
            return ResponseEntity.ok("AuthenTicated");
        } else if (result.equals("EXPIRED")) {
            return ResponseEntity.status(HttpStatus.GONE).body("The login has been expired , please login again");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
    }
}

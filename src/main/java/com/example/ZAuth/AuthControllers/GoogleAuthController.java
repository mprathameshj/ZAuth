package com.example.ZAuth.AuthControllers;

import com.example.ZAuth.Cache.ClientCredintialCache;
import com.example.ZAuth.Cache.ClientIdCache;
import com.example.ZAuth.DataEncryptor.BcryptEncrypt;
import com.example.ZAuth.DataModelsForClientCred.GoogleCred;
import com.example.ZAuth.DatabaseHelper.AddUserWithGoogleAndroid;
import com.example.ZAuth.FirebaseClasses.MyFirebaseTwo;
import com.example.ZAuth.Helper.ReturnAuthDataToClient;
import com.example.ZAuth.Helper.ReturnUserInfoAfterAuthSuccess;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

@RestController
public class GoogleAuthController {

    private static final String SCOPE = "openid profile email";
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    public GoogleAuthController() throws GeneralSecurityException, IOException {
    }

    @Autowired
    MyFirebaseTwo myFirebaseTwo;

    @Autowired
    ClientIdCache clientIdCache;
    @Autowired
    ClientCredintialCache clientCredintialCache;


    @GetMapping("/GoogleLoginBuild")
    public ResponseEntity<?> googleLogin(HttpServletResponse response,@RequestParam("clientId") String clientId,
                                         @RequestParam("api") String api,@RequestParam("apipass") String apiPass) throws IOException {

        //Validate client details
        if(!clientIdCache.validateClient(clientId, api, apiPass)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid_client_credientilas");
        }

        

        GoogleCred cred=clientCredintialCache.getGoogleAuthCredintials(clientId);
        if (cred==null) return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("" +
                "The client secretes are missing ,please add the google api key and password in ZAuth dashboard");

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, cred.getGoogleId(),cred.getGoogleIdSecrete(), Collections.singletonList(SCOPE))
                .build();

        String REDIRECT_URI = "http://localhost:8080/googleLoginCallback/"+clientId;


        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI);

        response.sendRedirect(url.build());
        return ResponseEntity.ok("Redirecting");
    }

    @GetMapping("/googleLoginCallback/{clientId}")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam("code") String code,
                                                   @PathVariable("clientId") String clientId) {
        try {

            System.out.println(clientId);
            GoogleCred cred=clientCredintialCache.getGoogleAuthCredintials("ASDFGHJKL");
            if (cred==null) return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("" +
                    "The client secretes are missing ,please add the google api key and password in ZAuth dashboard");


            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, cred.getGoogleId(),cred.getGoogleIdSecrete(), Collections.singletonList(SCOPE))
                    .build();
            // Handle the Google callback to exchange the authorization code for tokens
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri("http://localhost:8080/googleLoginCallback/"+clientId)
                    .execute();

            // Extract user email from the ID token
            GoogleIdToken.Payload payload = tokenResponse.parseIdToken().getPayload();
            String googleId= payload.getUserId();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String profilePictureUrl = (String) payload.get("picture");

            if(email==null|| email.isEmpty()){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error_during_authentication");
            }

            AddUserWithGoogleAndroid data=new AddUserWithGoogleAndroid();
            data.setEmail(email);
            data.setGoogleId(googleId);
            data.setPlatform("WEB");
            data.setRole("nullByDefault");
            data.setSessionTime("null");
            data.setClientId(clientId);

            String token= String.valueOf(UUID.randomUUID());
            String encryptedToken= BcryptEncrypt.encrypt(token);

            String result=myFirebaseTwo.updateUserWithGoogleLogin(data,encryptedToken);

            if(result.startsWith("ERROR")) return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            else if (result.equals("BLOCKED")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user_is_blocked");
            else if(result.equals("NEWUSER")){
                String userId=myFirebaseTwo.createUserWithGoogleLogin(data,encryptedToken);
                return ResponseEntity.ok(new ReturnAuthDataToClient(userId,token));
            }else{
                return ResponseEntity.ok(new ReturnAuthDataToClient(result,token));
            }


        } catch (IOException e) {
            // Handle error appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }

    @PostMapping("/androidGoogleSignInOrLogin")
    public ResponseEntity<?> signInGoogle(@RequestBody AddUserWithGoogleAndroid data){
        if(!clientIdCache.validateClient(data.getClientId(),
                data.getClintApi(), data.getClientApikey())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid_client_credintials");
        }

        String token= String.valueOf(UUID.randomUUID());
        String encryptedToken= BcryptEncrypt.encrypt(token);

        String result=myFirebaseTwo.updateUserWithGoogleLogin(data,encryptedToken);

        if(result.startsWith("ERROR")) return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        else if (result.equals("BLOCKED")) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user_is_blocked");
        else if(result.equals("NEWUSER")){
            String userId=myFirebaseTwo.createUserWithGoogleLogin(data,encryptedToken);
            return ResponseEntity.ok(new ReturnAuthDataToClient(userId,token));
        }else{
            return ResponseEntity.ok(new ReturnAuthDataToClient(result,token));
        }
    }

}

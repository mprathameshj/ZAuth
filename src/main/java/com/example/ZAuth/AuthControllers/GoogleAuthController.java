package com.example.ZAuth.AuthControllers;

import com.example.ZAuth.Cache.ClientIdCache;
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

@RestController
public class GoogleAuthController {

    private static final String CLIENT_ID = "122784136099-b192mrnguj4hauien22urkbb8jagcfcp.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8080/googleLoginCallback";
    private static final String SCOPE = "openid profile email";

    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

    public GoogleAuthController() throws GeneralSecurityException, IOException {
    }

    @Autowired
    ClientIdCache clientIdCache;


    @GetMapping("/GoogleLoginBuild")
    public ResponseEntity<?> googleLogin(HttpServletResponse response,@RequestParam("clientId") String clientId,
                                         @RequestParam("api") String api,@RequestParam("apipass") String apiPass) throws IOException {

        //Validate client details
        if(!clientIdCache.validateClient(clientId, api, apiPass)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid_client_credientilas");
        }

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, Collections.singletonList(SCOPE))
                .build();

        GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl()
                .setRedirectUri(REDIRECT_URI);

        response.sendRedirect(url.build());
        return ResponseEntity.ok("Redirecting");
    }

    @GetMapping("/googleLoginCallback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam("code") String code) {
        try {

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, Collections.singletonList(SCOPE))
                    .build();
            // Handle the Google callback to exchange the authorization code for tokens
            GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                    .setRedirectUri(REDIRECT_URI)
                    .execute();

            // Extract user email from the ID token
            GoogleIdToken.Payload payload = tokenResponse.parseIdToken().getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            String profilePictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String gender = (String) payload.get("gender");
            String birthday = (String) payload.get("birthdate");

            if(email==null|| email.isEmpty()){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error_during_authentication");
            }

            ReturnUserInfoAfterAuthSuccess data=new ReturnUserInfoAfterAuthSuccess();
            data.setEmail(email);
            if(name!=null) data.setName(name);
            if (profilePictureUrl!=null) data.setProfilePictureUrl(profilePictureUrl);

            return ResponseEntity.ok().body(data);
        } catch (IOException e) {
            // Handle error appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error_during_authentication");
        }
    }
}

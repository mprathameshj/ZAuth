package com.example.ZAuth.AuthControllers;

import com.example.ZAuth.DataEncryptor.BcryptEncrypt;
import com.example.ZAuth.DatabaseHelper.AddUserWithFacebookData;
import com.example.ZAuth.FirebaseClasses.MyFirebaseThree;
import com.example.ZAuth.Helper.ReturnAuthDataToClient;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@RestController
public class FacebookAuthController {

    @Autowired
    MyFirebaseThree myFirebaseThree;

    @Value("${facebook.app.id}")
    private String facebookAppId;

    @Value("${facebook.app.secret}")
    private String facebookAppSecret;

    private static final String FACEBOOK_GRAPH_API_BASE_URL = "https://graph.facebook.com/v12.0";

    @GetMapping("/loginWithFacebook")
    public String redirectToFacebookLogin(@RequestParam("clientId") String clientId,
                                          @RequestParam("api") String api,
                                          @RequestParam("apipass") String apiPass,
                                          HttpServletResponse response) {
        String redirectUri = "http://localhost:8080/callbackFacebook/"+clientId;
        String facebookLoginUrl = "https://www.facebook.com/v12.0/dialog/oauth"
                + "?client_id=" + facebookAppId
                + "&redirect_uri=" + redirectUri;


        try {
            response.sendRedirect(facebookLoginUrl);
        } catch (IOException e) {
            return e.toString();
        }
        return "Please complete the authentication";
    }

    @GetMapping("/callbackFacebook/{clientId}")
    public ResponseEntity<?> handleFacebookCallback(@RequestParam("code") String code,
                                                    @PathVariable("clientId") String clientId) {
        // Exchange code for access token
        String accessToken = getAccessToken(code,clientId);

        // Use the access token to get user profile
        FacebookUserProfileResponse userProfile = getUserProfile(accessToken);

        AddUserWithFacebookData data=new AddUserWithFacebookData();
        data.setFacebookId(userProfile.id);
        data.setClientId(clientId);
        data.setPlatform("WEB");
        data.setRole("nullByDefault");
        data.setSessionTime("null");

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

    private String getAccessToken(String code,String clientId) {
        String tokenEndpoint = "https://graph.facebook.com/v13.0/oauth/access_token";
        String params = "?client_id=" + facebookAppId +
                "&redirect_uri=" + "http://localhost:8080/callbackFacebook/"+clientId +
                "&client_secret=" + facebookAppSecret +
                "&code=" + code;

        String accessTokenUrl = tokenEndpoint + params;

        RestTemplate restTemplate = new RestTemplate();
        FacebookAccessTokenResponse response = restTemplate.getForObject(accessTokenUrl, FacebookAccessTokenResponse.class);

        return response.getAccess_token();
    }
    private String fetchUserProfile(String accessToken) {
        String userProfileUri = FACEBOOK_GRAPH_API_BASE_URL + "/me?fields=id,name,email";
        String userProfileUriWithToken = userProfileUri + "&access_token=" + accessToken;

        RestTemplate restTemplate = new RestTemplate();
        String userProfile = restTemplate.getForObject(userProfileUriWithToken, String.class);

        // Handle user profile data (parse JSON)
        return userProfile;
    }
    private FacebookUserProfileResponse getUserProfile(String accessToken) {
        String profileEndpoint = "https://graph.facebook.com/v13.0/me";
        String params = "?fields=id,name,email&access_token=" + accessToken;

        String profileUrl = profileEndpoint + params;

        RestTemplate restTemplate = new RestTemplate();
        FacebookUserProfileResponse response = restTemplate.getForObject(profileUrl, FacebookUserProfileResponse.class);

        return response;
    }
}

class FacebookAccessTokenResponse {
    private String access_token;
    private Long expires_in;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public Long getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(Long expires_in) {
        this.expires_in = expires_in;
    }
}

class FacebookUserProfileResponse {
    public String id;
    public String name;
    public String email;

    // Getters and setters for id, name, email

    @Override
    public String toString() {
        return "ID: " + id + ", Name: " + name + ", Email: " + email;
    }
}
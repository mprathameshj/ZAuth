package com.example.ZAuth.AuthControllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class FacebookAuthController {

    @Value("${facebook.app.id}")
    private String facebookAppId;

    @Value("${facebook.app.secret}")
    private String facebookAppSecret;

    private static final String FACEBOOK_GRAPH_API_BASE_URL = "https://graph.facebook.com/v12.0";

    @GetMapping("/loginWithFacebook")
    public String redirectToFacebookLogin() {
        String redirectUri = "http://localhost:8080/callbackFacebook";
        String facebookLoginUrl = "https://www.facebook.com/v12.0/dialog/oauth"
                + "?client_id=" + facebookAppId
                + "&redirect_uri=" + redirectUri;
        return "redirect:" + facebookLoginUrl;
    }

    @GetMapping("/callbackFacebook")
    public ResponseEntity<?> handleFacebookCallback(@RequestParam("code") String code) {
        // Exchange code for access token
        String accessToken = getAccessToken(code);

        // Use the access token to get user profile
        FacebookUserProfileResponse userProfile = getUserProfile(accessToken);

        return ResponseEntity.ok(userProfile);
    }

    private String getAccessToken(String code) {
        String tokenEndpoint = "https://graph.facebook.com/v13.0/oauth/access_token";
        String params = "?client_id=" + facebookAppId +
                "&redirect_uri=" + "http://localhost:8080/callbackFacebook" +
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
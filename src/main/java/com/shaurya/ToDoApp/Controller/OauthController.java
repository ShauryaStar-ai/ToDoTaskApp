package com.shaurya.ToDoApp.Controller;
import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/oauth/google")
public class OauthController {
    @Autowired RestTemplate restTemplate;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @GetMapping("/callback")
    // This fucntion is called by the front end when the user is authenicated and the front end calls by passing in
    // acess code. The role of this function is to auth code for tokens
    public ResponseEntity<String> giveCodeWhenProvidedTocken(@RequestParam("code") String accessCode){
        
        // Using environment variables for ClientID and ClientSecret
        String clientId = System.getenv("ClientID");
        String clientSecret = System.getenv("ClientSecret");
        

        try {
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", accessCode);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);

            params.add("redirect_uri", "https://developers.google.com/oauthplayground");
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            String idToken = (String) tokenResponse.getBody().get("id_token");

            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                
                // Check if user exists, if not create one
                User user = null;
                try {
                     user = userService.getUserByUserName(email);
                } catch (Exception e) {
                    // User not found, create new
                }

                if(user == null){
                    User newUser = new User();
                    newUser.setUserName(email);
                    newUser.setEmailAddress(email);
                    newUser.setRoles(new ArrayList<>(Arrays.asList("USER")));
                    String password = "GoogleUser";
                    newUser.setPassWord(password);
                    userService.saveNewUser(newUser);
                }
                return ResponseEntity.ok("User authenticated successfully: " + email);
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to get user info");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}

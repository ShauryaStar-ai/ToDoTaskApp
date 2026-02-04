package com.shaurya.ToDoApp.Controller;
import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
public class OauthController {
    @Autowired RestTemplate restTemplate;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String githubClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String githubClientSecret;


    /*
     * GOOGLE AUTHENTICATION FLOW
     * 1. Frontend redirects user to Google's OAuth URL.
     * 2. User logs in and approves.
     * 3. Google redirects back to frontend with a "code".
     * 4. Frontend calls this endpoint with that "code".
     */
    @GetMapping("/google/callback")
    public ResponseEntity<String> giveCodeWhenProvidedTocken(@RequestParam("code") String accessCode){
        
        // 1. Retrieve Client ID and Secret from environment variables
        String clientId = System.getenv("GoogleClientID");
        String clientSecret = System.getenv("GoogleClientSecret");
        
        try {
            // 2. Prepare the request to exchange the 'code' for an 'access token' (and id_token)
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("code", accessCode);
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("redirect_uri", "https://developers.google.com/oauthplayground"); // Must match the one used in frontend
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // 3. Make the POST request to Google to get the tokens
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            
            if (tokenResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve token from Google");
            }
            
            // 4. Extract the 'id_token' which contains user identity info
            String idToken = (String) tokenResponse.getBody().get("id_token");

            // 5. Use the id_token to get user details (Email, Name, etc.)
            // This is the "User Info URL" for Google
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            if (userInfoResponse.getStatusCode() == HttpStatus.OK && userInfoResponse.getBody() != null) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                
                // 6. Check if user exists in our DB, if not create one
                User user = null;
                try {
                     user = userService.getUserByUserName(email);
                } catch (Exception e) {
                    // User not found, will create new below
                }

                if(user == null){
                    User newUser = new User();
                    newUser.setUserName(email);
                    newUser.setEmailAddress(email);
                    newUser.setRoles(new ArrayList<>(Arrays.asList("USER")));
                    // Set a default password for OAuth users (they won't use it directly usually)
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

    /*
     * GITHUB AUTHENTICATION FLOW
     * To generate the auth code, your frontend should redirect the user to:
     * https://github.com/login/oauth/authorize?client_id=YOUR_CLIENT_ID&redirect_uri=http://localhost:8080/oauth/github/callback&scope=user:email
     * 
     * After user approves, GitHub redirects to your callback URL with ?code=...
     */
    @GetMapping("/github/callback")
    public ResponseEntity<String> githubCallback(@RequestParam("code") String accessCode) {
        System.out.println("Received GitHub code: " + accessCode);
        
        // 1. Retrieve Client ID and Secret from application.properties (which gets them from env vars)
        // This is more reliable if Spring is already handling the property resolution
        String clientId = githubClientId;
        String clientSecret = githubClientSecret;

        if (clientId == null || clientSecret == null) {
            return ResponseEntity.internalServerError().body("Error: GitHubClientID or GitHubClientSecret properties are not set.");
        }

        try {
            // 2. Prepare the request to exchange the 'code' for an 'access token'
            String tokenEndpoint = "https://github.com/login/oauth/access_token";

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("client_id", clientId);
            params.add("client_secret", clientSecret);
            params.add("code", accessCode);
            // Important: If you provided a redirect_uri in the authorization URL, you MUST provide it here too.
            params.add("redirect_uri", "http://localhost:8080/oauth/github/callback"); 

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON)); // We want JSON response

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // 3. Make the POST request to GitHub to get the access token
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);
            
            if (tokenResponse.getBody() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve access token from GitHub: Empty response body");
            }
            
            // 4. Extract the 'access_token'
            String accessToken = (String) tokenResponse.getBody().get("access_token");

            if (accessToken == null) {
                // If access_token is null, GitHub likely returned an error. Let's return that error to the user.
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to retrieve access token from GitHub. Response: " + tokenResponse.getBody());
            }

            // 5. Use the access_token to get user details
            // This is the "User Info URL" for GitHub
            String userInfoUrl = "https://api.github.com/user";
            
            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setBearerAuth(accessToken); // GitHub requires Bearer token in header
            HttpEntity<String> entity = new HttpEntity<>(authHeaders);

            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, Map.class);

            if (userInfoResponse.getStatusCode() == HttpStatus.OK && userInfoResponse.getBody() != null) {
                Map<String, Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");

                // 6. Special handling for GitHub: Email might be private (null)
                // If so, we need to fetch it from a different endpoint
                if (email == null) {
                    String emailsUrl = "https://api.github.com/user/emails";
                    ResponseEntity<List> emailsResponse = restTemplate.exchange(emailsUrl, HttpMethod.GET, entity, List.class);
                    
                    if (emailsResponse.getStatusCode() == HttpStatus.OK && emailsResponse.getBody() != null) {
                        List<Map<String, Object>> emails = emailsResponse.getBody();
                        for (Map<String, Object> emailObj : emails) {
                            // We look for the primary and verified email
                            if ((Boolean) emailObj.get("primary") && (Boolean) emailObj.get("verified")) {
                                email = (String) emailObj.get("email");
                                break;
                            }
                        }
                    }
                }

                if (email == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not retrieve email from GitHub");
                }

                // 7. Check if user exists in our DB, if not create one
                User user = null;
                try {
                    user = userService.getUserByUserName(email);
                } catch (Exception e) {
                    // User not found
                }

                if (user == null) {
                    User newUser = new User();
                    newUser.setUserName(email);
                    newUser.setEmailAddress(email);
                    newUser.setRoles(new ArrayList<>(Arrays.asList("USER")));
                    // Set a default password for GitHub users
                    newUser.setPassWord("GitHubUser");
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

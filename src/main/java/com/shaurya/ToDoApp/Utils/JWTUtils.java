package com.shaurya.ToDoApp.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTUtils {

    // This injects the value from application.properties where key is "jwt.secret"
    // Yes, the name "jwt.secret" is totally up to you. You could call it "my.super.secret.key"
    // as long as it matches what is in application.properties.
    @Value("${jwt.secret}")
    private String SECRET;

    // Generates a token for a given username
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    // Creates the actual JWT string
    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims) // Claims are extra data you want to store in the token (like roles, email, etc.)
                .setSubject(userName) // The "Subject" is usually the username or user ID
                .setIssuedAt(new Date(System.currentTimeMillis())) // When was this token created?
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 3)) // Expires in 3 minutes
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Sign the token with your secret key so it can't be tampered with
                .compact();
    }

    // Decodes your secret key into a format that the JWT library can use
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Extracts the username (subject) from the token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Generic method to extract any specific piece of information (Claim) from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parses the token to get all the data (Claims) inside it
    // This method will fail if the token has been tampered with (signature check fails)
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Checks if the token has expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Gets the expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Validates the token:
    // 1. Does the username in the token match the user we are checking against?
    // 2. Is the token not expired?
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}

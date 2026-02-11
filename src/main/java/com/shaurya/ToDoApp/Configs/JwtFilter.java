package com.shaurya.ToDoApp.Configs;

import com.shaurya.ToDoApp.Utils.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Get the Authorization header from the incoming request
        String authorizationHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 2. Check if the header is present and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Extract the actual token (remove "Bearer " prefix)
            username = jwtUtils.extractUsername(jwt); // Extract the username from the token
        }

        // 3. If we have a username and the user is not already authenticated in the current context
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Load the user details from the database using the username
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 4. Validate the token against the user details
            if (jwtUtils.validateToken(jwt, userDetails.getUsername())) {
                
                // Create an authentication object (UsernamePasswordAuthenticationToken)
                // This object tells Spring Security that the user is authenticated
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                
                // Set additional details (like IP address, session ID) from the request
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 5. Set the authentication in the SecurityContext
                // This effectively logs the user in for this specific request
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        
        // 6. Continue the filter chain (pass the request to the next filter or the controller)
        filterChain.doFilter(request, response);
    }
}

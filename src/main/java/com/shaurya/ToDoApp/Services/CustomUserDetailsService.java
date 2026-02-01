package com.shaurya.ToDoApp.Services;

import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Repositires.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String[] roles = user.getRoles() != null
                ? user.getRoles().toArray(new String[0])
                : new String[0];

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassWord()) // how does this know about hash encoding of passwords
                .roles(roles)
                .build();
    }
}

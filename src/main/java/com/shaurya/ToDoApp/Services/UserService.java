package com.shaurya.ToDoApp.Services;

import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Repositires.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class UserService {
    @Autowired
    UserRepo userRepo;
    public User getLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

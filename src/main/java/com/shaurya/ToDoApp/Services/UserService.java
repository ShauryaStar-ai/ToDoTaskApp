package com.shaurya.ToDoApp.Services;

import com.shaurya.ToDoApp.Objects.Task;
import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Repositires.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    public User getUserByUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        return userRepo.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean saveNewUser(User user){
        boolean userSavedSucessfully = false;
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // 2. Required field checks
        if (user.getUserName().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (user.getPassWord().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        // 3. Uniqueness checks
        if (userRepo.existsByUserName(user.getUserName())) {
            throw new IllegalStateException("Username already exists");
        }

        else{        userRepo.save(user);
        userSavedSucessfully = true;
        }
        return userSavedSucessfully;
    }
}

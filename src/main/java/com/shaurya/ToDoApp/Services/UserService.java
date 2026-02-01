package com.shaurya.ToDoApp.Services;

import com.shaurya.ToDoApp.Objects.Task;
import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Repositires.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    public User getUserByUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        return userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public User getUserByUserName(String userName) {
        return userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public List<User> returnAllEntries(){
        return userRepo.findAll();
    }
    public boolean saveUpdatedUser(User userNew){
        boolean userSaved ;
        try{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        User user = userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserName(userNew.getUserName());
        user.setPassWord(p.encode(userNew.getPassWord()));
        user.setRoles(userNew.getRoles());
        user.setEmailAddress(userNew.getEmailAddress());
        userRepo.save(user);
             userSaved = true;
        return userSaved;
        }
        catch (Exception e) {
            userSaved = false;
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }
    public boolean deleteUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        User user = userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));
        userRepo.delete(user);
        return true;
    }

    @Autowired
    PasswordEncoder p;

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

        else{
            String rawPassword = user.getPassWord(); // get the plain password from user object
            String encodedPassword = p.encode(rawPassword); // encode it with BCrypt
            user.setPassWord(encodedPassword);
            user.setRoles(new ArrayList<>(Arrays.asList("USER")));
            userRepo.save(user);
        userSavedSucessfully = true;
        }
        return userSavedSucessfully;
    }
}

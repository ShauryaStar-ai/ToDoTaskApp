package com.shaurya.ToDoApp.Services;

import com.shaurya.ToDoApp.Objects.AdminUser;
import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Repositires.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class AdminSerivce {
    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder p;
    
    public boolean saveNewAdmin(AdminUser admin){
        List<User> users = userRepo.findAll();
        for (User user : users){
            if(user.getRoles().contains("ADMIN")){
                throw new IllegalStateException("There can only be one admin");
            }
        }
        boolean userSavedSucessfully = false;
        if (admin == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // 2. Required field checks
        if (admin.getUserName().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (admin.getPassWord().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        // 3. Uniqueness checks
        if (userRepo.existsByUserName(admin.getUserName())) {
            throw new IllegalStateException("Username already exists");
        }

        else{
            User newUser = new User();
            newUser.setUserName(admin.getUserName());
            newUser.setEmailAddress(admin.getEmailAddress());

            String rawPassword = admin.getPassWord(); // get the plain password from user object
            String encodedPassword = p.encode(rawPassword); // encode it with BCrypt
            newUser.setPassWord(encodedPassword);
            
            newUser.setRoles(new ArrayList<>(Arrays.asList("ADMIN")));
            userRepo.save(newUser);
            userSavedSucessfully = true;
        }
        return userSavedSucessfully;
    }
    
    public AdminUser findUserInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepo.findByUserName(userName).orElse(null);
        if(user != null){
            AdminUser adminUser = new AdminUser();
            adminUser.setUserName(user.getUserName());
            adminUser.setRoles(user.getRoles());
            adminUser.setEmailAddress(user.getEmailAddress());
            adminUser.setPassWord(user.getPassWord());
            return adminUser;
        }
        return null;
    }
    public List<User> getAllUsersInfo(){
        List<User> all = userRepo.findAll();
        return all;
    }
    public boolean updateAdmin(AdminUser adminUser){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null){
            throw new RuntimeException("Authentication not found");
        }
        String userName = auth.getName();
        User user = userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));
        if(adminUser.getUserName() == null || adminUser.getPassWord() == null || adminUser.getRoles() == null  ){
            throw new RuntimeException("Incomplete admin user information");
        }
        user.setUserName(adminUser.getUserName());
        String rawPassword = adminUser.getPassWord(); // get the plain password from user object
        String encodedPassword = p.encode(rawPassword); // encode it with BCrypt
        user.setPassWord(encodedPassword);
        user.setEmailAddress(adminUser.getEmailAddress());
        user.setRoles((ArrayList<String>) adminUser.getRoles());
        userRepo.save(user);
        return true;
    }
    public boolean deleteAdmin(){
        boolean adminDeleted = false;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null){
            throw new RuntimeException("Authentication not found");
        }
        String name = auth.getName();
        User user = userRepo.findByUserName(name).orElseThrow(() -> new RuntimeException("User not found"));
        userRepo.delete(user);
        adminDeleted = true;
        return adminDeleted;
    }
}

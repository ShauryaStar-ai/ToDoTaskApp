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

@Service
public class AdminSerivce {
    @Autowired
    UserRepo userRepo;
    @Autowired
    PasswordEncoder p;
    
    public boolean saveNewAdmin(AdminUser admin){

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
}

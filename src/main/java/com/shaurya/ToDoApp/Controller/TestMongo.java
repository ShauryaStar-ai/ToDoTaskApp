package com.shaurya.ToDoApp.Controller;

import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Repositires.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.core.env.Environment;

import java.util.ArrayList;

//@Component
public class TestMongo /*implements CommandLineRunner*/ {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Environment env;

/*    @Override
    public void run(String... args) throws Exception {
        System.out.println("------------------------------------------------");
        System.out.println("Active MongoDB URI: " + env.getProperty("spring.data.mongodb.uri"));
        System.out.println("Active Database Name: " + env.getProperty("spring.data.mongodb.database"));
        System.out.println("------------------------------------------------");

        User u = new User();
        u.setUserName("testuser_sonu");
        u.setPassWord(passwordEncoder.encode("1234"));
        u.setRoles(new ArrayList<>());
        userRepo.save(u);

        System.out.println("User saved to Mongo!");
        System.out.println();
    }*/
}
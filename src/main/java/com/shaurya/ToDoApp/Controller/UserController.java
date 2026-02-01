package com.shaurya.ToDoApp.Controller;

import com.mongodb.DBRef;
import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;

import com.shaurya.ToDoApp.Objects.Task;
import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Repositires.UserRepo;
import com.shaurya.ToDoApp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")

public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    MongoClient mongoClient;





    @GetMapping("/getUserInfo")
    public ResponseEntity<Object> getUserTask(){
      // getting the name of the user logged in using the security context
        try {

            User loggedInUser = userService.getUserByUserName(); // we now have the user and then authenicated
            String userName = loggedInUser.getUserName();
          List<Task> toDoTasks = loggedInUser.getTasksByTheUser();
            List<Object> userInformation = Arrays.asList(userName,toDoTasks);
            // Entries found → return 200 OK + data
            return ResponseEntity.ok(userInformation);

        } catch (Exception e) {
            // Something went wrong → return 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong");
        }

    }
    @PostMapping("/createUser")
    public ResponseEntity<String> addUser(@RequestBody User user){
        boolean userSavingConidtion = userService.saveNewUser(user);
        if(userSavingConidtion){
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("User Created Successfully");
        }
       else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User cannot be made ");
        }
    }
    @PutMapping("/editUserInfo")
    public ResponseEntity<String> editUserInfo(@RequestBody User userNew){


        boolean isUpdatedUser = userService.saveUpdatedUser(userNew);
        if(isUpdatedUser){
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("User updated Successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User cannot be updated ");
        }
    }
        @DeleteMapping("/deleteUser")
        public ResponseEntity<String> deleteUser(){
            boolean isDeleteUser = userService.deleteUser();
            if(isDeleteUser){
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("User deleted Successfully");
            }
            else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User cannot be deleted ");
            }
        }


    }

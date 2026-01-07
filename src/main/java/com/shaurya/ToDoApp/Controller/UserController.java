package com.shaurya.ToDoApp.Controller;

import com.mongodb.DBRef;
import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Repositires.UserRepo;
import com.shaurya.ToDoApp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@RestController
@RequestMapping("/user")

public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/getUserInfo")
    public ResponseEntity<Object> getUserTask(){
        try {
            User loggedInUser = userService.getUserByUserName(); // we now have the user and then authenicated
            String userName = loggedInUser.getUserName();
            String passWord = loggedInUser.getPassWord(); // do some thing for privacy later here
            ArrayList<DBRef> toDoTasks = loggedInUser.getToDoTasks();
            List<Serializable> userInformation = Arrays.asList(userName, passWord, toDoTasks);
            // Entries found → return 200 OK + data
            return ResponseEntity.ok(userInformation);

        } catch (Exception e) {
            // Something went wrong → return 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

    }
    @PostMapping("/createUser")
    public ResponseEntity<String> addUser(@RequestBody User user){
        boolean userSavingConidtion = userService.saveNewUser(user);
        if(userSavingConidtion){
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
       else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

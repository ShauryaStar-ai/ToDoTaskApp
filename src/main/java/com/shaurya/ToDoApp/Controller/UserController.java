package com.shaurya.ToDoApp.Controller;

import com.mongodb.DBRef;
import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

public class UserController {
    @Autowired
    UserService userService;
    @GetMapping("/user/getUserInfo")
    public ResponseEntity<Object> getUserTask(){
        try {
            User loggedInUser = userService.getLoggedInUser(); // we now have the user and then authenicated
            ArrayList<DBRef> toDoTasks = loggedInUser.getToDoTasks();

            // Entries found → return 200 OK + data
            return ResponseEntity.ok(toDoTasks);

        } catch (Exception e) {
            // Something went wrong → return 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }

    }
}

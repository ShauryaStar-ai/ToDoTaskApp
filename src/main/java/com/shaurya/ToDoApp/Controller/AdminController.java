package com.shaurya.ToDoApp.Controller;

import com.shaurya.ToDoApp.Objects.AdminUser;
import com.shaurya.ToDoApp.Services.AdminSerivce;
import com.shaurya.ToDoApp.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// have a send email meathod to the user
@RestController
@RequestMapping ("/admin")
public class AdminController {
    @Autowired
    AdminSerivce adminSerivce;
    @PostMapping("/makeNewAdmin")
    public ResponseEntity<String> makeAdmin(@RequestBody AdminUser admin){
        boolean isAdminnSaved = adminSerivce.saveNewAdmin(admin);
        if(isAdminnSaved){
            return ResponseEntity.status(200).body("Admin Saved");
        }
        else {
            return ResponseEntity.status(500).body("Admin could not be Saved");
        }
    }
    @GetMapping("/getAdminInfo")
    public ResponseEntity<String> getAdminInfo(){
        boolean userInformation = adminSerivce.findUserInfo();
    }

}

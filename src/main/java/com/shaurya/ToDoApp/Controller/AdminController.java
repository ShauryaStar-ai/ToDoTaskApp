package com.shaurya.ToDoApp.Controller;

import com.shaurya.ToDoApp.Objects.AdminUser;
import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Services.AdminSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<?> getAdminInfo(){
        AdminUser adminUser = adminSerivce.findUserInfo();
        if (adminUser != null) {
            return ResponseEntity.ok(adminUser);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin not found");
    }
    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsersInfo(){
        List<User> allUsersInfo = adminSerivce.getAllUsersInfo();
        if (allUsersInfo != null) {
            return ResponseEntity.ok(allUsersInfo);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Could not return all users");
        }

    }
    @PutMapping("/updateAdminInfo")
    public ResponseEntity<?> updateAdminInfo(@RequestBody AdminUser adminUser){
        boolean updateAdmin = adminSerivce.updateAdmin(adminUser);
        if(updateAdmin){
            return ResponseEntity.ok("Admin updated successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin could not be updated");
        }
    }
    @DeleteMapping("/deleteAdmin")
    public ResponseEntity<?> deleteAdmin(){
        boolean deleteAdmin = adminSerivce.deleteAdmin();
        if(deleteAdmin){
            return ResponseEntity.ok("Admin deleted successfully");
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin could not be updated");
        }
    }


}

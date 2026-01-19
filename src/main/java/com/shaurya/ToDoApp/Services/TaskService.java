package com.shaurya.ToDoApp.Services;

import com.shaurya.ToDoApp.Objects.Task;
import com.shaurya.ToDoApp.Objects.User;
import com.shaurya.ToDoApp.Repositires.TaskRepo;
import com.shaurya.ToDoApp.Repositires.UserRepo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    TaskRepo taskRepo;
    
    public List<Task> getAllTasks(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new RuntimeException("Authentication not found");
        }
        String userName = auth.getName();
        User user = userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));
        List<Task> tasks = user.getTasksByTheUser();
        return tasks != null ? tasks : new ArrayList<>();
    }
    
    public boolean addTask(Task t){
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                throw new RuntimeException("Authentication not found");
            }
            String userName = auth.getName();
            User user = userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));
            
            // Save the task first to generate an ID
            Task savedTask = taskRepo.save(t);
            
            // Add the task to the user's list
            if (user.getTasksByTheUser() == null) {
                user.setTasksByTheUser(new ArrayList<>());
            }
            user.getTasksByTheUser().add(savedTask);
            
            // Save the user to update the reference
            userRepo.save(user);
            
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean upDateTask(ObjectId id, Task newTask){
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                throw new RuntimeException("Authentication not found");
            }
            String userName = auth.getName();
            User user = userRepo.findByUserName(userName).orElseThrow(() -> new RuntimeException("User not found"));

            // Check if the task belongs to the user
            boolean isOwned = false;
            if (user.getTasksByTheUser() != null) {
                for (Task t : user.getTasksByTheUser()) {
                    if (t.getId().equals(id)) {
                        isOwned = true;
                        break;
                    }
                }
            }
            
            if (!isOwned) {
                return false;
            }

            Optional<Task> taskOptional = taskRepo.findById(id);
            if (taskOptional.isPresent()) {
                Task existingTask = taskOptional.get();
                existingTask.setContent(newTask.getContent());
                existingTask.setDueDate(newTask.getDueDate());
                taskRepo.save(existingTask);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

package com.shaurya.ToDoApp.Controller;

import com.shaurya.ToDoApp.Objects.Task;
import com.shaurya.ToDoApp.Services.TaskService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    TaskService taskService;

    @GetMapping("/getAllTasks")
    public ResponseEntity<?> getTask() {
        try {
            List<Task> allTasks = taskService.getAllTasks();
            return ResponseEntity.ok(allTasks);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching tasks: " + e.getMessage());
        }
    }

    @PostMapping("/addTask")
    public ResponseEntity<String> addTask(@RequestBody Task t) {
        boolean taskSaved = taskService.addTask(t);
        if (taskSaved) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Task Created Successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Task cannot be made. Check server logs for details.");
        }
    }
    
    @PutMapping("/editTask/{id}")
    public ResponseEntity<String> editTask(@PathVariable ObjectId id, @RequestBody Task t) {
        boolean taskUpdated = taskService.upDateTask(id, t);
        if (taskUpdated) {
            return ResponseEntity.ok("Task updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found or not updated");
        }
    }
    @DeleteMapping("/deleteTask/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable ObjectId id) {
        boolean taskDeleted = taskService.removeTask(id);
        if(taskDeleted){
            return ResponseEntity.ok("Task updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found or not updated");
        }
        }
    }


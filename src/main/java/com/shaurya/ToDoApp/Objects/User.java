package com.shaurya.ToDoApp.Objects;

import com.mongodb.DBRef;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
@Getter
@Setter
@Document(collection = "users") // MongoDB collection name

public class User {
    private ObjectId id;
    @NonNull
    private String userName;
    @NonNull
    private String passWord;
    private ArrayList<String> roles;
    private ArrayList<DBRef> toDoTasks;
    private String emailAddress;

}

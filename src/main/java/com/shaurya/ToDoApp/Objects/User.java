package com.shaurya.ToDoApp.Objects;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Document(collection = "users") // MongoDB collection name

public class User {
    @Id private ObjectId id;
    @NonNull
    private String userName;
    @NonNull
    private String passWord;
    private ArrayList<String> roles;
    @DBRef private List<Task> tasksByTheUser = new ArrayList<>();
    private String emailAddress;

}

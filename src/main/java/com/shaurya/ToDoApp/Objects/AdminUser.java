package com.shaurya.ToDoApp.Objects;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Document(collection = "users")
public class AdminUser {
    @Id
    private ObjectId id;
    @NonNull
    private String userName;
    @NonNull
    private String passWord;
    private String emailAddress;
    private List<String> roles;


}

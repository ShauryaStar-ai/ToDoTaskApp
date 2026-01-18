package com.shaurya.ToDoApp.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@Document(collection  = "tasks")
public class Task {
    @Id private ObjectId id;
    private String dueDate;
    private String content;

}

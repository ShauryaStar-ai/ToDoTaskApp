package com.shaurya.ToDoApp.Objects;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
@Getter
@Setter
public class Task {
    ObjectId id;
    String dueDate;
    String content;
}

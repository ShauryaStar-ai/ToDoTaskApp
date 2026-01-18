package com.shaurya.ToDoApp.Repositires;

import com.shaurya.ToDoApp.Objects.Task;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepo extends MongoRepository<Task, ObjectId> {

}

package com.shaurya.ToDoApp.Repositires;

import com.shaurya.ToDoApp.Objects.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends MongoRepository<User, ObjectId> {
    Optional<User> findByUserName(String userName);
    boolean existsByUserName(String userName);
}
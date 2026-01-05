package com.shaurya.ToDoApp.Repositires;

import com.shaurya.ToDoApp.Objects.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);

}

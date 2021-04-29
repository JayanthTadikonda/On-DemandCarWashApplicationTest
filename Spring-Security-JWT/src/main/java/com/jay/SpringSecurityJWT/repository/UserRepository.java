package com.jay.SpringSecurityJWT.repository;

import com.jay.SpringSecurityJWT.model.User;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, Integer>{

    public User findByUsername(String name);

}

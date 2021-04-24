package com.jay.SpringSecurityJWT.repository;

import com.jay.SpringSecurityJWT.model.User;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, Integer>{

    public User getByUsername(String name);

}

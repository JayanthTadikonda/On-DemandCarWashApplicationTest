package com.jay.MongoDBUserCreation.repository;

import com.jay.MongoDBUserCreation.model.Washer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface WasherRepository extends MongoRepository<Washer, Integer> {

    public List<Washer> findAll();
}

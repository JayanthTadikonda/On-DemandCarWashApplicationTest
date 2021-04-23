package com.jay.MongoDBUserCreation.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "washers")
public class Washer {

    private String name;
    @Id
    private int id;

    public Washer() {
    }

    public Washer(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Washer{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}

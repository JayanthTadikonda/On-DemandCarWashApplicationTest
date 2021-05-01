package com.jay.CWWasherMicroservice.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "washer")

public class Washer {

    private int washerId;
    private String name;
    private String password;
    private List<String> address;

    public Washer(int washerId, String name, String password, List<String> address) {
        this.washerId = washerId;
        this.name = name;
        this.password = password;
        this.address = address;
    }

    public Washer() {
    }

    public int getWasherId() {
        return washerId;
    }

    public void setWasherId(int washerId) {
        this.washerId = washerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }
}

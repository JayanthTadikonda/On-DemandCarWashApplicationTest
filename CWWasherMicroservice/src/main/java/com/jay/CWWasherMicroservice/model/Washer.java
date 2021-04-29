package com.jay.CWWasherMicroservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.processing.Generated;
import java.util.List;

@Document(collection = "washer")
public class Washer {

    private int washer_id;
    private String name;
    private List<String> address;

    public Washer(int washer_id, String name, List<String> address) {
        this.washer_id = washer_id;
        this.name = name;
        this.address = address;
    }

    public Washer() {
    }

    public int getWasher_id() {
        return washer_id;
    }

    public void setWasher_id(int washer_id) {
        this.washer_id = washer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Washer{" +
                "washer_id=" + washer_id +
                ", name='" + name + '\'' +
                ", address=" + address +
                '}';
    }
}

package com.jay.SpringSecurityJWT.model;

public class Customer {

    private String name;
    private String pass;

    public Customer(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }

    public Customer() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}

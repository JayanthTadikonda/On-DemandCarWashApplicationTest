package com.jay.MongoDBUserCreation.controller;

import com.jay.MongoDBUserCreation.model.Customer;
import com.jay.MongoDBUserCreation.repository.CustomerRepository;
import com.jay.MongoDBUserCreation.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/register-customer")
public class CustomerController {

    @Autowired
    Resource resource;

    @Autowired
    CustomerRepository customerRepository;

    @PostMapping(value = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String add(@RequestBody Customer customer){
        resource.addCustomer(customer);
        return "Added new Customer";
    }

    @GetMapping("/get/{name}")
    public Customer getCustomer(@PathVariable String name){
        return resource.findByName(name);
    }

    @GetMapping("/all")
    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

}

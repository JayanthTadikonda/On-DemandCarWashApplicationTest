package com.jay.MongoDBUserCreation.controller;

import com.jay.MongoDBUserCreation.model.Customer;
import com.jay.MongoDBUserCreation.repository.CustomerRepository;
import com.jay.MongoDBUserCreation.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/book-wash") //Wash Service Booking
    public String bookWash() throws Exception{
        restTemplate.getForObject("http://washer-microservice/washer/wash",String.class);
//        if(customerService.washBookingResponseFromWasher().equalsIgnoreCase("accepted-wash-request")){
//            restTemplate.g
//        }
        return customerService.sendNotification("book-wash");
    }

    @PostMapping(value = "/add-customer", consumes = MediaType.APPLICATION_JSON_VALUE) // New Customer Registration
    public String addCustomer(@RequestBody Customer customer){
        customerRepository.save(customer);
        return "Added new Customer";
    }

    @GetMapping("/get-customer/{name}") // Get customer with name
    public Customer getCustomerByName(@PathVariable String name){
        return customerService.findByName(name);
    }

    @GetMapping("/customer-id/{id}") // Get customer with ID
    public Customer getCustomerById(@PathVariable int id){
        return customerRepository.findById(id);
    }

    @GetMapping("/all-customers") //Lists all the customers in the db
    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    @PostMapping("/update-profile/{name}") // Update user profile
    public String updateProfile(@PathVariable String name, @RequestBody Customer customer){
        return customerService.updateProfile(name,customer);
    }

    @GetMapping("/confirmation") //Call to activate Reception of notifications from Customer
    public String notificationTest() throws Exception{
        return customerService.receiveNotification();
    }

}

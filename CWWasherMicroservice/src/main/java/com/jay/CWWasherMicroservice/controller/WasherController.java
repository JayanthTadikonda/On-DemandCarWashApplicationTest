package com.jay.CWWasherMicroservice.controller;

import com.jay.CWWasherMicroservice.model.Washer;
import com.jay.CWWasherMicroservice.repository.WasherRepository;
import com.jay.CWWasherMicroservice.service.WasherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/washer")
public class WasherController {

    @Autowired
    private WasherService washerService;

    @Autowired
    private WasherRepository washerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @PostMapping(value = "/add-washer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String addWasher(@RequestBody Washer washer){
        washerRepository.save(washer);
        return "New Washer Added !";
    }

    @GetMapping("/get-washer/{name}")
    public Washer getWasherByName(@PathVariable String name){
        return washerService.findByName(name);
    }

    @GetMapping("/all-washers") // Shows all Washers available in the DB
    public List<Washer> getAllWashers(){
        return washerRepository.findAll();
    }

    @GetMapping("/wash") //Call to activate Reception of notifications from Customer
    public String notificationTest() throws Exception{
        return washerService.receiveNotification();
    }

    @GetMapping("/washer-choice") //Washer accepting/Rejecting received Wash-Request
    public String acceptWash(@RequestParam ("option") Boolean option){
        String status = null;
        if(washerService.washRequestFromCustomer().equalsIgnoreCase("book-wash")&& option){
            restTemplate.getForObject("http://customer-microservice/customer/confirmation",String.class);
            washerService.sendNotification("accepted-wash-request");
            return "Wash Booking Accepted !";
        }
        else if(option || washerService.washRequestFromCustomer().equalsIgnoreCase("book-wash"))
            status = "Wash Booking Rejected !";
            restTemplate.getForObject("http://customer-microservice/customer/confirmation",String.class);
            washerService.sendNotification("Rejected-wash-request");
        return status;
    }
}

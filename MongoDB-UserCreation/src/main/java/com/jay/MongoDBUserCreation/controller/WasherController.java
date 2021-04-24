package com.jay.MongoDBUserCreation.controller;

import com.jay.MongoDBUserCreation.model.Washer;
import com.jay.MongoDBUserCreation.repository.WasherRepository;
import com.jay.MongoDBUserCreation.resource.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register-washer")
public class WasherController {

    @Autowired
    WasherRepository washerRepository;

    @Autowired
    Resource resource;

    @PostMapping("/add")
    public String addWasher(@RequestBody Washer washer){
        resource.addWasher(washer);
        return "washer added !";
    }

//    @GetMapping("/washer-login")
//    public String washerLogin(Washer washer){
//        if(washerRepository.)
//    }
}

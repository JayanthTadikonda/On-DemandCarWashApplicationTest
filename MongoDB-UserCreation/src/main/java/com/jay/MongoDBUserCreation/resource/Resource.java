package com.jay.MongoDBUserCreation.resource;

import com.jay.MongoDBUserCreation.model.Customer;
import com.jay.MongoDBUserCreation.model.Washer;
import com.jay.MongoDBUserCreation.repository.CustomerRepository;
import com.jay.MongoDBUserCreation.repository.WasherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class Resource {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    WasherRepository washerRepository;

    public void addCustomer(Customer customer){

        Customer customer1 = new Customer();
        customer1.setId(customer.getId());
        customer1.setName(customer.getName());
        customerRepository.save(customer1);
    }
    public void addWasher(Washer washer){

        Washer washer1 = new Washer();
        washer1.setId(washer.getId());
        washer1.setName(washer.getName());
        washerRepository.save(washer1);
    }

    public Customer findByName(String name){

        return customerRepository
                .findAll()
                .stream()
                .filter(a->a.getName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }
}

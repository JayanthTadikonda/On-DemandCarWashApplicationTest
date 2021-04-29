package com.jay.MongoDBUserCreation.service;

import com.jay.MongoDBUserCreation.model.Customer;
import com.jay.MongoDBUserCreation.repository.CustomerRepository;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    private static final String request = "request-booking";
    //private static final String EXCHANGE_NAME = "logs";
    private static final String booking = "booking-queue";

    String copyMsg = "";

    public String washBookingResponseFromWasher(){
        return copyMsg;
    }

    public Customer findByName(String name){

        return customerRepository
                .findAll()
                .stream()
                .filter(a->a.getName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }

    public String updateProfile(String name, Customer customer){
        if(customerRepository.findByName(name).getName().equalsIgnoreCase(name)){
            Customer customer1 = customerRepository.findByName(name);
            customer1.setName(customer.getName());
            customer1.setPassword(customer.getPassword());
            customer1.setAddress(customer.getAddress());
            customer1.setCarModel(customer.getCarModel());
            customerRepository.save(customer);
        }
        else
            return "No Customer by that name:" + name;
        return name + " Profile Updated";
    }

    public String sendNotification(String notification){
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())

        {
            channel.queueDeclare(request,false,false,false,null);
            channel.basicPublish("",request,null, notification.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent Message is: "+ notification);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        return "Wash Request Sent";
    }

    public String receiveNotification()throws Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(booking,false,false,false,null);
        System.out.println("Waiting for messages from Sender, ctrl+c to quit");

        DeliverCallback deliverCallback = (ct, delivery)-> {
            String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
            copyMsg = msg;
            System.out.println("Received Message is: "+ msg);

        };
        channel.basicConsume(booking,true, deliverCallback, c->{});

        return "Received Washer acceptance notification";
    }
}

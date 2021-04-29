package com.jay.CWWasherMicroservice.service;

import com.jay.CWWasherMicroservice.model.Washer;
import com.jay.CWWasherMicroservice.repository.WasherRepository;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class WasherService {

    private static final String request = "request-booking";
    //private static final String EXCHANGE_NAME = "logs";
    private static final String booking = "booking-queue";

    @Autowired
    private WasherRepository washerRepository;

    String copyMsg = ""; //notification received from the customer

    //Notification received from the customer
    public String washRequestFromCustomer(){
        return copyMsg;
    }

    //API to receive notification from the customers
    public String receiveNotification()throws Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(request,false,false,false,null);
        System.out.println("Waiting for messages from Sender, ctrl+c to quit");


        DeliverCallback deliverCallback = (ct, delivery)-> {
            String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("Received Message is: "+ msg);
            copyMsg = msg;
        };

        channel.basicConsume(request,true, deliverCallback, c->{});

        return "Received Wash Request !";
    }

    //Send notifications to the customers
    public void sendNotification(String notification){
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel())

        {
            channel.queueDeclare(booking,false,false,false,null);
            channel.basicPublish("",booking,null, notification.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent Message is: "+ notification);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public Washer findByName(String name) {

        return washerRepository
                .findAll()
                .stream()
                .filter(a->a.getName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }
}

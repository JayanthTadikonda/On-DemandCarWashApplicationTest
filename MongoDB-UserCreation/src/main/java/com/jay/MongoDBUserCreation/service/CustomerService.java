package com.jay.MongoDBUserCreation.service;

import com.jay.MongoDBUserCreation.filter.JwtFilter;
import com.jay.MongoDBUserCreation.model.*;
import com.jay.MongoDBUserCreation.repository.CustomerRepository;
import com.jay.MongoDBUserCreation.util.JwtUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;


    private static final String request = "request-booking";
    private static final String booking = "booking-queue";

    String washerResponse = "";

    public String washBookingResponseFromWasher() {
        return washerResponse;
    }

    public Customer findByName(String name) {

        return customerRepository
                .findAll()
                .stream()
                .filter(a -> a.getName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }

    public String updateProfile(String name, Customer customer) {
        if (customerRepository.findByName(name).getName().equalsIgnoreCase(name)) {
            Customer customer1 = customerRepository.findByName(name);
            customer1.setName(customer.getName());
            customer1.setPassword(customer.getPassword());
            customer1.setAddress(customer.getAddress());
            customer1.setCarModel(customer.getCarModel());
            customerRepository.save(customer);
        } else
            return "No Customer by that name:" + name;
        return name + " Profile Updated";
    }

    public String sendNotification(String notification) {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(request, false, false, false, null);
            channel.basicPublish("", request, null, notification.getBytes(StandardCharsets.UTF_8));
            System.out.println("Sent Message is: " + notification);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return "Wash Request Sent";
    }

    public String receiveNotification() throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(booking, false, false, false, null);
        System.out.println("Waiting for messages from Sender, ctrl+c to quit");

        DeliverCallback deliverCallback = (ct, delivery) -> {
            String msg = new String(delivery.getBody(), StandardCharsets.UTF_8);
            washerResponse = msg;
            System.out.println("Received Message is: " + msg);

        };
        channel.basicConsume(booking, true, deliverCallback, c -> {
        });
        return washerResponse;
    }

    public TransactionResponse doPay(Order order) {
        Payment payment = new Payment();
        payment.setCustomerName(order.getCustomerName());
        payment.setWasherName(order.getWasherName());
        payment.setOrderId(order.getOrderId());
        payment.setAmount(order.getAmount());
        TransactionRequest request = new TransactionRequest(order, payment);
        sendNotification("Payment Processed with ID:" + payment.getTransactionId());
        return restTemplate.postForObject("http://payment-microservice/payment/pay", request, TransactionResponse.class);
    }

    public TransactionResponse payAfterWash() {
        Payment payment = new Payment();
        Customer customer = customerRepository.findByName(jwtFilter.getLoggedInUserName());

        List<Payment> paymentList = null;
        List<Order> orderList = null;
        TransactionResponse response = new TransactionResponse();
        TransactionResponse finalResponse = new TransactionResponse();

        try {
            ResponseEntity<List<Payment>> claimResponse = restTemplate.exchange(
                    "http://payment-microservice/payment/get-payment/" + customer.getName(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Payment>>() {
                    });
            if (claimResponse.hasBody()) {
                paymentList = claimResponse.getBody();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }

        try {
            ResponseEntity<List<Order>> claimResponse = restTemplate.exchange(
                    "http://order-microservice/order/get-orders/" + customer.getName(),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Order>>() {
                    });
            if (claimResponse.hasBody()) {
                orderList = claimResponse.getBody();
            }
        } catch (RestClientException e) {
            e.printStackTrace();
        }

        List<Integer> orderId_paymentList = paymentList.stream().map(Payment::getOrderId).collect(Collectors.toList());

        assert orderList != null;
        for (Order o : orderList) {
            //Condition for FIRST-TIME payment
            if (!orderId_paymentList.contains(o.getOrderId())) {
                //Do 1st Time payment
                return doPay(o);
            } else if (!orderId_paymentList.contains(o.getOrderId()) && o.getPaymentStatus().contains("pending")) {
                return doPay(o);
            }
        }
        return new TransactionResponse(finalResponse.getOrder(), finalResponse.getTransactionId(), finalResponse.getAmount(), "All Payments Successful, Nothing Pending Payments");
    }

    public Order placeOrder() {
        Order placedOrder = null;
        String resp = washBookingResponseFromWasher();

        if (resp.contains("accepted-wash-request")) {
            Customer customer = customerRepository.findByName(jwtFilter.getLoggedInUserName());
            Order order = new Order();
            order.setCustomerName(jwtFilter.getLoggedInUserName());
            order.setCarModel(customer.getCarModel());
            order.setWasherName(resp.substring(41));
            order.setWashName("basic-wash");
            order.setDate(new Date(System.currentTimeMillis()));
            placedOrder = restTemplate.postForObject("http://order-microservice/order/place-order", order, Order.class);
            //System.out.println(placedOrder.toString());
        } else
            System.out.println("Washer rejected the request, please try again.");
        return placedOrder;
    }

}

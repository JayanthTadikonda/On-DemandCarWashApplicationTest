package com.jay.CWOrderService.service;

import com.jay.CWOrderService.common.*;
import com.jay.CWOrderService.model.Order;
import com.jay.CWOrderService.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AutomatedOrderId orderId;

    public TransactionResponse saveOrder(TransactionRequest request){

        String response="";

        Customer customer = restTemplate.getForObject("http://customer-microservice/customer/get-customer/"+request.getOrder().getCustomerName(),Customer.class);
        Washer washer = restTemplate.getForObject("http://washer-microservice/washer/get-washer/"+request.getOrder().getWasherName(),Washer.class);

        Order order = request.getOrder();
        order.setOrderId(orderId.getNextOrderId("orderId"));
        assert washer != null;
        //order.setWasherId(washer.getWasherId());
        assert customer != null;
        order.setCarModel(customer.getCarModel());

        Payment payment = new Payment();//= request.getPayment(); change to new Object
        payment.setCustomerName(customer.getName());
        payment.setWasherName(washer.getName());
        payment.setOrderId(order.getOrderId());
        payment.setAmount(order.getAmount());

        Payment paymentResponse = restTemplate.postForObject("http://payment-microservice/payment/pay",payment,Payment.class);
        //restTemplate.getForObject("http://customer-microservice/customer/book-wash",String.class);

        orderRepository.save(order);
        assert paymentResponse != null;
        response = paymentResponse.getPaymentStatus().equalsIgnoreCase("success")?"payment Successful, Order Booked":"Sorry, payment failed !";
        return new TransactionResponse(order,paymentResponse.getTransactionId(), paymentResponse.getAmount(),response,washer);
    }

    public Order payAfterWash(Order order){

        order.setOrderId(orderId.getNextOrderId("orderId"));
        if(order.getWashName().contains("basic-wash")){
            order.setAmount(999);
            orderRepository.save(order);
        }else
            order.setAmount(555);
            orderRepository.save(order);
        return order;
    }

//    public Order findById(int id){
//        return orderRepository.findAll()
//                .stream().filter(o -> o.)
//    }

    public List<Order> getOrderListByName(String name){
        return orderRepository.findAll()
                .stream().filter(order -> order.getCustomerName().equalsIgnoreCase(name))
                .collect(Collectors.toList());
    }
}

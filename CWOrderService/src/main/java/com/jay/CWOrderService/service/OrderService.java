package com.jay.CWOrderService.service;

import com.jay.CWOrderService.common.Payment;
import com.jay.CWOrderService.common.TransactionRequest;
import com.jay.CWOrderService.common.TransactionResponse;
import com.jay.CWOrderService.model.Order;
import com.jay.CWOrderService.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    public TransactionResponse saveOrder(TransactionRequest request){

        String response="";
        Order order = request.getOrder();
        Payment payment = request.getPayment();
        payment.setOrderId(order.getOrderId());
        payment.setAmount(order.getAmount());

        Payment paymentResponse = restTemplate.postForObject("http://payment-microservice/payment/pay",payment,Payment.class);
        //restTemplate.getForObject("http://customer-microservice/customer/book-wash",String.class);

        orderRepository.save(order);
        assert paymentResponse != null;
        response = paymentResponse.getPaymentStatus().equalsIgnoreCase("success")?"payment Successful, Order Booked":"Sorry, payment failed !";
        return new TransactionResponse(order,paymentResponse.getTransactionId(), paymentResponse.getAmount(),response);
    }
}

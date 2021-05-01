package com.jay.CWOrderService.controller;

import com.jay.CWOrderService.common.Payment;
import com.jay.CWOrderService.common.TransactionRequest;
import com.jay.CWOrderService.common.TransactionResponse;
import com.jay.CWOrderService.model.Order;
import com.jay.CWOrderService.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place-order")
    public Order placeOrder(@RequestBody Order order) {
        return orderService.payAfterWash(order);
    }

    @PostMapping("/book-wash")
    public TransactionResponse placeOrder(@RequestBody TransactionRequest request) {
        return orderService.saveOrder(request);
    }

//    @GetMapping("/get-order")
//    public Order getOrderById(int id){
//        orderService.findById(id);
//    }
    @GetMapping("/get-orders/{name}")
    public List<Order> getOrdersByName(String name){
        return orderService.getOrderListByName(name);
    }
}

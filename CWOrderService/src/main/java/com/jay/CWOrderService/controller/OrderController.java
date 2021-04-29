package com.jay.CWOrderService.controller;

import com.jay.CWOrderService.common.Payment;
import com.jay.CWOrderService.common.TransactionRequest;
import com.jay.CWOrderService.common.TransactionResponse;
import com.jay.CWOrderService.model.Order;
import com.jay.CWOrderService.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/place-order")
    public String placeOrder(){

        return "Order Placed";
    }

    @PostMapping("/book-wash")
    public TransactionResponse placeOrder(@RequestBody TransactionRequest request){

        return orderService.saveOrder(request);
    }
}

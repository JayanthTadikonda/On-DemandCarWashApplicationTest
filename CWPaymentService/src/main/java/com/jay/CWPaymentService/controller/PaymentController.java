package com.jay.CWPaymentService.controller;

import com.jay.CWPaymentService.model.Payment;
import com.jay.CWPaymentService.repository.PaymentRepository;
import com.jay.CWPaymentService.service.PaymentService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public Payment payAmount(@RequestBody Payment payment){
        return paymentService.doPayment(payment);
    }

    @GetMapping("/get-payments-list")
    public List<Payment> paymentList(){
        return paymentRepository.findAll();
    }

    @GetMapping("/get-payment/{name}")
    public List<Payment> paymentListOfName(){
        return null;
    }
}

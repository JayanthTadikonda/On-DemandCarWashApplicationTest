package com.jay.CWPaymentService.controller;

import com.jay.CWPaymentService.model.Payment;
import com.jay.CWPaymentService.repository.PaymentRepository;
import com.jay.CWPaymentService.service.PaymentService;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}

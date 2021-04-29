package com.jay.CWPaymentService.service;

import com.jay.CWPaymentService.model.Payment;
import com.jay.CWPaymentService.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AutomatedPaymentId paymentId;


    public Payment doPayment(Payment payment){
        payment.setPaymentId(paymentId.getNextPaymentId("paymentId"));
        payment.setPaymentStatus(paymentProcessing());
        payment.setTransactionId(UUID.randomUUID().toString());
        return paymentRepository.save(payment);
    }

    public String paymentProcessing(){
        //3rd party api payment gateway (BrainTree)
        return new Random().nextBoolean()?"success":"payment failed, please try again !";
    }
}

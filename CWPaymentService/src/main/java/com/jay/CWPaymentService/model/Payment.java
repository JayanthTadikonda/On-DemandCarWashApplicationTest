package com.jay.CWPaymentService.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private int paymentId;
    private String customerName;
    private String washerName;
    private String paymentStatus;
    private String transactionId;
    private int orderId;
    private double amount;
    private LocalDateTime paymentDate;
}

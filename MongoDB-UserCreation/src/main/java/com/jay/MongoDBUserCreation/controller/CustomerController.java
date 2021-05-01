package com.jay.MongoDBUserCreation.controller;

import com.jay.MongoDBUserCreation.filter.JwtFilter;
import com.jay.MongoDBUserCreation.model.*;
import com.jay.MongoDBUserCreation.repository.CustomerRepository;
import com.jay.MongoDBUserCreation.service.CustomerService;
import com.jay.MongoDBUserCreation.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtFilter jwtFilter;


    @GetMapping("/book-wash") //Wash Service Booking
    public String bookWash() throws Exception {

        String sent = customerService.sendNotification("book-wash" + "By customer: " + jwtFilter.getLoggedInUserName());
        String resp = customerService.receiveNotification();

        if (resp.contains("accepted-wash-request")) {
            Customer customer = customerRepository.findByName(jwtFilter.getLoggedInUserName());
            Order order = new Order();
            order.setCustomerName(jwtFilter.getLoggedInUserName());
            order.setCarModel(customer.getCarModel());
            order.setWasherName(resp);
            order.setWashName("basic-wash");
            order.setDate(new Date(System.currentTimeMillis()));
            Order placedOrder = restTemplate.postForObject("http://order-microservice/order/place-order", order, Order.class);
            customer.setOrder(placedOrder);
            customerRepository.save(customer);
        } else
            return resp;
        return sent;
    }

    @GetMapping("/pay")
    public PaymentResponse doPayment() {

        Payment payment = new Payment();
        Customer customer = customerRepository.findByName(jwtFilter.getLoggedInUserName());

        List<Payment> paymentList = null;
        List<Order> orderList = null;
        Payment payment1 = null;

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

        assert orderList != null;
        for (Order o : orderList) {
            assert paymentList != null;
            for (Payment p : paymentList) {
                if (o.getOrderId() == p.getOrderId()) {
                    if (p.getPaymentStatus().contains("failed")) {
                        payment1 = doPay(customer);
                    } else
                        return new PaymentResponse(payment1, "Already Paid");
                }
            }
        }
        return new PaymentResponse(payment1, "Payment completed");
    }

    public Payment doPay(Customer customer) {
        Payment payment = new Payment();
        payment.setCustomerName(customer.getOrder().getCustomerName());
        payment.setWasherName(customer.getOrder().getWasherName());
        payment.setOrderId(customer.getOrder().getOrderId());
        payment.setAmount(customer.getOrder().getAmount());

        customerService.sendNotification("Payment Processed with ID:" + payment.getTransactionId());
        return restTemplate.postForObject("http://payment-microservice/payment/pay", payment, Payment.class);
    }

    @PostMapping(value = "/add-customer", consumes = MediaType.APPLICATION_JSON_VALUE) // New Customer Registration
    public Customer addCustomer(@RequestBody Customer customer) {
        customerRepository.save(customer);
        return customer;
    }

    @GetMapping("/get-customer/{name}") // Get customer with name
    public Customer getCustomerByName(@PathVariable String name) {
        return customerService.findByName(name);
    }

    @GetMapping("/customer-id/{id}") // Get customer with ID
    public Customer getCustomerById(@PathVariable int id) {
        return customerRepository.findById(id);
    }

    @GetMapping("/all-customers") //Lists all the customers in the db
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @PostMapping("/update-profile/{name}") // Update user profile
    public String updateProfile(@PathVariable String name, @RequestBody Customer customer) {
        return customerService.updateProfile(name, customer);
    }

    @GetMapping("/confirmation") //Call to activate Reception of notifications from Customer
    public String notificationTest() throws Exception {
        return customerService.receiveNotification();
    }

    @GetMapping(value = "/test-security")
    public String sayHelloOnAuthentication() {
        return "Hey there " + jwtFilter.getLoggedInUserName();
    }

    @PostMapping("/authenticate")
    public String generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Invalid Username or Password Entered !");
        }
        return jwtUtil.generateToken(authRequest.getUsername());
    }
}

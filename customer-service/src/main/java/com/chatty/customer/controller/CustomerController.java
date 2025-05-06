
package com.chatty.customer.controller;

import com.chatty.customer.dto.CustomerRegistrationRequest;
import com.chatty.customer.dto.CustomerRegistrationResponse;
import com.chatty.customer.model.CustomerProfile;
import com.chatty.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<CustomerRegistrationResponse> registerCustomer(
            @Valid @RequestBody CustomerRegistrationRequest request) {
        log.info("Received customer registration request for: {}", request.getCustomerProfile().getCustomerEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.registerCustomer(request));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerProfile> getCustomerById(@PathVariable String customerId) {
        log.info("Fetching customer with ID: {}", customerId);
        return ResponseEntity.ok(customerService.getCustomerByCustomerId(customerId));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<CustomerProfile> getCustomerByEmail(@PathVariable String email) {
        log.info("Fetching customer with email: {}", email);
        return ResponseEntity.ok(customerService.getCustomerByEmail(email));
    }

    @PutMapping("/{customerId}/verify")
    public ResponseEntity<CustomerProfile> verifyCustomer(@PathVariable String customerId) {
        log.info("Verifying customer with ID: {}", customerId);
        return ResponseEntity.ok(customerService.verifyCustomer(customerId));
    }

    @PutMapping("/{customerId}/activate")
    public ResponseEntity<CustomerProfile> activateCustomer(@PathVariable String customerId) {
        log.info("Activating customer with ID: {}", customerId);
        return ResponseEntity.ok(customerService.activateCustomer(customerId));
    }
}


package com.chatty.auth.client;

import com.chatty.auth.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {

    @GetMapping("/api/v1/customers/{customerId}")
    ResponseEntity<CustomerDto> getCustomerById(@PathVariable String customerId);
    
    @GetMapping("/api/v1/customers/email/{email}")
    ResponseEntity<CustomerDto> getCustomerByEmail(@PathVariable String email);
    
    @PutMapping("/api/v1/customers/{customerId}/verify")
    ResponseEntity<CustomerDto> verifyCustomer(@PathVariable String customerId);
    
    @PutMapping("/api/v1/customers/{customerId}/activate")
    ResponseEntity<CustomerDto> activateCustomer(@PathVariable String customerId);
}

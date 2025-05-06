
package com.chatty.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegistrationResponse {
    private String customerId;
    private String customerName;
    private String customerEmail;
    private String message;
    private boolean registrationSuccess;
}

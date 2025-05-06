
package com.chatty.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {
    private String customerId;
    private String customerName;
    private String customerEmail;
    private boolean isVerified;
    private boolean isActive;
    
    // Primary admin details
    private String primaryAdminName;
    private String primaryAdminEmail;
    private String primaryAdminPhone;
}

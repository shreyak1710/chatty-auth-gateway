
package com.chatty.auth.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateApiKeyRequest {
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    @NotBlank(message = "Key name is required")
    private String keyName;
    
    @NotNull(message = "Daily limit is required")
    @Min(value = 10, message = "Daily limit must be at least 10")
    private Integer dailyLimit;
    
    @NotNull(message = "Monthly limit is required")
    @Min(value = 300, message = "Monthly limit must be at least 300")
    private Integer monthlyLimit;
    
    @NotNull(message = "Rate limit is required")
    @Min(value = 1, message = "Rate limit must be at least 1")
    private Integer rateLimitPerMinute;
    
    private boolean readAccess = true;
    private boolean writeAccess;
    private boolean adminAccess;
    
    @NotBlank(message = "Environment is required")
    private String environment; // PROD, DEV, TEST
}

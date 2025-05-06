
package com.chatty.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyResponse {
    private String customerId;
    private String keyName;
    private String apiKey;
    private String apiSecret; // Only returned once upon creation
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastUsedAt;
    private Integer rateLimitPerMinute;
    private String environment;
    private boolean readAccess;
    private boolean writeAccess;
    private boolean adminAccess;
}

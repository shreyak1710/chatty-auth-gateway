
package com.chatty.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "api_keys")
public class ApiKey {
    @Id
    private String id;
    private String customerId;
    private String keyName;
    private String apiKey;
    private String apiSecret;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastUsedAt;
    private ApiKeyPermissions permissions;
    private int dailyLimit;
    private int monthlyLimit;
    private String environment; // PROD, DEV, TEST
}

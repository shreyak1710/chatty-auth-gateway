
package com.chatty.auth.service;

import com.chatty.auth.client.CustomerServiceClient;
import com.chatty.auth.dto.ApiKeyResponse;
import com.chatty.auth.dto.GenerateApiKeyRequest;
import com.chatty.auth.exception.ApiKeyAlreadyExistsException;
import com.chatty.auth.exception.ApiKeyNotFoundException;
import com.chatty.auth.model.ApiKey;
import com.chatty.auth.model.ApiKeyPermissions;
import com.chatty.auth.repository.ApiKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyService {

    private static final String API_KEY_PREFIX = "chatty_";
    private static final String API_KEY_SECRET_PREFIX = "sec_";
    private final ApiKeyRepository apiKeyRepository;
    private final CustomerServiceClient customerServiceClient;
    
    public ApiKeyResponse generateApiKey(GenerateApiKeyRequest request) {
        log.info("Generating API key for customer: {}", request.getCustomerId());
        
        // Verify customer exists and is active
        customerServiceClient.getCustomerById(request.getCustomerId());
        
        // Check if key name already exists for this customer
        if (apiKeyRepository.existsByKeyNameAndCustomerId(request.getKeyName(), request.getCustomerId())) {
            throw new ApiKeyAlreadyExistsException("API key with name " + request.getKeyName() + " already exists");
        }
        
        // Generate unique API key and secret
        String apiKey = generateUniqueApiKey();
        String apiSecret = generateApiSecret();
        
        ApiKeyPermissions permissions = ApiKeyPermissions.builder()
                .readAccess(request.isReadAccess())
                .writeAccess(request.isWriteAccess())
                .adminAccess(request.isAdminAccess())
                .rateLimitPerMinute(request.getRateLimitPerMinute())
                .build();
        
        ApiKey newApiKey = ApiKey.builder()
                .customerId(request.getCustomerId())
                .keyName(request.getKeyName())
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .active(true)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusYears(1))
                .permissions(permissions)
                .dailyLimit(request.getDailyLimit())
                .monthlyLimit(request.getMonthlyLimit())
                .environment(request.getEnvironment())
                .build();
        
        ApiKey savedApiKey = apiKeyRepository.save(newApiKey);
        
        log.info("API key generated successfully for customer: {}", request.getCustomerId());
        
        return mapToApiKeyResponse(savedApiKey, true);
    }
    
    public List<ApiKeyResponse> getApiKeysByCustomerId(String customerId) {
        log.info("Fetching API keys for customer: {}", customerId);
        
        List<ApiKey> apiKeys = apiKeyRepository.findByCustomerId(customerId);
        
        return apiKeys.stream()
                .map(apiKey -> mapToApiKeyResponse(apiKey, false))
                .collect(Collectors.toList());
    }
    
    public boolean revokeApiKey(String apiKey) {
        log.info("Revoking API key");
        
        ApiKey existingApiKey = apiKeyRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new ApiKeyNotFoundException("API key not found"));
        
        existingApiKey.setActive(false);
        existingApiKey.setUpdatedAt(LocalDateTime.now());
        
        apiKeyRepository.save(existingApiKey);
        
        log.info("API key revoked successfully for customer: {}", existingApiKey.getCustomerId());
        
        return true;
    }
    
    public boolean validateApiKey(String apiKey) {
        log.info("Validating API key");
        
        ApiKey existingApiKey = apiKeyRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new ApiKeyNotFoundException("API key not found"));
        
        if (!existingApiKey.isActive()) {
            log.warn("API key is inactive: {}", apiKey);
            return false;
        }
        
        if (existingApiKey.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("API key is expired: {}", apiKey);
            return false;
        }
        
        existingApiKey.setLastUsedAt(LocalDateTime.now());
        apiKeyRepository.save(existingApiKey);
        
        log.info("API key validation successful");
        
        return true;
    }
    
    private String generateUniqueApiKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        String apiKey = API_KEY_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        
        // Ensure key doesn't already exist
        while (apiKeyRepository.findByApiKey(apiKey).isPresent()) {
            random.nextBytes(bytes);
            apiKey = API_KEY_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        }
        
        return apiKey;
    }
    
    private String generateApiSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return API_KEY_SECRET_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    
    private ApiKeyResponse mapToApiKeyResponse(ApiKey apiKey, boolean includeSecret) {
        return ApiKeyResponse.builder()
                .customerId(apiKey.getCustomerId())
                .keyName(apiKey.getKeyName())
                .apiKey(apiKey.getApiKey())
                .apiSecret(includeSecret ? apiKey.getApiSecret() : null)
                .active(apiKey.isActive())
                .createdAt(apiKey.getCreatedAt())
                .expiresAt(apiKey.getExpiresAt())
                .lastUsedAt(apiKey.getLastUsedAt())
                .rateLimitPerMinute(apiKey.getPermissions() != null ? apiKey.getPermissions().getRateLimitPerMinute() : null)
                .environment(apiKey.getEnvironment())
                .readAccess(apiKey.getPermissions() != null && apiKey.getPermissions().isReadAccess())
                .writeAccess(apiKey.getPermissions() != null && apiKey.getPermissions().isWriteAccess())
                .adminAccess(apiKey.getPermissions() != null && apiKey.getPermissions().isAdminAccess())
                .build();
    }
}

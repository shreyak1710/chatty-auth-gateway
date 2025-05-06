
package com.chatty.auth.controller;

import com.chatty.auth.dto.ApiKeyResponse;
import com.chatty.auth.dto.GenerateApiKeyRequest;
import com.chatty.auth.service.ApiKeyService;
import com.chatty.auth.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/api-keys")
@RequiredArgsConstructor
@Slf4j
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final EmailService emailService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<ApiKeyResponse> generateApiKey(@Valid @RequestBody GenerateApiKeyRequest request) {
        log.info("Received API key generation request for customer: {}", request.getCustomerId());
        ApiKeyResponse response = apiKeyService.generateApiKey(request);
        
        // Send email notification
        emailService.sendApiKeyCreationNotification(request.getKeyName(), response.getApiKey());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<List<ApiKeyResponse>> getApiKeysByCustomerId(@PathVariable String customerId) {
        log.info("Fetching API keys for customer: {}", customerId);
        return ResponseEntity.ok(apiKeyService.getApiKeysByCustomerId(customerId));
    }
    
    @DeleteMapping("/{apiKey}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
    public ResponseEntity<String> revokeApiKey(@PathVariable String apiKey) {
        log.info("Received request to revoke API key");
        boolean revoked = apiKeyService.revokeApiKey(apiKey);
        
        if (revoked) {
            return ResponseEntity.ok("API key revoked successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to revoke API key");
        }
    }
    
    @GetMapping("/validate/{apiKey}")
    public ResponseEntity<Boolean> validateApiKey(@PathVariable String apiKey) {
        log.info("Validating API key");
        return ResponseEntity.ok(apiKeyService.validateApiKey(apiKey));
    }
}


package com.chatty.gateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyService {

    @Value("${services.auth-service.url}")
    private String authServiceUrl;
    
    private final WebClient.Builder webClientBuilder;

    public Mono<Boolean> validateApiKey(String apiKey) {
        log.debug("Validating API key");
        
        return webClientBuilder
                .baseUrl(authServiceUrl)
                .build()
                .get()
                .uri("/api/v1/api-keys/validate/{apiKey}", apiKey)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorReturn(false);
    }
}

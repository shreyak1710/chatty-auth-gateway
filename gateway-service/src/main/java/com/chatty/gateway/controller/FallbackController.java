
package com.chatty.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authServiceFallback() {
        return createFallbackResponse("Auth Service is currently unavailable");
    }

    @GetMapping("/customer")
    public Mono<ResponseEntity<Map<String, Object>>> customerServiceFallback() {
        return createFallbackResponse("Customer Service is currently unavailable");
    }

    @GetMapping("/api-key")
    public Mono<ResponseEntity<Map<String, Object>>> apiKeyServiceFallback() {
        return createFallbackResponse("API Key Service is currently unavailable");
    }

    @GetMapping("/chatbot")
    public Mono<ResponseEntity<Map<String, Object>>> chatbotServiceFallback() {
        return createFallbackResponse("Chatbot Service is currently unavailable");
    }

    private Mono<ResponseEntity<Map<String, Object>>> createFallbackResponse(String message) {
        Map<String, Object> response = Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
            "error", "Service Unavailable",
            "message", message
        );
        
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}

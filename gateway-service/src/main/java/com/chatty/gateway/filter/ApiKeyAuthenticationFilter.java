
package com.chatty.gateway.filter;

import com.chatty.gateway.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiKeyAuthenticationFilter implements GatewayFilter {

    private final ApiKeyService apiKeyService;
    private static final String API_KEY_HEADER = "X-API-KEY";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        if (!request.getHeaders().containsKey(API_KEY_HEADER)) {
            return onError(exchange, "Missing API key", HttpStatus.UNAUTHORIZED);
        }

        String apiKey = request.getHeaders().getFirst(API_KEY_HEADER);
        
        if (apiKey == null || apiKey.isEmpty()) {
            return onError(exchange, "Invalid API key", HttpStatus.UNAUTHORIZED);
        }

        return apiKeyService.validateApiKey(apiKey)
                .flatMap(isValid -> {
                    if (Boolean.TRUE.equals(isValid)) {
                        return chain.filter(exchange);
                    } else {
                        return onError(exchange, "Invalid API key", HttpStatus.UNAUTHORIZED);
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error validating API key", e);
                    return onError(exchange, "Error validating API key", HttpStatus.INTERNAL_SERVER_ERROR);
                });
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}

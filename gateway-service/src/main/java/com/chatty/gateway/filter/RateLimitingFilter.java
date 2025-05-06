
package com.chatty.gateway.filter;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitingFilter implements GatewayFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final int DEFAULT_LIMIT = 10; // Default requests per minute
    
    private final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();
    private final RateLimiterRegistry rateLimiterRegistry;
    
    public RateLimitingFilter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .limitForPeriod(DEFAULT_LIMIT)
                .timeoutDuration(Duration.ofMillis(100))
                .build();
        
        this.rateLimiterRegistry = RateLimiterRegistry.of(config);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Get API Key from header
        String apiKey = request.getHeaders().getFirst(API_KEY_HEADER);
        
        if (apiKey == null || apiKey.isEmpty()) {
            // If no API key, use IP address as identifier
            apiKey = request.getRemoteAddress().getAddress().getHostAddress();
        }
        
        // Get or create rate limiter for this key
        RateLimiter rateLimiter = rateLimiters.computeIfAbsent(apiKey, 
            k -> rateLimiterRegistry.rateLimiter(apiKey)
        );
        
        // Try to acquire permission
        boolean permission = rateLimiter.acquirePermission();
        
        if (!permission) {
            log.warn("Rate limit exceeded for API key: {}", apiKey);
            return onError(exchange, "Rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS);
        }
        
        return chain.filter(exchange);
    }
    
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }
}

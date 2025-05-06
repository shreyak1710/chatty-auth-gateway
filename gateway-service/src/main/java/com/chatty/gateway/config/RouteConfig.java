
package com.chatty.gateway.config;

import com.chatty.gateway.filter.ApiKeyAuthenticationFilter;
import com.chatty.gateway.filter.JwtAuthenticationFilter;
import com.chatty.gateway.filter.RateLimitingFilter;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class RouteConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;
    private final RateLimitingFilter rateLimitingFilter;

    public RouteConfig(JwtAuthenticationFilter jwtAuthenticationFilter, 
                       ApiKeyAuthenticationFilter apiKeyAuthenticationFilter,
                       RateLimitingFilter rateLimitingFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        RetryGatewayFilterFactory.Retry retryConfig = new RetryGatewayFilterFactory.Retry();
        retryConfig.setRetries(3);
        retryConfig.setMethods(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name());
        
        return builder.routes()
            // Public routes
            .route("auth-service-public", r -> r.path("/api/v1/auth/**")
                .filters(f -> f.rewritePath("/api/v1/auth/(?<segment>.*)", "/api/v1/auth/${segment}")
                              .circuitBreaker(c -> c.setName("authServiceCircuitBreaker")
                                                   .setFallbackUri("forward:/fallback/auth")))
                .uri("lb://auth-service"))
                
            // Routes requiring JWT authentication
            .route("customer-service-protected", r -> r.path("/api/v1/customers/**")
                .filters(f -> f.filter(jwtAuthenticationFilter)
                              .rewritePath("/api/v1/customers/(?<segment>.*)", "/api/v1/customers/${segment}")
                              .circuitBreaker(c -> c.setName("customerServiceCircuitBreaker")
                                                   .setFallbackUri("forward:/fallback/customer")))
                .uri("lb://customer-service"))
                
            .route("auth-service-protected", r -> r.path("/api/v1/api-keys/**")
                .filters(f -> f.filter(jwtAuthenticationFilter)
                              .rewritePath("/api/v1/api-keys/(?<segment>.*)", "/api/v1/api-keys/${segment}")
                              .circuitBreaker(c -> c.setName("apiKeyServiceCircuitBreaker")
                                                   .setFallbackUri("forward:/fallback/api-key")))
                .uri("lb://auth-service"))
                
            // API routes requiring API Key authentication
            .route("chatbot-api", r -> r.path("/api/v1/chatbot/**")
                .filters(f -> f.filter(apiKeyAuthenticationFilter)
                              .filter(rateLimitingFilter)
                              .rewritePath("/api/v1/chatbot/(?<segment>.*)", "/api/v1/chatbot/${segment}")
                              .circuitBreaker(c -> c.setName("chatbotServiceCircuitBreaker")
                                                   .setFallbackUri("forward:/fallback/chatbot")))
                .uri("lb://chatbot-service"))
            .build();
    }
}

package org.app.apigateway.config;

import com.netflix.discovery.DiscoveryClient;
import org.app.apigateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

  private final JwtAuthenticationFilter filter;

  public GatewayConfig(JwtAuthenticationFilter filter) {
    this.filter = filter;
  }

  @Bean
  public RouteLocator routes(RouteLocatorBuilder builder) {
    return builder.routes()
        // Auth service routes (no JWT required)
        .route("auth-login", r -> r.path("/auth/**")
            .uri("lb://auth-service"))

        // Public event routes
        .route("events-all-public", r -> r.path("/events/all","/events", "/events/nearby", "/events/{id}")
            .uri("lb://event-service"))

        // Public review routes
        .route("reviews-all-public", r -> r.path("/reviews", "/reviews/{id}","/reviews/event/{id}")
            .uri("lb://review-service"))

        // User registration (public)
        .route("auth-service-public", r -> r.path("/users").and().method("POST")
                .uri("lb://auth-service"))

        // Admin routes - require ADMIN role
        .route("admin-routes", r -> r.path("/admin/**")
            .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
            .uri("lb://auth-service"))

        // User management routes - protected
        .route("auth-service-protected", r -> r.path("/users/**")
            .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
            .uri("lb://auth-service"))

        // Payment service routes - protected
        .route("payment-service", r -> r.path("/payments/**")
            .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
            .uri("lb://payment-service"))

        // Review service routes - protected (except public ones)
        .route("review-service", r -> r.path("/reviews/**")
            .and().not(p -> p.path("/reviews", "/reviews/{id}","/reviews/event/{id}"))
            .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
            .uri("lb://review-service"))

        // Invitation service routes - protected
        .route("invitation-service", r -> r.path("/invitations/**")
            .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
            .uri("lb://invitation-service"))

        // Registration service routes - protected
        .route("registration-service", r -> r.path("/registrations/**")
            .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
            .uri("lb://registration-service"))

        // Event service routes - protected (except public ones)
        .route("event-service", r -> r.path("/events/**")
            .and().not(p -> p.path("/events/all","/events", "/events/nearby", "/events/{id}"))
            .filters(f -> f.filter(filter.apply(new JwtAuthenticationFilter.Config())))
            .uri("lb://event-service"))

        .build();
  }
}

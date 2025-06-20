package org.app.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

  private final WebClient.Builder webClientBuilder;

  public JwtAuthenticationFilter(WebClient.Builder webClientBuilder) {
    super(Config.class);
    this.webClientBuilder = webClientBuilder;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      String path = exchange.getRequest().getURI().getPath();
      HttpMethod method = exchange.getRequest().getMethod();

      // Public endpoints don't need authentication
      if (path.startsWith("/auth/")) {
        return chain.filter(exchange);
      }

      // Check for Authorization header
      if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
        return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
      }

      String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
      }

      // Validate token and check roles
      return webClientBuilder.build()
          .post()
          .uri("lb://auth-service/auth/validate")
          .header(HttpHeaders.AUTHORIZATION, authHeader)
          .retrieve()
          .bodyToMono(Map.class)
          .flatMap(response -> {
            boolean isValid = (boolean) response.get("valid");
            if (!isValid) {
              return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
            }

            // Check role-based access
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) response.get("roles");

              // Admin endpoints - require ADMIN role
            if (path.contains("/admin/") && !hasRole(roles, "ROLE_ADMIN")) {
                log.warn("Access denied for admin endpoint: {}", path);
              return onError(exchange, "Access denied", HttpStatus.FORBIDDEN);
            }

            // User endpoints - require USER or ADMIN role
            if ((path.startsWith("/events/") && method == HttpMethod.POST) || 
                (path.startsWith("/reviews/") && method == HttpMethod.POST)) {
              if (!hasRole(roles, "ROLE_USER") && !hasRole(roles, "ROLE_ADMIN")) {
                return onError(exchange, "Access denied", HttpStatus.FORBIDDEN);
              }
            }

            // Protected endpoints that require specific role checks can be added here

            return chain.filter(exchange);
          })
          .onErrorResume(error -> onError(exchange, "Error validating token: " + error.getMessage(), HttpStatus.UNAUTHORIZED));
    };
  }

  private boolean hasRole(List<String> roles, String requiredRole) {
    return roles != null && roles.contains(requiredRole);
  }

  public Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
    exchange.getResponse().setStatusCode(httpStatus);
    return exchange.getResponse().setComplete();
  }

  public static class Config {
    // Add configuration properties if needed
  }
}

package org.app.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
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
      if (path.startsWith("/auth/")) {
        return chain.filter(exchange);
      }
      if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
        return onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
      }

      String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

      if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
      }

      return webClientBuilder.build()
          .post()
          .uri("lb://auth-service/auth/validate")
          .header(HttpHeaders.AUTHORIZATION, authHeader)
          .retrieve()
          .bodyToMono(Void.class)
          .then(chain.filter(exchange))
          .onErrorResume(error -> onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED));
    };
  }

  public Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
    exchange.getResponse().setStatusCode(httpStatus);
    return exchange.getResponse().setComplete();
  }

  public static class Config {
    // Add configuration properties if needed
  }
}
package org.app.authservice.controller;

import java.util.HashMap;
import java.util.Map;
import org.app.authservice.dto.AuthRequest;
import org.app.authservice.dto.AuthResponse;
import org.app.authservice.security.JwtUtils;
import org.app.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final JwtUtils jwtUtil;

  public AuthController(AuthService authService, JwtUtils jwtUtil) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
    return ResponseEntity.ok(authService.authenticate(authRequest));
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
    if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
      refreshToken = refreshToken.substring(7);
      return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
    return ResponseEntity.badRequest().build();
  }

  @PostMapping("/validate")
  public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);
      try {
        String username = jwtUtil.extractEmail(token);
        if (username != null) {
          Map<String, Object> response = new HashMap<>();
          response.put("valid", true);
          response.put("username", username);
          response.put("message", "Token is valid");
          return ResponseEntity.ok(response);
        }
      } catch (Exception e) {
        // Invalid token
      }
    }
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("valid", false);
    errorResponse.put("message", "Invalid or missing token");
    return ResponseEntity.status(401).body(errorResponse);
  }

}
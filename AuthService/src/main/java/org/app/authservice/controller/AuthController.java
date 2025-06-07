package org.app.authservice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import org.app.authservice.dto.AuthRequest;
import org.app.authservice.dto.AuthResponse;
import org.app.authservice.dto.UserDTO;
import org.app.authservice.dto.UserResponseDTO;
import org.app.authservice.security.JwtUtils;
import org.app.authservice.service.AuthService;
import org.app.authservice.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final AuthService authService;
  private final JwtUtils jwtUtil;
    private final UserService userService;

  public AuthController(AuthService authService, JwtUtils jwtUtil, UserService userService) {
    this.authService = authService;
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
    AuthResponse authResponse = authService.authenticate(authRequest);

    ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
            .httpOnly(true)
            .path("/")
            .maxAge(7 * 24 * 60 * 60) // 7 days in seconds
            .build();

    authResponse.setRefreshToken(null);

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(authResponse);

  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
    if (refreshToken != null) {
      AuthResponse authResponse = authService.refreshToken(refreshToken);

      ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", authResponse.getRefreshToken())
              .httpOnly(true)
              .path("/")
              .maxAge(7 * 24 * 60 * 60) // 7 days
              .build();

      authResponse.setRefreshToken(null);

      return ResponseEntity.ok()
              .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
              .body(authResponse);
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

          // Extract and add roles to the response
          try {
            List<String> roles = jwtUtil.extractRoles(token);
            response.put("roles", roles);
          } catch (Exception e) {
            // If roles can't be extracted, default to empty list
            response.put("roles", new ArrayList<>());
          }

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

  @PostMapping("/logout")
  public ResponseEntity<Map<String, String>> logout() {
    ResponseCookie clearCookie = ResponseCookie.from("refresh_token", "")
            .httpOnly(true)
            .path("/")
            .maxAge(0)
            .build();

    Map<String, String> response = new HashMap<>();
    response.put("message", "Logged out successfully");

    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
            .body(response);
  }

  @PostMapping("/register")
  public UserResponseDTO createUser(@Valid @RequestBody UserDTO userDTO) {
    return userService.createUser(userDTO);
  }


}

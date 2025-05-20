package org.app.authservice.service;

import io.jsonwebtoken.Claims;
import org.app.authservice.dto.AuthRequest;
import org.app.authservice.dto.AuthResponse;
import org.app.authservice.entity.User;
import org.app.authservice.respository.UserRepository;
import org.app.authservice.security.JwtUtils;
import org.app.authservice.security.SecurityUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final UserRepository userRepository;
  private final JwtUtils jwtUtil;

  public AuthService(AuthenticationManager authenticationManager,
                     UserDetailsService userDetailsService,
                     UserRepository userRepository,
                     JwtUtils jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.userRepository = userRepository;
    this.jwtUtil = jwtUtil;
  }

  public AuthResponse authenticate(AuthRequest authRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
      );

      SecurityUser userDetails = (SecurityUser) authentication.getPrincipal();

      var roles = userDetails.getAuthorities().stream()
          .map(authority -> authority.getAuthority())  // get the role name
          .toList();

      Map<String, Object> claims = new HashMap<>();
      claims.put("roles", roles);

      String accessToken = jwtUtil.generateToken(userDetails, claims);
      String refreshToken = jwtUtil.generateRefreshToken(userDetails);

      User user = userRepository.findByEmail(userDetails.getUsername())
          .orElseThrow(() -> new UsernameNotFoundException("User not found"));

      AuthResponse authResponse = new AuthResponse();
      authResponse.setUuid(user.getUuid());
      authResponse.setEmail(user.getEmail());
      authResponse.setAccessToken(accessToken);
      authResponse.setRefreshToken(refreshToken);
      authResponse.setRole(user.getRole().getName());

      return authResponse;
    } catch (AuthenticationException e) {
      throw new RuntimeException("Invalid username or password", e);
    }
  }

  public AuthResponse refreshToken(String refreshToken) {
    try {
      String username = jwtUtil.extractEmail(refreshToken);
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      if (jwtUtil.validateToken(refreshToken, userDetails)) {
        SecurityUser securityUser = (SecurityUser) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", securityUser.getAuthorities());

        String newAccessToken = jwtUtil.generateToken(userDetails, claims);

        User user = userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        AuthResponse authResponse = new AuthResponse();
        authResponse.setUuid(user.getUuid());
        authResponse.setEmail(user.getEmail());
        authResponse.setRole(user.getRole().getName());
        authResponse.setAccessToken(newAccessToken);
        authResponse.setRefreshToken(refreshToken); // Reuse the same refresh token

        return authResponse;
      } else {
        throw new RuntimeException("Invalid refresh token");
      }
    } catch (Exception e) {
      throw new RuntimeException("Error processing refresh token", e);
    }
  }
}
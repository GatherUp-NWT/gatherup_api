package org.app.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.function.Function;

@Component
public class JwtUtils {

  @Value("${jwt.secret:123456789012345678901234567890")
  private String secret;

  @Value("${jwt.expiration:3600000}")
  private long jwtExpiration;

  @Value("${jwt.refresh-expiration:86400000}")
  private long refreshExpiration;

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }
  public String extractUserId(String token) {
    Claims claims = extractAllClaims(token);
    return claims.get("uuid", String.class);
  }


  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public List<String> extractRoles(String token) {
    Claims claims = extractAllClaims(token);
    Object rolesObj = claims.get("roles");

      return switch (rolesObj) {


          // Handle different possible formats
          case List<?> rolesList -> rolesList.stream()
                  .map(role -> {
                      if (role instanceof String) {
                          return (String) role;
                      } else if (role instanceof Map<?, ?> roleMap) {
                          // Handle case where roles are stored as authority objects
                          return (String) roleMap.get("authority");
                      } else {
                          return role.toString();
                      }
                  })
                  .toList();


          // Fallback for single role as string
          case String s -> List.of(s);
          case null, default -> new ArrayList<>();
      };

  }

  private Boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String generateToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, userDetails.getUsername(), jwtExpiration);
  }

  public String generateToken(UserDetails userDetails, Map<String, Object> claims) {
    return createToken(claims, userDetails.getUsername(), jwtExpiration);
  }

  public String generateRefreshToken(UserDetails userDetails) {
    return createToken(new HashMap<>(), userDetails.getUsername(), refreshExpiration);
  }

  private String createToken(Map<String, Object> claims, String subject, long expiration) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public Boolean validateToken(String token, UserDetails userDetails) {
    final String username =extractEmail(token);
    return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }
}

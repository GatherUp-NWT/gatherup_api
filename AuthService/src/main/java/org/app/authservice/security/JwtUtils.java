package org.app.authservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    List<Map<String, String>> roles = (List<Map<String, String>>) claims.get("roles");
    return roles.stream()
        .map(roleMap -> roleMap.get("authority"))
        .toList();
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

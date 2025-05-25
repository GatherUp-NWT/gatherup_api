package org.app.authservice.dto;


import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.app.authservice.entity.Role;

@Getter
@Setter

public class AuthResponse {
  private UUID uuid;
  private String email;
  private String accessToken;
  private String refreshToken;
  private String role;


}
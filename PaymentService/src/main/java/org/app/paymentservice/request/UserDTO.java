package org.app.paymentservice.request;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
  private UUID uuid;
  private String firstName;
  private String lastName;
  private String email;
  private String bio;
  private String role;
}
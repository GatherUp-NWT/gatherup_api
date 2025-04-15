package org.app.invitationservice.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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

  public UserDTO(UUID senderId, String alice, String mail) {
  }
}
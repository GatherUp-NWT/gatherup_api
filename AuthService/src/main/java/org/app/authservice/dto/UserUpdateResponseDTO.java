package org.app.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateResponseDTO {
    private String status;
    private String message;
    private UserNonSensitiveDTO user;
}

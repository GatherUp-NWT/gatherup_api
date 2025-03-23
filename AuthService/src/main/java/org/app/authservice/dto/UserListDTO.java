package org.app.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListDTO {

    @NotNull(message = "UUID is required")
    private UUID uuid;

    @NotNull(message = "First name is required")
    @Size(min = 2, max = 30)
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 2, max = 30)
    private String lastName;

    @NotNull(message = "Email is required")
    @Size(min = 5, max = 50)
    @Email(message = "Email should be valid")
    private String email;


    private String bio;
    private String role;
}

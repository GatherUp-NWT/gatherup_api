package org.app.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID uuid = UUID.randomUUID();

    @NotNull(message = "First name is required")
    @Size(min = 2, max = 15, message = "First name should be between 2 and 15 characters")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(min = 2, max = 15, message = "Last name should be between 2 and 15 characters")
    private String lastName;

    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password should be at least 8 characters and at most 20 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$",
            message = "Password must contain at least one digit, one letter, one special character, and no spaces")
    private String password;

    @Size(max = 100, message = "Bio should be at most 100 characters")
    private String bio;

    private String role = "USER";
}

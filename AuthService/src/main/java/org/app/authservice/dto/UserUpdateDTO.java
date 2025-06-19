package org.app.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.authservice.config.PasswordMatches;


@Data
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class UserUpdateDTO {

    @Size(min = 2, max = 15, message = "First name should be between 2 and 15 characters")
    private String firstName;

    @Size(min = 2, max = 15, message = "Last name should be between 2 and 15 characters")
    private String lastName;

    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 8, max = 20, message = "Password should be at least 8 characters and at most 20 characters")
    private String password;

    @Size(min = 8, max = 20, message = "Confirm password should be at least 8 characters and at most 20 characters")
    private String confirmPassword;

    @Size(max = 100, message = "Bio should be at most 100 characters")
    private String bio;

    private String role = "USER";
}


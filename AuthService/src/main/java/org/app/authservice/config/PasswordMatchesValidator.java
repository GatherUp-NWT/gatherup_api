package org.app.authservice.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.app.authservice.dto.UserUpdateDTO;

// Create validator implementation
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserUpdateDTO> {
    @Override
    public boolean isValid(UserUpdateDTO dto, ConstraintValidatorContext context) {
        boolean isValid = true;

        if ((dto.getPassword() != null && dto.getConfirmPassword() == null) ||
                (dto.getPassword() == null && dto.getConfirmPassword() != null)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Both password fields must be provided together")
                    .addConstraintViolation();
            isValid = false;
        } else if (dto.getPassword() != null && !dto.getPassword().equals(dto.getConfirmPassword())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords don't match")
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}

package org.app.registrationservice.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private Long id;
    @NotNull(message = "User ID cannot be null")
    private UUID userId;
    @NotNull(message = "Event ID cannot be null")
    private UUID eventId;
    private Timestamp timestamp;
}


package org.app.registrationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDeletionStatusMessage {
    private UUID eventId;
    private String correlationId;
    private boolean success;
    private String errorMessage;
}

package org.app.registrationservice.dto;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private Long id;
    private UUID userId;
    private UUID eventId;
    private Timestamp timestamp;
}


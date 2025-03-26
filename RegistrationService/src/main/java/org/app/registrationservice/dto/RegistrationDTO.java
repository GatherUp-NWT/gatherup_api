package org.app.registrationservice.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDTO {
    private Long id;
    private UUID userId;
    private UUID eventId;
}


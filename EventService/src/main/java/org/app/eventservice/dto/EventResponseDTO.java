package org.app.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDTO {
    private Boolean status;
    private String message;
    private UUID eventUUID;
}

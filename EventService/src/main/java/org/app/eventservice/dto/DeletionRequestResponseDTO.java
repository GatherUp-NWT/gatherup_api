package org.app.eventservice.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Builder
public class DeletionRequestResponseDTO {
    private String status;
    private String correlationId;
    private LocalDateTime timestamp;
    private String message;
    private String statusEndpoint;
}

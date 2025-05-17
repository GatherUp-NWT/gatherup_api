package org.app.eventservice.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class EventDeletionStatusDTO {
    private String status;
    private String correlationId;
    private LocalDateTime timestamp;
    private String message;
    private ErrorDetails error;

    @Data
    @Builder
    public static class ErrorDetails {
        private String code;
        private String message;
    }
}
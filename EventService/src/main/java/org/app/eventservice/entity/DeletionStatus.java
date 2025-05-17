package org.app.eventservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deletion_status")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeletionStatus {
    @Id
    private String correlationId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String message;

    private String errorCode;

    @Column(length = 1000)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @Column(nullable = false)
    private UUID eventId;
}
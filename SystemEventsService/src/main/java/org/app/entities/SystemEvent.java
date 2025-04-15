package org.app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "system_events")
public class SystemEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String serviceName;
    private String endpoint;
    private String httpMethod;
    private String resourceId;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    private Integer statusCode;
    private String userId;
    private Instant timestamp;
    private Long durationMs;
}

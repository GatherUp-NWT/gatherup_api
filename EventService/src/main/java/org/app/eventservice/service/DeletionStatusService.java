package org.app.eventservice.service;

import lombok.RequiredArgsConstructor;
import org.app.eventservice.entity.DeletionStatus;
import org.app.eventservice.repository.DeletionStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeletionStatusService {

    private final DeletionStatusRepository deletionStatusRepository;

    @Value("${app.deletion-status.retention-hours:24}")
    private int retentionHours;

    @Transactional
    public void initializeDeletion(String correlationId, UUID eventId) {
        DeletionStatus status = DeletionStatus.builder()
                .correlationId(correlationId)
                .eventId(eventId)
                .status("PENDING")
                .timestamp(LocalDateTime.now())
                .message("Deletion process initiated")
                .expiryTime(LocalDateTime.now().plusHours(retentionHours))
                .build();

        deletionStatusRepository.save(status);
    }

    @Transactional
    public void updateStatus(String correlationId, boolean success, String message,
                             String errorCode, String errorMessage) {
        DeletionStatus status = deletionStatusRepository.findById(correlationId)
                .orElseThrow(() -> new IllegalStateException("Status not found"));

        status.setStatus(success ? "COMPLETED" : "FAILED");
        status.setTimestamp(LocalDateTime.now());
        status.setMessage(message);
        status.setErrorCode(errorCode);
        status.setErrorMessage(errorMessage);

        deletionStatusRepository.save(status);
    }

    public DeletionStatus getStatus(String correlationId) {
        return deletionStatusRepository.findById(correlationId)
                .orElse(null);
    }

    // Run cleanup every hour
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void cleanupExpiredStatuses() {
        deletionStatusRepository.deleteExpiredStatuses(LocalDateTime.now());
    }
}
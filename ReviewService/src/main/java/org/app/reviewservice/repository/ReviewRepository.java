package org.app.reviewservice.repository;

import jakarta.validation.constraints.NotNull;
import org.app.reviewservice.entity.Review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByUserIdAndEventId(@NotNull(message = "User ID cannot be null") UUID userId, @NotNull(message = "Event ID cannot be null") UUID eventId);
}

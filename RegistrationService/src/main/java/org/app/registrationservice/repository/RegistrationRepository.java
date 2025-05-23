package org.app.registrationservice.repository;

import jakarta.validation.constraints.NotNull;
import org.app.registrationservice.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByUserId(UUID userId);
    List<Registration> findByEventId(UUID eventId);
    boolean existsByUserIdAndEventId(@NotNull(message = "User ID cannot be null") UUID userId, @NotNull(message = "Event ID cannot be null") UUID eventId);

    void deleteByEventId(UUID eventId);
}

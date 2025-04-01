package org.app.eventservice.repository;

import java.util.List;
import java.util.UUID;

import jakarta.persistence.Entity;
import org.app.eventservice.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findAllByCreatorUUID(UUID creatorUUID);

    List<Event> findAllByEventCategory_Name(String categoryName);

    @EntityGraph(attributePaths = {"agendas", "agendas.location", "status", "eventCategory"})
    List<Event> findAll();

    @EntityGraph(attributePaths = {"agendas", "agendas.location", "status", "eventCategory"})
    Page<Event> findAll(Pageable pageable);

    @Query(value = """
    SELECT e.*,
    (6371 * acos(cos(radians(:latitude)) * cos(radians(l.latitude::numeric)) *
    cos(radians(l.longitude::numeric) - radians(:longitude)) +
    sin(radians(:latitude)) * sin(radians(l.latitude::numeric)))) AS distance
    FROM events.events e
    JOIN events.agenda a ON e.uuid = a.event_id
    JOIN events.location l ON a.location_id = l.id
    WHERE e.start_date > CURRENT_TIMESTAMP
    GROUP BY e.uuid, distance
    HAVING MIN(6371 * acos(cos(radians(:latitude)) * cos(radians(l.latitude::numeric)) *
    cos(radians(l.longitude::numeric) - radians(:longitude)) +
    sin(radians(:latitude)) * sin(radians(l.latitude::numeric)))) <= :radius
    ORDER BY distance
    LIMIT :limit
    """, nativeQuery = true)
    //@EntityGraph(attributePaths = {"agendas", "agendas.location", "status", "eventCategory"})
    List<Event> findNearbyEvents(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radius") double radius,
            @Param("limit") int limit);
}

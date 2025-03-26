package org.app.eventservice.repository;

import java.util.List;
import java.util.UUID;
import org.app.eventservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findAllByCreatorUUID(UUID creatorUUID);

    List<Event> findAllByEventCategory_Name(String categoryName);
}

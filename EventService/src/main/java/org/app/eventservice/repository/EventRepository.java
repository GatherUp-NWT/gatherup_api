package org.app.eventservice.repository;

import java.util.UUID;
import org.app.eventservice.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, UUID> {

}

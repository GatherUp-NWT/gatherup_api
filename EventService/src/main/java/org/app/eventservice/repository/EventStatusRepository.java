package org.app.eventservice.repository;

import org.app.eventservice.entity.Event;
import org.app.eventservice.entity.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventStatusRepository  extends JpaRepository<EventStatus, Long> {
    EventStatus findByName(String name);
}

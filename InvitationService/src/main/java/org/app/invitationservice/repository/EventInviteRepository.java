package org.app.invitationservice.repository;

import java.util.Optional;
import java.util.UUID;
import org.app.invitationservice.entity.EventInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventInviteRepository extends JpaRepository<EventInvite, UUID> {
  Optional<EventInvite> findByEventName(String eventName);


  Optional<EventInvite> findById(Long uuid);
}

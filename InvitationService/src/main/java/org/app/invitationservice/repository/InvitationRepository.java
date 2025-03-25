package org.app.invitationservice.repository;

import java.util.Optional;
import org.app.invitationservice.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {


  @Query("SELECT i FROM Invitation i WHERE i.user.id = :userId AND i.event.id = :eventId")
  Invitation findByUserAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);
}

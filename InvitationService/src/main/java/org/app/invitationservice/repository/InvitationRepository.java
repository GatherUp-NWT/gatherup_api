
package org.app.invitationservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.app.invitationservice.entity.Invitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {


  @Query("SELECT i FROM Invitation i WHERE i.user.id = :userId AND i.event.id = :eventId")
  Invitation findByUserAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

  @Query("SELECT i.receivedInvitations FROM UserInvite i WHERE i.userId = :userId")
  Page<Invitation> getReceivedInvitations(@Param("userId") UUID userId, Pageable pageable);
  ;
}

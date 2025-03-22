package org.app.invitationservice.repository;

import org.app.invitationservice.entity.InvitationResponseType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationTypeRepository extends JpaRepository<InvitationResponseType, Long> {
}

package org.app.invitationservice.repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import org.app.invitationservice.entity.UserInvite;
import org.modelmapper.internal.bytebuddy.dynamic.DynamicType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInviteRepository extends JpaRepository<UserInvite, UUID> {
  Optional<UserInvite> findByUserId(UUID userId);

  Optional<UserInvite> findByEmail(String email);

  Optional<UserInvite> findById(Long userId);
}

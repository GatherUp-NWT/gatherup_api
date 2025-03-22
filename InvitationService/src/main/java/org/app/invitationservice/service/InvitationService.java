package org.app.invitationservice.service;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.app.invitationservice.entity.Invitation;
import org.app.invitationservice.entity.InvitationResponseType;
import org.app.invitationservice.repository.InvitationRepository;
import org.app.invitationservice.repository.InvitationTypeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvitationService {

  private final InvitationRepository invitationRepository;
  private final InvitationTypeRepository invitationResponseTypeRepository;

  @PostConstruct
  public void initDatabase() {
    if (invitationResponseTypeRepository.count() == 0) { // Prevents duplicate inserts


      InvitationResponseType accepted = new InvitationResponseType();
      accepted.setResponseType("Accepted");
      invitationResponseTypeRepository.save(accepted);

      InvitationResponseType declined = new InvitationResponseType();
      declined.setResponseType("Declined");
      invitationResponseTypeRepository.save(declined);


      Invitation invitation = new Invitation();
      invitation.setUserId(UUID.randomUUID());
      invitation.setEventId(UUID.randomUUID());
      invitation.setSendDate(LocalDateTime.now());
      invitation.setResponseDate(LocalDateTime.now().plusDays(1));
      invitation.setInvitationResponseType(accepted);


      invitationRepository.save(invitation);

      System.out.println("Invitation data populated!");
    }
  }
}


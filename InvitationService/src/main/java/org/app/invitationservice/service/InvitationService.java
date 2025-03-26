package org.app.invitationservice.service;


import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.app.invitationservice.entity.EventInvite;
import org.app.invitationservice.entity.Invitation;
import org.app.invitationservice.entity.InvitationResponseType;
import org.app.invitationservice.entity.UserInvite;

import org.app.invitationservice.repository.EventInviteRepository;
import org.app.invitationservice.repository.InvitationRepository;
import org.app.invitationservice.repository.UserInviteRepository;
import org.app.invitationservice.request.InvitationRequest;
import org.app.invitationservice.response.InvitationModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationService {

  private final InvitationRepository invitationRepository;

  private final  UserInviteRepository userInviteRepository;
  private final EventInviteRepository eventInviteRepository;




  public InvitationModel sendInvitation(InvitationRequest invitationRequest) {
    UserInvite sender = userInviteRepository.findByUserId(invitationRequest.getSenderUserId())
        .orElseThrow(() -> new RuntimeException("Sender does not exist"));

    UserInvite receiver = userInviteRepository.findByEmail(invitationRequest.getReceiverEmail())
        .orElseThrow(() -> new RuntimeException("Receiver does not exist"));

    EventInvite event = eventInviteRepository.findByEventName(invitationRequest.getEventName())
        .orElseThrow(() -> new RuntimeException("Event does not exist"));

    Invitation invitation = new Invitation();
    invitation.setSendBy(sender);
    invitation.setUser(receiver);
    invitation.setEvent(event);
    invitation.setSendDate(LocalDateTime.now());
    invitation.setInvitationResponseType(InvitationResponseType.PENDING);

    invitationRepository.save(invitation);

    receiver.getReceivedInvitations().add(invitation);
    userInviteRepository.save(receiver);

    return new InvitationModel(
        sender.getUserName(),
        event.getEventName(),
        invitation.getSendDate(),
        "PENDING"
    );
  }


  public List<Invitation> getAllUserInvitations(Long userId) {
    UserInvite user = userInviteRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

    List<Invitation> userInvitations = user.getReceivedInvitations();

    if (userInvitations.isEmpty()) {
      throw new RuntimeException("No invitations found");
    }
    return userInvitations;
  }



  public String answerInvite(Long userId, Long eventId, String response) {


    Invitation invitation=invitationRepository.findByUserAndEventId(userId, eventId);
    if (!response.equalsIgnoreCase("ACCEPTED") && !response.equalsIgnoreCase("REJECTED")) {
      throw new IllegalArgumentException("Invalid response type Use 'ACCEPTED' or 'REJECTED'.");
    }
    if(response.equalsIgnoreCase("ACCEPTED")){
    invitation.setInvitationResponseType(InvitationResponseType.ACCEPTED);}
    else if(response.equalsIgnoreCase("REJECTED")){
      invitation.setInvitationResponseType(InvitationResponseType.REJECTED);
    }
    invitation.setResponseDate(LocalDateTime.now());

    invitationRepository.save(invitation);

    return "Invitation " + response.toUpperCase();

  }


//  @PostConstruct
//  public void initDatabase() {
//    if (invitationResponseTypeRepository.count() == 0) { // Prevents duplicate inserts
//
//
//      InvitationResponseType accepted = new InvitationResponseType();
//      accepted.setResponseType("Accepted");
//      invitationResponseTypeRepository.save(accepted);
//
//      InvitationResponseType declined = new InvitationResponseType();
//      declined.setResponseType("Declined");
//      invitationResponseTypeRepository.save(declined);
//
//
//      Invitation invitation = new Invitation();
//      invitation.setUserId(UUID.randomUUID());
//      invitation.setEventId(UUID.randomUUID());
//      invitation.setSendDate(LocalDateTime.now());
//      invitation.setResponseDate(LocalDateTime.now().plusDays(1));
//      invitation.setInvitationResponseType(accepted);
//
//
//      invitationRepository.save(invitation);
//
//      System.out.println("Invitation data populated!");
//    }
//  }


}



package org.app.invitationservice.service;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.app.invitationservice.clients.AuthClient;
import org.app.invitationservice.clients.EventClient;
import org.app.invitationservice.entity.EventInvite;
import org.app.invitationservice.entity.Invitation;
import org.app.invitationservice.entity.InvitationResponseType;
import org.app.invitationservice.entity.TimeStatus;
import org.app.invitationservice.entity.UserInvite;

import org.app.invitationservice.repository.EventInviteRepository;
import org.app.invitationservice.repository.InvitationRepository;
import org.app.invitationservice.repository.UserInviteRepository;
import org.app.invitationservice.request.InvitationRequest;
import org.app.invitationservice.response.EventDto;
import org.app.invitationservice.response.EventResponseDto;
import org.app.invitationservice.response.InvitationModel;
import org.app.invitationservice.response.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvitationService {

  private final InvitationRepository invitationRepository;

  private final  UserInviteRepository userInviteRepository;
  private final EventInviteRepository eventInviteRepository;
  private final AuthClient authClient;
  private final EventClient eventClient;

  UserInvite mapToUserInvite(UserDTO userDTO) {
    return new UserInvite(null,userDTO.getUuid(), userDTO.getFirstName(), userDTO.getEmail(), null);
  }
  EventInvite mapToEventInvite(EventDto eventDto) {
   return  new EventInvite(null, eventDto.getUuid(),eventDto.getName(), eventDto.getStartDate().atZone(ZoneId.systemDefault()).toLocalDateTime());

  }


  public InvitationModel sendInvitation(InvitationRequest invitationRequest) {

    UserDTO sender = authClient.getUserById(invitationRequest.getSenderUserId().toString());
    UserDTO receiver = authClient.getUserByEmail(invitationRequest.getReceiverEmail());
    if (receiver == null) {
      throw new RuntimeException("Receiver not found with email: " + invitationRequest.getReceiverEmail());
    }
    EventResponseDto eventResponseDto = eventClient.getEventById(invitationRequest.getEventId().toString());
    EventDto event = eventResponseDto.getEvent();

    EventInvite eventInvite = mapToEventInvite(event);
    eventInviteRepository.save(eventInvite);

    UserInvite senderOfInvitation = mapToUserInvite(sender);
    UserInvite receiverOfInvitation = mapToUserInvite(receiver);
    userInviteRepository.save(senderOfInvitation);

    Invitation invitation = new Invitation();
    invitation.setSendBy(senderOfInvitation);
    invitation.setUser(receiverOfInvitation);  // receiver
    invitation.setEvent(eventInvite);
    invitation.setSendDate(LocalDateTime.now());
    invitation.setInvitationResponseType(InvitationResponseType.PENDING);
    invitation.setStatusInTime(TimeStatus.UPCOMING);

    userInviteRepository.save(receiverOfInvitation);

    invitationRepository.save(invitation);


    return new InvitationModel(
        senderOfInvitation.getUserName(),
        eventInvite.getEventName(),
        invitation.getSendDate(),
        "PENDING"
    );
  }

  public Page<Invitation> getAllUserInvitations(UUID userId, Pageable pageable) {

      return invitationRepository.getReceivedInvitations(userId, pageable);
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

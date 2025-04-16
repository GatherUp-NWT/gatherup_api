package org.app.invitationservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import org.app.invitationservice.clients.AuthClient;
import org.app.invitationservice.clients.EventClient;
import org.app.invitationservice.repository.EventInviteRepository;
import org.app.invitationservice.repository.InvitationRepository;
import org.app.invitationservice.repository.UserInviteRepository;
import org.app.invitationservice.request.InvitationRequest;
import org.app.invitationservice.response.EventDto;
import org.app.invitationservice.response.EventResponseDto;
import org.app.invitationservice.response.InvitationModel;
import org.app.invitationservice.response.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class InvitationServiceTest {

  @InjectMocks
  private InvitationService invitationService;

  @Mock
  private InvitationRepository invitationRepository;
  @Mock
  private UserInviteRepository userInviteRepository;
  @Mock
  private EventInviteRepository eventInviteRepository;
  @Mock
  private AuthClient authClient;
  @Mock
  private EventClient eventClient;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }


  @Test
  void testSendInvitation() {
    UUID senderId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    InvitationRequest request = new InvitationRequest(senderId, receiverId, eventId);
    request.setSenderUserId(senderId);
    request.setReceiverUserId(receiverId);
    request.setEventId(eventId);

    UserDTO senderDTO = new UserDTO(senderId, "Alice", "alice@email.com");
    senderDTO.setFirstName("Alice");
    senderDTO.setLastName("Bob");
    senderDTO.setEmail("alice@email.com");

    UserDTO receiverDTO = new UserDTO(receiverId, "Bob", "bob@email.com");
    receiverDTO.setFirstName("Bob");
    receiverDTO.setLastName("Alice");
    receiverDTO.setEmail("alice@email.com");


    EventDto eventDto = new EventDto(eventId,"Party",  "Birthday Party", LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());

    EventResponseDto eventResponseDto = new EventResponseDto(eventDto);
    eventResponseDto.setEvent(eventDto);
    eventResponseDto.setStatus(true);
    eventResponseDto.setMessage("Hello World");

    when(authClient.getUserById(senderId.toString())).thenReturn(senderDTO);
    when(authClient.getUserById(receiverId.toString())).thenReturn(receiverDTO);
    when(eventClient.getEventById(eventId.toString())).thenReturn(eventResponseDto);

    InvitationModel response = invitationService.sendInvitation(request);

    assertNotNull(response);
    assertEquals("Alice", response.sendByUserName());
    assertEquals("Party", response.eventName());
    assertEquals("PENDING", response.responseType());
  }
  @Test
  void testSendInvitation_NullSenderId_ThrowsException() {
    UUID receiverId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    InvitationRequest request = new InvitationRequest(null, receiverId, eventId);

    Exception exception = org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
      invitationService.sendInvitation(request);
    });

    assertEquals("Cannot invoke \"java.util.UUID.toString()\" because the return value of \"org.app.invitationservice.request.InvitationRequest.getSenderUserId()\" is null", exception.getMessage());
  }
  @Test
  void testSendInvitation_UserNotFound() {
    UUID senderId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    InvitationRequest request = new InvitationRequest(senderId, receiverId, eventId);

    when(authClient.getUserById(senderId.toString())).thenReturn(null); // simulate user not found

    Exception exception = org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
      invitationService.sendInvitation(request);
    });

    assertNotNull(exception);
  }
  @Test
  void testSendInvitation_EventNotFound() {
    UUID senderId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    InvitationRequest request = new InvitationRequest(senderId, receiverId, eventId);

    UserDTO senderDTO = new UserDTO(senderId, "Alice", "alice@email.com");
    UserDTO receiverDTO = new UserDTO(receiverId, "Bob", "bob@email.com");

    when(authClient.getUserById(senderId.toString())).thenReturn(senderDTO);
    when(authClient.getUserById(receiverId.toString())).thenReturn(receiverDTO);
    when(eventClient.getEventById(eventId.toString())).thenReturn(null); // simulate missing event

    Exception exception = org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> {
      invitationService.sendInvitation(request);
    });

    assertNotNull(exception);
  }





}


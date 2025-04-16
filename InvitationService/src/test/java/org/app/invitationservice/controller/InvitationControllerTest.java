package org.app.invitationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.app.invitationservice.controllers.InvitationController;
import org.app.invitationservice.entity.EventInvite;
import org.app.invitationservice.entity.Invitation;
import org.app.invitationservice.entity.UserInvite;
import org.app.invitationservice.repository.EventInviteRepository;
import org.app.invitationservice.repository.UserInviteRepository;
import org.app.invitationservice.request.InvitationRequest;
import org.app.invitationservice.response.InvitationModel;
import org.app.invitationservice.service.InvitationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InvitationController.class)
class InvitationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private InvitationService invitationService;

  @MockitoBean
  private UserInviteRepository userInviteRepository;

  @MockitoBean
  private EventInviteRepository eventInviteRepository;

  private InvitationRequest request;
  private InvitationModel model;

  @BeforeEach
  void setUp() {
    UUID senderId = UUID.randomUUID();
    UUID receiverId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();

    request = new InvitationRequest(senderId, receiverId, eventId);
    model = new InvitationModel(

        "Alice",
        "Party",
        LocalDateTime.now(),
        "PENDING"


    );
  }

  @Test
  void testCreateInvitation() throws Exception {
    Mockito.when(invitationService.sendInvitation(any(InvitationRequest.class)))
        .thenReturn(model);

    mockMvc.perform(post("/api/v1/invitations/new")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.sendByUserName").value("Alice"))
        .andExpect(jsonPath("$.eventName").value("Party"))
        .andExpect(jsonPath("$.responseType").value("PENDING"));
  }

  @Test
  void testGetInvitations() throws Exception {
    Invitation invitation = new Invitation(); // minimal mock
    Page<Invitation> page = new PageImpl<>(List.of(invitation));

    Mockito.when(invitationService.getAllUserInvitations(eq(1L), any(PageRequest.class)))
        .thenReturn(page);

    mockMvc.perform(get("/api/v1/invitations/1?page=0&size=10"))
        .andExpect(status().isOk());
  }

  @Test
  void testAnswerInvitation() throws Exception {
    UserInvite userInvite = new UserInvite();
    EventInvite eventInvite = new EventInvite();

    Mockito.when(userInviteRepository.findById(1L)).thenReturn(java.util.Optional.of(userInvite));
    Mockito.when(eventInviteRepository.findById(2L)).thenReturn(java.util.Optional.of(eventInvite));
    Mockito.when(invitationService.answerInvite(1L, 2L, "ACCEPTED")).thenReturn("Invitation Accepted");

    mockMvc.perform(post("/api/v1/invitations/1/2/ACCEPTED"))
        .andExpect(status().isOk())
        .andExpect(content().string("Invitation Accepted"));
  }

  @Test
  void testAnswerInvitation_UserNotFound() throws Exception {
    Mockito.when(userInviteRepository.findById(1L)).thenReturn(java.util.Optional.empty());

    mockMvc.perform(post("/api/v1/invitations/1/2/DECLINED"))
        .andExpect(status().isInternalServerError());
  }

  @Test
  void testAnswerInvitation_EventNotFound() throws Exception {
    Mockito.when(userInviteRepository.findById(1L)).thenReturn(java.util.Optional.of(new UserInvite()));
    Mockito.when(eventInviteRepository.findById(2L)).thenReturn(java.util.Optional.empty());

    mockMvc.perform(post("/api/v1/invitations/1/2/DECLINED"))
        .andExpect(status().isInternalServerError());
  }
}

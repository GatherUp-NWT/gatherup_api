package org.app.invitationservice.controllers;

import java.util.List;
import org.app.invitationservice.entity.EventInvite;
import org.app.invitationservice.entity.Invitation;
import org.app.invitationservice.entity.UserInvite;
import org.app.invitationservice.repository.EventInviteRepository;
import org.app.invitationservice.repository.UserInviteRepository;
import org.app.invitationservice.request.InvitationRequest;
import org.app.invitationservice.response.InvitationModel;
import org.app.invitationservice.service.InvitationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("invitations")
public class InvitationController {

  private final InvitationService invitationService;
  private final UserInviteRepository userInviteRepository;
  private final EventInviteRepository eventInviteRepository;

  public InvitationController(InvitationService invitationService, UserInviteRepository userInviteRepository,
                              EventInviteRepository eventInviteRepository) {
    this.invitationService = invitationService;
    this.userInviteRepository = userInviteRepository;
    this.eventInviteRepository = eventInviteRepository;
  }




  @PostMapping("/new")
  public ResponseEntity<InvitationModel> createInvitation(@RequestBody InvitationRequest request) {
    InvitationModel response = invitationService.sendInvitation(request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{userId}")
  public Page<Invitation> getInvitations(@PathVariable Long userId, Pageable pageable) {
    return invitationService.getAllUserInvitations(userId, pageable);
  }
  @PostMapping("/{userId}/{eventId}/{response}")
  public ResponseEntity<String> answerInvitation(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable String response) {
    UserInvite userInvite = userInviteRepository.findById(userId).orElseThrow(()->new RuntimeException("User Invite not found"));
    EventInvite eventInvite=eventInviteRepository.findById(eventId).orElseThrow(()->new RuntimeException("Event Invite not found"));
    return ResponseEntity.ok(invitationService.answerInvite(userId, eventId, response));
  }




}
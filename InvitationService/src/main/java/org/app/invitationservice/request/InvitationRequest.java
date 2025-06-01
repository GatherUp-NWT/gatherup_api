package org.app.invitationservice.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NotNull
public class InvitationRequest {
  private UUID senderUserId;
  private String receiverEmail;
  private UUID eventId;
  private LocalDateTime sendDate;

  public InvitationRequest(UUID senderId, UUID receiverId, UUID eventId) {
  }
}
package org.app.invitationservice.request;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class InvitationRequest {
  private UUID senderUserId;
  private UUID receiverUserId;
  private String receiverEmail;
  private UUID eventId;
  private String eventName;
  private LocalDateTime sendDate;
}
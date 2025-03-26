package org.app.invitationservice.request;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class InvitationRequest {
  private UUID senderUserId;
  private String receiverEmail;
  private String eventName;
  private LocalDateTime sendDate;
}
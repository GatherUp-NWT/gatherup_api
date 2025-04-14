package org.app.invitationservice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.invitationservice.response.EventResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDto {
  private Boolean status;
  private String message;
  private EventDto event;
}

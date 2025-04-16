package org.app.invitationservice.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.invitationservice.response.EventResponseDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@NotNull
public class EventResponseDto {
  private Boolean status;
  private String message;
  private EventDto event;

  public EventResponseDto(EventDto eventDto) {
  }
}

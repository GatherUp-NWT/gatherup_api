package org.app.invitationservice.response;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NotNull
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {
  public UUID uuid;
  private String name;
  private String description;
  private Instant startDate;


  public EventDto(UUID eventId, String birthdayParty, LocalDateTime localDateTime) {
  }
}


package org.app.invitationservice.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventDto {
  public UUID uuid;
  private String name;
  private String description;
  private Instant startDate;


}


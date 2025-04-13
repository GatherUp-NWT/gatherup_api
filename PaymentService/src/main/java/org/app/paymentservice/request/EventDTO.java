package org.app.paymentservice.request;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EventDTO {
  public UUID uuid;
  private String name;
  private String description;
  private Instant startDate;


}

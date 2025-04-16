package org.app.paymentservice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class EventResponseDTO {
  private Boolean status;
  private String message;
  private EventDTO event;
}


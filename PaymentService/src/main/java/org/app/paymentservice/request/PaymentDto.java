package org.app.paymentservice.request;

import java.util.UUID;
import lombok.Data;
import lombok.Getter;

@Getter

public class PaymentDto {
  private  UUID userId;
  private  UUID eventId;
  private  Integer price;
}

package org.app.paymentservice.response;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.app.paymentservice.entity.Ticket;
import org.mapstruct.Mapping;



public record PaymentModel(UUID userId, UUID eventId, LocalDateTime paidDate, Integer price){



}

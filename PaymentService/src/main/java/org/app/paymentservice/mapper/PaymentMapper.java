package org.app.paymentservice.mapper;

import org.app.paymentservice.entity.Ticket;
import org.app.paymentservice.request.PaymentDto;
import org.app.paymentservice.response.PaymentModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface PaymentMapper {
  @Mapping(target = "price", source = "price")
  @Mapping(target = "paidDate", source = "paidDate")
  PaymentModel mapToModel(Ticket ticket);



  Ticket mapToTicket(PaymentModel paymentModel);



}

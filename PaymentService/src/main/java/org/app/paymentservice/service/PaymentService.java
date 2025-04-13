package org.app.paymentservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.paymentservice.clients.AuthClient;
import org.app.paymentservice.clients.EventClient;
import org.app.paymentservice.entity.Ticket;
import org.app.paymentservice.mapper.PaymentMapper;
import org.app.paymentservice.repository.PaymentRepository;
import org.app.paymentservice.request.EventDTO;
import org.app.paymentservice.request.EventResponseDTO;
import org.app.paymentservice.request.PaymentDto;
import org.app.paymentservice.request.UserDTO;
import org.app.paymentservice.response.EventSold;
import org.app.paymentservice.response.PaymentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Validated
@Service

public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;
  private final AuthClient authClient;
  private final EventClient eventClient;

  public PaymentService(PaymentRepository paymentRepository,
                        PaymentMapper paymentMapper, AuthClient authClient, EventClient eventClient) {
    this.paymentRepository = paymentRepository;
    this.paymentMapper = paymentMapper;
    this.authClient = authClient;
    this.eventClient = eventClient;
  }

  @PostConstruct
  public void initDatabase() {
    if (paymentRepository.count() == 0) {


      Ticket ticket = new Ticket();
      ticket.setUserId(UUID.fromString("13004255-59fa-4df6-9ab8-34e40f7058bf"));
      ticket.setEventId(UUID.fromString("df77cdec-e4d0-488e-99fe-210a4cada331"));
      ticket.setPaidDate(LocalDateTime.now());


     paymentRepository.save(ticket);

      System.out.println("Ticket data populated!");
    }
  }

  public Page<PaymentModel> getAllPayments(Pageable pageable) {
    Page<Ticket> payments =  paymentRepository.findAll(pageable);
    if (payments.isEmpty()) {
      throw new RuntimeException("No payments found!");
    }
    return payments.map(paymentMapper::mapToModel);
  }

  @Transactional
  public PaymentModel crateNewPayment(PaymentDto paymentDto) {

    PaymentModel newTicket;
    try {
      UserDTO user=authClient.getUserById(paymentDto.getUserId().toString());
      EventResponseDTO event = eventClient.getEventById(paymentDto.getEventId().toString());


      System.out.println("Fetched event: " + event.getEvent().getUuid());


      if (user == null || event.getEvent().getUuid() == null) {
        throw new IllegalStateException("User or event not found");
      }
      newTicket = new PaymentModel(user.getUuid(), event.getEvent().getUuid(), LocalDateTime.now(),paymentDto.getPrice());
      paymentRepository.save(paymentMapper.mapToTicket(newTicket));
      return newTicket;
    } catch (Exception e) {
      throw new RuntimeException("Error creating payment: " + e.getMessage(), e);
    }

  }

  public Page<PaymentModel> getAllUserPayments(UUID userId ,Pageable pageable) {
    Page<Ticket> payments =  paymentRepository.findByUserId(userId, pageable);
    if (payments.isEmpty()) {
      throw new RuntimeException("No payments found!");
    }
    return payments.map(paymentMapper::mapToModel);
  }

  public EventSold getANumberOfSoldTicketsForEvent(UUID eventId) {
    Ticket payment=paymentRepository.findByEventId(eventId);
    if (payment==null) {
      throw new RuntimeException("No payment found for event!");
    }
    return paymentRepository.getNumberOfTicketsSoldForEvent(eventId);
  }
}

package org.app.paymentservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.app.paymentservice.entity.Ticket;
import org.app.paymentservice.mapper.PaymentMapper;
import org.app.paymentservice.repository.PaymentRepository;
import org.app.paymentservice.request.PaymentDto;
import org.app.paymentservice.response.EventSold;
import org.app.paymentservice.response.PaymentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentMapper paymentMapper;

  public PaymentService(PaymentRepository paymentRepository,
                        PaymentMapper paymentMapper) {
    this.paymentRepository = paymentRepository;
    this.paymentMapper = paymentMapper;
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
    UUID userId = paymentRepository.findUserId(paymentDto.getUserId());
    if (userId == null) {
      throw new RuntimeException("User not found!");
    }


    PaymentModel newTicket=new PaymentModel(paymentDto.getUserId(), paymentDto.getEventId(), LocalDateTime.now(),paymentDto.getPrice());
    paymentRepository.save(paymentMapper.mapToTicket(newTicket));
    return newTicket;
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

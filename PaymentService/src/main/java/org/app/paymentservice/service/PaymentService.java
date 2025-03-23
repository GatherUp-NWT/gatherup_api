package org.app.paymentservice.service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.app.paymentservice.entity.Ticket;
import org.app.paymentservice.mapper.PaymentMapper;
import org.app.paymentservice.repository.PaymentRepository;
import org.app.paymentservice.response.PaymentModel;
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

  public List<PaymentModel> getAllPayments() {
    List<Ticket> payments = (List<Ticket>) paymentRepository.findAll();
    if (payments.isEmpty()) {
      throw new RuntimeException("No payments found!");
    }
    return payments.stream().map(paymentMapper::mapToModel).toList();
  }

  public PaymentModel crateNewPayment(UUID userId, UUID eventId) {
    PaymentModel newTicket=new PaymentModel(userId, eventId, LocalDateTime.now());
    paymentRepository.save(paymentMapper.mapToTicket(newTicket));
    return newTicket;
  }

  public List<PaymentModel> getAllUserPayments(UUID userId) {
    List<Ticket> payments =  paymentRepository.findByUserId(userId);
    if (payments.isEmpty()) {
      throw new RuntimeException("No payments found!");
    }
    return payments.stream().map(paymentMapper::mapToModel).toList();
  }
}

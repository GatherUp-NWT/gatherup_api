package org.app.paymentservice.service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.UUID;
import org.app.paymentservice.entity.Ticket;
import org.app.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

  private final PaymentRepository paymentRepository;

  public PaymentService(PaymentRepository paymentRepository) {
    this.paymentRepository = paymentRepository;
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
}

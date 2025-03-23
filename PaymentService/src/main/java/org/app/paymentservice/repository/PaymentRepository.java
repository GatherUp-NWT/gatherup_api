package org.app.paymentservice.repository;

import java.util.List;
import java.util.UUID;
import org.app.paymentservice.entity.Ticket;
import org.app.paymentservice.response.PaymentModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Ticket, Long> {


  List<Ticket> findByUserId(UUID userId);
}

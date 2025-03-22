package org.app.paymentservice.repository;

import org.app.paymentservice.entity.Ticket;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRepository extends CrudRepository<Ticket, Long> {
}

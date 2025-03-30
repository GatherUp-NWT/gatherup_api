package org.app.paymentservice.repository;

import java.util.List;
import java.util.UUID;
import org.app.paymentservice.entity.Ticket;
import org.app.paymentservice.response.PaymentModel;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends CrudRepository<Ticket, Long> {


  @Query("SELECT t.userId FROM Ticket t WHERE t.userId = :id")
UUID findUserId(@Param("id") UUID id);


  List<Ticket> findByUserId(UUID userId);
}

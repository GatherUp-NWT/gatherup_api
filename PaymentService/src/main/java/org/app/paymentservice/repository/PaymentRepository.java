package org.app.paymentservice.repository;

import java.util.List;
import java.util.UUID;
import org.app.paymentservice.entity.Ticket;
import org.app.paymentservice.response.EventSold;
import org.app.paymentservice.response.PaymentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends CrudRepository<Ticket, Long> {


  @Query("SELECT t.userId FROM Ticket t WHERE t.userId = :id")
UUID findUserId(@Param("id") UUID id);


  List<Ticket> findByUserId(UUID userId);
  Page<Ticket> findByUserId(UUID userId,
                            Pageable pageable);

  Page<Ticket> findAll(Pageable pageable);

  Ticket findByEventId(UUID eventId);

  @Query("SELECT new org.app.paymentservice.response.EventSold(e.eventId, COUNT(e)) " +
      "FROM Ticket e " +
      "WHERE e.eventId = :eventId " +
      "GROUP BY e.eventId")
  EventSold getNumberOfTicketsSoldForEvent(@Param("eventId") UUID eventId);

}

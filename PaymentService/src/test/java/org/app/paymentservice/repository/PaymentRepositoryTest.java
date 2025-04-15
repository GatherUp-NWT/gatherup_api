package org.app.paymentservice.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;
import org.app.paymentservice.entity.Ticket;
import org.app.paymentservice.response.EventSold;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
class PaymentRepositoryTest {

  @Autowired
  private PaymentRepository paymentRepository;

  private Ticket sampleTicket;

  private UUID userId;
  private UUID eventId;

  @BeforeEach
  void setUp() {
    // Prepare test data
    userId = UUID.randomUUID();
    eventId = UUID.randomUUID();
    sampleTicket = new Ticket();
    sampleTicket.setUserId(userId);
    sampleTicket.setEventId(eventId);
    paymentRepository.save(sampleTicket);
  }

  @Test
  void testFindUserId() {
    UUID result = paymentRepository.findUserId(userId);
    assertEquals(userId, result, "User ID should match");
  }

  @Test
  void testFindByUserId() {
    List<Ticket> tickets = paymentRepository.findByUserId(userId);
    assertFalse(tickets.isEmpty(), "Tickets should not be empty");
    assertEquals(userId, tickets.get(0).getUserId(), "User ID should match");
  }

  @Test
  void testFindByUserIdWithPagination() {
    // Testing with Pageable
    List<Ticket> tickets = paymentRepository.findByUserId(userId, Pageable.unpaged()).getContent();
    assertFalse(tickets.isEmpty(), "Tickets should not be empty");
  }

  @Test
  void testFindAll() {
    List<Ticket> tickets = paymentRepository.findAll(Pageable.unpaged()).getContent();
    assertFalse(tickets.isEmpty(), "Tickets should not be empty");
  }

  @Test
  void testFindByEventId() {
    Ticket ticket = paymentRepository.findByEventId(eventId);
    assertNotNull(ticket, "Ticket should not be null");
    assertEquals(eventId, ticket.getEventId(), "Event ID should match");
  }

  @Test
  void testGetNumberOfTicketsSoldForEvent() {
    EventSold eventSold = paymentRepository.getNumberOfTicketsSoldForEvent(eventId);
    assertNotNull(eventSold, "EventSold object should not be null");
    assertEquals(eventId, eventSold.getEventId(), "Event ID should match");
    assertEquals(1, eventSold.getNumberOfSoldTickets(), "Number of tickets should be 1");
  }
}

package org.app.paymentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.app.paymentservice.clients.AuthClient;
import org.app.paymentservice.clients.EventClient;
import org.app.paymentservice.entity.Ticket;
import org.app.paymentservice.mapper.PaymentMapper;
import org.app.paymentservice.repository.PaymentRepository;
import org.app.paymentservice.request.PaymentDto;
import org.app.paymentservice.response.EventSold;
import org.app.paymentservice.response.PaymentModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class PaymentServiceTest {

  @Mock private PaymentRepository paymentRepository;
  @Mock private PaymentMapper paymentMapper;
  @Mock private AuthClient authClient;
  @Mock private EventClient eventClient;

  @InjectMocks private PaymentService paymentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void initDatabase_shouldInsertSampleTicketIfEmpty() {
    when(paymentRepository.count()).thenReturn(0L);
    when(paymentRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArgument(0));

    paymentService.initDatabase();

    verify(paymentRepository, times(1)).save(any(Ticket.class));
  }

  @Test
  void getAllPayments_shouldReturnMappedPayments() {
    Ticket ticket = new Ticket();
    Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket));
    PaymentModel model = new PaymentModel(null,null,null,null);

    when(paymentRepository.findAll(any(Pageable.class))).thenReturn(ticketPage);
    when(paymentMapper.mapToModel(ticket)).thenReturn(model);

    Page<PaymentModel> result = paymentService.getAllPayments(PageRequest.of(0, 10));

    assertEquals(1, result.getTotalElements());
    verify(paymentMapper, times(1)).mapToModel(ticket);
  }

  @Test
  void getAllPayments_shouldThrowExceptionWhenEmpty() {
    when(paymentRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> paymentService.getAllPayments(PageRequest.of(0, 10)));

    assertEquals("No payments found!", exception.getMessage());
  }



  @Test
  void crateNewPayment_shouldThrowIfUserOrEventMissing() {
    UUID userId = UUID.randomUUID();
    UUID eventId = UUID.randomUUID();
    PaymentDto dto = new PaymentDto(userId, eventId, 10);

    when(authClient.getUserById(userId.toString())).thenReturn(null);

    RuntimeException ex = assertThrows(RuntimeException.class,
        () -> paymentService.crateNewPayment(dto));

    assertTrue(ex.getMessage().contains("Error creating payment"));
  }

  @Test
  void getAllUserPayments_shouldReturnMappedUserPayments() {
    UUID userId = UUID.randomUUID();
    Ticket ticket = new Ticket();
    Page<Ticket> tickets = new PageImpl<>(List.of(ticket));
    PaymentModel model = new PaymentModel(null,null,null,null);

    when(paymentRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(tickets);
    when(paymentMapper.mapToModel(ticket)).thenReturn(model);

    Page<PaymentModel> result = paymentService.getAllUserPayments(userId, PageRequest.of(0, 5));

    assertEquals(1, result.getTotalElements());
    verify(paymentMapper).mapToModel(ticket);
  }

  @Test
  void getAllUserPayments_shouldThrowWhenEmpty() {
    UUID userId = UUID.randomUUID();
    when(paymentRepository.findByUserId(eq(userId), any(Pageable.class))).thenReturn(Page.empty());

    assertThrows(RuntimeException.class,
        () -> paymentService.getAllUserPayments(userId, PageRequest.of(0, 5)));
  }

  @Test
  void getANumberOfSoldTicketsForEvent_shouldReturnCount() {
    UUID eventId = UUID.randomUUID();
    Ticket ticket = new Ticket();
    EventSold eventSold = new EventSold(eventId, 10L);

    when(paymentRepository.findByEventId(eventId)).thenReturn(ticket);
    when(paymentRepository.getNumberOfTicketsSoldForEvent(eventId)).thenReturn(eventSold);

    EventSold result = paymentService.getANumberOfSoldTicketsForEvent(eventId);

    assertEquals(10L, result.getNumberOfSoldTickets());
  }

  @Test
  void getANumberOfSoldTicketsForEvent_shouldThrowIfNone() {
    UUID eventId = UUID.randomUUID();
    when(paymentRepository.findByEventId(eventId)).thenReturn(null);

    assertThrows(RuntimeException.class,
        () -> paymentService.getANumberOfSoldTicketsForEvent(eventId));
  }
}

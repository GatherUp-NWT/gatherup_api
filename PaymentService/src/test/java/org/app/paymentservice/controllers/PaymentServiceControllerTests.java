package org.app.paymentservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.app.paymentservice.controller.PaymentController;
import org.app.paymentservice.request.PaymentDto;
import org.app.paymentservice.response.EventSold;
import org.app.paymentservice.response.PaymentModel;
import org.app.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentServiceControllerTests {


  @Autowired
  private MockMvc mockMvc;


  @MockitoBean
  private PaymentService paymentService;

  @Autowired
  private ObjectMapper objectMapper;

  private PaymentModel samplePayment;
  private PaymentDto paymentDto;

  @BeforeEach
  void setUp() {
    samplePayment = new PaymentModel(UUID.randomUUID(), UUID.randomUUID(), LocalDateTime.now(), 100);
    paymentDto = new PaymentDto(samplePayment.userId(), samplePayment.eventId(), samplePayment.price());
  }

  @Test
  void testGetAllPayments() throws Exception {
    Pageable pageable = PageRequest.of(0, 10);
    Mockito.when(paymentService.getAllPayments(any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(samplePayment)));

    mockMvc.perform(get("/api/v1/payments/all?page=0&size=10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].price").value(samplePayment.price()));
  }

  @Test
  void testCreatePayment() throws Exception {
    Mockito.when(paymentService.crateNewPayment(any(PaymentDto.class)))
        .thenReturn(samplePayment);

    mockMvc.perform(post("/api/v1/payments/new")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(paymentDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(samplePayment.userId().toString()))
        .andExpect(jsonPath("$.price").value(samplePayment.price()));
  }

  @Test
  void testGetAllUserPayments() throws Exception {
    UUID userId = UUID.randomUUID();
    Mockito.when(paymentService.getAllUserPayments(eq(userId), any(Pageable.class)))
        .thenReturn(new PageImpl<>(List.of(samplePayment)));

    mockMvc.perform(get("/api/v1/payments/user/all")
            .param("userId", userId.toString())
            .param("page", "0")
            .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].userId").value(samplePayment.userId().toString()));
  }

  @Test
  void testGetNumberOfPayments() throws Exception {
    UUID eventId = UUID.randomUUID();
    EventSold eventSold = new EventSold(eventId, 42L);

    Mockito.when(paymentService.getANumberOfSoldTicketsForEvent(eventId))
        .thenReturn(eventSold);

    mockMvc.perform(get("/api/v1/payments/" + eventId + "/numberOfPayments"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.numberOfSoldTickets").value(42));
  }
}

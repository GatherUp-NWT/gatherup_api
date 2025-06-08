package org.app.paymentservice.controller;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import org.app.paymentservice.repository.PaymentRepository;
import org.app.paymentservice.request.PaymentDto;
import org.app.paymentservice.response.EventSold;
import org.app.paymentservice.response.PaymentModel;
import org.app.paymentservice.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("payments")
public class PaymentController {


  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {

    this.paymentService = paymentService;
  }

  @GetMapping
  public Page<PaymentModel> getPayments(Pageable pageable) {

    return paymentService.getAllPayments(pageable);

  }
  @PostMapping("/new")
  public ResponseEntity<PaymentModel> createPayment( @RequestBody PaymentDto paymentDto) {
   return ResponseEntity.ok(paymentService.crateNewPayment(paymentDto));
  }
  @GetMapping("/user/all")
  public Page<PaymentModel> getAllUserPayments(@RequestParam UUID userId, Pageable pageable) {
    return paymentService.getAllUserPayments(userId, pageable);
  }
  @GetMapping("/{eventId}/numberOfPayments")
  public EventSold getNumberOfPayments(@PathVariable UUID eventId) {
    return paymentService.getANumberOfSoldTicketsForEvent(eventId);
  }

}

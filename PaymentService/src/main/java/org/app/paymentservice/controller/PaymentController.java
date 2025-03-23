package org.app.paymentservice.controller;

import java.util.List;
import java.util.UUID;
import org.app.paymentservice.repository.PaymentRepository;
import org.app.paymentservice.response.PaymentModel;
import org.app.paymentservice.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/api/v1/payments")
public class PaymentController {


  private final PaymentService paymentService;

  public PaymentController(PaymentService paymentService) {

    this.paymentService = paymentService;
  }

  @GetMapping("/all")
  public ResponseEntity<List<PaymentModel>> getPayments() {

    return ResponseEntity.ok(paymentService.getAllPayments());

  }
  @PostMapping("/new")
  public ResponseEntity<PaymentModel> createPayment(@RequestParam UUID userId, @RequestParam UUID eventId) {
   return ResponseEntity.ok(paymentService.crateNewPayment(userId,eventId));
  }
  @GetMapping("/user/all")
  public ResponseEntity<List<PaymentModel>> getAllUserPayments(@RequestParam UUID userId) {
    return ResponseEntity.ok(paymentService.getAllUserPayments(userId));
  }

}

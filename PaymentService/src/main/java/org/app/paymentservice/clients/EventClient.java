package org.app.paymentservice.clients;


import org.app.paymentservice.request.EventDTO;
import org.app.paymentservice.request.EventResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service")
public interface EventClient {
  @GetMapping("/events/{id}")
  EventResponseDTO getEventById(@PathVariable("id") String id);
}

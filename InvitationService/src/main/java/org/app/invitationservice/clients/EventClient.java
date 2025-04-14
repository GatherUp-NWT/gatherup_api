package org.app.invitationservice.clients;

import org.app.invitationservice.response.EventResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service")
public interface EventClient {
  @GetMapping("/events/{id}")
  EventResponseDto getEventById(@PathVariable("id") String id);
}


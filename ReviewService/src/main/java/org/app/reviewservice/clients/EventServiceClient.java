package org.app.reviewservice.clients;

import org.app.reviewservice.dto.EventResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service")
public interface EventServiceClient {
    @GetMapping("/events/{eventId}")
    EventResponseDTO getEventById(@PathVariable String eventId);
}

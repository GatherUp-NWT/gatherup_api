package org.app.eventservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.eventservice.dto.EventDeletionMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventDeletionPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.event-deletion}")
    private String eventDeletionRoutingKey;

    public String publishEventDeletion(UUID eventId) {
        String correlationId = UUID.randomUUID().toString();
        EventDeletionMessage message = new EventDeletionMessage(eventId, correlationId);

        rabbitTemplate.convertAndSend(exchange, eventDeletionRoutingKey, message);

        log.info("Event deletion message sent for eventId: {} with correlationId: {}", eventId, correlationId);

        return correlationId;
    }
}

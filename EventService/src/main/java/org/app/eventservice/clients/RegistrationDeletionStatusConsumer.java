package org.app.eventservice.clients;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.eventservice.dto.RegistrationDeletionStatusMessage;
import org.app.eventservice.dto.RegistrationRestorationMessage;
import org.app.eventservice.service.DeletionStatusService;
import org.app.eventservice.service.EventService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationDeletionStatusConsumer {

    private final EventService eventService;
    private final DeletionStatusService deletionStatusService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.registration-restoration}")
    private String registrationRestorationRoutingKey;


    @RabbitListener(queues = "${rabbitmq.queue.registration-deletion-status.name}")
    public void handleRegistrationDeletionStatus(RegistrationDeletionStatusMessage statusMessage) {
        log.info("Received registration deletion status for eventId: {} with correlationId: {}. Success: {}",
                statusMessage.getEventId(), statusMessage.getCorrelationId(), statusMessage.isSuccess());

        if (statusMessage.isSuccess()) {
            try {
                eventService.finalizeEventDeletion(statusMessage.getEventId());
                deletionStatusService.updateStatus(
                        statusMessage.getCorrelationId(),
                        true,
                        "Successfully deleted event and its registrations for eventId: " + statusMessage.getEventId(),
                        null,
                        null
                );
            } catch (Exception e) {
                log.error("Event deletion failed after registrations were successfully deleted. Initiating compensation.", e);

                RegistrationRestorationMessage restorationMessage = new RegistrationRestorationMessage(
                        statusMessage.getEventId(),
                        statusMessage.getCorrelationId()
                );

                rabbitTemplate.convertAndSend(exchange, registrationRestorationRoutingKey, restorationMessage);

                deletionStatusService.updateStatus(
                        statusMessage.getCorrelationId(),
                        false,
                        "Failed to delete event after registrations were removed. Compensation initiated.",
                        "EVENT_DELETION_FAILED",
                        e.getMessage()
                );
            }
        } else {
            deletionStatusService.updateStatus(
                    statusMessage.getCorrelationId(),
                    false,
                    "Failed to delete registrations for eventId: " + statusMessage.getEventId(),
                    "REGISTRATION_DELETION_FAILED",
                    statusMessage.getErrorMessage()
            );
        }

    }

}

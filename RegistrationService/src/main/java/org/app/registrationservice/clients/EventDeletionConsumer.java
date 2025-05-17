package org.app.registrationservice.clients;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.app.registrationservice.dto.EventDeletionMessage;
import org.app.registrationservice.dto.RegistrationDeletionStatusMessage;
import org.app.registrationservice.dto.RegistrationRestorationMessage;
import org.app.registrationservice.entity.Registration;
import org.app.registrationservice.repository.RegistrationRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventDeletionConsumer {

    private final RegistrationRepository registrationRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, List<Registration>> deletedRegistrationsCache = new ConcurrentHashMap<>();


    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.registration-deletion-status}")
    private String registrationDeletionStatusRoutingKey;

    @RabbitListener(queues = "${rabbitmq.queue.event-deletion.name}")
    @Transactional
    public void handleEventDeletion(EventDeletionMessage message) {
        log.info("Received event deletion message for eventId: {} with correlationId: {}",
                message.getEventId(), message.getCorrelationId());

        RegistrationDeletionStatusMessage statusMessage = new RegistrationDeletionStatusMessage(
                message.getEventId(),
                message.getCorrelationId(),
                false,
                null
        );

        try {
            // Find all registrations for this event
            List<Registration> registrations = registrationRepository.findByEventId(message.getEventId());

            if (!registrations.isEmpty()) {
                log.info("Deleting {} registrations for eventId: {}", registrations.size(), message.getEventId());

                // Store registrations for potential rollback
                List<Registration> registrationDataList = new ArrayList<>();
                for (Registration registration : registrations) {
                    Registration data = new Registration();
                    data.setEventId(registration.getEventId());
                    data.setUserId(registration.getUserId());
                    data.setTimestamp(registration.getTimestamp());

                    registrationDataList.add(data);
                    log.info("Storing registration: {} for eventId: {}", registration.getId(), message.getEventId());
                }

                deletedRegistrationsCache.put(message.getCorrelationId(), registrationDataList);

                // Delete registrations
                registrationRepository.deleteByEventId(message.getEventId());

                // Send success message
                statusMessage.setSuccess(true);
                log.info("Successfully deleted registrations for eventId: {}", message.getEventId());
            } else {
                log.info("No registrations found for eventId: {}", message.getEventId());
                statusMessage.setSuccess(true);
            }
        } catch (Exception e) {
            log.error("Error deleting registrations for eventId: {}: {}", message.getEventId(), e.getMessage(), e);
            statusMessage.setSuccess(false);
            statusMessage.setErrorMessage("Failed to delete registrations: " + e.getMessage());
        } finally {
            // Send status back to Event Service
            rabbitTemplate.convertAndSend(exchange, registrationDeletionStatusRoutingKey, statusMessage);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.registration-restoration.name}")
    @Transactional
    public void handleRegistrationRestoration(RegistrationRestorationMessage message) {
        log.info("Received registration restoration message for eventId: {} with correlationId: {}",
                message.getEventId(), message.getCorrelationId());

        try {
            List<Registration> deletedRegistrations = deletedRegistrationsCache.get(message.getCorrelationId());
            for (Registration registration : deletedRegistrations) {
                log.info("Restoring registration: {} for eventId: {}",
                        registration.getId(), message.getEventId());
            }

            if (!deletedRegistrations.isEmpty()) {
                log.info("Restoring {} registrations for eventId: {}",
                        deletedRegistrations.size(), message.getEventId());

                registrationRepository.saveAll(deletedRegistrations);

                deletedRegistrationsCache.remove(message.getCorrelationId());

                log.info("Successfully restored registrations for eventId: {}", message.getEventId());
            } else {
                log.warn("No cached registrations found for eventId: {} with correlationId: {}",
                        message.getEventId(), message.getCorrelationId());
            }
        } catch (Exception e) {
            log.error("Error restoring registrations for eventId: {}: {}",
                    message.getEventId(), e.getMessage(), e);
        }
    }

}

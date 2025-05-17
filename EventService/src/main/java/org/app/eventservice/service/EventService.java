package org.app.eventservice.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.app.eventservice.dto.*;
import org.app.eventservice.entity.Event;
import org.app.eventservice.mappers.EventMapper;
import org.app.eventservice.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final EventDeletionPublisher eventDeletionPublisher;


    public EventService(EventMapper eventMapper, EventRepository eventRepository, EventDeletionPublisher eventDeletionPublisher) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
        this.eventDeletionPublisher = eventDeletionPublisher;
    }

    // Get all events
    public EventListResponseDTO getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return eventMapper.toResponseDto(events, true, "Events retrieved successfully");

    }


    // Get all events by user id
    public EventListResponseDTO getAllEventsByUserId(String userId) {
        UUID userUUID = UUID.fromString(userId);
        return eventMapper.toResponseDto(eventRepository.findAllByCreatorUUID(userUUID), true, "Events retrieved successfully");
    }

    // Get all events by category name
    public EventListResponseDTO getAllEventsByCategoryName(String categoryName) {
        return eventMapper.toResponseDto(eventRepository.findAllByEventCategory_Name(categoryName), true, "Events retrieved successfully");
    }

    // Get event by event id
    public EventObjectResponseDTO getEventById(String eventId) {
        UUID eventUUID = UUID.fromString(eventId);
        Event event = eventRepository.findById(eventUUID).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        return eventMapper.toObjectResponse(event, true, "Event retrieved successfully");
    }


    // Create event
    public EventResponseDTO createEvent(EventDTO event) {

        // Validate event data
        if (event.getStartDate().isAfter(event.getEndDate())) {
            throw new IllegalArgumentException("Event start date cannot be after end date");
        }

        Event createdEvent = eventMapper.toEntity(event);
        Event savedEvent = eventRepository.save(createdEvent);

        return eventMapper.toResponseDto(savedEvent, true, "Event created successfully");

    }

    public EventResponseDTO updateEvent(EventUpdateDTO event) {
        if (event.getStartDate().isAfter(event.getEndDate())) {
            throw new IllegalArgumentException("Event start date cannot be after end date");
        }

        Event existingEvent = eventRepository.findById(event.getUuid())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Event updatedEvent = eventMapper.updateEventFromUpdateDTO(event, existingEvent);
        Event savedEvent = eventRepository.save(updatedEvent);

        return eventMapper.toResponseDto(savedEvent, true, "Event updated successfully");
    }

    @Transactional
    public EventResponseDTO deleteEvent(String eventId) {
        UUID eventUUID = UUID.fromString(eventId);
        Event event = eventRepository.findById(eventUUID).orElseThrow(() -> new IllegalArgumentException("Event not found"));
        eventRepository.delete(event);
        return eventMapper.toResponseDto(event, true, "Event deleted successfully");
    }


    public EventListPagedDTO getAllEventsPaginated(int page, int size, String sortBy, String sortOrder) {

        Sort sort = sortOrder.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Event> pageEvents = eventRepository.findAll(pageable);

        List<Event> events = pageEvents.getContent();

        return eventMapper.toPagedResponseDto(
                events,
                pageEvents.getTotalElements(),
                pageEvents.getTotalPages(),
                pageEvents.getNumber(),
                pageEvents.getSize(),
                true,
                "Events retrieved successfully"
        );
    }

    public EventListResponseDTO getNearbyEvents(double latitude, double longitude, double radius, int limit) {
        List<Event> events = eventRepository.findNearbyEvents(latitude, longitude, radius, limit);
        return eventMapper.toResponseDto(events, true, "Nearby events retrieved successfully");
    }

    public String requestEventDeletion(String eventId) {
        UUID eventUUID = UUID.fromString(eventId);
        log.info("Initiating deletion process for eventId: {}", eventId);

        String correlationId = eventDeletionPublisher.publishEventDeletion(eventUUID);

        log.info("Event deletion request published for eventId: {}. Waiting for registration deletion confirmation.", eventId);

        return correlationId;
    }

    @Transactional
    public void finalizeEventDeletion(UUID eventId) {
        log.info("Finalizing deletion of event: {}", eventId);
        try {
            //throw new RuntimeException("Simulated error during event deletion");
            eventRepository.deleteById(eventId); // Actual deletion from the database
            log.info("Event {} successfully deleted from database.", eventId);
        } catch (Exception e) {
            log.error("Failed to finalize event deletion for eventId: {}. This might require manual intervention.", eventId, e);
            throw new RuntimeException("Failed to finalize event deletion", e);
        }
    }




}

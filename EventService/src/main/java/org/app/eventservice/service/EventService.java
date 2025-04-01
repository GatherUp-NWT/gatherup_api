package org.app.eventservice.service;

import org.app.eventservice.appConfig.StatisticsService;
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
public class EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final StatisticsService statisticsService;


    public EventService(EventMapper eventMapper, EventRepository eventRepository, StatisticsService statisticsService) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
        this.statisticsService = statisticsService;
    }

    // Get all events
    public EventListResponseDTO getAllEvents() {
        statisticsService.clearStatistics();

        List<Event> events = eventRepository.findAll();

        statisticsService.logStatistics();

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
}

package org.app.eventservice.service;

import org.app.eventservice.dto.*;
import org.app.eventservice.entity.Event;
import org.app.eventservice.mappers.EventMapper;
import org.app.eventservice.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;


    public EventService(EventMapper eventMapper, EventRepository eventRepository) {
        this.eventMapper = eventMapper;
        this.eventRepository = eventRepository;
    }
    // Get all events
    public EventListResponseDTO getAllEvents() {
        return eventMapper.toResponseDto(eventRepository.findAll(), true, "Events retrieved successfully");
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
        } else if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new IllegalArgumentException("Event end date cannot be before start date");
        }

        Event createdEvent = eventMapper.toEntity(event);
        Event savedEvent = eventRepository.save(createdEvent);

        return eventMapper.toResponseDto(savedEvent, true, "Event created successfully");

    }

    public EventResponseDTO updateEvent(EventUpdateDTO event) {
        if (event.getStartDate().isAfter(event.getEndDate())) {
            throw new IllegalArgumentException("Event start date cannot be after end date");
        } else if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new IllegalArgumentException("Event end date cannot be before start date");
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



}

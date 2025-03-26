package org.app.eventservice.controllers;

import jakarta.validation.Valid;
import org.app.eventservice.dto.*;
import org.app.eventservice.service.EventService;
import org.springframework.web.bind.annotation.*;

@RestController
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Get all events - mainly for admin
    @GetMapping("/events")
    public EventListResponseDTO getAllEvents() {
        return eventService.getAllEvents();
    }

    // Get all events by user id
    @GetMapping("/events/user/{userId}")
    public EventListResponseDTO getAllEventsByUserId(@PathVariable String userId) {
        return eventService.getAllEventsByUserId(userId);
    }

    // Get all events by category name
    @GetMapping("/events/category/{categoryName}")
    public EventListResponseDTO getAllEventsByCategoryName(@PathVariable String categoryName) {
        return eventService.getAllEventsByCategoryName(categoryName);
    }

    // Get event by event id
    @GetMapping("/events/{eventId}")
    public EventObjectResponseDTO getEventById(@PathVariable String eventId) {
        return eventService.getEventById(eventId);
    }

    // Get upcoming events


    // Get all events by status

    // Create event
    @PostMapping("/events/create")
    public EventResponseDTO createEvent(@Valid @RequestBody EventDTO event) {
        return eventService.createEvent(event);
    }

    // Update event

    @PutMapping("/events/update")
    public EventResponseDTO updateEvent(@Valid @RequestBody EventUpdateDTO event) {
        return eventService.updateEvent(event);
    }

    // Delete event
    @DeleteMapping("/events/delete/{eventId}")
    public EventResponseDTO deleteEvent(@PathVariable String eventId) {
        return eventService.deleteEvent(eventId);
    }



}

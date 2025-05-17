package org.app.eventservice.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.app.eventservice.dto.*;
import org.app.eventservice.entity.DeletionStatus;
import org.app.eventservice.service.DeletionStatusService;
import org.app.eventservice.service.EventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("events")
@Slf4j
public class EventController {
    private final EventService eventService;
    private final DeletionStatusService deletionStatusService;

    public EventController(EventService eventService, DeletionStatusService deletionStatusService) {
        this.eventService = eventService;
        this.deletionStatusService = deletionStatusService;
    }

    @Value("${server.port}")
    private String port;

    // Get all events - mainly for admin
    @GetMapping
    public EventListResponseDTO getAllEvents() {
        return eventService.getAllEvents();
    }

    // Get all events (sortable and paginated)
    @GetMapping("/all")
    public EventListPagedDTO getAllEventsPaginated(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestParam(defaultValue = "startDate") String sortBy,
                                                      @RequestParam(defaultValue = "asc") String sortOrder) {
        return eventService.getAllEventsPaginated(page, size, sortBy, sortOrder);
    }

    // Get all events nearby
    @GetMapping("/nearby")
    public EventListResponseDTO getNearbyEvents(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10.0") double radius,
            @RequestParam(defaultValue = "20") int limit) {
        return eventService.getNearbyEvents(latitude, longitude, radius, limit);
    }

    // Get all events by user id
    @GetMapping("/user/{userId}")
    public EventListResponseDTO getAllEventsByUserId(@PathVariable String userId) {
        return eventService.getAllEventsByUserId(userId);
    }

    // Get all events by category name
    @GetMapping("/category/{categoryName}")
    public EventListResponseDTO getAllEventsByCategoryName(@PathVariable String categoryName) {
        return eventService.getAllEventsByCategoryName(categoryName);
    }

    // Get event by event id
    @GetMapping("/{eventId}")
    public EventObjectResponseDTO getEventById(@PathVariable String eventId) {
        String formattedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));

        System.out.println("PORT: " + port + " | Time: " + formattedTime);

        return eventService.getEventById(eventId);
    }

    // Get upcoming events


    // Get all events by status

    // Create event
    @PostMapping
    public EventResponseDTO createEvent(@Valid @RequestBody EventDTO event) {
        return eventService.createEvent(event);
    }

    // Update event

    @PutMapping
    public EventResponseDTO updateEvent(@Valid @RequestBody EventUpdateDTO event) {
        return eventService.updateEvent(event);
    }

    // Delete event
    @DeleteMapping("/{eventId}")
    public ResponseEntity<DeletionRequestResponseDTO> deleteEvent(@PathVariable String eventId) {
        try {
            String correlationId = eventService.requestEventDeletion(eventId);
            UUID eventUUID = UUID.fromString(eventId);
            log.info("Event deletion requested for eventId: {} with correlationId: {}", eventId, correlationId);
            deletionStatusService.initializeDeletion(correlationId, eventUUID);

            DeletionRequestResponseDTO response = DeletionRequestResponseDTO.builder()
                    .status("PENDING")
                    .correlationId(correlationId)
                    .timestamp(LocalDateTime.now())
                    .message("Event deletion process initiated")
                    .statusEndpoint("/events/deletion-status/" + correlationId)
                    .build();

            return ResponseEntity.accepted().body(response);
        } catch (Exception e) {
            log.error("Failed to initiate event deletion: {}", e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to initiate event deletion: " + e.getMessage()
            );
        }
    }

    @GetMapping("/deletion-status/{correlationId}")
    public ResponseEntity<EventDeletionStatusDTO> getDeletionStatus(
            @PathVariable String correlationId) {

        DeletionStatus status =
                deletionStatusService.getStatus(correlationId);

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Deletion status not found for correlationId: " + correlationId
            );
        }

        EventDeletionStatusDTO.ErrorDetails errorDetails = null;
        if (status.getErrorCode() != null) {
            errorDetails = EventDeletionStatusDTO.ErrorDetails.builder()
                    .code(status.getErrorCode())
                    .message(status.getErrorMessage())
                    .build();
        }

        EventDeletionStatusDTO response = EventDeletionStatusDTO.builder()
                .status(status.getStatus())
                .correlationId(correlationId)
                .timestamp(status.getTimestamp())
                .message(status.getMessage())
                .error(errorDetails)
                .build();

        return status.getStatus().equals("PENDING")
                ? ResponseEntity.accepted().body(response)
                : ResponseEntity.ok(response);
    }



}

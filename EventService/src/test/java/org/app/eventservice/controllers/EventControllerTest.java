package org.app.eventservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.eventservice.dto.*;
import org.app.eventservice.entity.Event;
import org.app.eventservice.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({EventController.class, org.app.eventservice.exception.GlobalExceptionHandler.class})
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID eventId;
    private EventDTO eventDTO;
    private EventUpdateDTO eventUpdateDTO;
    private EventResponseDTO eventResponseDTO;
    private EventListResponseDTO eventListResponseDTO;
    private EventObjectResponseDTO eventObjectResponseDTO;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        UUID creatorId = UUID.randomUUID();

        // Setup EventDTO
        eventDTO = new EventDTO();
        eventDTO.setName("Test Event");
        eventDTO.setDescription("Test Description");
        eventDTO.setCreatorUUID(creatorId);
        eventDTO.setRegistrationEndDate(Instant.now().plusSeconds(86400));
        eventDTO.setStartDate(Instant.now().plusSeconds(172800));
        eventDTO.setEndDate(Instant.now().plusSeconds(259200));
        eventDTO.setCapacity(100);
        eventDTO.setPrice(50.0);
        eventDTO.setCategory("Technology");

        // Setup EventUpdateDTO
        eventUpdateDTO = new EventUpdateDTO();
        eventUpdateDTO.setUuid(eventId);
        eventUpdateDTO.setName("Updated Event");
        eventUpdateDTO.setStartDate(Instant.now().plusSeconds(172800));
        eventUpdateDTO.setEndDate(Instant.now().plusSeconds(259200));

        // Setup EventResponseDTO
        eventResponseDTO = new EventResponseDTO();
        eventResponseDTO.setEventUUID(eventId);
        eventResponseDTO.setStatus(true);
        eventResponseDTO.setMessage("Success");

        // Setup EventListResponseDTO
        eventListResponseDTO = new EventListResponseDTO();
        eventListResponseDTO.setEvents(new ArrayList<>());
        eventListResponseDTO.setStatus(true);
        eventListResponseDTO.setMessage("Success");

        // Setup EventObjectResponseDTO
        eventObjectResponseDTO = new EventObjectResponseDTO();
        eventObjectResponseDTO.setEvent(new Event());
        eventObjectResponseDTO.setStatus(true);
        eventObjectResponseDTO.setMessage("Success");
    }

    @Test
    void getAllEvents_ShouldReturnAllEvents() throws Exception {
        when(eventService.getAllEvents()).thenReturn(eventListResponseDTO);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.events").isArray());

        verify(eventService).getAllEvents();
    }

    @Test
    void getAllEventsByUserId_ShouldReturnUserEvents() throws Exception {
        String userId = UUID.randomUUID().toString();
        when(eventService.getAllEventsByUserId(userId)).thenReturn(eventListResponseDTO);

        mockMvc.perform(get("/events/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(eventService).getAllEventsByUserId(userId);
    }

    @Test
    void getAllEventsByUserId_WithInvalidUUID_ShouldReturnError() throws Exception {
        String invalidUserId = "invalid-uuid";
        when(eventService.getAllEventsByUserId(invalidUserId))
                .thenThrow(new IllegalArgumentException("Invalid UUID format"));

        mockMvc.perform(get("/events/user/{userId}", invalidUserId))
                .andExpect(status().isBadRequest());

        verify(eventService).getAllEventsByUserId(invalidUserId);
    }

    @Test
    void getAllEventsByCategoryName_ShouldReturnCategoryEvents() throws Exception {
        String categoryName = "Technology";
        when(eventService.getAllEventsByCategoryName(categoryName)).thenReturn(eventListResponseDTO);

        mockMvc.perform(get("/events/category/{categoryName}", categoryName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(eventService).getAllEventsByCategoryName(categoryName);
    }

    @Test
    void getEventById_ShouldReturnEvent() throws Exception {
        when(eventService.getEventById(eventId.toString())).thenReturn(eventObjectResponseDTO);

        mockMvc.perform(get("/events/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Success"));

        verify(eventService).getEventById(eventId.toString());
    }

    @Test
    void getEventById_WithInvalidUUID_ShouldReturnError() throws Exception {
        String invalidEventId = "invalid-uuid";
        when(eventService.getEventById(invalidEventId))
                .thenThrow(new IllegalArgumentException("Invalid UUID format"));

        mockMvc.perform(get("/events/{eventId}", invalidEventId))
                .andExpect(status().isBadRequest());

        verify(eventService).getEventById(invalidEventId);
    }

    @Test
    void createEvent_ShouldCreateAndReturnEvent() throws Exception {
        when(eventService.createEvent(any(EventDTO.class))).thenReturn(eventResponseDTO);

        mockMvc.perform(post("/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.eventUUID").value(eventId.toString()));

        verify(eventService).createEvent(any(EventDTO.class));
    }

    @Test
    void createEvent_WithInvalidData_ShouldReturnError() throws Exception {
        EventDTO invalidEventDTO = new EventDTO();

        when(eventService.createEvent(any(EventDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid event data"));

        mockMvc.perform(post("/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEventDTO)))
                .andDo(result -> {
                    System.out.println("Response status: " + result.getResponse().getStatus());
                    System.out.println("Response body: " + result.getResponse().getContentAsString());
                    System.out.println("Handler used: " + result.getHandler());
                })
                .andExpect(status().isBadRequest());

        verify(eventService).createEvent(any(EventDTO.class));
    }

    @Test
    void updateEvent_ShouldUpdateAndReturnEvent() throws Exception {
        when(eventService.updateEvent(any(EventUpdateDTO.class))).thenReturn(eventResponseDTO);

        mockMvc.perform(put("/events/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.eventUUID").value(eventId.toString()));

        verify(eventService).updateEvent(any(EventUpdateDTO.class));
    }

    @Test
    void updateEvent_WithInvalidData_ShouldReturnError() throws Exception {
        when(eventService.updateEvent(any(EventUpdateDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid event data"));

        mockMvc.perform(put("/events/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventUpdateDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService).updateEvent(any(EventUpdateDTO.class));
    }

    @Test
    void deleteEvent_ShouldDeleteAndReturnEvent() throws Exception {
        when(eventService.deleteEvent(eventId.toString())).thenReturn(eventResponseDTO);

        mockMvc.perform(delete("/events/delete/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(true))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.eventUUID").value(eventId.toString()));

        verify(eventService).deleteEvent(eventId.toString());
    }

    @Test
    void deleteEvent_WithInvalidUUID_ShouldReturnError() throws Exception {
        String invalidEventId = "invalid-uuid";
        when(eventService.deleteEvent(invalidEventId))
                .thenThrow(new IllegalArgumentException("Invalid UUID format"));

        mockMvc.perform(delete("/events/delete/{eventId}", invalidEventId))
                .andExpect(status().isBadRequest());

        verify(eventService).deleteEvent(invalidEventId);
    }
}
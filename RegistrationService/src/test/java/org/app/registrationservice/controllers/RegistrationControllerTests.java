package org.app.registrationservice.controllers;

import org.app.registrationservice.controller.RegistrationController;
import org.app.registrationservice.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.registrationservice.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({RegistrationController.class, org.app.registrationservice.exception.GlobalExceptionHandler.class})
public class RegistrationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegistrationDTO registrationDTO;
    private List<RegistrationDTO> registrationList;
    private Long registrationId;
    private UUID userId;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        registrationId = 1L;
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        registrationDTO = new RegistrationDTO();
        registrationDTO.setId(registrationId);
        registrationDTO.setUserId(userId);
        registrationDTO.setEventId(eventId);

        registrationList = new ArrayList<>();
        registrationList.add(registrationDTO);
    }

    @Test
    void getAllRegistrations_ShouldReturnAllRegistrations() throws Exception {
        when(registrationService.getAllRegistrations()).thenReturn(registrationList);

        mockMvc.perform(get("/api/v1/registrations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(registrationId));

        verify(registrationService).getAllRegistrations();
    }


    @Test
    void getRegistrationById_ShouldReturnRegistration() throws Exception {
        when(registrationService.getRegistrationById(registrationId)).thenReturn(registrationDTO);

        mockMvc.perform(get("/api/v1/registrations/{id}", registrationId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(registrationId));

        verify(registrationService).getRegistrationById(registrationId);
    }


    @Test
    void createRegistration_ShouldReturnCreatedRegistration() throws Exception {
        when(registrationService.saveRegistration(any(RegistrationDTO.class))).thenReturn(registrationDTO);

        mockMvc.perform(post("/api/v1/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(registrationId));

        verify(registrationService).saveRegistration(any(RegistrationDTO.class));
    }

    @Test
    void deleteRegistration_ShouldReturnNoContent() throws Exception {
        doNothing().when(registrationService).deleteRegistration(registrationId);

        mockMvc.perform(delete("/api/v1/registrations/{id}", registrationId))
                .andExpect(status().isNoContent());

        verify(registrationService).deleteRegistration(registrationId);
    }

    @Test
    void getRegistrationsByUser_ShouldReturnUserRegistrations() throws Exception {
        when(registrationService.getRegistrationsByUser(userId)).thenReturn(registrationList);

        mockMvc.perform(get("/api/v1/registrations/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));

        verify(registrationService).getRegistrationsByUser(userId);
    }

    @Test
    void getRegistrationsByEvent_ShouldReturnEventRegistrations() throws Exception {
        when(registrationService.getRegistrationsByEvent(eventId)).thenReturn(registrationList);

        mockMvc.perform(get("/api/v1/registrations/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].eventId").value(eventId.toString()));

        verify(registrationService).getRegistrationsByEvent(eventId);
    }
}
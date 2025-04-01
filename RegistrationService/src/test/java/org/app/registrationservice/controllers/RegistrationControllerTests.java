package org.app.registrationservice.controllers;

import org.app.registrationservice.controller.RegistrationController;
import org.app.registrationservice.service.RegistrationService;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.registrationservice.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void getRegistrationById_ShouldReturnNotFound() throws Exception {
        when(registrationService.getRegistrationById(registrationId)).thenThrow(new NoSuchElementException("Registration not found"));

        mockMvc.perform(get("/api/v1/registrations/{id}", registrationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Registration not found"));

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
    void createRegistration_ShouldReturnBadRequest() throws Exception {
        registrationDTO.setUserId(null); // Set userId to null to trigger validation error

        mockMvc.perform(post("/api/v1/registrations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registrationDTO)))
                .andExpect(status().isBadRequest())  // Expect a 400 Bad Request status
                .andExpect(jsonPath("$.message").value("User ID cannot be null"));

        verify(registrationService, never()).saveRegistration(any());  // Ensure service method is never called
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

    @Test
    void deleteRegistration_ShouldReturnNotFound() throws Exception {
        doThrow(new NoSuchElementException("Registration not found")).when(registrationService).deleteRegistration(registrationId);

        mockMvc.perform(delete("/api/v1/registrations/{id}", registrationId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Registration not found"));

        verify(registrationService).deleteRegistration(registrationId);
    }

    // Test for GET /user/{userId}
    @Test
    void getRegistrationsByUser_ShouldReturnRegistrations() throws Exception {
        when(registrationService.getRegistrationsByUser(userId)).thenReturn(registrationList);

        mockMvc.perform(get("/api/v1/registrations/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));

        verify(registrationService).getRegistrationsByUser(userId);
    }

    @Test
    void getRegistrationsByEvent_ShouldReturnRegistrations() throws Exception {
        when(registrationService.getRegistrationsByEvent(eventId)).thenReturn(registrationList);

        mockMvc.perform(get("/api/v1/registrations/event/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].eventId").value(eventId.toString()));

        verify(registrationService).getRegistrationsByEvent(eventId);
    }

    // Test for PATCH /{id}
    @Test
    void patchRegistration_ShouldReturnUpdatedRegistration() throws Exception {
        // Create a registrationDTO that matches the update
        RegistrationDTO updatedRegistration = new RegistrationDTO();
        updatedRegistration.setId(registrationId);
        updatedRegistration.setUserId(UUID.randomUUID());
        updatedRegistration.setEventId(UUID.randomUUID());

        // Mock the service to return the updated registration
        when(registrationService.patchRegistration(eq(registrationId), any(RegistrationDTO.class)))
                .thenReturn(updatedRegistration);

        // Perform the PATCH request with the correct registrationDTO
        mockMvc.perform(patch("/api/v1/registrations/{id}", registrationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedRegistration))) // Use the same updatedRegistration object here
                .andExpect(status().isOk());

        // Verify the service method was called with the correct arguments
        ArgumentCaptor<RegistrationDTO> registrationCaptor = ArgumentCaptor.forClass(RegistrationDTO.class);
        verify(registrationService).patchRegistration(eq(registrationId), registrationCaptor.capture());

        // Ensure that the RegistrationDTO passed to the service is the same as what was mocked
        RegistrationDTO capturedRegistration = registrationCaptor.getValue();
        assertEquals(updatedRegistration.getUserId(), capturedRegistration.getUserId());
        assertEquals(updatedRegistration.getEventId(), capturedRegistration.getEventId());
    }

    @Test
    void patchRegistration_ShouldReturnNotFound() throws Exception {
        // ID for a registration that doesn't exist
        Long nonExistentRegistrationId = 999L;

        // Create an updated registration (but it won't be found)
        RegistrationDTO updatedRegistration = new RegistrationDTO();
        updatedRegistration.setId(nonExistentRegistrationId);
        updatedRegistration.setUserId(UUID.randomUUID());
        updatedRegistration.setEventId(UUID.randomUUID());

        // Mock the service to throw NoSuchElementException when the registration is not found
        when(registrationService.patchRegistration(eq(nonExistentRegistrationId), any(RegistrationDTO.class)))
                .thenThrow(new NoSuchElementException("Registration not found"));

        // Perform the PATCH request with the non-existent registration ID
        mockMvc.perform(patch("/api/v1/registrations/{id}", nonExistentRegistrationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedRegistration)))
                .andExpect(status().isNotFound());  // Expect 404 Not Found status

        // Verify that the service method was called with the correct arguments
        ArgumentCaptor<RegistrationDTO> registrationCaptor = ArgumentCaptor.forClass(RegistrationDTO.class);
        verify(registrationService).patchRegistration(eq(nonExistentRegistrationId), registrationCaptor.capture());
    }
}
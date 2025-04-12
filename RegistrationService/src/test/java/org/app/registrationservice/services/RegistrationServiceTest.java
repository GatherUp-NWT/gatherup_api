package org.app.registrationservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.app.registrationservice.dto.RegistrationDTO;
import org.app.registrationservice.entity.Registration;
import org.app.registrationservice.mapper.RegistrationMapper;
import org.app.registrationservice.repository.RegistrationRepository;
import org.app.registrationservice.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private RegistrationMapper registrationMapper;

    private Registration registration;
    private RegistrationDTO registrationDTO;
    private Long registrationId;

    @BeforeEach
    void setUp() {
        registrationId = 1L;
        UUID userId = UUID.fromString("aa49e083-777a-4627-8c87-8427b98e5d60");
        UUID eventId = UUID.fromString("bb44e083-666a-2327-8c87-2227b98e5d60");

        registration = new Registration();
        registration.setId(registrationId);
        registration.setUserId(userId);
        registration.setEventId(eventId);

        registrationDTO = new RegistrationDTO();
        registrationDTO.setId(registrationId);
        registrationDTO.setUserId(userId);
        registrationDTO.setEventId(eventId);

        // Mock the mapping
        when(registrationMapper.toDto(any(Registration.class))).thenReturn(registrationDTO);
    }

    @Test
    void getRegistrationById_ShouldReturnRegistration() {
        when(registrationRepository.findById(registrationId)).thenReturn(Optional.of(registration));

        RegistrationDTO foundRegistration = registrationService.getRegistrationById(registrationId);

        assertNotNull(foundRegistration);
        assertEquals(registrationDTO.getUserId(), foundRegistration.getUserId());
    }
}
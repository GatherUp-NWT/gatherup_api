package org.app.registrationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.app.registrationservice.dto.RegistrationDTO;
import org.app.registrationservice.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/registrations")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @GetMapping
    public List<RegistrationDTO> getAllRegistrations() {
        return registrationService.getAllRegistrations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationDTO> getRegistrationById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.getRegistrationById(id));
    }

    @PostMapping
    public ResponseEntity<RegistrationDTO> createRegistration(@Valid @RequestBody RegistrationDTO dto) {
        return ResponseEntity.ok(registrationService.saveRegistration(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable Long id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public List<RegistrationDTO> getRegistrationsByUser(@PathVariable UUID userId) {
        return registrationService.getRegistrationsByUser(userId);
    }

    @GetMapping("/event/{eventId}")
    public List<RegistrationDTO> getRegistrationsByEvent(@PathVariable UUID eventId) {
        return registrationService.getRegistrationsByEvent(eventId);
    }
}

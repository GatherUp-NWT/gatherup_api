package org.app.registrationservice.service;

import lombok.RequiredArgsConstructor;
import org.app.registrationservice.dto.RegistrationDTO;
import org.app.registrationservice.entity.Registration;
import org.app.registrationservice.mapper.RegistrationMapper;
import org.app.registrationservice.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {
  private final RegistrationRepository registrationRepository;
  private final RegistrationMapper registrationMapper;

  public List<RegistrationDTO> getAllRegistrations() {
    return registrationRepository.findAll()
            .stream()
            .map(registrationMapper::toDto)
            .collect(Collectors.toList());
  }

  public RegistrationDTO getRegistrationById(Long id) {
    return registrationRepository.findById(id)
            .map(registrationMapper::toDto)
            .orElseThrow(() -> new RuntimeException("Registration not found"));
  }

  public RegistrationDTO createRegistration(RegistrationDTO dto) {
    Registration registration = registrationMapper.toEntity(dto);
    return registrationMapper.toDto(registrationRepository.save(registration));
  }

  public void deleteRegistration(Long id) {
    registrationRepository.deleteById(id);
  }

  public List<RegistrationDTO> getRegistrationsByUser(UUID userId) {
    return registrationRepository.findByUserId(userId)
            .stream()
            .map(registrationMapper::toDto)
            .collect(Collectors.toList());
  }

  public List<RegistrationDTO> getRegistrationsByEvent(UUID eventId) {
    return registrationRepository.findByEventId(eventId)
            .stream()
            .map(registrationMapper::toDto)
            .collect(Collectors.toList());
  }
}


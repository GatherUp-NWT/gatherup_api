package org.app.registrationservice.service;

import jakarta.validation.Valid;
import org.app.registrationservice.dto.RegistrationDTO;
import org.app.registrationservice.entity.Registration;
import org.app.registrationservice.exception.ConflictException;
import org.app.registrationservice.mapper.RegistrationMapper;
import org.app.registrationservice.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RegistrationService {
  private final RegistrationRepository registrationRepository;
  private final RegistrationMapper registrationMapper;

    public RegistrationService(RegistrationRepository registrationRepository, RegistrationMapper registrationMapper) {
        this.registrationRepository = registrationRepository;
        this.registrationMapper = registrationMapper;
    }

    public List<RegistrationDTO> getAllRegistrations() {
    return registrationRepository.findAll()
            .stream()
            .map(registrationMapper::toDto)
            .toList();
  }

  public RegistrationDTO getRegistrationById(Long id) {
    return registrationRepository.findById(id)
            .map(registrationMapper::toDto)
            .orElseThrow(() -> new NoSuchElementException("Registration not found"));
  }

  public RegistrationDTO saveRegistration(@Valid RegistrationDTO registrationDTO) {
    boolean exists = registrationRepository.existsByUserIdAndEventId(registrationDTO.getUserId(), registrationDTO.getEventId());

    if (exists) {
      throw new ConflictException("User has already created a registration for this event.");
    }
    Registration registration = registrationMapper.toEntity(registrationDTO);
    registration.setTimestamp(Timestamp.valueOf(LocalDateTime.now())); // Set current timestamp
    return registrationMapper.toDto(registrationRepository.save(registration));
  }

  public void deleteRegistration(Long id) {
    if (!registrationRepository.existsById(id)) {
      throw new NoSuchElementException("Registration with ID " + id + " does not exist");
    }
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

  public RegistrationDTO patchRegistration(Long id, RegistrationDTO registrationDTO) {
    Registration existingRegistration = registrationRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Registration not found"));

    if (registrationDTO.getEventId() != null) {
      existingRegistration.setEventId(registrationDTO.getEventId());
    }
    if (registrationDTO.getUserId() != null) {
      existingRegistration.setUserId(registrationDTO.getUserId());
    }

    Registration updatedRegistration = registrationRepository.save(existingRegistration);
    return registrationMapper.toDto(updatedRegistration);
  }
}


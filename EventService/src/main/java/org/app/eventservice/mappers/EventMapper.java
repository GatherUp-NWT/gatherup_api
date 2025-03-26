package org.app.eventservice.mappers;

import org.app.eventservice.dto.*;
import org.app.eventservice.entity.*;
import org.app.eventservice.repository.EventCategoryRepository;
import org.app.eventservice.repository.EventStatusRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

@Service
public class EventMapper {

    private final ModelMapper modelMapper;
    private final EventStatusRepository eventStatusRepository;
    private final EventCategoryRepository eventCategoryRepository;

    public EventMapper(ModelMapper modelMapper, EventStatusRepository eventStatusRepository, EventCategoryRepository eventCategoryRepository) {
        this.modelMapper = modelMapper;
        this.eventStatusRepository = eventStatusRepository;
        this.eventCategoryRepository = eventCategoryRepository;

        modelMapper.createTypeMap(EventDTO.class, Event.class).addMappings(mapper -> {
            mapper.skip(Event::setEventCategory);
            mapper.skip(Event::setAgendas);
            //mapper.skip(Event::setEventBanner);
            mapper.skip(Event::setStatus);
        }).setPostConverter(context -> {
            EventDTO source = context.getSource();
            Event destination = context.getDestination();

            if (destination.getUuid() == null) {
                destination.setUuid(source.getUuid() != null ? source.getUuid() : null);
            }

            if (destination.getCreationDate() == null) {
                destination.setCreationDate(Instant.now());
            }

            EventStatus status = eventStatusRepository.findByName("OpenToRegistration");
            destination.setStatus(status);

            EventCategory category;
            if (source.getCategory() != null && !source.getCategory().isEmpty()) {
                category = eventCategoryRepository.findByName(source.getCategory());
            } else {
                category = eventCategoryRepository.findByName("General");
            }
            destination.setEventCategory(category);
/*
            if (source.getEventBanner() != null && !source.getEventBanner().isEmpty()) {
                try {
                    // Clean up the Base64 string before decoding
                    String cleanBase64 = source.getEventBanner().trim();
                    // Handle potential padding issues with Base64
                    if (cleanBase64.contains(",")) {
                        cleanBase64 = cleanBase64.split(",")[1];
                    }
                    destination.setEventBanner(Base64.getDecoder().decode(cleanBase64));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid Base64 string for event banner", e);
                }
            }
*/
            if (source.getAgendas() != null) {
                if (destination.getAgendas() == null) {
                    destination.setAgendas(new HashSet<>());
                }

                source.getAgendas().forEach(agendaDTO -> {
                    Agenda agenda = modelMapper.map(agendaDTO, Agenda.class);

                    if (agendaDTO.getLocation() != null) {
                        Location location = modelMapper.map(agendaDTO.getLocation(), Location.class);
                        agenda.setLocation(location);
                    }

                    destination.addAgenda(agenda);
                });
            }

            return destination;
        });

        modelMapper.createTypeMap(Event.class, EventDTO.class).addMappings(mapper -> {
            mapper.skip(EventDTO::setStatus);
            mapper.skip(EventDTO::setCategory);
            mapper.skip(EventDTO::setAgendas);
            //mapper.skip(EventDTO::setEventBanner);
        }).setPostConverter(context -> {
            Event source = context.getSource();
            EventDTO destination = context.getDestination();

            if (source.getStatus() != null) {
                destination.setStatus(source.getStatus().getName());
            }

            if (source.getEventCategory() != null) {
                destination.setCategory(source.getEventCategory().getName());
            }
/*
            if (source.getEventBanner() != null && source.getEventBanner().length > 0) {
                destination.setEventBanner(Base64.getEncoder().encodeToString(source.getEventBanner()));
            }*/

            if (source.getAgendas() != null && !source.getAgendas().isEmpty()) {
                destination.setAgendas(source.getAgendas().stream().map(agenda -> modelMapper.map(agenda, AgendaDTO.class)).toList());
            }

            return destination;
        });
    }

    public EventDTO toDto(Event event) {
        return modelMapper.map(event, EventDTO.class);
    }

    public Event toEntity(EventDTO eventDTO) {
        return modelMapper.map(eventDTO, Event.class);
    }

    public EventResponseDTO toResponseDto(Event event, Boolean status, String message) {
        EventResponseDTO responseDTO = new EventResponseDTO();
        responseDTO.setEventUUID(event.getUuid());
        responseDTO.setStatus(status);
        responseDTO.setMessage(message);
        return responseDTO;
    }

    public EventListResponseDTO toResponseDto(List<Event> events, Boolean status, String message) {
        EventListResponseDTO responseDTO = new EventListResponseDTO();
        responseDTO.setEvents(events);
        responseDTO.setStatus(status);
        responseDTO.setMessage(message);
        return responseDTO;
    }

    public EventObjectResponseDTO toObjectResponse(Event event, Boolean status, String message) {
        EventObjectResponseDTO responseDTO = new EventObjectResponseDTO();
        responseDTO.setEvent(event);
        responseDTO.setStatus(status);
        responseDTO.setMessage(message);
        return responseDTO;
    }

    public Event updateEventFromUpdateDTO(EventUpdateDTO dto, Event existingEvent) {

        if (dto.getName() != null) existingEvent.setName(dto.getName());
        if (dto.getDescription() != null) existingEvent.setDescription(dto.getDescription());
        if (dto.getCreatorUUID() != null) existingEvent.setCreatorUUID(dto.getCreatorUUID());
        if (dto.getRegistrationEndDate() != null) existingEvent.setRegistrationEndDate(dto.getRegistrationEndDate());
        if (dto.getStartDate() != null) existingEvent.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null) existingEvent.setEndDate(dto.getEndDate());
        if (dto.getCapacity() != null) existingEvent.setCapacity(dto.getCapacity());
        if (dto.getPrice() != null) existingEvent.setPrice(dto.getPrice());

        if (dto.getCategory() != null) {
            EventCategory category = eventCategoryRepository.findByName(dto.getCategory());
            existingEvent.setEventCategory(category);
        }

        if (dto.getStatus() != null) {
            EventStatus status = eventStatusRepository.findByName(dto.getStatus());
            existingEvent.setStatus(status);
        }

        if (dto.getAgendas() != null) {
            existingEvent.getAgendas().clear();
            for (Agenda agenda : dto.getAgendas()) {
                agenda.setEvent(existingEvent);
                existingEvent.getAgendas().add(agenda);
            }
        }

        return existingEvent;
    }
}


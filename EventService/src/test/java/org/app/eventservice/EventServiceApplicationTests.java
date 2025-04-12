package org.app.eventservice;

import org.app.eventservice.dto.EventDTO;
import org.app.eventservice.dto.EventListResponseDTO;
import org.app.eventservice.dto.EventResponseDTO;
import org.app.eventservice.dto.EventUpdateDTO;
import org.app.eventservice.entity.Event;
import org.app.eventservice.mappers.EventMapper;
import org.app.eventservice.repository.EventRepository;
import org.app.eventservice.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@SpringBootTest
class EventServiceApplicationTests {


  @Mock
  private EventRepository eventRepository;

  @Mock
  private EventMapper eventMapper;

  @InjectMocks
  private EventService eventService;

  private UUID eventId;
  private EventDTO eventDTO;
  private EventUpdateDTO eventUpdateDTO;
  private Event event;

  @BeforeEach
  void setUp() {
    eventId = UUID.randomUUID();

    eventDTO = new EventDTO();
    eventDTO.setName("Test Event");
    eventDTO.setDescription("Test Description");
    eventDTO.setCreatorUUID(UUID.randomUUID());
    eventDTO.setRegistrationEndDate(Instant.now().plusSeconds(86400));
    eventDTO.setStartDate(Instant.now().plusSeconds(172800));
    eventDTO.setEndDate(Instant.now().plusSeconds(259200));
    eventDTO.setCapacity(100);
    eventDTO.setPrice(50.0);

    event = new Event();
    event.setUuid(eventId);
    event.setName("Test Event");

    eventUpdateDTO = new EventUpdateDTO();
    eventUpdateDTO.setUuid(eventId);
    eventUpdateDTO.setName("Updated Event");
    eventUpdateDTO.setStartDate(Instant.now().plusSeconds(172800));
    eventUpdateDTO.setEndDate(Instant.now().plusSeconds(259200));
  }

  @Test
  void getAllEvents_ShouldReturnAllEvents() {
    // Arrange
    List<Event> events = Collections.singletonList(event);
    when(eventRepository.findAll()).thenReturn(events);

    // Act
    eventService.getAllEvents();

    // Assert
    verify(eventRepository).findAll();
    verify(eventMapper).toResponseDto(eq(events), eq(true), anyString());
  }

  @Test
  void getEventById_WhenEventExists_ShouldReturnEvent() {
    // Arrange
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

    // Act
    eventService.getEventById(eventId.toString());

    // Assert
    verify(eventRepository).findById(eventId);
    verify(eventMapper).toObjectResponse(eq(event), eq(true), anyString());
  }

  @Test
  void getEventById_WhenEventDoesNotExist_ShouldThrowException() {
    // Arrange
    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            eventService.getEventById(eventId.toString())
    );
  }

  @Test
  void createEvent_WithValidData_ShouldCreateEvent() {
    // Arrange
    when(eventMapper.toEntity(eventDTO)).thenReturn(event);
    when(eventRepository.save(event)).thenReturn(event);

    // Act
    eventService.createEvent(eventDTO);

    // Assert
    verify(eventMapper).toEntity(eventDTO);
    verify(eventRepository).save(event);
    verify(eventMapper).toResponseDto(eq(event), eq(true), anyString());
  }


  @Test
  void createEvent_WithInvalidDates_ShouldThrowException() {
    // Arrange
    eventDTO.setStartDate(Instant.now().plusSeconds(259200));
    eventDTO.setEndDate(Instant.now().plusSeconds(172800));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            eventService.createEvent(eventDTO)
    );
  }

  @Test
  void updateEvent_WhenEventExists_ShouldUpdateEvent() {
    // Arrange
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
    when(eventMapper.updateEventFromUpdateDTO(eventUpdateDTO, event)).thenReturn(event);
    when(eventRepository.save(event)).thenReturn(event);

    // Create a response DTO to be returned by the mock
    EventResponseDTO expectedResponse = new EventResponseDTO();
    expectedResponse.setEventUUID(eventId);
    expectedResponse.setMessage("Event updated successfully");
    expectedResponse.setStatus(true);

    // Add this mock behavior
    when(eventMapper.toResponseDto(eq(event), eq(true), eq("Event updated successfully")))
            .thenReturn(expectedResponse);

    // Act
    EventResponseDTO actualResponse = eventService.updateEvent(eventUpdateDTO);

    // Assert
    verify(eventRepository).findById(eventId);
    verify(eventMapper).updateEventFromUpdateDTO(eventUpdateDTO, event);
    verify(eventRepository).save(event);
    verify(eventMapper).toResponseDto(eq(event), eq(true), eq("Event updated successfully"));

    assertNotNull(actualResponse);
    assertEquals("Event updated successfully", actualResponse.getMessage());
  }

  @Test
  void getAllEventsByUserId_ShouldReturnUserEvents() {
    // Arrange
    UUID creatorId = UUID.randomUUID();
    event.setCreatorUUID(creatorId);
    List<Event> events = Collections.singletonList(event);
    when(eventRepository.findAllByCreatorUUID(creatorId)).thenReturn(events);

    EventListResponseDTO expectedResponse = new EventListResponseDTO();
    expectedResponse.setEvents(Collections.emptyList());
    expectedResponse.setStatus(true);
    expectedResponse.setMessage("Events retrieved successfully");

    when(eventMapper.toResponseDto(eq(events), eq(true), eq("Events retrieved successfully")))
            .thenReturn(expectedResponse);

    // Act
    EventListResponseDTO result = eventService.getAllEventsByUserId(creatorId.toString());

    // Assert
    verify(eventRepository).findAllByCreatorUUID(creatorId);
    verify(eventMapper).toResponseDto(eq(events), eq(true), eq("Events retrieved successfully"));

    assertNotNull(result);
    assertEquals("Events retrieved successfully", result.getMessage());
  }

  @Test
  void getAllEventsByCategoryName_ShouldReturnCategoryEvents() {
    // Arrange
    String categoryName = "Technology";
    List<Event> events = Collections.singletonList(event);
    when(eventRepository.findAllByEventCategory_Name(categoryName)).thenReturn(events);

    EventListResponseDTO expectedResponse = new EventListResponseDTO();
    expectedResponse.setStatus(true);
    expectedResponse.setMessage("Events retrieved successfully");

    when(eventMapper.toResponseDto(eq(events), eq(true), eq("Events retrieved successfully")))
            .thenReturn(expectedResponse);

    // Act
    EventListResponseDTO result = eventService.getAllEventsByCategoryName(categoryName);

    // Assert
    verify(eventRepository).findAllByEventCategory_Name(categoryName);
    verify(eventMapper).toResponseDto(eq(events), eq(true), eq("Events retrieved successfully"));

    assertNotNull(result);
  }

  @Test
  void deleteEvent_WhenEventExists_ShouldDeleteEvent() {
    // Arrange
    when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

    EventResponseDTO expectedResponse = new EventResponseDTO();
    expectedResponse.setEventUUID(eventId);
    expectedResponse.setStatus(true);
    expectedResponse.setMessage("Event deleted successfully");

    when(eventMapper.toResponseDto(eq(event), eq(true), eq("Event deleted successfully")))
            .thenReturn(expectedResponse);

    // Act
    EventResponseDTO result = eventService.deleteEvent(eventId.toString());

    // Assert
    verify(eventRepository).findById(eventId);
    verify(eventRepository).delete(event);
    verify(eventMapper).toResponseDto(eq(event), eq(true), eq("Event deleted successfully"));

    assertNotNull(result);
    assertEquals(eventId, result.getEventUUID());
  }

  @Test
  void deleteEvent_WhenEventDoesNotExist_ShouldThrowException() {
    // Arrange
    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            eventService.deleteEvent(eventId.toString())
    );
  }

  @Test
  void updateEvent_WithInvalidDates_ShouldThrowException() {
    // Arrange
    eventUpdateDTO.setStartDate(Instant.now().plusSeconds(259200));
    eventUpdateDTO.setEndDate(Instant.now().plusSeconds(172800));

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            eventService.updateEvent(eventUpdateDTO)
    );
  }

  @Test
  void updateEvent_endDateBeforeStartDate_throwsException() {

    eventUpdateDTO.setStartDate(Instant.now().plusSeconds(10800));
    eventUpdateDTO.setEndDate(Instant.now().plusSeconds(7200));

    assertThrows(IllegalArgumentException.class, () ->
            eventService.updateEvent(eventUpdateDTO)
    );
  }

  @Test
  void updateEvent_WhenEventDoesNotExist_ShouldThrowException() {
    // Arrange
    when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            eventService.updateEvent(eventUpdateDTO)
    );
  }

  @Test
  void getEventById_WithInvalidUUID_ShouldThrowException() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            eventService.getEventById("invalid-uuid")
    );
  }

  @Test
  void getAllEventsByUserId_WithInvalidUUID_ShouldThrowException() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            eventService.getAllEventsByUserId("invalid-uuid")
    );
  }

  @Test
  void deleteEvent_WithInvalidUUID_ShouldThrowException() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () ->
            eventService.deleteEvent("invalid-uuid")
    );
  }


}

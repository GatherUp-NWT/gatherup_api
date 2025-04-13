package org.app.eventservice;

import org.app.eventservice.dto.*;
import org.app.eventservice.entity.Event;
import org.app.eventservice.mappers.EventMapper;
import org.app.eventservice.repository.EventRepository;
import org.app.eventservice.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;


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

  @Test
  void getAllEventsPaginated_WithAscendingSort_ShouldReturnPagedEvents() {
    // Arrange
    int page = 0;
    int size = 10;
    String sortBy = "name";
    String sortOrder = "asc";

    List<Event> events = Collections.singletonList(event);
    Page<Event> pageEvents = new PageImpl<>(events, PageRequest.of(page, size, Sort.by(sortBy).ascending()), 1);

    when(eventRepository.findAll(any(Pageable.class))).thenReturn(pageEvents);

    EventListPagedDTO expectedResponse = new EventListPagedDTO();
    expectedResponse.setStatus(true);
    expectedResponse.setMessage("Events retrieved successfully");
    expectedResponse.setEvents(Collections.emptyList());
    expectedResponse.setTotalElements(1L);
    expectedResponse.setTotalPages(1);
    expectedResponse.setPageNumber(0);
    expectedResponse.setPageSize(10);

    when(eventMapper.toPagedResponseDto(
            eq(events),
            eq(1L),
            eq(1),
            eq(0),
            eq(10),
            eq(true),
            eq("Events retrieved successfully")
    )).thenReturn(expectedResponse);

    // Act
    EventListPagedDTO result = eventService.getAllEventsPaginated(page, size, sortBy, sortOrder);

    // Assert
    verify(eventRepository).findAll(any(Pageable.class));
    verify(eventMapper).toPagedResponseDto(
            eq(events),
            eq(1L),
            eq(1),
            eq(0),
            eq(10),
            eq(true),
            eq("Events retrieved successfully")
    );

    assertNotNull(result);
    assertEquals(1L, result.getTotalElements());
    assertEquals(1, result.getTotalPages());
    assertEquals(0, result.getPageNumber());
    assertEquals(10, result.getPageSize());
  }

  @Test
  void getAllEventsPaginated_WithDescendingSort_ShouldReturnPagedEvents() {
    // Arrange
    int page = 0;
    int size = 10;
    String sortBy = "startDate";
    String sortOrder = "desc";

    List<Event> events = Collections.singletonList(event);
    Page<Event> pageEvents = new PageImpl<>(events, PageRequest.of(page, size, Sort.by(sortBy).descending()), 1);

    when(eventRepository.findAll(any(Pageable.class))).thenReturn(pageEvents);

    EventListPagedDTO expectedResponse = new EventListPagedDTO();
    expectedResponse.setStatus(true);
    expectedResponse.setMessage("Events retrieved successfully");
    when(eventMapper.toPagedResponseDto(
            any(), anyLong(), anyInt(), anyInt(), anyInt(), eq(true), anyString()
    )).thenReturn(expectedResponse);

    // Act
    EventListPagedDTO result = eventService.getAllEventsPaginated(page, size, sortBy, sortOrder);

    // Assert
    verify(eventRepository).findAll(any(Pageable.class));
    verify(eventMapper).toPagedResponseDto(
            eq(events),
            eq(1L),
            eq(1),
            eq(0),
            eq(10),
            eq(true),
            eq("Events retrieved successfully")
    );

    assertNotNull(result);
  }

  @Test
  void getAllEventsPaginated_WithEmptyPage_ShouldReturnEmptyList() {
    // Arrange
    int page = 0;
    int size = 10;
    String sortBy = "name";
    String sortOrder = "asc";

    List<Event> emptyList = Collections.emptyList();
    Page<Event> emptyPage = new PageImpl<>(emptyList, PageRequest.of(page, size, Sort.by(sortBy).ascending()), 0);

    when(eventRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

    EventListPagedDTO expectedResponse = new EventListPagedDTO();
    expectedResponse.setStatus(true);
    expectedResponse.setMessage("Events retrieved successfully");
    expectedResponse.setEvents(Collections.emptyList());
    expectedResponse.setTotalElements(0L);

    when(eventMapper.toPagedResponseDto(
            eq(emptyList),
            eq(0L),
            eq(0),
            eq(0),
            eq(10),
            eq(true),
            eq("Events retrieved successfully")
    )).thenReturn(expectedResponse);

    // Act
    EventListPagedDTO result = eventService.getAllEventsPaginated(page, size, sortBy, sortOrder);

    // Assert
    verify(eventRepository).findAll(any(Pageable.class));
    verify(eventMapper).toPagedResponseDto(
            eq(emptyList),
            eq(0L),
            eq(0),
            eq(0),
            eq(10),
            eq(true),
            eq("Events retrieved successfully")
    );

    assertNotNull(result);
    assertEquals(0L, result.getTotalElements());
  }

  @Test
  void getNearbyEvents_ShouldReturnNearbyEvents() {
    // Arrange
    double latitude = 40.7128;
    double longitude = -74.0060;
    double radius = 5.0;
    int limit = 10;

    List<Event> events = Collections.singletonList(event);
    when(eventRepository.findNearbyEvents(latitude, longitude, radius, limit)).thenReturn(events);

    EventListResponseDTO expectedResponse = new EventListResponseDTO();
    expectedResponse.setEvents(Collections.emptyList());
    expectedResponse.setStatus(true);
    expectedResponse.setMessage("Nearby events retrieved successfully");

    when(eventMapper.toResponseDto(eq(events), eq(true), eq("Nearby events retrieved successfully")))
            .thenReturn(expectedResponse);

    // Act
    EventListResponseDTO result = eventService.getNearbyEvents(latitude, longitude, radius, limit);

    // Assert
    verify(eventRepository).findNearbyEvents(latitude, longitude, radius, limit);
    verify(eventMapper).toResponseDto(eq(events), eq(true), eq("Nearby events retrieved successfully"));

    assertNotNull(result);
    assertEquals("Nearby events retrieved successfully", result.getMessage());
    assertTrue(result.getStatus());
  }


}

package org.app.eventservice.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.app.eventservice.entity.*;
import org.app.eventservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DataSeedEvent {


  private final EventRepository eventRepository;
  private final AgendaRepository agendaRepository;
  private final EventCategoryRepository eventCategoryRepository;
  private final EventStatusRepository eventStatusRepository;
  private final LocationRepository locationRepository;

  @PostConstruct
  public void initDatabase() {
    if (eventRepository.count() == 0) {


      EventCategory category = new EventCategory();
      category.setName("Technology");
      category = eventCategoryRepository.save(category);

      EventStatus status = new EventStatus();
      status.setName("Upcoming");
      status = eventStatusRepository.save(status);






      Location location = new Location();
      location.setLatitude("40.7128");
      location.setLongitude("-74.0060");
      location = locationRepository.save(location);


      Event event = new Event();
      event.setUuid(UUID.randomUUID());
      event.setName("Tech Conference 2025");
      event.setDescription("A conference about emerging technologies.");
      event.setCreationDate(LocalDateTime.now());
      event.setCreatorUUID(UUID.randomUUID());
      event.setRegistrationEndDate(LocalDateTime.now().plusDays(30));
      event.setStartDate(LocalDateTime.now().plusDays(40));
      event.setEndDate(LocalDateTime.now().plusDays(42));
      event.setCapacity(200);
      event.setPrice(99.99);

      event.setEventCategory(category);
      event.setProfilePicture(null);


      event = eventRepository.save(event);


      Agenda agenda = new Agenda();
      agenda.setEvent(event);
      agenda.setLocation(location);
      agenda = agendaRepository.save(agenda);


      eventRepository.save(event);

      System.out.println("Database populated with initial data!");
    }
  }
}

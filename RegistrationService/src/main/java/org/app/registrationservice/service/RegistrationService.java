package org.app.registrationservice.service;


import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.UUID;
import org.app.registrationservice.entity.Registration;
import org.app.registrationservice.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

  private final RegistrationRepository registrationRepository;

  public RegistrationService(RegistrationRepository registrationRepository) {
    this.registrationRepository = registrationRepository;
  }

  @PostConstruct
  public void initDatabase() {
    if (registrationRepository.count() == 0) {


      Registration r = new Registration();
      r.setUserId(UUID.fromString("13004255-59fa-4df6-9ab8-34e40f7058bf"));
      r.setEventId(UUID.fromString("df77cdec-e4d0-488e-99fe-210a4cada331"));


      registrationRepository.save(r);

      System.out.println(" data populated!");
    }
  }
}

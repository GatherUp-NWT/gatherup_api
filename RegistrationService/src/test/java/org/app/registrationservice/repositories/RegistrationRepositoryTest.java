package org.app.registrationservice.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.app.registrationservice.entity.Registration;
import org.app.registrationservice.repository.RegistrationRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    public class RegistrationRepositoryTest {
    
        @Autowired
        private RegistrationRepository registrationRepository;
    
        @PersistenceContext
        private EntityManager entityManager;
    
        private Statistics statistics;
    
        private Registration registration;
        private UUID userId;
        private UUID eventId;
    
        @BeforeEach
        void setUp() {
            // Setup session statistics to monitor queries
            SessionFactory sessionFactory = entityManager.unwrap(Session.class).getSessionFactory();
            statistics = sessionFactory.getStatistics();
            statistics.setStatisticsEnabled(true);
    
            // Prepare test data
            userId = UUID.randomUUID();
            eventId = UUID.randomUUID();
    
            registration = new Registration();
            registration.setUserId(userId);
            registration.setEventId(eventId);
    
            // Save the registration to the repository
            registrationRepository.save(registration);
            // Flush to ensure the insert is executed before the find
            entityManager.flush();
            entityManager.clear(); // Clear the persistence context to avoid dirty reads
        }
    
        @Test
        void whenFetchingRegistrations_NoNPlusOneProblem() {
            // Fetch all registrations
            List<Registration> registrations = registrationRepository.findAll();
    
            // Fetch statistics about the queries executed
            long queryCount = statistics.getQueryExecutionCount();
    
            // Verify that only one query is executed (no N+1 problem)
            assertEquals(1, queryCount, "There should be only one query executed!");
        }
    }

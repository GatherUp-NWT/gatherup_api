package org.app.reviewservice.service;

import org.app.reviewservice.clients.EventServiceClient;
import org.app.reviewservice.dto.EventResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoadTestService {

    private final EventServiceClient eventServiceClient;

    @Autowired
    public LoadTestService(EventServiceClient eventServiceClient) {
        this.eventServiceClient = eventServiceClient;
    }

    public void sendRequests(int numberOfRequests, String eventId) {
        for (int i = 0; i < numberOfRequests; i++) {
            try {
                EventResponseDTO response = eventServiceClient.getEventById(eventId);
                System.out.println("Response: " + response);
            } catch (Exception e) {
                System.err.println("Error calling event service: " + e.getMessage());
            }
        }
    }
}

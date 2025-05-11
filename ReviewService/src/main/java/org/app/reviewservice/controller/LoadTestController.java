package org.app.reviewservice.controller;

import org.app.reviewservice.service.LoadTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reviews")
public class LoadTestController {

    private final LoadTestService testService;

    @Autowired
    public LoadTestController(LoadTestService loadTestService) {
        this.testService = loadTestService;
    }

    @GetMapping("/test-event-service")
    public String testEventService(@RequestParam String eventId, @RequestParam int numberOfRequests) {
        testService.sendRequests(numberOfRequests, eventId);
        return "Test finished";
    }
}


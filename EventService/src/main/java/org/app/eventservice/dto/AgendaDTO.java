package org.app.eventservice.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class AgendaDTO {
    private Long id;
    private String name;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private LocationDTO location;
}

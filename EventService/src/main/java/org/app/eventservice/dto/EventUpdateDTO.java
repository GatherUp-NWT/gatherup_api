package org.app.eventservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.eventservice.entity.Agenda;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUpdateDTO {
    @NotNull(message = "Event UUID is required")
    private UUID uuid;

    @Size(min = 2, max = 50, message = "Event name should be between 2 and 50 characters")
    private String name;

    @Size(max = 500, message = "Event description should be at most 500 characters")
    private String description;

    private UUID creatorUUID;

    @FutureOrPresent(message = "Registration end date must be in the future or present")
    private Instant registrationEndDate;

    @Future(message = "Event start date must be in the future")
    private Instant startDate;

    @Future(message = "Event end date must be in the future")
    private Instant endDate;

    @PositiveOrZero(message = "Event capacity must be positive or zero")
    private Integer capacity;

    @PositiveOrZero(message = "Event price must be positive or zero")
    private Double price;

    private String status;
    private String category;

    private Set<Agenda> agendas = new HashSet<>();
}
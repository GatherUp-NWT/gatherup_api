package org.app.eventservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private UUID uuid = UUID.randomUUID();

    @NotNull(message = "Event must have a name")
    @Size(min = 2, max = 50, message = "Event name should be between 2 and 50 characters")
    private String name;

    @Size(max = 500, message = "Event description should be at most 500 characters")
    private String description;

    @NotNull(message = "Event must specify which user created it")
    private UUID creatorUUID;

    @NotNull(message = "Event must have a creation date")
    @NotNull(message = "Event must have registration due date")
    @FutureOrPresent(message = "Event registration due date must be in the future or present")
    private Instant registrationEndDate;

    @NotNull(message = "Event must have start date")
    @Future(message = "Event start date must be in the future")
    private Instant startDate;

    @NotNull(message = "Event must have end date")
    @Future(message = "Event end date must be in the future")
    private Instant endDate;

    @NotNull(message = "Event must have specified capacity")
    @PositiveOrZero(message = "Event capacity must be positive or zero")
    private int capacity;

    @PositiveOrZero(message = "Event price must be positive or zero")
    private double price;

    private String status;
    private String category;
    private List<AgendaDTO> agendas;
    //private String eventBanner;

}

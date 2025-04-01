package org.app.eventservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "\"events\"")
public class Event {

    @Id
    private UUID uuid = UUID.randomUUID();

    @NotNull(message = "Event must have a name")
    @Size(min = 2, max = 50, message = "Event name should be between 2 and 50 characters")
    private String name;

    @Size(max = 500, message = "Event description should be at most 500 characters")
    private String description;

    @NotNull
    private Instant creationDate;

    @NotNull
    private UUID creatorUUID;

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

    @ManyToOne
    private EventStatus status;

    @ManyToOne
    private EventCategory eventCategory;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Agenda> agendas = new HashSet<>();
/*
    @Lob
    @Column(name = "event_banner", columnDefinition = "BYTEA")
    private byte[] eventBanner;*/


    public void addAgenda(Agenda agenda) {
        agendas.add(agenda);
        agenda.setEvent(this);
    }


}

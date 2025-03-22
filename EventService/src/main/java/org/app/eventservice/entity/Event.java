package org.app.eventservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event {

  @Id
  private UUID uuid = UUID.randomUUID();

  private String name;
  private String description;
  private LocalDateTime creationDate;

  @NotNull

  private UUID creatorUUID = UUID.randomUUID();

  private LocalDateTime registrationEndDate;
  private LocalDateTime startDate;
  private LocalDateTime endDate;

  private int capacity;
  @NotNull
  private double price;
  @ManyToOne
  private EventStatus status;
  @ManyToOne
  private EventCategory eventCategory;

  @OneToMany(mappedBy = "event")
  Set<Agenda> Agends;

  @Lob
  private byte[] profilePicture;
}

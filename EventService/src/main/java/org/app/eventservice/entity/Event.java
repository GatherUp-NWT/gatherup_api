package org.app.eventservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.Set;
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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private LocalDateTime creationDate;

  @NotNull
  private Long creatorId;

  private LocalDateTime registrationEndDate;
  private LocalDateTime startDate;
  private LocalDateTime endDate;

  private int capacity;
  private double price;
  @ManyToOne
  @JoinColumn(name = "status_id", referencedColumnName = "id")
  private EventStatus status;
  @ManyToOne()
  private EventCategory eventCategory;

  @OneToMany
  Set<Agenda> Agends;
}

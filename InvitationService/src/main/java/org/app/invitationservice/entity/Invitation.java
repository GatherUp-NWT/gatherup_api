
package org.app.invitationservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Invitation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)

  private EventInvite event;

  @ManyToOne(fetch=FetchType.EAGER)
  private UserInvite user;

  @ManyToOne(fetch=FetchType.EAGER)
  @JoinColumn(name = "send_by_id")
  private UserInvite sendBy;

  @PastOrPresent(message = "Response date must be in the past or present")
  private LocalDateTime sendDate;
  @PastOrPresent(message = "Response date must be in the past or present")
  private LocalDateTime responseDate;

  private InvitationResponseType invitationResponseType;

  private TimeStatus statusInTime;


}

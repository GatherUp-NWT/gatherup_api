package org.app.authservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "\"user\"")
public class User {


  @Id
  private UUID uuid = UUID.randomUUID();

  @NotNull(message = "First name is required")
  @Size(min = 2, max = 15, message = "First name should be between 2 and 15 characters")
  private String firstName;

  @NotNull(message = "Last name is required")
  @Size(min = 2, max = 15, message = "Last name should be between 2 and 15 characters")
  private String lastName;

  @NotNull(message = "Email is required")
  @Email(message = "Email should be valid")
  private String email;

  @NotNull(message = "Password is required")
  private String password;

  @Size(max = 100, message = "Bio should be at most 100 characters")
  private String bio;

  @JsonManagedReference
  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
  private Set<UserRole> userRoles;


}

package org.app.authservice.service;

import jakarta.annotation.PostConstruct;
import org.app.authservice.entity.Role;
import org.app.authservice.entity.User;
import org.app.authservice.entity.UserRole;
import org.app.authservice.respository.RoleRepository;
import org.app.authservice.respository.UserRepository;
import org.app.authservice.respository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {


  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final RoleRepository roleRepository;

  public UserService(UserRepository userRepository, UserRoleRepository userRoleRepository, RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
    this.roleRepository = roleRepository;
  }

  @PostConstruct
  public void init() {

    //set roles
    Role adminRole = new Role();
    adminRole.setName("ADMIN");
    roleRepository.save(adminRole);

    Role userRole = new Role();
    userRole.setName("USER");
    roleRepository.save(userRole);

    User user1 = new User();
    user1.setFirstName("John");

    user1.setLastName("Doe");
    user1.setEmail("john.doe@example.com");
    user1.setPassword("password123");
    user1.setBio("A regular user.");
    userRepository.save(user1);

    UserRole userRole1 = new UserRole();
    userRole1.setRole(userRole);
    userRole1.setUser(user1);
    userRoleRepository.save(userRole1);

    System.out.println("Saved data successfully");


  }


}

package org.app.authservice;

import org.app.authservice.dto.*;
import org.app.authservice.entity.*;
import org.app.authservice.respository.UserRepository;
import org.app.authservice.service.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)  // Add this to allow unused stubs
class AuthServiceApplicationTests {

  @Mock
  private UserRepository userRepository;

  private static final Logger logger = LoggerFactory.getLogger(AuthServiceApplicationTests.class);

  @Mock
  private UserMapperService userMapperService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private User user;
  private UserDTO userDTO;
  private UserUpdateDTO userUpdateDTO;
  private UserResponseDTO userResponseDTO;
  private UUID userId;
  private Role role;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    role = new Role(1L, "USER");

    user = new User();
    user.setUuid(userId);
    user.setFirstName("John");
    user.setLastName("Doe");
    user.setEmail("john.doe@example.com");
    user.setPassword("encodedPassword");
    user.setRole(role);

    userDTO = new UserDTO();
    userDTO.setFirstName("John");
    userDTO.setLastName("Doe");
    userDTO.setEmail("john.doe@example.com");
    userDTO.setPassword("Password@123");
    userDTO.setRole("USER");

    userUpdateDTO = new UserUpdateDTO();
    userUpdateDTO.setFirstName("John");
    userUpdateDTO.setLastName("Smith");
    userUpdateDTO.setEmail("john.smith@example.com");
    userUpdateDTO.setPassword("NewPassword@123");
    userUpdateDTO.setConfirmPassword("NewPassword@123");

    userResponseDTO = new UserResponseDTO("true", "Success", userId);
  }

  @Test
  void deleteUser_WithInvalidUUID_ShouldThrowException() {
    // Test with actual invalid UUID string
    assertThrows(IllegalArgumentException.class, () -> userService.deleteUser("invalid-uuid"));
    verify(userRepository, never()).deleteById(any(UUID.class));
  }

  @Test
  void updateUser_WithDuplicateEmail_ShouldThrowException() {
    User existingUser = new User();
    existingUser.setUuid(userId);
    existingUser.setEmail("original@example.com");

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(existingUser));
    when(userRepository.existsByEmail(userUpdateDTO.getEmail())).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId.toString(), userUpdateDTO));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void getUserById_WithInvalidUUID_ShouldThrowException() {
    assertThrows(IllegalArgumentException.class, () -> userService.getUserById("invalid-uuid"));
  }


  @Test
  void partialUpdateUser_WithInvalidField_ShouldThrowException() {
    Map<String, Object> updates = Map.of("invalidField", "value");
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));

    assertThrows(IllegalArgumentException.class, () -> userService.partialUpdateUser(userId.toString(), updates));
    verify(userRepository, never()).save(any(User.class));
  }
}
package org.app.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.authservice.dto.UserDTO;
import org.app.authservice.dto.UserNonSensitiveDTO;
import org.app.authservice.dto.UserResponseDTO;
import org.app.authservice.dto.UserUpdateDTO;
import org.app.authservice.security.SecurityConfig;
import org.app.authservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({UserController.class, SecurityConfig.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID userId;
    private UserDTO userDTO;
    private UserUpdateDTO userUpdateDTO;
    private UserResponseDTO userResponseDTO;
    private UserNonSensitiveDTO userNonSensitiveDTO;
    private Page<UserNonSensitiveDTO> userPage;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        // Setup UserDTO for create request
        userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("john.doe@example.com");
        userDTO.setPassword("Password@123");
        userDTO.setBio("Software developer");
        userDTO.setRole("USER");

        // Setup UserUpdateDTO for update request
        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName("John");
        userUpdateDTO.setLastName("Smith");
        userUpdateDTO.setEmail("john.smith@example.com");
        userUpdateDTO.setPassword("NewPassword@123");
        userUpdateDTO.setConfirmPassword("NewPassword@123");
        userUpdateDTO.setBio("Senior developer");

        // Setup UserResponseDTO for create/update responses
        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setUuid(userId);
        userResponseDTO.setStatus("true");
        userResponseDTO.setMessage("Success");

        // Setup UserNonSensitiveDTO for get responses
        userNonSensitiveDTO = new UserNonSensitiveDTO();
        userNonSensitiveDTO.setUuid(userId);
        userNonSensitiveDTO.setFirstName("John");
        userNonSensitiveDTO.setLastName("Doe");
        userNonSensitiveDTO.setEmail("john.doe@example.com");
        userNonSensitiveDTO.setBio("Software developer");
        userNonSensitiveDTO.setRole("USER");

        // Setup Page of users
        userPage = new PageImpl<>(Collections.singletonList(userNonSensitiveDTO));
    }

    @Test
    void getAllUsers_WithDefaultParams_ShouldReturnUsers() throws Exception {
        when(userService.getAllUsers(eq(0), eq(10), eq("lastName"), eq(Sort.Direction.ASC)))
                .thenReturn(userPage);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].uuid").value(userId.toString()))
                .andExpect(jsonPath("$.content[0].firstName").value("John"))
                .andExpect(jsonPath("$.content[0].lastName").value("Doe"));

        verify(userService).getAllUsers(0, 10, "lastName", Sort.Direction.ASC);
    }

    @Test
    void getAllUsers_WithCustomParams_ShouldReturnUsers() throws Exception {
        when(userService.getAllUsers(eq(1), eq(5), eq("firstName"), eq(Sort.Direction.DESC)))
                .thenReturn(userPage);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "firstName")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(userService).getAllUsers(1, 5, "firstName", Sort.Direction.DESC);
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() throws Exception {
        when(userService.createUser(any(UserDTO.class))).thenReturn(userResponseDTO);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("true"))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.uuid").value(userId.toString()));

        verify(userService).createUser(any(UserDTO.class));
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnError() throws Exception {
        UserDTO invalidUserDTO = new UserDTO();
        invalidUserDTO.setEmail("invalid-email");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_ShouldDeleteAndReturnSuccess() throws Exception {
        Map<String, String> response = Map.of("status", "true", "message", "User deleted successfully");
        when(userService.deleteUser(userId.toString())).thenReturn(response);

        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("true"))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(userService).deleteUser(userId.toString());
    }

    @Test
    void deleteUser_WithInvalidUUID_ShouldReturnError() throws Exception {
        String invalidUserId = "invalid-uuid";
        when(userService.deleteUser(invalidUserId))
                .thenThrow(new IllegalArgumentException("Invalid UUID format"));

        mockMvc.perform(delete("/api/v1/users/{id}", invalidUserId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() throws Exception {
        when(userService.updateUser(eq(userId.toString()), any(UserUpdateDTO.class)))
                .thenReturn(userResponseDTO);

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("true"))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.uuid").value(userId.toString()));

        verify(userService).updateUser(eq(userId.toString()), any(UserUpdateDTO.class));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        when(userService.getUserById(userId.toString())).thenReturn(userNonSensitiveDTO);

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid").value(userId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(userService).getUserById(userId.toString());
    }

    @Test
    void partialUpdateUser_ShouldUpdateSpecificFields() throws Exception {
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "Robert");
        updates.put("bio", "Updated bio information");

        UserResponseDTO updatedResponse = new UserResponseDTO();
        updatedResponse.setUuid(userId);
        updatedResponse.setStatus("true");
        updatedResponse.setMessage("User updated successfully");

        when(userService.partialUpdateUser(eq(userId.toString()), anyMap()))
                .thenReturn(updatedResponse);

        mockMvc.perform(patch("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("true"))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.uuid").value(userId.toString()));

        verify(userService).partialUpdateUser(eq(userId.toString()), anyMap());
    }

    @Test
    void partialUpdateUser_WithInvalidField_ShouldReturnError() throws Exception {
        Map<String, Object> invalidUpdates = Map.of("invalidField", "some value");

        when(userService.partialUpdateUser(eq(userId.toString()), anyMap()))
                .thenThrow(new IllegalArgumentException("Field 'invalidField' is not updatable"));

        mockMvc.perform(patch("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUpdates)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void partialUpdateUser_WithInvalidUUID_ShouldReturnError() throws Exception {
        String invalidUserId = "invalid-uuid";
        Map<String, Object> updates = Map.of("firstName", "Robert");

        mockMvc.perform(patch("/api/v1/users/{id}", invalidUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updates)))
                .andExpect(status().isBadRequest());
    }
}

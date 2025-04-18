package org.app.authservice.controller;

import jakarta.validation.Valid;
import org.app.authservice.dto.UserDTO;
import org.app.authservice.dto.UserNonSensitiveDTO;
import org.app.authservice.dto.UserResponseDTO;
import org.app.authservice.dto.UserUpdateDTO;
import org.app.authservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/api/v1/users")
    public Page<UserNonSensitiveDTO> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        return userService.getAllUsers(page, size, sort, sortDirection);
    }

    @PostMapping("/api/v1/users")
    public UserResponseDTO createUser(@Valid @RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @DeleteMapping("/api/v1/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    @PutMapping("/api/v1/users/{id}")
    public UserResponseDTO updateUser(@PathVariable String id, @Valid @RequestBody UserUpdateDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    @GetMapping("/api/v1/users/{id}")
    public UserNonSensitiveDTO getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    @PatchMapping("/api/v1/users/{id}")
    public ResponseEntity<UserResponseDTO> partialUpdateUser(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {

        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }

        return ResponseEntity.ok(userService.partialUpdateUser(id, updates));
    }
}

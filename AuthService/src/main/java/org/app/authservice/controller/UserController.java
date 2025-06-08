package org.app.authservice.controller;

import jakarta.validation.Valid;
import org.app.authservice.dto.*;
import org.app.authservice.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Regular user endpoints - require USER role
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public Page<UserNonSensitiveDTO> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        return userService.getAllUsers(page, size, sort, sortDirection);
    }

    // Admin endpoints - require ADMIN role
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }

    // Admin endpoint to get all users with additional details
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/all")
    public Page<UserNonSensitiveDTO> getAllUsersForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "lastName") String sort,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        return userService.getAllUsers(page, size, sort, sortDirection);
    }

    // User can update their own profile
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable String id, @Valid @RequestBody UserUpdateDTO userDTO) {
        return userService.updateUser(id, userDTO);
    }

    // Anyone with USER role can view user profiles
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/{id}")
    public UserNonSensitiveDTO getUserById(@PathVariable String id) {
        return userService.getUserById(id);
    }

    // User can partially update their own profile
    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{id}")
    public ResponseEntity<UserUpdateResponseDTO> partialUpdateUser(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {

        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }

        return ResponseEntity.ok(userService.partialUpdateUser(id, updates));
    }

    // Anyone with USER role can look up users by email
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/email/{email}")
    public UserNonSensitiveDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }
}

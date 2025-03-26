package org.app.authservice.controller;

import jakarta.validation.Valid;
import org.app.authservice.dto.UserDTO;
import org.app.authservice.dto.UserNonSensitiveDTO;
import org.app.authservice.dto.UserResponseDTO;
import org.app.authservice.dto.UserUpdateDTO;
import org.app.authservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/api/v1/users")
    List<UserNonSensitiveDTO> getAllUsers() {
        return userService.getAllUsers();
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
}

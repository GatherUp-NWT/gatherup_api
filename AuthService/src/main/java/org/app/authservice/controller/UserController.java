package org.app.authservice.controller;

import org.app.authservice.dto.UserDTO;
import org.app.authservice.dto.UserListDTO;
import org.app.authservice.dto.UserResponseDTO;
import org.app.authservice.entity.User;
import org.app.authservice.respository.UserRepository;
import org.app.authservice.service.UserMapperService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {
    private final UserRepository userRepository;
    private final UserMapperService userMapperService;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapperService userMapperService) {
        this.userRepository = userRepository;
        this.userMapperService = userMapperService;
    }


    @GetMapping("/api/v1/users")
    List<UserListDTO> getAllUsers() {

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new RuntimeException("No users found");
        }

        return userMapperService.toDtoList(users);

    }

    @PostMapping("/api/v1/users")
    public UserResponseDTO createUser(@RequestBody UserDTO userDTO) {

        if (userDTO.getFirstName() == null || userDTO.getLastName() == null || userDTO.getEmail() == null || userDTO.getPassword() == null) {
            throw new IllegalArgumentException("One or more required fields are missing");
        }

        // Check if the user already exists
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User savedUser = userRepository.save(userMapperService.toEntity(userDTO));

        return userMapperService.toResponseDto(savedUser, "User created successfully");
    }
}

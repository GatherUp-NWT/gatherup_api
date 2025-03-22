package org.app.authservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.app.authservice.dto.UserDTO;
import org.app.authservice.dto.UserListDTO;
import org.app.authservice.dto.UserResponseDTO;
import org.app.authservice.entity.User;
import org.app.authservice.respository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final UserMapperService userMapperService;

    public UserService(UserRepository userRepository, UserMapperService userMapperService) {
        this.userRepository = userRepository;
        this.userMapperService = userMapperService;
    }

    @PostConstruct
    public void init() {

    }

    public List<UserListDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new RuntimeException("No users found");
        }
        return userMapperService.toDtoList(users);
    }

    @Transactional
    public UserResponseDTO createUser(@Valid UserDTO userDTO) {
        if (userDTO.getFirstName() == null || userDTO.getLastName() == null || userDTO.getEmail() == null || userDTO.getPassword() == null) {
            throw new IllegalArgumentException("One or more required fields are missing");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = userMapperService.toEntity(userDTO);
        User savedUser = userRepository.save(user);

        return userMapperService.toResponseDto(savedUser, "User created successfully");
    }


}

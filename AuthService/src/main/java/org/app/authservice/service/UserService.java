package org.app.authservice.service;

import org.app.authservice.dto.UserDTO;
import org.app.authservice.dto.UserNonSensitiveDTO;
import org.app.authservice.dto.UserResponseDTO;
import org.app.authservice.dto.UserUpdateDTO;
import org.app.authservice.entity.User;
import org.app.authservice.respository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final UserMapperService userMapperService;

    public UserService(UserRepository userRepository, UserMapperService userMapperService) {
        this.userRepository = userRepository;
        this.userMapperService = userMapperService;
    }

    public Page<UserNonSensitiveDTO> getAllUsers(int page, int size, String sort, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(userMapperService::toDto);
    }

    @Transactional
    public UserResponseDTO createUser(UserDTO userDTO) {

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = userMapperService.toEntity(userDTO);
        try {
            User savedUser = userRepository.save(user);
            return userMapperService.toResponseDto(savedUser, "true", "User created successfully");
        } catch (Exception e) {
            return userMapperService.toResponseDto(user, "false", "Save failed: " + e.getMessage());
        }
    }

    @Transactional
    public Map<String, String> deleteUser(String id) {
        UUID uuid = UUID.fromString(id);

        if (!userRepository.existsById(uuid)) {
            throw new IllegalArgumentException("User not found");
        }

        userRepository.deleteById(uuid);

        if (userRepository.existsById(uuid)) {
            throw new IllegalArgumentException("User deletion failed");
        }

        return Map.of("status", "true", "message", "User deleted successfully");
    }

    public UserResponseDTO updateUser(String id, UserUpdateDTO userDTO) {

        UUID uuid = UUID.fromString(id);

        User existingUser = userRepository.findById(uuid).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userRepository.existsByEmail(userDTO.getEmail()) && !existingUser.getEmail().equals(userDTO.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User updatedUser = userMapperService.mapForUpdate(userDTO, existingUser);

        try {
            User savedUser = userRepository.save(updatedUser);
            return userMapperService.toResponseDto(savedUser, "true", "User updated successfully");
        } catch (Exception e) {
            return userMapperService.toResponseDto(updatedUser, "false", "Update failed: " + e.getMessage());
        }
    }

    public UserNonSensitiveDTO getUserById(String id) {
        UUID uuid = UUID.fromString(id);

        User user = userRepository.findById(uuid).orElseThrow(() -> new IllegalArgumentException("User not found"));

        return userMapperService.toDto(user);
    }
}

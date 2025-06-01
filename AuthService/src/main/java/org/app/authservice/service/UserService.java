package org.app.authservice.service;

import org.app.authservice.dto.*;
import org.app.authservice.entity.User;
import org.app.authservice.respository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class UserService {


    private final UserRepository userRepository;
    private final UserMapperService userMapperService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapperService userMapperService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapperService = userMapperService;
        this.passwordEncoder = passwordEncoder;
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

    @Transactional
    public UserUpdateResponseDTO partialUpdateUser(String id, Map<String, Object> updates) { // Changed return type
        UUID uuid = UUID.fromString(id);

        User existingUser = userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (updates.containsKey("email")) {
            String newEmail = (String) updates.get("email");
            if (!existingUser.getEmail().equals(newEmail) &&
                    userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("User with this email already exists");
            }
        }

        applyPartialUpdates(existingUser, updates);

        try {
            User savedUser = userRepository.save(existingUser);
            UserNonSensitiveDTO userDto = userMapperService.toDto(savedUser);
            return new UserUpdateResponseDTO("true", "User updated successfully", userDto); // Changed to UserUpdateResponseDTO
        } catch (Exception e) {
            UserNonSensitiveDTO userDto = userMapperService.toDto(existingUser); // Consider if userDto should be null or existing here
            return new UserUpdateResponseDTO("false", "Update failed: " + e.getMessage(), userDto); // Changed to UserUpdateResponseDTO
        }
    }

    private void applyPartialUpdates(User user, Map<String, Object> updates) {
        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            switch (field) {
                case "firstName":
                    if (value instanceof String && ((String) value).length() >= 2 && ((String) value).length() <= 15) {
                        user.setFirstName((String) value);
                    } else {
                        throw new IllegalArgumentException("First name should be between 2 and 15 characters");
                    }
                    break;
                case "lastName":
                    if (value instanceof String && ((String) value).length() >= 2 && ((String) value).length() <= 15) {
                        user.setLastName((String) value);
                    } else {
                        throw new IllegalArgumentException("Last name should be between 2 and 15 characters");
                    }
                    break;
                case "email":
                    if (value instanceof String email) {
                        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                            throw new IllegalArgumentException("Invalid email format");
                        }
                        user.setEmail(email);
                    }
                    break;
                case "password":
                    if (value instanceof String password) {
                        if (password.length() < 8 || password.length() > 20 ||
                                !password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$")) {
                            throw new IllegalArgumentException("Password must contain at least 8 characters, one digit, one letter, one special character, and no spaces");
                        }
                        user.setPassword(passwordEncoder.encode(password));
                    }
                    break;
                case "bio":
                    if (value == null || (value instanceof String && ((String) value).length() <= 100)) {
                        user.setBio((String) value);
                    } else {
                        throw new IllegalArgumentException("Bio should be at most 100 characters");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Field '" + field + "' is not updatable");
            }
        }
    }

    public UserNonSensitiveDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return userMapperService.toDto(user);
    }
}

package org.app.authservice.service;

import org.app.authservice.dto.UserDTO;
import org.app.authservice.dto.UserNonSensitiveDTO;
import org.app.authservice.dto.UserResponseDTO;
import org.app.authservice.dto.UserUpdateDTO;
import org.app.authservice.entity.Role;
import org.app.authservice.entity.User;
import org.app.authservice.respository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserMapperService {
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public UserMapperService(ModelMapper modelMapper, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;

        modelMapper.createTypeMap(User.class, UserNonSensitiveDTO.class).addMappings(mapper -> mapper.skip(UserNonSensitiveDTO::setRole)).setPostConverter(context -> {
            User source = context.getSource();
            UserNonSensitiveDTO destination = context.getDestination();

            if (source.getRole() != null) {
                destination.setRole(source.getRole().getName());
            } else {
                destination.setRole("No role");
            }

            return destination;
        });

        modelMapper.createTypeMap(UserDTO.class, User.class).addMappings(mapper -> {
            mapper.skip(User::setRole);
            mapper.skip(User::setPassword);
        }).setPostConverter(context -> {
            UserDTO source = context.getSource();
            User destination = context.getDestination();

            if (source.getRole() != null) {
                Role role = roleRepository.findByName(source.getRole()).orElseThrow(() -> new IllegalArgumentException("Role not found: " + source.getRole()));
                destination.setRole(role);
            } else {
                throw new IllegalArgumentException("Role is required");
            }

            if (source.getPassword() != null) {
                destination.setPassword(passwordEncoder.encode(source.getPassword()));
            }

            return destination;
        });

    }

    public UserNonSensitiveDTO toDto(User user) {
        return modelMapper.map(user, UserNonSensitiveDTO.class);
    }


    public User toEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    public List<UserNonSensitiveDTO> toDtoList(List<User> users) {
        return users.stream().map(this::toDto).toList();
    }

    public UserResponseDTO toResponseDto(User user, String status, String message) {
        UserResponseDTO responseDTO = modelMapper.map(user, UserResponseDTO.class);
        responseDTO.setStatus(status);
        responseDTO.setMessage(message);
        return responseDTO;
    }

    public User mapForUpdate(UserUpdateDTO userDTO, User existingUser) {
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        User userCopy = new User();
        modelMapper.map(existingUser, userCopy);

        modelMapper.map(userDTO, userCopy);

        if (userDTO.getPassword() != null) {
            userCopy.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        return userCopy;
    }
}

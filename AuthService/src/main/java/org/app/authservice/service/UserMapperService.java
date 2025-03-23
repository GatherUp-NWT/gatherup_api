package org.app.authservice.service;

import org.app.authservice.dto.UserDTO;
import org.app.authservice.dto.UserListDTO;
import org.app.authservice.dto.UserResponseDTO;
import org.app.authservice.entity.Role;
import org.app.authservice.entity.User;
import org.app.authservice.entity.UserRole;
import org.app.authservice.respository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserMapperService {
    private final ModelMapper modelMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserMapperService(ModelMapper modelMapper, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

        modelMapper.createTypeMap(User.class, UserListDTO.class)
                .addMappings(mapper -> mapper.skip(UserListDTO::setRole))
                .setPostConverter(context -> {
                    User source = context.getSource();
                    UserListDTO destination = context.getDestination();

                    if (source.getUserRoles() != null && !source.getUserRoles().isEmpty()) {
                        String roleNames = source.getUserRoles().stream()
                                .map(userRole -> userRole.getRole().getName())
                                .collect(Collectors.joining(", "));
                        destination.setRole(roleNames);
                    } else {
                        destination.setRole("No roles");
                    }

                    return destination;
                });
    }

    public UserListDTO toDto(User user) {
        return modelMapper.map(user, UserListDTO.class);
    }

    public User toEntity(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        String roleName = userDTO.getRole() != null ? userDTO.getRole() : "USER";

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);

        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(userRole);
        user.setUserRoles(userRoles);

        return user;
    }

    public List<UserListDTO> toDtoList(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .toList();
    }

    public UserResponseDTO toResponseDto(User user, String status) {
        UserResponseDTO responseDTO = modelMapper.map(user, UserResponseDTO.class);
        responseDTO.setStatus(status);
        return responseDTO;
    }
}

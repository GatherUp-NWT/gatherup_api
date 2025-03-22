package org.app.authservice.controller;

import org.app.authservice.dto.UserDTO;
import org.app.authservice.dto.UserListDTO;
import org.app.authservice.dto.UserResponseDTO;
import org.app.authservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/api/v1/users")
    List<UserListDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/api/v1/users")
    public UserResponseDTO createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }
}

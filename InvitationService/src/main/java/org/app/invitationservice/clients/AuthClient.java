package org.app.invitationservice.clients;

import org.app.invitationservice.response.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthClient {
  @GetMapping("/users/{id}")
  UserDTO getUserById(@PathVariable String id);

  @GetMapping("/users/email/{email}")
    UserDTO getUserByEmail(@PathVariable String email);

}


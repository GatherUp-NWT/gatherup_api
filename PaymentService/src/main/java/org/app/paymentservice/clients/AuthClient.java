package org.app.paymentservice.clients;

import org.app.paymentservice.request.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthClient {
  @GetMapping("/users/{id}")
  UserDTO getUserById(@PathVariable String id);

}

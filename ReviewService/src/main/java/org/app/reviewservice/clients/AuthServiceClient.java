package org.app.reviewservice.clients;

import org.app.reviewservice.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service")
public interface AuthServiceClient {
    @GetMapping("/api/v1/users/{id}")
    UserDTO getUserById(@PathVariable String id);

}

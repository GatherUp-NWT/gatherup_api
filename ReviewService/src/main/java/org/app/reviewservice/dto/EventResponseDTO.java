package org.app.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventResponseDTO {
    private Boolean status;
    private String message;
    private EventDTO event;
}

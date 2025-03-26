package org.app.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.eventservice.entity.Event;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventObjectResponseDTO {
    private Boolean status;
    private String message;
    private Event event;
}

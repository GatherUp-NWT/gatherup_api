package org.app.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.eventservice.entity.Event;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventListResponseDTO {
    private Boolean status;
    private String message;
    private List<Event> events;
}

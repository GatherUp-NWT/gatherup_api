package org.app.eventservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.eventservice.entity.Event;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventListPagedDTO {
    private Boolean status;
    private String message;
    private Long totalElements;
    private Integer totalPages;
    private Integer pageNumber;
    private Integer pageSize;
    private List<Event> events;
}

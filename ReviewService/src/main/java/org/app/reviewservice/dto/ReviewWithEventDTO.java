package org.app.reviewservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewWithEventDTO extends ReviewDTO {
    private String eventName;
    private Instant eventStartDate;
    private String eventDescription;
}

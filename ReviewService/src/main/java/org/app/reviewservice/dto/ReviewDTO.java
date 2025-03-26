package org.app.reviewservice.dto;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.reviewservice.Rate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long id;
    private UUID userId;
    private UUID eventId;
    private String comment;
    private Timestamp timestamp;
    private Rate rate;
}

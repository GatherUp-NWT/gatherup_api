package org.app.reviewservice.dto;

import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.reviewservice.Rate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long id;

    @NotNull(message = "User ID cannot be null")
    private UUID userId;

    @NotNull(message = "Event ID cannot be null")
    private UUID eventId;

    @NotBlank(message = "Comment is required")
    @Size(max = 255, message = "Comment must be at most 255 characters")
    private String comment;

    private Timestamp timestamp;

    @NotNull(message = "Rate is required")
    private Rate rate;
}

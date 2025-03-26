package org.app.reviewservice.mapper;

import org.app.reviewservice.dto.ReviewDTO;
import org.app.reviewservice.entity.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    ReviewDTO toDTO(Review review);
    Review toEntity(ReviewDTO reviewDTO);
}

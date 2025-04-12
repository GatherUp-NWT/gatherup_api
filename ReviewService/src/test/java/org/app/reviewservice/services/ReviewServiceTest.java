package org.app.reviewservice.services;

import org.app.reviewservice.Rate;
import org.app.reviewservice.dto.ReviewDTO;
import org.app.reviewservice.entity.Review;
import org.app.reviewservice.mapper.ReviewMapper;
import org.app.reviewservice.repository.ReviewRepository;
import org.app.reviewservice.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewMapper reviewMapper;

    private Review review;
    private ReviewDTO reviewDTO;

    @BeforeEach
    void setUp() {
        long reviewId = 1L;
        UUID eventId = UUID.fromString("aa49e083-777a-4627-8c87-8427b98e5d60");
        UUID userId = UUID.fromString("bb44e083-666a-2327-8c87-2227b98e5d60");

        review = new Review();
        review.setId(reviewId);
        review.setComment("Great event!");
        review.setRate(Rate.EXCEPTIONAL);
        review.setEventId(eventId);
        review.setUserId(userId);

        reviewDTO = new ReviewDTO();
        reviewDTO.setId(reviewId);
        reviewDTO.setComment("Great event!");
        reviewDTO.setRate(Rate.EXCEPTIONAL);
        reviewDTO.setEventId(eventId);
        reviewDTO.setUserId(userId);
        when(reviewMapper.toDTO(any(Review.class))).thenReturn(reviewDTO);
    }

    @Test
    void getReviewById_ShouldReturnReview() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        ReviewDTO foundReview = reviewService.getReviewById(1L);

        assertNotNull(foundReview);
        assertEquals(reviewDTO.getComment(), foundReview.getComment());
    }
}

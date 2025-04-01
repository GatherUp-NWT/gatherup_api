package org.app.reviewservice.service;

import jakarta.validation.Valid;
import org.app.reviewservice.dto.ReviewDTO;
import org.app.reviewservice.entity.Review;
import org.app.reviewservice.exception.ConflictException;
import org.app.reviewservice.mapper.ReviewMapper;
import org.app.reviewservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Validated
@Service
public class ReviewService {
  private final ReviewRepository reviewRepository;
  private final ReviewMapper reviewMapper;

    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
    }

    public List<ReviewDTO> getAllReviews() {
    return reviewRepository.findAll().stream()
            .map(reviewMapper::toDTO)
            .toList();
  }

  public ReviewDTO getReviewById(Long id) {
    return reviewRepository.findById(id)
            .map(reviewMapper::toDTO)
            .orElseThrow(() -> new NoSuchElementException("Review with ID " + id + " not found"));
  }

  public ReviewDTO saveReview(@Valid ReviewDTO reviewDTO) {
    boolean exists = reviewRepository.existsByUserIdAndEventId(reviewDTO.getUserId(), reviewDTO.getEventId());

    if (exists) {
      throw new ConflictException("User has already submitted a review for this event");
    }

    Review review = reviewMapper.toEntity(reviewDTO);
    review.setTimestamp(Timestamp.valueOf(LocalDateTime.now())); // Set current timestamp
    return reviewMapper.toDTO(reviewRepository.save(review));
  }

  public void deleteReview(Long id) {
    if (!reviewRepository.existsById(id)) {
      throw new NoSuchElementException("Review with ID " + id + " does not exist");
    }
    reviewRepository.deleteById(id);
  }
}
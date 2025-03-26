package org.app.reviewservice.service;

import lombok.RequiredArgsConstructor;
import org.app.reviewservice.dto.ReviewDTO;
import org.app.reviewservice.entity.Review;
import org.app.reviewservice.mapper.ReviewMapper;
import org.app.reviewservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

  public Optional<ReviewDTO> getReviewById(Long id) {
    return reviewRepository.findById(id)
            .map(reviewMapper::toDTO);
  }

  public ReviewDTO saveReview(ReviewDTO reviewDTO) {
    Review review = reviewMapper.toEntity(reviewDTO);
    review.setTimestamp(Timestamp.valueOf(LocalDateTime.now())); // Set current timestamp
    return reviewMapper.toDTO(reviewRepository.save(review));
  }

  public void deleteReview(Long id) {
    reviewRepository.deleteById(id);
  }
}
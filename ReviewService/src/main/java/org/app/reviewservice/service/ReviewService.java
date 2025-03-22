package org.app.reviewservice.service;



import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.app.reviewservice.Rate;
import org.app.reviewservice.entity.Review;
import org.app.reviewservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

  private final ReviewRepository reviewRepository;

  @PostConstruct
  public void initDatabase() {
    if (reviewRepository.count() == 0) { // Prevents duplicate inserts


      Review review = new Review();
      review.setUserId(UUID.fromString("13004255-59fa-4df6-9ab8-34e40f7058bf"));
      review.setEventId(UUID.fromString("df77cdec-e4d0-488e-99fe-210a4cada331"));
      review.setComment("Great event, very informative and well organized.");

      review.setRate(Rate.VERY_GOOD);

      reviewRepository.save(review);

      System.out.println(" Review data populated!");
    }
  }
}


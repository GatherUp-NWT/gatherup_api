package org.app.reviewservice.service;

import jakarta.validation.Valid;
import org.app.reviewservice.clients.AuthServiceClient;
import org.app.reviewservice.clients.EventServiceClient;
import org.app.reviewservice.dto.*;
import org.app.reviewservice.entity.Review;
import org.app.reviewservice.exception.ConflictException;
import org.app.reviewservice.mapper.ReviewMapper;
import org.app.reviewservice.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Validated
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final AuthServiceClient authServiceClient;
    private final EventServiceClient eventServiceClient;

    public ReviewService(ReviewRepository reviewRepository, ReviewMapper reviewMapper, AuthServiceClient authServiceClient, EventServiceClient eventServiceClient) {
        this.reviewRepository = reviewRepository;
        this.reviewMapper = reviewMapper;
        this.authServiceClient = authServiceClient;
        this.eventServiceClient = eventServiceClient;

    }

    public Page<ReviewDTO> getReviews(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction direction = Sort.Direction.ASC;

        if ("desc".equalsIgnoreCase(sortOrder)) {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Review> reviewPage = reviewRepository.findAll(pageable);

        return reviewPage.map(review -> new ReviewDTO(
                review.getId(),
                review.getUserId(),
                review.getEventId(),
                review.getComment(),
                review.getTimestamp(),
                review.getRate()
        ));
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

    public ReviewDTO patchReview(Long id, ReviewDTO reviewDTO) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found"));

        if (reviewDTO.getComment() != null) {
            existingReview.setComment(reviewDTO.getComment());
        }

        if (reviewDTO.getRate() != null) {
            existingReview.setRate(reviewDTO.getRate());
        }

        if (reviewDTO.getEventId() != null) {
            existingReview.setEventId(reviewDTO.getEventId());
        }
        if (reviewDTO.getUserId() != null) {
            existingReview.setUserId(reviewDTO.getUserId());
        }

        Review updatedReview = reviewRepository.save(existingReview);
        return reviewMapper.toDTO(updatedReview);
    }

    public List<ReviewDTO> getReviewsByUser(UUID userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getReviewsByEvent(UUID eventId) {
        return reviewRepository.findByEventId(eventId)
                .stream()
                .map(reviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewWithUserDTO> getReviewsByEventWithUserDetails(UUID eventId) {
        List<ReviewDTO> reviews = getReviewsByEvent(eventId);

        return reviews.stream()
                .map(review -> {
                    ReviewWithUserDTO reviewWithUser = new ReviewWithUserDTO();
                    reviewWithUser.setId(review.getId());
                    reviewWithUser.setUserId(review.getUserId());
                    reviewWithUser.setEventId(review.getEventId());
                    reviewWithUser.setComment(review.getComment());
                    reviewWithUser.setTimestamp(review.getTimestamp());
                    reviewWithUser.setRate(review.getRate());

                    try {
                        UserDTO user = authServiceClient.getUserById(review.getUserId().toString());
                        reviewWithUser.setUserFirstName(user.getFirstName());
                        reviewWithUser.setUserLastName(user.getLastName());
                    } catch (Exception e) {
                        reviewWithUser.setUserFirstName("Unknown");
                        reviewWithUser.setUserLastName("User");
                    }

                    return reviewWithUser;
                })
                .collect(Collectors.toList());
    }

    public List<ReviewWithEventDTO> getReviewsByUserWithEventDetails(UUID userId) {
        List<ReviewDTO> reviews = getReviewsByUser(userId);

        return reviews.stream()
                .map(review -> {
                    ReviewWithEventDTO reviewWithEvent = new ReviewWithEventDTO();
                    reviewWithEvent.setId(review.getId());
                    reviewWithEvent.setUserId(review.getUserId());
                    reviewWithEvent.setEventId(review.getEventId());
                    reviewWithEvent.setComment(review.getComment());
                    reviewWithEvent.setTimestamp(review.getTimestamp());
                    reviewWithEvent.setRate(review.getRate());

                    try {
                        EventResponseDTO eventResponse = eventServiceClient.getEventById(review.getEventId().toString());
                        if (eventResponse.getStatus() && eventResponse.getEvent() != null) {
                            EventDTO event = eventResponse.getEvent();
                            reviewWithEvent.setEventName(event.getName());
                            reviewWithEvent.setEventStartDate(event.getStartDate());
                            reviewWithEvent.setEventDescription(event.getDescription());
                        }
                    } catch (Exception e) {
                        reviewWithEvent.setEventName("Unknown Event");
                        reviewWithEvent.setEventDescription("No description available");
                    }

                    return reviewWithEvent;
                })
                .collect(Collectors.toList());
    }
}
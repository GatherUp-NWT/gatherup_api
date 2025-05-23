package org.app.reviewservice.controller;

import jakarta.validation.Valid;
import org.app.reviewservice.dto.ReviewDTO;
import org.app.reviewservice.dto.ReviewWithEventDTO;
import org.app.reviewservice.dto.ReviewWithUserDTO;
import org.app.reviewservice.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewDTO> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @PostMapping
    public ReviewDTO createReview(@Valid @RequestBody ReviewDTO reviewDTO) {
        return reviewService.saveReview(reviewDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReviewDTO> patchReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDTO) {
        ReviewDTO updatedReview = reviewService.patchReview(id, reviewDTO);
        return ResponseEntity.ok(updatedReview);
    }

    @GetMapping("/user/{userId}")
    public List<ReviewWithEventDTO> getReviewsByUser(@PathVariable UUID userId) {
        return reviewService.getReviewsByUserWithEventDetails(userId);
    }



    @GetMapping("/event/{eventId}")
    public List<ReviewWithUserDTO> getReviewsByEvent(@PathVariable UUID eventId) {
        return reviewService.getReviewsByEventWithUserDetails(eventId);
    }

    @GetMapping("/sorted/")
    public Page<ReviewDTO> getReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {

        return reviewService.getReviews(page, size, sortBy, sortOrder);
    }
}





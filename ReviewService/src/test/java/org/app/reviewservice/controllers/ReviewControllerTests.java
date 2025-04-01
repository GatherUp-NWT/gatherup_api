package org.app.reviewservice.controllers;

import org.app.reviewservice.Rate;
import org.app.reviewservice.controller.ReviewController;
import org.app.reviewservice.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.reviewservice.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({ReviewController.class, org.app.reviewservice.exception.GlobalExceptionHandler.class})
public class ReviewControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewService reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReviewDTO reviewDTO;
    private List<ReviewDTO> reviewList;
    private Long reviewId;

    @BeforeEach
    void setUp() {
        reviewId = 1L;

        reviewDTO = new ReviewDTO();
        reviewDTO.setId(reviewId);
        reviewDTO.setComment("Great event!");
        reviewDTO.setRate(Rate.EXCEPTIONAL);
        reviewDTO.setEventId(UUID.fromString("aa49e083-777a-4627-8c87-8427b98e5d60"));
        reviewDTO.setUserId(UUID.fromString("bb44e083-666a-2327-8c87-2227b98e5d60"));
        reviewList = new ArrayList<>();
        reviewList.add(reviewDTO);
    }

    @Test
    void getAllReviews_ShouldReturnAllReviews() throws Exception {
        when(reviewService.getAllReviews()).thenReturn(reviewList);

        mockMvc.perform(get("/api/v1/reviews"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(reviewId));

        verify(reviewService).getAllReviews();
    }

    @Test
    void getReviewById_ShouldReturnReview() throws Exception {
        when(reviewService.getReviewById(reviewId)).thenReturn(reviewDTO);

        mockMvc.perform(get("/api/v1/reviews/{id}", reviewId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(reviewId));

        verify(reviewService).getReviewById(reviewId);
    }

    @Test
    void createReview_ShouldReturnCreatedReview() throws Exception {
        when(reviewService.saveReview(any(ReviewDTO.class))).thenReturn(reviewDTO);

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(reviewId));

        verify(reviewService).saveReview(any(ReviewDTO.class));
    }

    @Test
    void deleteReview_ShouldReturnNoContent() throws Exception {
        doNothing().when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/api/v1/reviews/{id}", reviewId))
                .andExpect(status().isNoContent());

        verify(reviewService).deleteReview(reviewId);
    }

    @Test
    void patchReview_ShouldReturnUpdatedReview() throws Exception {
        Long reviewId = 1L;
        ReviewDTO updatedReview = new ReviewDTO();
        updatedReview.setId(reviewId);
        updatedReview.setUserId(UUID.randomUUID());
        updatedReview.setEventId(UUID.randomUUID());
        updatedReview.setRate(Rate.EXCEPTIONAL);
        updatedReview.setComment("Great event!");

        // Mock the service to return the updated review
        when(reviewService.patchReview(eq(reviewId), any(ReviewDTO.class)))
                .thenReturn(updatedReview);

        // Perform the PATCH request
        mockMvc.perform(patch("/api/v1/reviews/{id}", reviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedReview)))
                .andExpect(status().isOk())  // Expect 200 OK status
                .andExpect(jsonPath("$.id").value(updatedReview.getId()))
                .andExpect(jsonPath("$.userId").value(updatedReview.getUserId().toString()))
                .andExpect(jsonPath("$.eventId").value(updatedReview.getEventId().toString()))
                .andExpect(jsonPath("$.comment").value(updatedReview.getComment()));

        // Verify that the service method was called with the correct arguments
        verify(reviewService).patchReview(eq(reviewId), any(ReviewDTO.class));
    }

    @Test
    void patchReview_ShouldReturnNotFound() throws Exception {
        Long nonExistentReviewId = 999L;
        ReviewDTO updatedReview = new ReviewDTO();
        updatedReview.setId(nonExistentReviewId);
        updatedReview.setUserId(UUID.randomUUID());
        updatedReview.setEventId(UUID.randomUUID());
        updatedReview.setRate(Rate.EXCEPTIONAL);
        updatedReview.setComment("Good event.");

        // Mock the service to throw NoSuchElementException when the review is not found
        when(reviewService.patchReview(eq(nonExistentReviewId), any(ReviewDTO.class)))
                .thenThrow(new NoSuchElementException("Review not found"));

        // Perform the PATCH request with the non-existent review ID
        mockMvc.perform(patch("/api/v1/reviews/{id}", nonExistentReviewId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedReview)))
                .andExpect(status().isNotFound());  // Expect 404 Not Found status

        // Verify that the service method was called with the correct arguments
        verify(reviewService).patchReview(eq(nonExistentReviewId), any(ReviewDTO.class));
    }

    @Test
    void getReviewsByUser_ShouldReturnReviews() throws Exception {
        UUID userId = UUID.randomUUID();
        ReviewDTO review1 = new ReviewDTO();
        review1.setId(1L);
        review1.setUserId(userId);
        review1.setEventId(UUID.randomUUID());
        review1.setRate(Rate.EXCEPTIONAL);
        review1.setComment("Nice event!");

        ReviewDTO review2 = new ReviewDTO();
        review2.setId(2L);
        review2.setUserId(userId);
        review2.setEventId(UUID.randomUUID());
        review2.setRate(Rate.EXCEPTIONAL);
        review2.setComment("Amazing event!");

        List<ReviewDTO> reviews = Arrays.asList(review1, review2);

        // Mock the service to return the reviews for the user
        when(reviewService.getReviewsByUser(eq(userId)))
                .thenReturn(reviews);

        // Perform the GET request
        mockMvc.perform(get("/api/v1/reviews/user/{userId}", userId))
                .andExpect(status().isOk())  // Expect 200 OK status
                .andExpect(jsonPath("$[0].id").value(review1.getId()))
                .andExpect(jsonPath("$[1].id").value(review2.getId()))
                .andExpect(jsonPath("$[0].userId").value(userId.toString()))
                .andExpect(jsonPath("$[1].userId").value(userId.toString()));

        // Verify that the service method was called with the correct arguments
        verify(reviewService).getReviewsByUser(eq(userId));
    }

    @Test
    void getReviewsByEvent_ShouldReturnReviews() throws Exception {
        UUID eventId = UUID.randomUUID();
        ReviewDTO review1 = new ReviewDTO();
        review1.setId(1L);
        review1.setUserId(UUID.randomUUID());
        review1.setEventId(eventId);
        review1.setRate(Rate.EXCEPTIONAL);
        review1.setComment("Nice event!");

        ReviewDTO review2 = new ReviewDTO();
        review2.setId(2L);
        review2.setUserId(UUID.randomUUID());
        review2.setEventId(eventId);
        review2.setRate(Rate.EXCEPTIONAL);
        review2.setComment("Amazing event!");

        List<ReviewDTO> reviews = Arrays.asList(review1, review2);

        // Mock the service to return the reviews for the event
        when(reviewService.getReviewsByEvent(eq(eventId)))
                .thenReturn(reviews);

        // Perform the GET request
        mockMvc.perform(get("/api/v1/reviews/event/{eventId}", eventId))
                .andExpect(status().isOk())  // Expect 200 OK status
                .andExpect(jsonPath("$[0].id").value(review1.getId()))
                .andExpect(jsonPath("$[1].id").value(review2.getId()))
                .andExpect(jsonPath("$[0].eventId").value(eventId.toString()))
                .andExpect(jsonPath("$[1].eventId").value(eventId.toString()));

        // Verify that the service method was called with the correct arguments
        verify(reviewService).getReviewsByEvent(eq(eventId));
    }

    @Test
    void createReview_ShouldReturnBadRequest_WhenUserIdIsNull() throws Exception {
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setUserId(null);
        reviewDTO.setEventId(UUID.randomUUID());
        reviewDTO.setComment("Nice event!");
        reviewDTO.setRate(Rate.BAD);

        // Perform the POST request with invalid userId
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest());  // Expect 400 Bad Request status

        // Verify that the service method was not called
        verify(reviewService, never()).saveReview(any(ReviewDTO.class));
    }

    @Test
    void createReview_ShouldReturnBadRequest_WhenEventIdIsNull() throws Exception {
        // ReviewDTO with null eventId (which is invalid based on the validation constraints)
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setUserId(UUID.randomUUID());
        reviewDTO.setEventId(null);  // Invalid because eventId is @NotNull
        reviewDTO.setComment("Nice event!");
        reviewDTO.setRate(Rate.BAD);

        // Perform the POST request with invalid eventId
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest());  // Expect 400 Bad Request status

        // Verify that the service method was not called
        verify(reviewService, never()).saveReview(any(ReviewDTO.class));
    }

    @Test
    void createReview_ShouldReturnBadRequest_WhenCommentIsBlank() throws Exception {
        // ReviewDTO with empty comment (which is invalid based on the validation constraints)
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setUserId(UUID.randomUUID());
        reviewDTO.setEventId(UUID.randomUUID());
        reviewDTO.setComment("");  // Invalid because comment is @NotBlank
        reviewDTO.setRate(Rate.NOT_BAD);

        // Perform the POST request with invalid comment
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest());  // Expect 400 Bad Request status

        // Verify that the service method was not called
        verify(reviewService, never()).saveReview(any(ReviewDTO.class));
    }

    @Test
    void createReview_ShouldReturnBadRequest_WhenRateIsNull() throws Exception {
        // ReviewDTO with null rate (which is invalid based on the validation constraints)
        ReviewDTO reviewDTO = new ReviewDTO();
        reviewDTO.setUserId(UUID.randomUUID());
        reviewDTO.setEventId(UUID.randomUUID());
        reviewDTO.setComment("Amazing event!");
        reviewDTO.setRate(null);  // Invalid because rate is @NotNull

        // Perform the POST request with invalid rate
        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(reviewDTO)))
                .andExpect(status().isBadRequest());  // Expect 400 Bad Request status

        // Verify that the service method was not called
        verify(reviewService, never()).saveReview(any(ReviewDTO.class));
    }

    @Test
    void deleteReview_ShouldReturnNotFound() throws Exception {
        doThrow(new NoSuchElementException("Review not found")).when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/api/v1/reviews/{id}", reviewId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Review not found"));

        verify(reviewService).deleteReview(reviewId);
    }
}


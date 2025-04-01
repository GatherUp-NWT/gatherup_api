package org.app.reviewservice.controllers;

import org.app.reviewservice.Rate;
import org.app.reviewservice.controller.ReviewController;
import org.app.reviewservice.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
}


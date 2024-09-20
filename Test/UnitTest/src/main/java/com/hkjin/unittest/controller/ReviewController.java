package com.hkjin.unittest.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hkjin.unittest.dto.ReviewDto;
import com.hkjin.unittest.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ReviewController {
	private final ReviewService reviewService;

	@PostMapping("/pokemon/{pokemonId}/reviews")
	public ResponseEntity<ReviewDto> createReview(@PathVariable(value = "pokemonId") long pokemonId, @RequestBody ReviewDto reviewDto) {
		return new ResponseEntity<>(reviewService.createReview(pokemonId, reviewDto), HttpStatus.CREATED);
	}

	@GetMapping("/pokemon/{pokemonId}/reviews")
	public List<ReviewDto> getReviewsByPokemonId(@PathVariable(value = "pokemonId") long pokemonId) {
		return reviewService.getReviewsByPokemonId(pokemonId);
	}

	@GetMapping("/pokemon/{pokemonId}/reviews/{id}")
	public ResponseEntity<ReviewDto> getReviewById(@PathVariable(value = "pokemonId") long pokemonId, @PathVariable(value = "id") long reviewId) {
		ReviewDto reviewDto = reviewService.getReviewById(pokemonId, reviewId);
		return new ResponseEntity<>(reviewDto, HttpStatus.OK);
	}

	@PutMapping("/pokemon/{pokemonId}/reviews/{id}")
	public ResponseEntity<ReviewDto> updateReview(@PathVariable(value = "pokemonId") long pokemonId, @PathVariable(value = "id") long reviewId,
		@RequestBody ReviewDto reviewDto) {
		ReviewDto updatedReview = reviewService.updateReview(pokemonId, reviewId, reviewDto);
		return new ResponseEntity<>(updatedReview, HttpStatus.OK);
	}

	@DeleteMapping("/pokemon/{pokemonId}/reviews/{id}")
	public ResponseEntity<String> deleteReview(@PathVariable(value = "pokemonId") long pokemonId, @PathVariable(value = "id") long reviewId) {
		reviewService.deleteReview(pokemonId, reviewId);
		return new ResponseEntity<>("Review deleted successfully", HttpStatus.OK);
	}
}

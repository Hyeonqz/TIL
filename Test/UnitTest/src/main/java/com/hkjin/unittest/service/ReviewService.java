package com.hkjin.unittest.service;

import java.util.List;

import com.hkjin.unittest.dto.ReviewDto;

public interface ReviewService {
	ReviewDto createReview(Long pokemonId, ReviewDto reviewDto);
	List<ReviewDto> getReviewsByPokemonId(Long id);
	ReviewDto getReviewById(Long reviewId, Long pokemonId);
	ReviewDto updateReview(Long pokemonId, Long reviewId, ReviewDto reviewDto);
	void deleteReview(Long pokemonId, Long reviewId);
}

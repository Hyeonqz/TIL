package com.hkjin.unittest.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hkjin.unittest.dto.ReviewDto;
import com.hkjin.unittest.entity.Pokemon;
import com.hkjin.unittest.entity.Review;
import com.hkjin.unittest.exception.PokemonNotFoundException;
import com.hkjin.unittest.exception.ReviewNotFoundException;
import com.hkjin.unittest.repository.PokemonRepository;
import com.hkjin.unittest.repository.ReviewRepository;
import com.hkjin.unittest.service.ReviewService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewServiceImpl implements ReviewService {
	private final ReviewRepository reviewRepository;
	private final PokemonRepository pokemonRepository;

	@Override
	public ReviewDto createReview (Long pokemonId, ReviewDto reviewDto) {
		Review review = mapToEntity(reviewDto);

		Pokemon pokemon = pokemonRepository.findById(pokemonId)
			.orElseThrow(() -> new PokemonNotFoundException("Pokemon with Associated review not found"));

		review.associatedWithPokemon(pokemon);

		return mapToDto(review);
	}

	@Override
	public List<ReviewDto> getReviewsByPokemonId (Long id) {
		List<Review> byPokemonId = reviewRepository.findByPokemonId(id);
		return byPokemonId.stream().map(this::mapToDto).toList();
	}

	@Override
	public ReviewDto getReviewById (Long reviewId, Long pokemonId) {
		Pokemon pokemon = pokemonRepository.findById(pokemonId)
			.orElseThrow(() -> new PokemonNotFoundException("Pokemon with Associated review not found"));

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewNotFoundException("Review Not Found"));

		if(!Objects.equals(review.getId(), pokemon.getId()))
			throw new ReviewNotFoundException("Review Not Found");

		return mapToDto(review);
	}

	@Override
	public ReviewDto updateReview (Long pokemonId, Long reviewId, ReviewDto reviewDto) {
		Pokemon pokemon = pokemonRepository.findById(pokemonId)
			.orElseThrow(() -> new PokemonNotFoundException("Pokemon with Associated review not found"));

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewNotFoundException("Review Not Found"));

		if(!Objects.equals(review.getId(), pokemon.getId()))
			throw new ReviewNotFoundException("This Review does not belong to pokemon");

		review.updateReview(reviewDto.title(), reviewDto.content(), reviewDto.stars());

		return mapToDto(review);
	}

	@Override
	public void deleteReview (Long pokemonId, Long reviewId) {
		Pokemon pokemon = pokemonRepository.findById(pokemonId)
			.orElseThrow(() -> new PokemonNotFoundException("Pokemon with Associated review not found"));

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewNotFoundException("Review Not Found"));

		if(!Objects.equals(review.getId(), pokemon.getId()))
			throw new ReviewNotFoundException("This Review does not belong to pokemon");

		reviewRepository.delete(review);

	}

	private Review mapToEntity (ReviewDto reviewDto) {
		return Review.builder()
			.id(reviewDto.id())
			.title(reviewDto.title())
			.content(reviewDto.content())
			.stars(reviewDto.stars())
			.build();
	}

	private ReviewDto mapToDto (Review review) {
		return ReviewDto.builder()
			.id(review.getId())
			.content(review.getContent())
			.title(review.getTitle())
			.stars(review.getStars())
			.build();
	}

}

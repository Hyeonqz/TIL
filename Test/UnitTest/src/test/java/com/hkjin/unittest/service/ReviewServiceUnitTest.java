package com.hkjin.unittest.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hkjin.unittest.dto.PokemonDto;
import com.hkjin.unittest.dto.ReviewDto;
import com.hkjin.unittest.entity.Pokemon;
import com.hkjin.unittest.entity.Review;
import com.hkjin.unittest.repository.PokemonRepository;
import com.hkjin.unittest.repository.ReviewRepository;
import com.hkjin.unittest.service.impl.ReviewServiceImpl;

@ExtendWith(MockitoExtension.class) // Mockito 확장
public class ReviewServiceUnitTest {

	@Mock
	private ReviewRepository reviewRepository;
	@Mock
	private PokemonRepository pokemonRepository;

	@InjectMocks
	private ReviewServiceImpl reviewService;

	private Pokemon pokemon;
	private Review review;
	private ReviewDto reviewDto;
	private PokemonDto pokemonDto;

	@BeforeEach
	public void setup() {
		pokemon = Pokemon.builder().name("라이츄").type("강화전기").build();
		pokemonDto = PokemonDto.builder().name("라이츄").type("강화전기").build();

		review = Review.builder().title("무슨제목?").content("무슨내용?").stars(0).build();
		reviewDto = ReviewDto.builder().title("무슨제목?").content("무슨내용?").stars(0).build();
	}

	@Test
	void ReviewService_CreateReview_ReturnsReviewDTO() {
		Mockito.when(pokemonRepository.findById(pokemon.getId())).thenReturn(Optional.of(pokemon));
		Mockito.lenient().when(reviewRepository.save(Mockito.any(Review.class))).thenReturn(review);

		ReviewDto savedReview = reviewService.createReview(pokemon.getId(), reviewDto);

		Assertions.assertThat(savedReview).isNotNull();
	}

	@Test
	void ReviewService_GetReviewByPokemonId_ReturnsReviewDTO() {
	    // given
		long reviewId = 1L;

	    // when
		Mockito.when(reviewRepository.findByPokemonId(reviewId)).thenReturn(Collections.singletonList(review));

		List<ReviewDto> pokemonReturn = reviewService.getReviewsByPokemonId(reviewId);

	    // then
		Assertions.assertThat(pokemonReturn).isNotNull();
	}

	@Test
	void ReviewService_GetReviewById_ReturnReviewDTO() {
		// given
		long reviewId = 1L;
		long pokemonId = 1L;

		review.associatedWithPokemon(pokemon);

	    // when
		Mockito.when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.of(pokemon));
		Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

		ReviewDto reviewReturn = reviewService.getReviewById(reviewId,pokemonId);

		// then
		Assertions.assertThat(reviewReturn).isNotNull();
		Assertions.assertThat(reviewReturn).isNotNull();
	}

	@Test
	void ReviewService_UpdatePokemon_ReturnReviewDTO() {
	    // given
		long pokemonId = 1L;
		long reviewId = 1L;

		pokemon.addReview(review);
		review.associatedWithPokemon(pokemon);

	    // when
		Mockito.when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.of(pokemon));
		Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		Mockito.lenient().when(reviewRepository.save(review)).thenReturn(review);

		ReviewDto updateReturn = reviewService.updateReview(pokemonId,reviewId,reviewDto);

	    // then
		Assertions.assertThat(updateReturn).isNotNull();
	}

	@Test
	void ReviewService_DeleteReviewByPokemonId_ReturnReviewDTO() {
	    // given
		long pokemonId = 1L;
		long reviewId = 1L;

	    // when
		Mockito.when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
		Mockito.when(pokemonRepository.findById(pokemonId)).thenReturn(Optional.of(pokemon));

		// then
		org.junit.jupiter.api.Assertions.assertAll(() -> reviewService.deleteReview(pokemonId,reviewId));
	}



















}

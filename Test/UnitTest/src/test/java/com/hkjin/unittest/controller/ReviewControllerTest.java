package com.hkjin.unittest.controller;

import java.util.Collections;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkjin.unittest.dto.PokemonDto;
import com.hkjin.unittest.dto.ReviewDto;
import com.hkjin.unittest.entity.Pokemon;
import com.hkjin.unittest.entity.Review;
import com.hkjin.unittest.service.ReviewService;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ReviewController.class)
class ReviewControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ReviewService reviewService;

	@Autowired
	private ObjectMapper objectMapper;

	private Pokemon pokemon;
	private Review review;
	private ReviewDto reviewDto;
	private PokemonDto pokemonDto;

	@BeforeEach
	public void setup () {
		pokemon = Pokemon.builder().name("라이츄").type("강화전기").build();
		pokemonDto = PokemonDto.builder().name("라이츄").type("강화전기").build();

		review = Review.builder().title("무슨제목?").content("무슨내용?").stars(0).build();
		reviewDto = ReviewDto.builder().title("무슨제목?").content("무슨내용?").stars(0).build();
	}


	@Test
	void ReviewController_GetReviewsByPokemonId_ReturnReviewDTO() throws Exception {
		long pokemonID = 1L;

		Mockito.when(reviewService.getReviewsByPokemonId(pokemonID)).thenReturn(Collections.singletonList(reviewDto));

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon/1/reviews")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(pokemonDto)));

		resultActions.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.size()",CoreMatchers.is(Collections.singletonList(reviewDto).size())));
	}

	@Test
	void ReviewController_ReviewUpdate_ReturnReviewDTO() throws Exception {
		long pokemonID = 1L;
		long reviewID = 1L;

		Mockito.when(reviewService.updateReview(pokemonID,reviewID,reviewDto)).thenReturn(reviewDto);

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/pokemon/1/reviews/1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(reviewDto)));

		resultActions.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(reviewDto.title())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(reviewDto.content())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.stars", CoreMatchers.is(reviewDto.stars())));
	}

	@Test
	void ReviewController_CreateReview_ReturnCreated () throws Exception {
		// given
		long pokemonID = 1L;

		Mockito.when(reviewService.createReview(pokemonID, reviewDto)).thenReturn(reviewDto);

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/pokemon/1/reviews")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(reviewDto)));

		resultActions.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(reviewDto.title())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(reviewDto.content())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.stars", CoreMatchers.is(reviewDto.stars())))
		;
	}

	@Test
	public void ReviewController_GetReviewId_ReturnReviewDto() throws Exception {
		long pokemonId = 1;
		long reviewId = 1;
		Mockito.when(reviewService.getReviewById(reviewId, pokemonId)).thenReturn(reviewDto);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon/1/reviews/1")
			.contentType(MediaType.APPLICATION_JSON));

		response.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.title", CoreMatchers.is(reviewDto.title())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.content", CoreMatchers.is(reviewDto.content())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.stars", CoreMatchers.is(reviewDto.stars())));
	}

	@Test
	public void ReviewController_DeleteReview_ReturnOk() throws Exception {
		long pokemonId = 1;
		long reviewId = 1;

		Mockito.doNothing().when(reviewService).deleteReview(pokemonId, reviewId);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.delete("/api/pokemon/1/reviews/1")
			.contentType(MediaType.APPLICATION_JSON));

		response.andExpect(MockMvcResultMatchers.status().isOk());
	}

}
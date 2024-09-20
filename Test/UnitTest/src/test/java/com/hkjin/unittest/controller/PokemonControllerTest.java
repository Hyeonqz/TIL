package com.hkjin.unittest.controller;

import java.util.Collections;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hkjin.unittest.dto.PokemonDto;
import com.hkjin.unittest.dto.PokemonPageableResponse;
import com.hkjin.unittest.dto.ReviewDto;
import com.hkjin.unittest.entity.Pokemon;
import com.hkjin.unittest.entity.Review;
import com.hkjin.unittest.service.PokemonService;

@WebMvcTest(controllers = PokemonController.class) // controller 를 테스트하기 위한 어노테이션
@AutoConfigureMockMvc(addFilters = false) // 자동 mock 추가, Spring Security 우회 하기 위한 설정
@ExtendWith(MockitoExtension.class) // Mockito 확장
class PokemonControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PokemonService pokemonService;

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
	void PokemonController_CreatePokemon_Return_ () throws Exception {
		// given
		BDDMockito.given(pokemonService.createPokemon(ArgumentMatchers.any()))
			.willAnswer(invocation -> invocation.getArgument(0));

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/api/pokemon/create")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(pokemonDto)));

		resultActions.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(pokemonDto.name())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.type", CoreMatchers.is(pokemonDto.type())))
		;
	}

	@Test
	void pokemonController_GetAllPokemon_ReturnResponseDTO () throws Exception {
		// given
		PokemonPageableResponse response = PokemonPageableResponse.builder()
			.pageSize(10)
			.pageNo(1)
			.last(true)
			.content(Collections.singletonList(pokemonDto))
			.build();

		// when
		Mockito.when(pokemonService.getAllPokemon(1, 10)).thenReturn(response);

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon")
			.contentType(MediaType.APPLICATION_JSON)
			.param("pageNo", "1")
			.param("pageSize", "10"));

		// then
		resultActions.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(response.content().size())));
		System.out.println("/api/pokemon/페이징" + "테스트 성공");
	}

	@Test
	void PokemonController_Pokemon_ReturnPokemonDTO() throws Exception {
		long pokemonID = 1L;
		Mockito.when(pokemonService.getPokemonById(1L)).thenReturn(pokemonDto);

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/pokemon/1")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(pokemonDto)));

		resultActions.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(pokemonDto.name())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.type", CoreMatchers.is(pokemonDto.type())));
	}

	@Test
	void PokemonController_PokemonUpdate_ReturnPokemonDTO() throws Exception {
		long pokemonID = 1L;
		Mockito.when(pokemonService.updatePokemon(pokemonDto,pokemonID)).thenReturn(pokemonDto);

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/pokemon/1/update")
			.contentType(MediaType.APPLICATION_JSON)
			.content(objectMapper.writeValueAsString(pokemonDto)));

		resultActions.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(pokemonDto.name())))
			.andExpect(MockMvcResultMatchers.jsonPath("$.type", CoreMatchers.is(pokemonDto.type())));
	}

	@Test
	void PokemonController_PokemonDelete_ReturnString() throws Exception {
		long pokemonID = 1L;
		Mockito.doNothing().when(pokemonService).deletePokemonId(pokemonID);

		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/pokemon/1/delete")
			.contentType(MediaType.APPLICATION_JSON));

		resultActions.andExpect(MockMvcResultMatchers.status().isOk());
	}


}
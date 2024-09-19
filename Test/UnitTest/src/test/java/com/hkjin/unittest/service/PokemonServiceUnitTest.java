package com.hkjin.unittest.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hkjin.unittest.dto.PokemonDto;
import com.hkjin.unittest.dto.PokemonPageableResponse;
import com.hkjin.unittest.entity.Pokemon;
import com.hkjin.unittest.repository.PokemonRepository;
import com.hkjin.unittest.service.impl.PokemonServiceImpl;

@ExtendWith(MockitoExtension.class) // Mock 객체 확장하기 위함
public class PokemonServiceUnitTest {

	@Mock // Mock 객체 선언
	private PokemonRepository pokemonRepository;

	@InjectMocks
	private PokemonServiceImpl pokemonService;

	@Test
	void PokemonService_CreatePokemon_ReturnsPokemonDTO () {
		// given
		Pokemon pokemon = Pokemon.builder()
			.id(1L)
			.name("피카츄")
			.type("전기")
			.build();

		PokemonDto pokemonDto = PokemonDto.builder()
			.name("피카츄")
			.type("전기")
			.build();

		// when
		// DB 에 접근하지 않고, Mock 을 이용해서 한다.
		// Mock 을 하는 대상은 실제로 DB 에 접근하려는 대상을 Mocking 한다??
		Mockito.when(pokemonRepository.save(Mockito.any(Pokemon.class))).thenReturn(pokemon);
		PokemonDto savedPokemonDto = pokemonService.createPokemon(pokemonDto);

		// then
		Assertions.assertThat(savedPokemonDto).isNotNull();
		Assertions.assertThat(savedPokemonDto.id()).isGreaterThan(0L);
	}

	@Test
	void PokemonService_GetAllPokemon_ReturnsPagingDto () {
		// given
		PokemonPageableResponse mock = Mockito.mock(PokemonPageableResponse.class);
		Page<Pokemon> pokemons = Mockito.mock(Page.class);

		// when
		Mockito.when(pokemonRepository.findAll(Mockito.any(Pageable.class))).thenReturn(pokemons);
		PokemonPageableResponse savedPokemon = pokemonService.getAllPokemon(1,10);

		// then
		Assertions.assertThat(savedPokemon).isNotNull();
	}

}

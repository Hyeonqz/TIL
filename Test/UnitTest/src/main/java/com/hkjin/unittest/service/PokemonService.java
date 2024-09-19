package com.hkjin.unittest.service;

import com.hkjin.unittest.dto.PokemonDto;
import com.hkjin.unittest.dto.PokemonPageableResponse;

public interface PokemonService {
	PokemonDto createPokemon (PokemonDto pokemonDto);
	PokemonPageableResponse getAllPokemon (int pageNo, int pageSize);
	PokemonDto getPokemonById(Long id);
	PokemonDto updatePokemon(PokemonDto pokemonDto, Long id);
	void deletePokemonId(Long id);
}

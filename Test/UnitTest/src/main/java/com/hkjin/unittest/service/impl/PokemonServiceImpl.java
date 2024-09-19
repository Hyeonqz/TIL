package com.hkjin.unittest.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hkjin.unittest.dto.PokemonDto;
import com.hkjin.unittest.dto.PokemonPageableResponse;
import com.hkjin.unittest.entity.Pokemon;
import com.hkjin.unittest.exception.PokemonNotFoundException;
import com.hkjin.unittest.repository.PokemonRepository;
import com.hkjin.unittest.service.PokemonService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PokemonServiceImpl implements PokemonService {
	private final PokemonRepository pokemonRepository;

	@Override
	public PokemonDto createPokemon (PokemonDto pokemonDto) {
		Pokemon pokemon = Pokemon.builder()
			.name(pokemonDto.name())
			.type(pokemonDto.type())
			.build();

		Pokemon saved = pokemonRepository.save(pokemon);

		return new PokemonDto(
			saved.getId(),
			saved.getName(),
			saved.getType()
		);
	}

	@Override
	public PokemonPageableResponse getAllPokemon (int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Pokemon> pokemons = pokemonRepository.findAll(pageable);

		List<Pokemon> listOfPokemon = pokemons.getContent();
		List<PokemonDto> content = listOfPokemon.stream()
			.map(this::mapToDto)
			.toList();

		return new PokemonPageableResponse(
			content,
			pokemons.getNumber(),
			pokemons.getSize(),
			pokemons.getTotalElements(),
			pokemons.getTotalPages(),
			pokemons.isLast()
		);
	}

	@Override
	public PokemonDto getPokemonById (Long id) {
		Pokemon pokemon = pokemonRepository.findById(id)
			.orElseThrow(() -> new PokemonNotFoundException("Pokemon is Null"));

		return mapToDto(pokemon);
	}

	@Override
	public PokemonDto updatePokemon (PokemonDto pokemonDto, Long id) {
		Pokemon pokemon = pokemonRepository.findById(id)
			.orElseThrow(() -> new PokemonNotFoundException("Pokemon is Null"));

		pokemonRepository.save(pokemon.builder()
			.name(pokemonDto.name())
			.type(pokemonDto.type())
			.build());
		return mapToDto(pokemon);
	}

	@Override
	public void deletePokemonId (Long id) {
		Pokemon pokemon = pokemonRepository.findById(id)
				.orElseThrow(() -> new PokemonNotFoundException("Pokemon is Null"));
		pokemonRepository.delete(pokemon);
	}

	private PokemonDto mapToDto(Pokemon pokemon) {
		return new PokemonDto(
			pokemon.getId(),
			pokemon.getName(),
			pokemon.getType()
		);
	}

	private Pokemon mapToEntity(PokemonDto pokemonDto) {
		return new Pokemon(
			pokemonDto.id(),
			pokemonDto.name(),
			pokemonDto.type()
		);
	}

}

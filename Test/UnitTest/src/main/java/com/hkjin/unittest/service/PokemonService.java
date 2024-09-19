package com.hkjin.unittest.service;

import org.springframework.stereotype.Service;

import com.hkjin.unittest.repository.PokemonRepository;

import lombok.RequiredArgsConstructor;

@Service
public class PokemonService {
	private PokemonRepository pokemonRepository;

	public PokemonService (PokemonRepository pokemonRepository) {
		this.pokemonRepository = pokemonRepository;
	}



}

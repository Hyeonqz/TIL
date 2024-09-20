package com.hkjin.unittest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hkjin.unittest.dto.PokemonDto;
import com.hkjin.unittest.dto.PokemonPageableResponse;
import com.hkjin.unittest.service.PokemonService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/")
@RestController
public class PokemonController {
	private final PokemonService pokemonService;

	@GetMapping("pokemon")
	public ResponseEntity<PokemonPageableResponse> getPokemons(
		@RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
		@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
	) {
		return new ResponseEntity<>(pokemonService.getAllPokemon(pageNo, pageSize), HttpStatus.OK);
	}

	@GetMapping("pokemon/{id}")
	public ResponseEntity<PokemonDto> pokemonDetail(@PathVariable long id) {
		return ResponseEntity.ok(pokemonService.getPokemonById(id));

	}

	@PostMapping("pokemon/create")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<PokemonDto> createPokemon(@RequestBody PokemonDto pokemonDto) {
		return new ResponseEntity<>(pokemonService.createPokemon(pokemonDto), HttpStatus.CREATED);
	}

	@PutMapping("pokemon/{id}/update")
	public ResponseEntity<PokemonDto> updatePokemon(@RequestBody PokemonDto pokemonDto, @PathVariable("id") long pokemonId) {
		PokemonDto response = pokemonService.updatePokemon(pokemonDto, pokemonId);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("pokemon/{id}/delete")
	public ResponseEntity<String> deletePokemon(@PathVariable("id") long pokemonId) {
		pokemonService.deletePokemonId(pokemonId);
		return new ResponseEntity<>("Pokemon delete", HttpStatus.OK);
	}

}
package com.hkjin.unittest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkjin.unittest.entity.Pokemon;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
	Optional<Pokemon> findByType(String type);
}

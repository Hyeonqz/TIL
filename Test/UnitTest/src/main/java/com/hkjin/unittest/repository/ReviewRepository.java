package com.hkjin.unittest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkjin.unittest.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
	List<Review> findByPokemonId(Long pokemonId);
}

package com.hkjin.unittest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Review {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String content;
	private Integer stars;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="pokemon_id")
	private Pokemon pokemon;

	public void associatedWithPokemon(Pokemon pokemon) {
		this.pokemon = pokemon;
	}

	public void updateReview(String title, String content, Integer stars) {
		this.title = title;
		this.content = content;
		this.stars = stars;
	}

}

package com.hkjin.unittest.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hkjin.unittest.entity.Pokemon;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@DataJpaTest // autowired 를 사용할 수 있게 함
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2) // h2 DB 사용하기 위함
class PokemonRepositoryUnitTest {
	@Autowired
	private PokemonRepository pokemonRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@BeforeEach
	public void setup() {
		System.out.println("UnitTest Setup");
		pokemonRepository.deleteAll();
		// 테스트 메소드 시작시 primary key 1로 초기화
		entityManager.createNativeQuery("alter table pokemon alter column id restart  with 1").executeUpdate();
	}

	@Test
	void PokemonRepository_SaveAll_ReturnPokemon() {
	    // given -> Builder 사용 권장
		Pokemon pokemon = Pokemon.builder()
			.name("피카츄")
			.type("Electric")
			.build();

	    // when
		Pokemon savedPokemon = pokemonRepository.save(pokemon);

		// then
		Assertions.assertThat(savedPokemon).isNotNull();
		Assertions.assertThat(savedPokemon.getId()).isGreaterThan(0);
	}

	@Test
	void PokemonRepository_GetAll_ReturnsMoreThanOnePokemon() {
	    // given
		Pokemon pokemon = Pokemon.builder()
			.name("라이츄")
			.type("Electric")
			.build();
		Pokemon pokemon1 = Pokemon.builder()
			.name("꼬북이")
			.type("Water")
			.build();

	    // when
		pokemonRepository.save(pokemon);
		pokemonRepository.save(pokemon1);

		List<Pokemon> pokemonList = pokemonRepository.findAll();

		// then
		Assertions.assertThat(pokemonList).isNotNull();
		Assertions.assertThat(pokemonList.size()).isEqualTo(2L);
	}

	@Test
	void PokemonRepository_FindById_ReturnOnePokemon() {
		//given
		Pokemon pokemon = Pokemon.builder()
			.name("꼬마돌")
			.type("돌덩이")
			.build();

		//when
		pokemonRepository.save(pokemon);

		// return 이 Optional 일 떄는 직접 return 이 아닌 get() 으로 객체를 꺼내서 사용해야 한다.
		Pokemon pokemonReturn = pokemonRepository.findById(pokemon.getId()).get();

		//then
		Assertions.assertThat(pokemon).isNotNull();
		Assertions.assertThat(pokemonReturn.getId()).isEqualTo(1L);
	}

	@Test
	void PokemonRepository_FindByType_ReturnsOnePokemonNutNull() {
	    // given
		Pokemon pokemon = Pokemon.builder()
			.name("꼬마돌")
			.type("돌덩이")
			.build();

	    // when
		pokemonRepository.save(pokemon);

		Pokemon pokemon1 = pokemonRepository.findByType(pokemon.getType()).get();

		Pokemon updatedPokemon = pokemonRepository.save(
			Pokemon.builder()
				.name("진화돌")
				.type("강력한돌")
				.build()
		);

	    // then
		Assertions.assertThat(pokemon1).isNotNull();
		Assertions.assertThat(pokemon1.getType()).isEqualTo("돌덩이");
		Assertions.assertThat(updatedPokemon.getType()).isEqualTo("강력한돌");
	}

	@Test
	void PokemonRepository_Delete_ReturnPokemonIsEmpty() {
		// given
		Pokemon pokemon = Pokemon.builder()
			.name("꼬마돌")
			.type("돌덩이")
			.build();

		// when
		pokemonRepository.save(pokemon);
		pokemonRepository.deleteById(pokemon.getId());
		Optional<Pokemon> pokemonOptional = pokemonRepository.findById(pokemon.getId());

		Assertions.assertThat(pokemonOptional).isEmpty();
	}





















}
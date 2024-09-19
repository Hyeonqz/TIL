package com.hkjin.unittest.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.hkjin.unittest.entity.Review;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class ReviewRepositoryUnitTest {

	private ReviewRepository reviewRepository;

	@Autowired
	public ReviewRepositoryUnitTest (ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}

	@Test
	void ReviewRepository_SaveAll_ReturnsSavedReview () {
		// given
		Review review = Review.builder()
			.title("타이틀")
			.content("내용")
			.stars(5)
			.build();

		// when
		Review saved = reviewRepository.save(review);

		// then
		Assertions.assertThat(saved).isNotNull();
		Assertions.assertThat(saved.getId()).isGreaterThan(0L);
	}

	@Test
	void ReviewRepository_GetAll_ReturnsMoreThanOneReview() {
	    // given
		Review review = Review.builder()
			.title("타이틀")
			.content("내용")
			.stars(5)
			.build();

		Review review2 = Review.builder()
			.title("피카츄 타이틀")
			.content("피카츄 스킬 내용")
			.stars(4)
			.build();

	    // when
		Review saved = reviewRepository.save(review);
		Review saved2 = reviewRepository.save(review2);

		List<Review> reviewList = reviewRepository.findAll();

		// then
		Assertions.assertThat(reviewList).isNotNull();
		Assertions.assertThat(reviewList.size()).isEqualTo(2);
	}

	@Test
	void ReviewRepository_FindById_ReturnsSavedReview () {
		// given
		Review review = Review.builder()
			.title("타이틀")
			.content("내용")
			.stars(5)
			.build();

		Review saved = reviewRepository.save(review);
		Review reviewReturn = reviewRepository.findById(saved.getId()).get();

		Assertions.assertThat(reviewReturn).isNotNull();
	}

	@Test
	void ReviewRepository_UpdateReview_ReturnsReview () {
		// given
		Review review = Review.builder()
			.title("타이틀")
			.content("내용")
			.stars(5)
			.build();

		Review saved = reviewRepository.save(review);
		Review reviewReturn = reviewRepository.findById(saved.getId()).get();

		Review updated = reviewRepository.save(Review.builder()
			.title("update title")
			.content("update content")
			.stars(4)
			.build());

		Assertions.assertThat(updated.getTitle()).isEqualTo("update title");
	}

	@Test
	void ReviewRepository_ReviewDelete_ReturnsReviewIsEmpty () {
		Review review = Review.builder()
			.title("Hi")
			.content("Hello?")
			.stars(1)
			.build();

		Review saved = reviewRepository.save(review);
		reviewRepository.delete(saved);
		Optional<Review> optional = reviewRepository.findById(saved.getId());

		Assertions.assertThat(optional).isEmpty();
	}

}

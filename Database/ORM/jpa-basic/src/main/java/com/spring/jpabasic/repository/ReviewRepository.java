package com.spring.jpabasic.repository;

import java.util.List;

import com.spring.jpabasic.entity.Hotel;
import com.spring.jpabasic.entity.Review;
import com.spring.jpabasic.utils.EMFUtils;

import jakarta.persistence.TypedQuery;

public class ReviewRepository {
	public List<Review> findByHotel(Hotel hotel, int startRow, int maxResults) {
		TypedQuery<Review> query = EMFUtils.currentEntityManager()
			.createQuery(
				"select r from Review r"+
					"wehre r.hotel = :hotel order by r.id desc", Review.class);

		query.setParameter("hotel", hotel);
		query.setFirstResult(startRow);
		query.setMaxResults(maxResults);
		return query.getResultList();
	}


}

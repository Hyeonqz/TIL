package com.spring.jpabasic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "hotel_review")
@Entity
public class Review {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String hotelId;

	public Review () {
	}

	public Review (Long id, String hotelId) {
		this.id = id;
		this.hotelId = hotelId;
	}

}

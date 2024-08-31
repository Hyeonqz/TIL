package com.spring.jpabasic.dtos;

import java.util.List;

import com.spring.jpabasic.entity.Hotel;
import com.spring.jpabasic.entity.Review;

public class HotelSummary {
	private Hotel hotel;
	private List<Review> reviews;

	public HotelSummary (Hotel hotel, List<Review> reviews) {
		this.hotel = hotel;
		this.reviews = reviews;
	}

	public HotelSummary () {
	}

	public Hotel getHotel () {
		return hotel;
	}

	public List<Review> getReviews () {
		return reviews;
	}

}

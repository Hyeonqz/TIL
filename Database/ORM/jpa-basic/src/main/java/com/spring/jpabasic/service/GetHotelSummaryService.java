package com.spring.jpabasic.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.spring.jpabasic.dtos.HotelSummary;
import com.spring.jpabasic.entity.Hotel;
import com.spring.jpabasic.entity.Review;
import com.spring.jpabasic.exception.HotelNotFoundException;
import com.spring.jpabasic.repository.HotelRepository;
import com.spring.jpabasic.repository.ReviewRepository;
import com.spring.jpabasic.utils.EMFUtils;

@Service
public class GetHotelSummaryService {
	private HotelRepository hotelRepository = new HotelRepository();
	private ReviewRepository reviewRepository = new ReviewRepository();

	public HotelSummary getHotelSummary(String hotelId) {
		try {
			Hotel hotel = hotelRepository.find(hotelId);
			if(hotel == null)
				throw new HotelNotFoundException("Hotel not found");

			List<Review> reviews = reviewRepository.findByHotel(hotel,0,3);
			return new HotelSummary(hotel,reviews);
		} finally {
			EMFUtils.closeCurrentEntityManager();
		}
	}
}
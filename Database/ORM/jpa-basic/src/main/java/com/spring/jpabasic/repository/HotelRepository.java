package com.spring.jpabasic.repository;

import com.spring.jpabasic.entity.Hotel;
import com.spring.jpabasic.utils.EMFUtils;

import jakarta.persistence.EntityManager;

public class HotelRepository {

	public Hotel find(String id) {
		EntityManager em = EMFUtils.currentEntityManager();
		return em.find(Hotel.class , id);
	}


}

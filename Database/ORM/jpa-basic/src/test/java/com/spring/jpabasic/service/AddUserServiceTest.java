package com.spring.jpabasic.service;

import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.spring.jpabasic.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class AddUserServiceTest {

	@Test
	@DisplayName("User_Add_Test")
	void 유저_추가_테스트() {
		// 영속(=엔티티) 단위 별로 EntityManagerFactory 를 생성한다.
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("jpastart");

		// 영속 관리하는 EntityManager 생성한다. -> 영속성 컨텍스트와 엔티티를 관리한다.
		EntityManager entityManager = entityManagerFactory.createEntityManager();

		// 영속 컨텍스트에서 EntityTransaction 을 구한다.
		EntityTransaction transaction = entityManager.getTransaction();

		try {
			transaction.begin();
			User user = new User("jin@naver.com","jin",new Date());
			entityManager.persist(user);
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		} finally {
			entityManager.close();
		}

		entityManagerFactory.close();
	}
}

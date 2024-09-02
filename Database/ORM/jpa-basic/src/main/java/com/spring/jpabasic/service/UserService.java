package com.spring.jpabasic.service;

import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.spring.jpabasic.entity.User;
import com.spring.jpabasic.exception.UserNotFoundException;
import com.spring.jpabasic.utils.EMFUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class UserService {

	public void test() {
		EntityManager em = EMFUtils.currentEntityManager();
		try {
			em.getTransaction().begin();
			User user = em.find(User.class, "email");
			Set<String> keywords = user.getKeywords();
			keywords.remove("서울");
		} finally {
			em.close();
		}
	}

	public void join(User user) {
		EntityManager em = EMFUtils.createEntityManager();
		em.getTransaction().begin();

		try {
			User found = em.find(User.class, user.getEmail());
			if(found != null) {
				throw new RuntimeException("이미 객체가 있습니다.");
			}
			em.persist(user);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	public Optional<User> getUser(String email) {
		EntityManager em = EMFUtils.createEntityManager();
		try {
			User user = em.find(User.class, email);
			return Optional.ofNullable(user);
		} finally {
			em.close();
		}
	}

	public void changeName(String email, String newName) {
		EntityManager em = EMFUtils.createEntityManager();
		try {
			em.getTransaction().begin();
			User user = em.find(User.class, email);
			if(user == null) {
				throw new UserNotFoundException();
			}
			user.changeName(newName);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	public List<User> getAllUsers() {
		EntityManager em = EMFUtils.createEntityManager();
		try {
			em.getTransaction().begin();
			TypedQuery<User> query = em.createQuery("select u from User u order by u.name", User.class);
			List<User> result = query.getResultList();
			em.getTransaction().commit();
			return result;
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			em.close();
		}
	}

	public void withdraw(String email) {
		EntityManager em = EMFUtils.createEntityManager();

		try {
			em.getTransaction().begin();
			User user = em.find(User.class, email);
			if(user == null)
				throw new UserNotFoundException();
			em.remove(user);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		} finally {
			em.close();
		}
	}

}

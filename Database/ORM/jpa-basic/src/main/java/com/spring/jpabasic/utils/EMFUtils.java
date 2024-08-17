package com.spring.jpabasic.utils;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EMFUtils {
	private static EntityManagerFactory emf;
	private static ThreadLocal<EntityManager> currentEm = new ThreadLocal<>();

	public static void init() {
		emf = Persistence.createEntityManagerFactory("jpastart");
	}

	public static EntityManager createEntityManager() {
		return emf.createEntityManager();
	}

	public static void close() {
		emf.close();;
	}

	public static EntityManager currentEntityManager() {
		EntityManager em = currentEm.get();
		if (em == null) {
			em = emf.createEntityManager();
			currentEm.set(em);
		}
		return em;
	}

	public static void closeCurrentEntityManager() {
		EntityManager em = currentEm.get();
		if(em != null) {
			currentEm.remove();
			em.close();
		}
	}
}

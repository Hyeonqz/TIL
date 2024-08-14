package com.spring.jpabasic.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EMFUtils {
	private static EntityManagerFactory emf;

	public static void init() {
		emf = Persistence.createEntityManagerFactory("jpastart");
	}

	public static EntityManager createEntityManager() {
		return emf.createEntityManager();
	}

	public static void close() {
		emf.close();;
	}
}

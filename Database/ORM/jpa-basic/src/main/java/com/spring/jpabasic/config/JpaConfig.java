package com.spring.jpabasic.config;

import java.util.List;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.spring.jpabasic.entity.Hotel;
import com.spring.jpabasic.entity.MembershipCard;
import com.spring.jpabasic.entity.Player;
import com.spring.jpabasic.entity.Review;
import com.spring.jpabasic.entity.Team;
import com.spring.jpabasic.entity.User;
import com.spring.jpabasic.utils.EMFUtils;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

@Entity
@Immutable
@Subselect("select s.id, s.name from sight s")
@Synchronize()
@Configuration
public class JpaConfig {

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}

	public void a() {
		EntityManager em = EMFUtils.currentEntityManager();
		em.getTransaction().begin();

		Query query = em.createQuery("select m from Hotel m where grade = :grade");
		query.setParameter("grade","STAR4");
		List<Object[]> results = query.getResultList();
		for (Object[] row : results) {
			String grade = (String) row[0];
		}
	}
}
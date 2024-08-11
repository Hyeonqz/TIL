package org.ddd.start.domain.order.infra;

import java.util.Optional;
import java.util.Queue;

import org.aspectj.weaver.ast.Or;
import org.ddd.start.domain.order.Order;
import org.ddd.start.domain.order.OrderRepository;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class JpaOrderRepository implements OrderRepository {
	@PersistenceContext
	EntityManager em;

	@Override
	public Optional<Order> findById (OrderNo no) {
		return Optional.of(em.find(Order.class, no)); //entityManager find 메소드를 이용해서 ID 로 애그리거트를 검색
	}

	@Override
	public void save (Order order) {
		em.persist(order);
	}


}

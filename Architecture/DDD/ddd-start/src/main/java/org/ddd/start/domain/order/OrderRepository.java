package org.ddd.start.domain.order;

public interface OrderRepository {
	Object findById(OrderNo no);
	void save(Order order);
}

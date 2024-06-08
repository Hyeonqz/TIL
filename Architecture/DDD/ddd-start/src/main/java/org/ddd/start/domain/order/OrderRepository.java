package org.ddd.start.domain.order;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

public interface OrderRepository {
	Object findById(OrderNo no);
	void save(Order order);

}

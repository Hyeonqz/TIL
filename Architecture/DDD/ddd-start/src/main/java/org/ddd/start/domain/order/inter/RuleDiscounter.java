package org.ddd.start.domain.order.inter;

import java.util.List;

import org.ddd.start.domain.order.Money;
import org.ddd.start.domain.order.OrderLine;

public interface RuleDiscounter {
	Money applyRules(Customer customer, List<OrderLine> orderLines);
}

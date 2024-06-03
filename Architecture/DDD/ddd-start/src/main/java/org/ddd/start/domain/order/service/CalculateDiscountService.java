package org.ddd.start.domain.order.service;

import java.util.List;

import org.ddd.start.domain.order.Money;
import org.ddd.start.domain.order.OrderLine;
import org.ddd.start.domain.order.inter.RuleDiscounter;

public class CalculateDiscountService {
	private RuleDiscounter ruleDiscounter;

	public CalculateDiscountService (RuleDiscounter ruleDiscounter) {
		this.ruleDiscounter = ruleDiscounter;
	}

	public Money calculateDiscount(List<OrderLine> orderLines, String customerId) {
		Customer customer = fincCustomer(customerId);
		return ruleDiscounter.applyRules(customer,orderLines);
	}

}

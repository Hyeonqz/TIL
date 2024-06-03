package org.ddd.start.domain.order.inter;

import java.util.List;

import org.ddd.start.domain.order.Money;
import org.ddd.start.domain.order.OrderLine;

public class DroolsRuleDiscounterImpl implements RuleDiscounter{
	private KieContainer kieContainer;

	public DroolsRuleDiscounterImpl (KieContainer kieContainer) {
		KieServices ks = Kieservices.Factory.get();
		Kcontainer = ks.getKieClasspathContainer();
	}

	@Override
	public Money applyRules (Customer customer, List<OrderLine> orderLines) {
		return null;
	}

}

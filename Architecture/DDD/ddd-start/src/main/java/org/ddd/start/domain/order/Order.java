package org.ddd.start.domain.order;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.Comment;

import lombok.Getter;

@Comment(value = "주문 테이블")
@Getter
public class Order {
	private List<OrderLine> orderLines = new ArrayList<>();
	private ShippingInfo shippingInfo;
	private OrderState orderState;
	private Money totalAmounts;

	private String orderNumber; // Order 엔티티 식별자

	@Override
	public boolean equals (Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Order order = (Order)o;
		if (this.orderNumber == null) return false;
		return Objects.equals(orderNumber, order.orderNumber);
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderNumber == null) ? 0 : orderNumber.hashCode());
		return result;
	}

	public Order (List<OrderLine> orderLines) {
		setOrderLines(orderLines);
	}

	public void changeShippingInfo(ShippingInfo newShippingInfo) {
		verifyNotYetShipped();
		setShippingInfo(newShippingInfo);
	}

	public void cancel() {
		verifyNotYetShipped();
		this.orderState = OrderState.CANCELED;
	}

	private void verifyNotYetShipped () {
		if(orderState != OrderState.PAYMENT_WAITING && orderState != OrderState.PREPARING)
			throw new IllegalArgumentException("Already shipped");
	}

	private void setOrderLines(List<OrderLine> orderLines) {
		verifyAtLeastOneOrMoreOrderLines(orderLines);
		this.orderLines = orderLines;
		calculateTotalAmounts();
	}

	private void verifyAtLeastOneOrMoreOrderLines(List<OrderLine> orderLines) {
		if(orderLines == null || orderLines.isEmpty()) {
			throw new IllegalArgumentException("Empty OrderLine");
		}
	}

	private void calculateTotalAmounts() {
		int sum =  orderLines.stream()
			.mapToInt(OrderLine::getAmounts)
			.sum();
		this.totalAmounts = new Money(sum);
	}

	// INFO 주문시 배송지 정보를 반드시 지정해야 한다는 내용
	public Order(List<OrderLine> orderLines, ShippingInfo shippingInfo) {
		setOrderLines(orderLines);
		setShippingInfo(shippingInfo);
	}


	// INFO 배송정보 null 체크 로직
	private void setShippingInfo(ShippingInfo newShippingInfo) {
		if(shippingInfo == null)
			throw new IllegalArgumentException("Null ShippingInfo");
		this.shippingInfo = newShippingInfo; // 새로 받아온 배송정보를 설정한다.
	}

}

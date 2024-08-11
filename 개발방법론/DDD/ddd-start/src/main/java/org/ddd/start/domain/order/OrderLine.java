package org.ddd.start.domain.order;

import org.ddd.start.domain.product.Product;
import org.hibernate.annotations.Comment;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Comment(value = "배송 라인 정보?")
@Getter
public class OrderLine {
	private Product product;
	private Money price;
	private Integer quantity;
	private Integer amounts;

	public OrderLine(Product product, Money price, Integer quantity) {
		this.product = 	product;
		this.price = price;
		this.quantity = quantity;
		this.amounts = calculateAmounts().getValue();
	}

	private Money calculateAmounts() {
		return price.multiply(quantity);
	}
}

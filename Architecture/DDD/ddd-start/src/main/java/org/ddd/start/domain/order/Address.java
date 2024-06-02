package org.ddd.start.domain.order;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Comment(value = "ShippingInfo 하위 도메인")
@Getter
public class Address {
	private String address1;
	private String address2;
	private String zipcode;
}

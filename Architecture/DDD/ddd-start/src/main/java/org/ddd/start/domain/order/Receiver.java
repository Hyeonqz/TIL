package org.ddd.start.domain.order;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Getter
@Comment(value = "ShippingInfo 하위 도메인 모델")
public class Receiver {
	public String name;
	public String phoneNumber;
}

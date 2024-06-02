package org.ddd.start.domain.order;


import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
@Getter
@Comment(value = "배송 받는 사람 정보 테이블")
public class ShippingInfo {

	private Receiver receiver;
	private Address address;

}

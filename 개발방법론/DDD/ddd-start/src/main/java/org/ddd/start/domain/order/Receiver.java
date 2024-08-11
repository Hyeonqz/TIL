package org.ddd.start.domain.order;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@Getter
@Comment(value = "ShippingInfo 하위 도메인 모델")
public class Receiver {
	@Column(name="receiver_name")
	public String name;
	@Column(name="receiver_phone")
	public String phoneNumber;

	protected Receiver() {
		// JPA 를 적용하기 위해 기본 생성자 추가
	}

}

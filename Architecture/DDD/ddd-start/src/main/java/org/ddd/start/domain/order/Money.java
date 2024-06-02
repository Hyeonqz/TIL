package org.ddd.start.domain.order;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Getter
@Comment(value = "OrderLine 하위 도메인 모델 -> 돈 계산 하기 위한 클래스")
public class Money {
	private int value;

	public Money add(Money money) {
		return new Money(this.value + money.value);
	}

	public Money multiply(int multiplier) {
		return new Money(value * multiplier);
	}

	// value 객체의 데이터를 변경할 때는 기존 데이터를 변경하기보다는 변경한 데이터를 갖는 새로운 Value 객체를 생성하는 방식을 선호한다.

	// 데이터 변경 기능을 제공하지 않으므로 불변 타입이라고 표현한다.

	// 불변 객체는 참조 투명성과 스레드에 안전한 특징을 갖고 있다.
	// https://ko.wikipedia.org/wiki/%EB%B6%88%EB%B3%80%EA%B0%9D%EC%B2%B4

}

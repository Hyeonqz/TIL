## TDD 시작
TDD 는 테스트부터 시작한다. 구현을 먼저하고 나중에 테스트 하는 것이 아닌, 테스트를 하고 그 다음에 구현한다 <br>
구현 코드가 없는데, 어떻게 테스트를 할 수 있을까? <br>

여기서 테스트를 먼저 한다는 것은 기능이 올바르게 동작하는지 검증하는 테스트 코드를 작성한다는 것을 의미한다 <br>
기능을 검증하는 테스트 코드를 먼저 작성하고 테스트를 통과시키기 위해 개발을 진행한다 <br>

## 암호 검사기
- 길이가 8글자 이상
- 0부터 9 사이의 숫자를 포함
- 대문자 포함
- 세 규칙을 모두 충족하면 암호는 강함이다.
- 2개의 규칙을 충족하면 암호는 보통이다.
- 1개 이하의 규칙을 충족하면 암호는 약함이다.

### 첫번째 테스트: 모든 규칙을 충족하는 경우
첫 번째 테스트를 잘 선택하지 않으면 이후 진행 과정이 순탄하게 흘러가지 않는다 <br>
첫 번째 테스트를 선택할 때에는 가장 쉽거나 가장 예외적인 상황을 선택해야 한다 <br>

## TDD 흐름
테스트 -> 코딩 -> 리팩토링 순으로 흐름을 가진다 <br>
TDD 는 기능을 검증하는 테스트를 먼저 작성한다 <br>

작성한 테스트를 통과하지 못하면 테스트를 통과할 만큼만 코드를 작성한다 <br>
테스트를 통과한 뒤에는 개선할 코드가 있으면 리팩토링 한다 <br>

### 초반에 복잡한 테스트 부터 시작하면 안 되는 이유
한 번에 완벽한 코드를 만들면 좋겠지만, 모두가 그럴 수 없다 <br>
보통의 개발자는 한 번에 많은 코드를 만들다 보면 나도 모르게 버그를 만들고 나중에 버그를 잡기 위해 많은 시간을 허비하게 된다 <br>

### 구현하기 쉬운 테스트부터 시작하기
하나의 테스트를 통과했으면 그 다음으로 구현하기 쉬운 테스트를 선택해야 한다 <br>
그래야 점진적으로 구현을 완성해 나갈 수 있다 <br>

### 예외 상황을 먼저 테스트해야 하는 이유
다양한 예외 상황은 복잡한 if~else 블록을 동반할 때가 많다 <br>
예외 상황을 전혀 고려하지 않은 코드에 예외 상황을 반영하려면 코드의 구조를 뒤집거나 코드 중간에 예외 상황을 처리하기 위해 조건문을 중복해서 추가하는 일이 벌어진다 <br>
이는 코드를 복잡하게 만들어 버그 발생 가능성을 높인다 <br>

### 완급 조절
1) 정해진 값을 리턴
2) 값 비교를 이용해서 정해진 값을 리턴
3) 다양한 테스트를 추가하면서 구현을 일반

### 지속적인 리팩토링
매번 리팩토링을 진행해야 하는 것은 아니지만, 적당한 후보가 보이면 리팩토링을 진행한다 <br>
코드 가독성이 높아지면 개발자는 더욱 빠르게 코드를 분석할 수 있어 수정 요청이 있을 때 변경할 코드를 빠르게 찾을 수 있다 <br>
이는 코드 변경의 어려움을 줄여주어 향후 유지보수에 도움이 된다 <br>

코드를 잘 변경하려면 변경하기 쉬운 구조를 가져야 하는데 이를 위한 것이 바로 리팩토링 이다 <br>

## 테스트 작성 순서 연습
1) 쉬운 것부터 테스트
```java
@Test
	@DisplayName("월 납입료는 만원이고, 1달 연장 가능")
	void 만원납부하면_한달뒤가_만료일임() {
	    // given
		LocalDate localDate = LocalDate.of(2019,3,1);
		int payAmount = 10_000;
	    // when
		ExpiryDateCalculator calculator = new ExpiryDateCalculator();
		LocalDate expirationDate = calculator.calculateExpirationDate(localDate,payAmount);
		// then
		Assertions.assertEquals(LocalDate.of(2019,4,1), expirationDate);
	}
```

2) 예시를 추가하면서 구현을 일반화하자
```java
	@Test
	@DisplayName("월 납입료는 만원이고, 1달 연장 가능")
	void 만원납부하면_한달뒤가_만료일임() {
	    // given
		LocalDate localDate = LocalDate.of(2019,3,1);
		int payAmount = 10_000;
	    // when
		ExpiryDateCalculator calculator = new ExpiryDateCalculator();
		LocalDate expirationDate = calculator.calculateExpirationDate(localDate,payAmount);
		// then
		Assertions.assertEquals(LocalDate.of(2019,4,1), expirationDate);

		LocalDate localDate1 = LocalDate.of(2019,5,5);
		int payAmount2 = 10_000;
		ExpiryDateCalculator calculator1 = new ExpiryDateCalculator();
		LocalDate expirationDate1 = calculator1.calculateExpirationDate(localDate1,payAmount2);
		Assertions.assertEquals(LocalDate.of(2019,6,5), expirationDate1);
	}
```

3) 코드 정리: 중복 제거
```java
public class ExpirationDateTest {
	
	@Test
	@DisplayName("월 납입료는 만원이고, 1달 연장 가능")
	void 만원납부하면_한달뒤가_만료일임() {
	    // given
		assertExpiryDate(LocalDate.of(2019,3,1),10_000, LocalDate.of(2019,4,1));
		assertExpiryDate(LocalDate.of(2019,7,1),10_000, LocalDate.of(2019,8,1));

	}

	private static void assertExpiryDate (LocalDate billingDate, int i, LocalDate expectedDate) {
		ExpiryDateCalculator calculator = new ExpiryDateCalculator();
		LocalDate realExpireDate = calculator.calculateExpirationDate(billingDate,i);
		Assertions.assertEquals(expectedDate,realExpireDate);
	}

}

```

4) 예외 상황 처리
예외 상황을 한번 찾아보자 <br>
```java
	@Test
	@DisplayName("")
	void 납부일과_한달뒤_날짜가_같지않음() {
	    assertExpiryDate(LocalDate.of(2019,1,31),10_000,LocalDate.of(2019,2,28));
	    assertExpiryDate(LocalDate.of(2019,5,31),10_000,LocalDate.of(2019,6,30));
	}
```

5) 다시 예외 상황
파라미터 개수는 적을수록 코드 가독성과 유지보수에 유리하므로 메소드의 파라미터 개수가 3개 이상이면 객체로 바꿔 한개로 줄이는 것을 고려해야 한다 <br>
```java

```

6) 

# Springboot 에서 Util 클래스 관리하기

Springboot 환경에서 util 클래스를 관리하는 방법을 이야기 해보려고 한다 <br>

필자가 설명할 내용은 100% 정답은 아니며 분명 더 좋은 방법이 있을 거라고 생각한다 <br>
현재 내가 실무에서 사용하는 방법을 설명해보겠다 <br>

필자는 gradle 기반 멀티 모듈 프로젝트를 사용중에 있다 <br>
그래서 도메인 또는 목적 별로 module 이 여러개 존재한다 <br>

필자는 공통 코드들을 정의하기 위해 module-common 이라는 모듈을 만들었고 java-library 로 사용중에 있다 <br>
여러 모듈은 module-common 을 주입받아서 사용중에 있다 <br>

보통 JDK 에서도 어느정도 유틸 클래스를 정의해두고 사용할 수 있게 만들어 두었긴 하지만, 좀더 세밀한 부분을 제어하기 위해서는 개발자들이 코드를 수정해야 할 일이 생긴다. <br>
(참고로 apache commons 라이브러리에 좋은 유틸 클래스들이 아주 많다) <br>

'Util' 의 해석하면 '유용한' 이라는 뜻을 가진다. 위 뜻을 프로그래밍에 대입을 한다면? <br>

프로그래밍에서 '**Util**' 이라 함은 비즈니스 로직 작성시 비즈니스 로직과 직접적인 연관은 없지만 유연한 코드 작성을 도와주는 클래스를 의미한다 <br>

예를 들면 아래와 같다
- LogUtil
- TimeUtil
- DateUtil

등등 프로젝트에서 자주 사용하는 코드들을 본인 상황에 맞게 코드들을 정의하여 유용하게 사용하기 위함이 목적이라고 생각한다 <br>

예를 들면 새벽에 결제 후 정산이 이루어져야 한다, 정산은 결제일+2 일 뒤에 이루어진다 <br>
위 상황에서 날짜를 표현하기 위해서는 어떻게 해야할까? <br>
```java
LocalDateTime settlementDateTime = LocalDateTime.now().plusDays(2);
```

보통은 위와 같은 로직을 작성할거라고 생각한다. <br>

하지만 위 코드가 여러 로직에서 계속 반복이 된다면? <br>
또는 다른 모듈에서도 사용이 된다면? <br>

매번 위 로직을 작성할 것인가? <br>

사실 그래도 상관은 없긴하다만, 필자라면 코드의 일관성 및 가독성을 위해 '**Util**' 클래스로 정의해서 사용할 것 같다 <br>

어떻게 사용할지는 바로 아래서 알아보자

## Util 클래스 생성 전략
Util 클래스를 어떻게 생성하여 활용할지에 대한 2가지 전략이 있다 <br>

### 1. Bean

```java
public class DateUtil {

	public LocalDateTime settlementDateTime() {
		return LocalDateTime.now().plusDays(2);
	}
	
}
```

위 코드처럼 정의를 해두고 여러 로직에서 호출을 하면서 사용할 수 있다 <br>

그럼 SpringBoot 에서 위 로직을 호출해서 바로 사용할 수 있을까? 라는 질문을 하면 답은 'No' 이다 <br>

Spring 에서는 Bean 을 사용하여 인스턴스를 스프링 컨테이너에 저장을 한다 <br>
그리고 스프링 컨테이너에 Bean 을 꺼내서 주입을 받는 'DI' 를 활용한다 <br>

Bean 등록을 위해서 보통 @Service, @Repository 어노테이션을 사용한다 <br>

그리고 대부분의 비즈니스 로직은 @Service 가 붙은 클래스에서 작성이 된다 <br>
왜 @Service 가 붙은 클래스에서 비즈니스 로직을 작성하냐고 이유를 물어본다면.. <br>
답변은 현재까지 관례가 그러하다 컨트롤러 -> 서비스 호출 아직까지는 이렇게 많이 쓰인다 <br>

위 플로우로 가지 않아도 결국에 제일 외부에서 요청을 받고 주는 Controller 는 Bean 에 등록된 인스턴스의 메소드를 사용할 수 밖에 없다 <br>
그게 싫으면, 컨트롤러에서 모든 비즈니스 로직을 작성하면 된다..... <br>

아래 코드는 위에 생성한 `DateUtil` 을 주입받아 실행시킨 결과이다 <br>
(편의를 위해 Lombok 을 사용하였다.)

```java
@RequiredArgsConstructor
@Transactional
@Service
public class PosService {
	private final PosRepository posRepository;
	private final DateTimeUtil dateTimeUtil;

	public Pos createPos() {
		Pos pos = Pos.builder()
			.uuid(UUID.randomUUID())
			.created(LocalDateTime.now())
			.build();

		posRepository.save(pos);

		LocalDateTime localDateTime = dateTimeUtil.settlementDateTime();

		return pos;
	}

}

```

아래는 Console 이다
```shell
Description:

Parameter 1 of constructor in org.hyeonqz.redislab.rdb.service.PosService required a bean of type 'org.hyeonqz.redislab.util.DateTimeUtil' that could not be found.


Action:

Consider defining a bean of type 'org.hyeonqz.redislab.util.DateTimeUtil' in your configuration.
```

콘솔의 읽어보면 생성자가 DateTimeUtil Bean을 찾지 못해서 요구한다는 메시지이다 <br>

해결책은 바로 위 클래스 @Component 어노테이션을 붙여서 Bean 으로 만들어버리면 된다.

```java
@Component
public class DateTimeUtil {

	public LocalDateTime settlementDateTime() {
		return LocalDateTime.now().plusDays(2);
	}
}

```
위 @Component 를 통해 Spring Application 이 기동이 될 때 Bean 들을 찾아서 Spring 컨테이너에 담아두었다 <br>
이제 여러 클래스에서 위 DateTimeUtil 을 주입받아 사용할 수 있다 <br>

위 방법으로 Util 클래스를 사용할시 ****'장점'**** 은 아래와 같다
- 의존성 주입 가능
- 라이프사이클 관리 -> Spring 컨테이너가 인스턴스 생성과 소멸 관리
- 테스트 용이성 -> Mock 객체 주입을 통한 테스트 구현 쉬움
- AOP 적용 가능 -> 로깅, 트랜잭션 적용 가능

4가지 정도가 장점으로 생각이 든다 <br>

그럼 ****'단점'**** 은 뭐가 있을까? <br>
- 접근성 -> 항상 DI 를 통해 접근해야함
- 메모리 사용 -> 각 인스턴스가 힙 메모리에 객체로 생성됨
- 오버헤드 -> Spring 컨테이너 시작 시 빈 생성 오버헤드

오버헤드는 그냥 @Lazy 를 통해서 늦게 초기화되거나 호출됬을 때 Bean 이 주입되게 할 수는 있지만, 그것 또한 비용이라고 생각한다 <br>


그럼 위 방식은 언제 사용해야 하는걸까? <br>
- 다른 서비스나 외부 리소스에 의존하는 기능
- 트랜잭션이나 AOP 같은 Spring 기능이 필요한 경우
- 테스트 용이성이 중요한 복잡한 비즈니스 로직

위와 같이 Spring 에 어떠한 기능과 관계가 있는 경우 사용을 권장한다 <br><br>

### 2. 정적 유틸리티 class
```java
public class DateTimeUtil {

	public static LocalDateTime settlementDateTime() {
		return LocalDateTime.now().plusDays(2);
	}
}
```

위처럼 만들어서 DateTimeUtil.settlementDateTime() 으로 사용할 수 있다 <br>
참고로 Lombok 에서 좋은 기능을 제공해준다. @UtilityClass 라는 어노테이션이다 <br>

위 어노테이션은 메소드에 자동으로 static 을 붙여준다.
```java
@UtilityClass
public class DateTimeUtil {

	public LocalDateTime settlementDateTime() {
		return LocalDateTime.now().plusDays(2);
	}
}
```

위 클래스도 DateTimeUtil.settlementDateTime() 바로 사용할 수 있다 <br>


그렇다면 정적 유틸리티 클래스의 장점은 뭘까? <br>
- 간편한 접근: 인스턴스 생성 없이 직접 접근 가능
- 메모리 효율성
  - 클래스 로딩 시 메소드 영역(Method Area)에 한 번만 로드됨
  - 매번 새로운 객체를 생성하지 않아도 메모리 오버헤드가 없음
- 상태 없음: 상태를 가지지 않아 스레드 안전성 확보가 수월함
- 초기화 비용 없음: 인스턴스 생성 비용이 없음
- GC 부담 감소: 객체가 생성되지 않으므로 GC 대상이 아님


그렇다면 단점은 뭘까 ?

- 테스트 어려움: 정적 메소드는 모킹하기 어려움
- OCP 위반 가능성: 확장보다 수정에 의존하게 될 수 있음
- 의존성 주입 불가: 다른 컴포넌트를 주입받을 수 없음


그렇다면 Bean 이랑 Static 둘중 어떠한 방법을 사용해야 할까? <br>

필자는 JVM 메모리 관점에서 분석을 하였고 결과는 아래와 같다 <br>

#### Spring Bean 관점 JVM
- 메모리 위치: 힙(Heap) 영역에 생성됨
- 메모리 사용
  - 모든 객체는 객체 헤더(12~16바이트)를 가짐
  - 인스턴스 변수에 따른 추가 메모리 사용
  - 싱글톤이라도 각 빈마다 별도의 객체 인스턴스가 생성됨
- GC 영향
  - 주로 오래 살아남아 Old Generation으로 이동
  - Major GC 때마다 살아있는지 확인하는 과정 필요
  - 애플리케이션 종료 전까지는 메모리에서 해제되지 않음


즉 애플리케이션 종료 전까지는 메모리에서 해제되지 않는다. GC 가 돌면서 객체 참조를 끊어주지 않는다 <br>
빈 사용후 스프링 컨테이너 반납을 하는 것이지, 스프링 컨테이너에 이미 생성된 인스턴스는 해제되지 않는다 <br>

#### 정적 유틸리티 클래스 방식 관점 JVM
- 메모리 위치: Method Area/Metaspace에 저장됨
- 메모리 사용
  - 객체 인스턴스가 생성되지 않음
  - 클래스 자체 정보만 메모리에 로드됨
  - 객체 헤더 오버헤드 없음
- GC 영향
  - 기본적으로 GC 대상이 아님
  - 메모리 해제를 위한 검사 과정 없음
  - 클래스 언로드가 필요한 특수한 경우에만 메모리에서 해제

즉 JVM 관점에서는 static 방식이 효율적이다 <br>

결론은 필자는 '정적 유틸리티' 방식을 추천한다 <br>

Util 의 용도에 맞게 언제 어디서든 쉽고 유용하게 호출을 해서 사용되기 위해선 위 정적 유틸리티로 만들어서 사용하는게 맞다고 판단하였다 <br><br>


### 결론
Util 클래스를 구현하는 방법은 Bean 방식과 Static 방식 2가지가 존재한다 <br>

일반적인 유틸리티 함수의 경우 정적 유틸리티 클래스(@UtilityClass) 방식을 추천한다. <br>
특히 상태가 없고 외부 의존성이 없는 헬퍼 메소드들은 정적 방식이 JVM 메모리 관점에서 Bean 보다 이점이 있다 <br>

하지만 외부 의존성 주입, 테스트 용이성이 중요하다고 판단 되면 Spring Bean 으로 설계하는 것도 좋다.<br>

제일 중요한 것은 모든 유틸리티 클래스를 한 가지 방식만 고집하여 구현하기보다는, 각 기능의 특성과 요구사항을 고려하여 적절한 방식을 선택하는 것이다 <br>


### REF
```shell
1. https://www.baeldung.com/java-helper-vs-utility-classes
```
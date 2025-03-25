# Spring 에서 이벤트 처리에 대하여 알아보자

실무에서 비즈니스 로직을 작성하며 코드 의존성을 줄일 수 있는 방법을 계속 생각하였다 <br>
위에 대한 방안을 찾던 중 이벤트 기반 아키텍쳐 라는 개념을 알게되었다 <br>

이벤트 라는 단어는 생소하였지만 궁금해져서 공부한 내용을 적어보려고 한다 <br>

### Event 란?
이벤트라 함은 어플리케이션 내에서 발생한 요청 및 상태 변화를 의미하며 이를 다른 컴포넌트에 알리는 역할을 한다 <br>
ex) 주문 취소 요청이 오면 -> 주문 취소 이벤트가 발생함 <br>

이벤트 기반 아키텍처에는 이벤트를 중심으로 시스템이 동작하며, 이벤트를 발생시키는 발행자(publisher) , 이를 처리하는 구독자(subscriber) 가 존재한다 <br>

위 역할을 통해 코드의 강결합을 제거하며 느슨하게 연결을 할 수 있다 <br>

결론은 이벤트가 발생했다는 것은 어떠한 '**상태**' 가 변경되었다는 것을 의미한다 <br>

## SpringBoot 에서 Event 처리 하는 방법
아래 코드를 보자 (코드 감소를 위해 Lombok 을 사용한다)
```java
@RequiredArgsConstructor
@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final ProductionItemRepository productionItemRepository;
	
	@Transactional
	public void createPayment(RequestDto dto) {

		// dto 값을 받아서 payment 를 생성한다.
		
		// dto 에 값을 토대로 product_item 을 조회하고 item 을 찾는다.
		
		// item 이 존재한다면 payment 에 상태를 변경한다.
	}
}

```

위 메소드에는 3가지 동작이 있다. <br>
- payment 생성
- product_item 조회
- payment 상태 변경

User 의 요청에 따라 비즈니스 로직 흐름이 되고, 마지막에는 payment 상태를 변경하게 된다 <br>
위 내용을 이벤트로 대입하였을 때 '결제 요청 이벤트' 가 발생했고 위에 따른 이벤트 처리를 생각할 수 있다 <br>

위와 같은 코드에서 명확한 한계가 있다 <br>
바로 1개의 트랜잭션이 생성된 후에 3가지 DB 요청이 발생하게 된다. <br>

단일 트랜잭션 내에서 세 가지 DB 요청(생성, 조회, 상태 변경)이 발생하면 트랜잭션 범위가 커지고, 한 단계라도 실패 시 전체가 롤백된다. <br>
이는 코드의 강결합과 트랜잭션 부담을 늘린다, 결론적으로는 1개의 트랜잭션이라도 실패하게 되면 에러를 뱉으며 rollback 하게 된다 <br>

위 코드를 어떻게 개선해야 할까? <br>

사실 방법이야 많다. 메소드 분리 또는 단일 책임 원칙을 지키게끔 설계를 진행한다거나 등등 여러 방법이 있을거라고 생각한다 <br>

하지만 위 글에서는 이벤트 기반 내용을 다룰 것이기에 이벤트 기반으로 해결책을 제시해보려고 한다 <br>

이벤트 기반 아키텍처를 적용하면 컴포넌트 간 강결합이 줄어들고, 트랜잭션이 분리되어 코드가 더 유연해진다. <br>
하지만, 동기 처리만으로는 성능 향상이 제한적이므로 성능향상 목적 또한 있다면 비동기 처리를 함께 고려해야 실질적인 성능 이점을 얻을 수 있다. <br>

기본적으로 Spring 은 ApplicationEvent 를 상속한 ApplicationEventPublisher 를 통해 이벤트를 발행하며 <br>
ApplicationListener(=@EventListener) 가 발행된 이벤트를 처리한다 <br>

- 이벤트 발행: ApplicationEventPublisher
- 이벤트 처리 : ApplicationListener

기본적으로 SpringBoot 어플리케이션이 시작될 때 Bean 이 스프링 컨테이너에 등록될 때 @EventListener 가 붙은 메소드를 스캔하여 ApplicationListener 로 등록을 한다 <br>
이벤트가 호출되면 ApplicationContext 가 등록된 모든 Listener 를 찾아 이벤트 타입과 매칭되는 Listener 를 호출한다. <br>
Listener 메소드가 실행되며 이벤트 데이터를 처리 <br>

기본적으로 흐름은 위와 같다 <br>

좀더 풀어서 설명하면 아래와 같다
- SpringBoot 서버 실행 시 ApplicationContext 초기화:
  - @EventListener를 스캔 → ApplicationListenerMethodAdapter로 래핑 → ApplicationEventMulticaster에 등록.
- publishEvent() 호출 시:
  - SimpleApplicationEventMulticaster.multicastEvent()가 실행.
  - 등록된 리스너 중 이벤트 타입과 일치하는 리스너를 찾아 호출.
  - @Async가 있으면 TaskExecutor를 통해 비동기 실행.

좀더 딥하게 가보자면 아래와 같다

- (3) 내부 동작 (핵심 컴포넌트)
- ApplicationContext: 이벤트 발행과 리스너 관리를 담당하는 중앙 허브. AbstractApplicationContext에서 publishEvent()를 호출하면 내부적으로 ApplicationEventMulticaster에게 위임.
- ApplicationEventMulticaster: 이벤트를 실제로 브로드캐스트하는 역할. 기본 구현체는 SimpleApplicationEventMulticaster로, 리스너 목록을 관리하고 이벤트를 순차적으로 전달.
- 동기 실행: 기본적으로 리스너를 같은 스레드에서 순차 호출.
- 비동기 실행: @Async를 추가하거나 SimpleApplicationEventMulticaster에 TaskExecutor를 설정하면 별도 스레드에서 실행.

추가적으로 이벤트를 처리할 객체는 Spring Bean 에 등록이 될 수 있게 해야 한다 <br>


로직 호출 순서를 그림으로 보면 아래와 같다.
```java
[PaymentService] → publishEvent(PaymentStateUpdatedEvent)
                     ↓
          [ApplicationContext]
                     ↓
    [ApplicationEventMulticaster] → 등록된 리스너 탐색
                     ↓
       [PaymentStateUpdatedListener] → handlePaymentStateUpdated()
```

아래 코드는 위 흐름을 토대로 이벤트 + 비동기 처리 코드를 적용해본 내용이다 <br>

- 이벤트 발행 클래스 정의
```java
@Getter
public class PaymentStateUpdatedEvent extends ApplicationEvent {
	private final RequestDto requestDto;

	// 발행할 이벤트를 정의한다. -> 이벤트를 발행한다
	public PaymentStateUpdatedEvent (Object source, RequestDto requestDto) {
		super(source);
		this.requestDto = requestDto;
	}

}
```

- 이벤트 발행 로직 할당
```java
@RequiredArgsConstructor
@Service
public class PaymentService {
	private final PaymentRepository paymentRepository;
	private final ProductionItemRepository productionItemRepository;

	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public void createPayment(RequestDto dto) {

		// dto 값을 받아서 payment 를 생성한다.

		// dto 에 값을 토대로 product_item 을 조회하고 item 을 찾는다.

		// item 이 존재한다면 payment 에 상태를 변경한다 -> 상태변경 이벤트 발생
		eventPublisher.publishEvent(new PaymentStateUpdatedEvent(this, dto));
	}
}
```

- 이벤트 처리 클래스 정의
```java
@RequiredArgsConstructor
@Component
public class PaymentStateUpdatedListener {
	private final PaymentRepository paymentRepository;

	@Async // 비동기 적용
	@EventListener(PaymentStateUpdatedEvent.class)
	@Transactional // 비동기 이므로 다른 트랜잭션을 이용한다.
	public void handlePaymentStateUpdated(PaymentStateUpdatedEvent event) {
		RequestDto dto = event.getRequestDto();
		
		if(dto.getAmount < 100000) {
			paymentRepository.updateState("BIG");
		}
	}
}

```

간단하게 보면 위 3가지 클래스를 통하여 이벤트를 발행하고 이벤트를 처리할 수 있다 <br>

생각보다 간단하다는 생각이 들지 않나? <br>
비교적 쉽게 코드 강결합을 줄였다 <br>

PaymentService 는 상태 업데이트 로직을 알 필요가 없다. 그냥 이벤트만 발행하면 끝이다 <br>

코드 의존성을 줄여야겠다는 내 1차적인 목표는 완성한 것 같다는 생각이 든다 <br>

단점을 뽑자면 단순 동기적 흐름보다는 코드 설계가 복잡하며 여러 리스너가 이벤트를 동시에 처리해야 한다면 실행 순서 보장 설정이 필요하다.<br>

나중에 위 장점들을 활용하며 나중에 로직에 트리거 같은 기능이 필요할 때 유용하게 사용할 수 있을거라는 생각이 든다 <br>

추가적으로 이벤트 처리를 더 잘 활용하기 위해서는 아래의 설정또한 있다.
- 조건 필터링 ex) @EventListener(condition = "#event.id > 100")
- 우선순위 지정 ex) @Order(1)

위 부분은 나중에 다시 다뤄보려고 한다 <br>

위 이벤트 기반 아키텍처랑 비슷한게 바로 Kafka & RabbitMq 같은 메시징 시스템이다 <br>
추후에는 분산환경에서 kafka 를 통해 이벤트 구현을 해본 후 글을 작성해보려고 한다 <br>


code: https://github.com/Hyeonqz/Hyeonq-Lab/tree/master/spring-lab/src/main/java/org/hyeonqz/springlab/event_example
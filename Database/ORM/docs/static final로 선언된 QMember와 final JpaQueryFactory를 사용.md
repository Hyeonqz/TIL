# 전역 static final 으로 선언된 Q클래스 를 사용하는 방법은 동시성에서 안전한가?

실무에서 Custom Repository 를 만들어서 queryDSL 을 사용하는 중이다 <br>
그리고 일반적으로 현재는 로직을 아래와 같이 작성하고 있었다 <br>

```java
public Payment findById(Long id) {
	QPayment qp = QPayment.Payment;
	
	return jpaQueryFactory.selectFrom(qp)
        .where(qp.id.eq(id))
        .fetchOne(); 
}
```

대충 위와 같은 식으로 메소드 내부에 Q클래스를 생성해두고, 위 클래스를 메소드 내부에서 사용하고는 했다 <br>

개발 중에 문득 궁금한 생각이 들었다 <br>
메소드 별로 Q클래스를 매번 생성하면, 메소드가 많아지면 매번 쿼리가 실행될 때 마다 새로운 인스턴스를 생성하는 것은 불필요하지 않나? 라는 생각을 했다 <br>
추가적으로 공유된 엔티티를 사용한다는 것은 문제가 생길 것이라는 생각을 했기에 안전한지에 대한 고민이 제일 컷다 <br>

그래서 위 고민에 대한 내용을 정리해서 작성해 보았다 <br>

### 본론
Q 클래스는 Immutable 객체 이므로 thread-safe 하다 <br>
즉 내부 상태가 변경되지 않는 다는 뜻이다 <br>
그러므로 여러 스레드가 동일한 인스턴스를 안전하게 공유해도 안전하다 <br>

위 부분을 구글에서 찾았을 때 처음했던 고민 1가지를 해결할 수 있었다 <br>
여러 User 가 동일한 기능을 동시에 사용했을 때 문제가 될 것이라고 생각했는데 다행히도 위 부분은 thread-safe 하므로 걱정할 필요는 없을 것 같다 <br>

애초에 JpaQueryFactory 가 스프링 빈에 등록되어 있고, 위 클래스를 Spring JPA 의 EntityManger 를 사용하므로 스프링이 빈을 만드는 싱글톤에 의해 thread-safe 하다 <br>
추가적으로 스프링은 트랜잭션 단위로 EntityManger 를 관리하므로 동시성에 대하여 걱정할 필요는 없을 것 같다는 확신이 들었다 <br>

정리를 하면 아래와 같다.
- Q클래스는 불변 객체이므로 동시 접근에 안전하다
- JpaQueryFactory 는 스프링이 관리하는 EntityManger 를 사용하므로 트랜잭션 단위로 격리 된다.
- 즉 각 CRUD 요청은 별도의 트랜잭션에서 실행되므로 서로 영향을 주지 않는다.


실제 사용 코드는 아래와 같다
```java
@Repository
public class CustomMemberRepository {
    private static final QPayment qp = QPayment.payment; // 1번 생성
	
    private final JpaQueryFactory queryFactory;

	public List<Payment> findById() {
		return jpaQueryFactory.selectFrom(qp)
			.where(qp.id.eq(id))
			.fetchOne();
	}
}
```
```java
@Repository
public class CustomMemberRepository {
	private final JpaQueryFactory queryFactory;

	public List<Member> findPayments() {
		QPayment qp = QPayment.payment; // 매번 생성
		return jpaQueryFactory
			.selectFrom(qp)
			.fetch(); // 10만건 조회
	}
}
```
<br>

추가적으로 궁금했던 부분은 위처럼 인스턴스를 메소드 마다 생성하는 것과 전역에서 한번 생성하는 것에 성능 차이에 대한 궁금증 또한 있었다 <br>

#### 객체 생성 비용에 대한 차이
Q 클래스 객체 생성 자체는 매우 가벼운 작업이다 <br>
Q 클래스는 QueryDSL이 미리 생성한 정적 클래스이므로 인스턴스화 과정이 거의 비용이 없음 <br>
즉 스프링부트 어플리케이션이 생성될 때 컴파일 시점에 Immutable 하게 생성이 된다 (기능 동작 시점에 동적으로 생성되는 것이 아니였음..)<br> 

그리고 실제 힙 메모리 할당이 되는 인스턴스가 생성이 된다면 조금이라도 영향을 줄것이라고 생각했다 <br>

쿼리 실행 메소드가 동작할시에 매번 인스턴스가 생성이되면 실제 메모리 할당과 GC 부하는 있을 수 있지만 <br>
데이터베이스에 커넥션을 맺고 실제 조회하는 시간에 비하면 무시할 만한 수준이라고 한다 <br>

위에 대한 근거가 있나??

<br>

#### 실제 쿼리 실행 시간
10만건 조회시 대부분의 시간은 데이터베이스 I/O에 소요된다 <br>
커넥션을 생성하고 쿼리 생성과 실행 과정은 동일하므로, 실제 쿼리 성능에는 차이가 없다. <br>

<br>

#### 대략적인 시간 비교:
1) Q 클래스 객체 생성: 마이크로초 단위
2) 데이터베이스 조회: 수초 ~ 수십 초

따라서 전체 수행 시간에서 체감될 만한 성능 차이는 거의 없다 <br>
10만건 조회시 발생하는 시간의 99% 이상은 데이터베이스 I/O에 사용되기 때문이다 <br>

Java 객체 생성 비용 < DB 쿼리 실행 + 결과 반환 비용 <br>


1) 메소드 요청시 매번 생성할 때
![img_6.png](../image/img_6.png) <br>

2) static 으로 한번에 생성하고 사용할 때
![img_5.png](../image/img_5.png) <br>


사실상 거의 차의가 없는 수준이다 <br>
실제 서비스가 운영이 되는 상황에서 또한 미비한 차이일 것으로 판단한다 <br>

그러나 개발자는 조금이라도 안정적이고 좋은 소프트웨어를 만들어야 함으로 조금이라도 더 나은 선택을 해야한다 <br><br>

#### 결론적으로는 위와 같은 관행은 성능보다는 다른 관점에서 전역 선언을 선호한다
- 코드 가독성
  - 객체 재사용으로 인한 가비지 컬렉션 부하 감소
  - 일관된 코드 스타일 유지

    
만약에 어플리케이션 내부에서 실제 성능 최적화가 필요하다면 다른 부분에서 최적화 부분을 찾아야 한다
- 적절한 인덱스 설정
- 페이징 처리
- 필요한 컬럼만 조회
- 조인 최적화
- 캐싱 전략 수립

등등 다른 요인에서 성능 개선점을 찾는 것이 좋다고 생각한다 

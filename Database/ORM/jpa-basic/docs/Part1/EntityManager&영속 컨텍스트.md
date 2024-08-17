# EntityManager 와 영속 컨텍스트 
새로운 엔티티를 생성하면 EntityManage.save() 메소드에 의해 영속성 컨텍스트에 등록이 된다 <br>
영속성 컨텍스트에 있는 DB는 물리적으로는 존재하지 않는다, 물리적이라 함은 rdb 에 저장된 테이블을 의미한다 <br>
즉 영속성 컨텍스트는 메모리에 올라가 있는 것을 의미한다 <br>

EntityManager 는 영속 객체를 관리할 때 영속 컨텍스트 라는 집합을 사용한다 <br>
이 집합은 일종의 메모리 저장소로서 EntityManager 가 관리할 엔티티 객체를 보관한다 <br>
그리고 EntityManager 는 DB 에서 읽어온 엔티티 객체를 영속 컨텍스트에 보관하고 save() 로 저장한 엔티티 객체 역시 영속 컨텍스트에 보관한다 <br>

즉 엔티티를 실질적으로 관리하는건 영속 컨텍스트 지만, 영속 컨텍스트를 관리하는 건 EntityManager 이다 <br>
EntityManager 는 트랜잭션 커밋 시점에 (또는 명싲거으로 flush()) 를 하면 영속 컨텍스트에 보관된 영속 객체의 변경 내역을 추적해서 DB 에 반영한다 <br>
데이터가 바뀐 객체는 update 쿼리를 이용해서 변경하고, 새롭게 추가된 객체는 persist() 를 통해 삽입한다 <br>

JPA 는 영속 컨텍스트에 보관한 엔티티를 구분할 때 식별자를 사용한다 <br>
즉 영속 컨텍스트는 (엔티티 타입 + 식별자) 를 키로 사용하고 엔티티 값으로 사용하는 데이터 구조를 갖는다 <br>
실제 하이버네이트는 맵을 사용해서 영속 컨텍스트를 구현하고 있다 <br>

## 영속 컨텍스트와 캐시
영속 컨텍스트는 (엔티티+식별자) 를 키로 사용하는 일종이ㅡ 보관소 이다 <br>
EntityManager 입장에서 영속 컨텍스트는 동일 식별자를 갖는 엔티티에 대한 캐시 역할을 한다 <br>
영속 컨텍스트는 Map 으로 구성되어 있어, Key:Value 구조를 가지고 있다 <br>

아래 코드를 보자
```java
EntityManager em = EntityMangerFacotr.createEntityManager();
try {
    User user1 = em.find(User.class, "1");	
    User user2 = em.find(User.class, "1");	
}
```

위 코드는 같은 엔티티 객체를 가르키는 엔티티를 조회하는 로직이다 <br>
이론상 으로는 조회가 2번 일어나야 하지만, 2번 째 find() 에서는 select 조회가 일어나지 않는다 <br>
왜 그럴까?? <br>

바로 캐시 때문에 그런것이다 <br>

select 쿼리를 날리는 대신 미리 날려둔 find() 를 통해 영속 컨텍스트에 보관된 같은 식별자를 갖는 엔티티 객체를 찾아 리턴한다 <br>

위 캐시는 EntityManger 를 종료하기 직전까지만 유효하다 <br>

## EntityManger 의 종류
1) 애플리케이션 EntityManger
   - 일반적으로 트랜잭션이 시작될 때 사용하는 것
2) 컨테이너 관리 EntityManager
   - @PersistenceContext 를 사용해서 구현한다.

## 트랜잭션 타입
JPA 는 자원 로컬 트랜잭션 타입과 JTA 타입의 두가지 트랜잭션 타입을 지원한다

### 자원 로컬 트랜잭션 타입
보통 우리가 사용하는 방식이다.
```java
EntityManager em = EntityMangerFactory.createEntityManager();
EntityTransaction et = em.getTransaction();
et.begin(); // 트랜잭션 시작
```

위 방식을 의미한다.

### JTA 타입
위 방식은 JPA 에서 트랜잭션을 관리하지 않는다 <br>
대신 EntityManager 를 JTA 트랜잭션에 참여시켜 트랜잭션을 관리한다 <br>

### EntityManager 의 영속 컨텍스트 전파
애플리케이션 로직을 수행하는 서비스와 영속성 관리하는 레포지토리로 분리해서 구현한다 <br>
보통 서비스는 트랜잭션을 관리하는 주체가 된다 <br>

즉 서비스 메소드의 시작 시점에 트랜잭션을 시작하고, 서비스 메소드의 종료 시점에 트랜잭션을 커밋한다 <br>

#### ThreadLocal 을 이용한 애플리케이션 관리, EntityManager 의 전파
ThreadLocal 은 쓰레드 단위로 객체를 공유할 때 사용하는 클래스다 <br>
이 클래스를 사용하면 한 메소드에서 호출하는 메소드가 동일한 객체를 공유할 수 있다 <br>

#### 컨테이너 관리 EntityManger 의 전파
```java
@PersistenceContext
EntityManger em;
```

위 코드를 통해 EntityManger 를 관리하면 항상 JTA 트랜잭션 타입을 사용해야 한다<br>
EntityManger 는 글로벌 트랜잭션 범위에 속하게 된다 <br>

스프링도 @PersistenceContext 어노테이션을 지원한다 <br>
JPA 표준에 정의된 컨테이너 관리 EntityManager 와 차이가 있다면 스프링은 애플리케이션 관리 EntityManger 에 대한 <br>
@PersistenceContext 도 지원하고 스프링이 제공하는 트랜잭션 범위에 묶인다는 점이다 
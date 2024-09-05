# JPQL
JPQL 은 JPA Query Language 약자로 JPA 에서 사용하는 쿼리이다 <br>
JPQL 의 쿼리 언어는 SQL 의 쿼리 언어와 매우 유사하다 <br>

이런 JPQL 은 엔티티 필드를 조회하거나 특정 조건을 이용해서 엔티티를 검색할 때 주로 사용한다 <br>

## JPQL 기본 코드
> select 별칭 from 엔티티이름 as 별칭
>> ex) select u from User

from 뒤의 엔티티이름은 @Entity 를 적용한 클래스의 이름이다 (또는 @Entity(name="?") name 속성을 이름을 사용한다<br>
별칭은 JPQL 에서 엔티티를 참조할 때 사용할 이름이다, **별칭은 필수** 이다 <br>

JPQL 을 실행하기 위해서는 EntityManger.createQuery() 메소드를 이용해서 TypedQuery 객체를 생성해야 한다 <br>
```java
EntityManager em = EMFUtils.currentEntityManager();
TypedQuery<User> query = em.createQuery("select u from User u", User.class);
List<User> users = query.getResultList();
```

TypedQuery.getResultList() 는 JPQL 을 실행하고 그 결과를 리턴한다 <br>

다른 방법으로는 TypedQuery 말고 Query 인터페이스를 사용할 수도 있다 <br>

TypedQuery 인터페이스는 Query 인터페이스를 상속받는다 <br>
return 타입이 명시적으로 알고 있다면 TypedQuery 를 사용하고 결과 타입을 모른다면 Query 인터페이스를 사용하자<br>

#### order by 를 이용한 정렬
```java
select u from User u order by u.id desc
```

#### 검색 조건 지정
일반 sql 과 같이 검색 조건을 지정해서 시용할 수 있다 <br>
> "select u from User u where u.id = '10'"

보통은 이름 기반의 파라미터 형식을 가진다.
```java
EntityManager em = EMFUtils.currentEntityManager();
TypedQuery<User> query = em.createQuery(
	"select u from User u where u.id =userID", User.class
);
query.setParameter("userId", 1L);
List<User> users = query.getResultList();
```

#### 비교 연산자
SQL 의 비교 연산자처럼 where 비교 연산자를 사용할 수 있다 <br>

#### 컬렉션 비교
Player 엔티티와 1:N 연관을 가지는 Team 엔티티가 있다고 보자 <br>
```java
EntityManager em = EMFUtils.currentEntityManager();
Player player = em.find(Player.class, "1");
TypedQuery<Team> query = em.createQuery(
	"select t from Team t where :player member of t.players order by t.name",
	Team.class
);
query.setParameter("player", player);
List<Team> teams = query.getResultList();
```

member of 를 통해 컬렉션 또한 조회할 수 있다 <br>

### 특정값 존재 체크 -> exists, all, any
> select u from user u where exists (select r from review r where r.user = u) order by u.name

## 페이징 처리
Query 와 TypedQuery 를 사용하면 간단하게 페이징 처리를 할 수 있다<br>
- setFirstResult(int start) : 조회할 첫 번째 결과 위치 지정
- setMaxResults(int max) : 조회할 최대 개수를 구한다.

```java
EntityManager em = EMFUtils.currentEntityManager();
TypedQuery<Review> query = em.createQuery(
	"select r from Review r" + 
		"where r.hotel.id = :hotelId order by r.id desc", Review.class
);
query.setFirstResult(10);
query.setMaxResults(5);
	
List<Review> reviews = query.getResultList();
```

위 메소드를 통해 10번부터 보여줄 것이고 한 번에 5개씩 보여줄 것임을 명시했다 <br>

### 지정 속성 조회
전체 엔티티가 아니라 특정 속성만 조회할 수 있다 <br>
Select 에 조회할 컬럼이 2개 이상일 때 결과 타입은 Object 로 지정한다 <br>
```java
TypedQuery<Object[]> query = em.createQuery(
	"select r.id, r.rate, r.comment from Review r", Object[].class
);
List<Object[]> list = query.getResultList();
```

### 한개 행 조회
```java
TypedQuery<Long> query = em.createQuery("select count(p) from Player p", Long.class);
Long count = query.getSingleResult();
```

### Join
JPQL 에서는 3가지 조인을 수행할 수 있다 <br>
- 자동 조인
- 명시적 조인
- where 절에서 조인

자동조인은 연관된 엔티티 속성에 접근할 때 발생한다 <br>
> select p from Player p where p.team.name = :teamName

위 쿼리를 보면 p.team.name 조건이 있기에 엔티티 연관에 의해 자동으로 join 을 날린다. <br>

명시적 조인은 쿼리에 join 을 넣기마녀 하면 된다
> select p from Player p join p.team where t.name = :teamName

위 join 은 inner join 을 이용해 두 테이블을 조인한다 <br>

### 집계함수
- count : Long
- max, min : 해당 타입
- avg : Double
- sum : 해당 타입

> select count(p), avg(p.salary), max(p.salary) from Player p
 
위 쿼리처럼 작성을 할 수 있다 <br>

### 함수와 연산잔
집계 함수외에 문자열과 수치 연산을 위한 기본 함수를 지원한다 <br>

#### 문자열 함수
- concat
- substring
- trim
- lower
- upper
- length
- locate

등이 있다.

#### 수학 함수
- abs
- sqrt
- mod
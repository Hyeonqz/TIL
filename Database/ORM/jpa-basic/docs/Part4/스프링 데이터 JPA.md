# Spring Data JPA 
```java
public class UserRepository {
	@PersistenceContext
    private EntityManager em;
	
	public void save(User user) {
		em.save(user);
    }
}
```

위처럼 Hibernate(=JPA) 기능을 사용해서 crud 를 진행할 수 있다 <br>

하지만 Spring 프레임워크에 스프링 데이터 JPA 의존성을 이용해서 많은 중복 코드 작성을 줄일 수 있다 <Br> 

Spring Data JPA 에 미리 추상화시켜둔 인터페이스를 통해 UserRepository 를 구현할 수 있다.
```java
public interface UserRepository extends Repository<User,String> {
	User findOne(String email);
	User save(User user);
	void delete(User user);
	@Query("select u from User u order by u.name")
    List<User> findAll();
}
```

Repository 는 스프링 데이터 JPA 가 제공하는 인터페이스 이다 <br>
이 인터페이스를 상속받고 정해진 규칙에 맞게 메소드를 작성하면 된다 <br>

SpringBoot 에서는 @EnableJpaRepositories 가 자동설정이 된다 <br>
https://parkadd.tistory.com/106 아래 글을 보면 원리를 통해 조금이해가 될 수도 있다 <br>

### 레포지토리 인터페이스 메소드 작성 규칙
예전에는 Repository 라는 인터페이스를 상속받아 사용했지만, 현재는 기본 메소드가 다 구현된 JpaRepository 인터페이스를 상속받아 사용하면 된다 <br>

#### 한개 결과를 조회하고 싶다면 엔티티 타입을 return 타입으로 하면 된다.

JPA 확장 기능 <br>
- https://velog.io/@neity16/5-%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-JPA-6-%ED%99%95%EC%9E%A5-%EA%B8%B0%EB%8A%A5-1-%EC%82%AC%EC%9A%A9%EC%9E%90-%EC%A0%95%EC%9D%98-repository-Auditing-CreatedDate-LastModifiedDate


스프링 데이터 JPA 를 사용하면 구현 코드 뿐만 아니라 중복된 메소드 마저 작성하지 않아도 된다 <br>
- Repository -> CrudRepository -> PagingAndSortingRepository -> JpaRepository

이런식의 흐름을 가진다. 그리고 우리가 사용하게 되는 것은 JpaRepository 를 사용하게 될것이다 <br>



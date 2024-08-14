# Entity
## Entity 클래스
JPA 에서 엔티티는 영속성을 가진 객체로서 가장 중요한 타입이다 <br>
JPA 의 엔티티는 DB 테이블에 보관할 대상이 된다 <br>

EntityManager 를 사용해서 엔티티 단위로 저장하고 조회하고 삭제한다 <br>

JPA 는 @Entity 어노테이션을 사용한 설정 및 XML 을 통한 설정이 있다 -> @Entity 방식 사용하자.. 가독성이 에바다 XML 은 <br>

아래 자주 쓰는 JPA 어노테이션이 있다.<br>
- @Entity : 영속성 컨텍스트 등록
- @Id : 엔티티의 식별자 등록, 엔티티를 구분하기 위해 사용한다.
  - @Id 어노테이션을 적용한 필드 값은 EntityManger.find() 메소드에서 엔티티 객체를 찾을 때 식별자로 사용한다.
  - 보통 테이블에서 Primary Key 컬럼에 @Id 를 매핑한다.
- @Basic : 위 어노테이션은 사실상 붙어 있는 것인데 생략되어있다.
  - JPA 는 영속 필드가 ing,long,String 같은 기본 타입일 경우 @Basic 을 사용한다
  - 사실상 어지간한 타입에는 다 붙어있다, 하지만 생략되어 있을 뿐이다.
- @Temporal : java.sql.Date(Time,TimeStamp) 를 매칭해준다. 
- @Enumerated : Enum 타입 매핑시 사용한다. 보통 (EnumType.STRING) 이랑 같이 쓰인다, 숫자 인 경우(EnumTYPE.ORDINAL) 
  - 위 어노테이션은 Enum 타입 사용시 꼭 해줘야 한다.
- @Column : 자바 필드 이름이랑 DB 컬럼 이름이랑 다를 때 사용함.
  - @Column(insertable=false, updatable=false) 를 통해 삽입,수정이 안돼는 읽기 전용 컬럼으로 설정할 수 있다.


JPA 매핑은 필드에만 설정할 수 있는 것은 아니다. get,set 메소드를 사용해서 엔티티 객체를 DB에 반영할 수도 있다 <br>
DB 에서 데이터를 읽어와 엔티티 객체에 전달할 때는 set 메소드를 사용한다 <br>
반대로 엔티티에서 DB 에 반영할 떄는 get 메소드를 사용한다 <br>
그러므로, set/get 메소드를 모두 정의해놔야 한다 <br>

기본적으로 필드 접근 방식을 사용하는데 특정 영속 대상에 대해서만 프로퍼티 접근 방식을 사용해야 한다고 하면 @Access 어노테이션을 사용한다 <br>
```java
@Entity
public class Room {
	@Id
    private Long id;
	private String name;

	@Access(AccessType.PROPERTY)
    private Long dbId;
}
```

위처럼 이미 필드 방식을 사용해서 데이터 읽고 쓰기를 할 때 한개의 컬럼만 프로퍼티 방식을 사용하고 싶을때 위처럼 사용하면 된다 <br>
프로퍼티만 사용해서 매핑을 하는건 효율적이지 않다고 생각함 -> 개인적인 생각임 <br>
왜냐? 객체지향 관점에서 캡슐화를 약화시키기 때문이다 <br>

### 영속 대상에서 제외하기
필드 접근 타입을 사용하는데, 영속 대상이 아닌 필드가 존재한다면 **@Transient** 키워드를 사용해서 영속 대상에서 제외할 수 있다 <br>

```java
import java.beans.Transient;

@Transient
private long timestamp = System.currentTimeMillis();
```

### 엔티티 클래스의 제약 조건
1) 엔티티 클래스는 인자가 없는 기본 생성자가 있어야 한다
2) 파라미터가 있는 생성자를 정의하면, 기본 생성자가 자동으로 생성되지 않으므로, 인자를 가진 생성자가 필요하다면 반드시 기본 생성자도 함께 정의해야 한다.
   - 기본 생성자의 접근 범위는 public, protected 를 권장한다. private 일 경우 Jpa 특정 기능이 작동안할 수도 있다.
3) 엔티티는 클래스여야 한다.
4) 엔티티 클래스는 final 이면 안된다.
   - Hibernate 에 따라 지연 로딩과 같은 기능을 제공하기 위해 엔티티 클래스를 상속받은 프록시 객체를 사용하는데, 엔티티 클래스가 final 이면 상속 기반 프록시 생성 불가능하기 때문

엔티티 클래스가 Serializable 인터페이스를 구현해야 할 수도 있다. <br>
Hibernate 가 캐시 구현 기술이 Serializable 인터페이스를 요구하는 경우 엔티티 클래스가 Serializable 인터페이스를 상속해야 한다 <br>
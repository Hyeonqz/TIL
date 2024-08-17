# Value & @Embeddable
예시를 보자 <br>
컬럼이 이름 번호 주소 가 있다 <br>
이름 과 번호는 고유하개 1가지만 되지만, 주소의 경우는 우편번호, 도로명주소, 집주소 이런식으로 세분화가 될 수 있다 <br>

여기서 주소 -> Value 라고 부른다 <br>
JPA 에서 Value 는 개념적으로 한 개의 값만 표현한다. 하지만 내부에는 3개의 컬럼을 가지고 있다 <br>

## Value 클래스의 구현
- 생성 시점에 모든 프로퍼티를 파라미터로 전달 받는다 
- 읽기 전용 프로퍼티만 제공한다.
- 각 프로퍼티 값을 비교하도록 equals() 메소드를 재정의 한다.
- 각 프로퍼티 값을 이용해서 해시코드를 생성하도록 hashCode() 메소드를 재정의한다.

```java
public class Address {
	private String zipcode;
	private String address1;
	private String address2;

	public Address (String zipcode, String address1, String address2) {
		this.zipcode = zipcode;
		this.address1 = address1;
		this.address2 = address2;
	}

	public String getZipcode () {
		return zipcode;
	}

	public String getAddress1 () {
		return address1;
	}

	public String getAddress2 () {
		return address2;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Address address = (Address)o;
		return Objects.equals(zipcode, address.zipcode) && Objects.equals(address1, address.address1)
			&& Objects.equals(address2, address.address2);
	}

	@Override
	public int hashCode () {
		return Objects.hash(zipcode, address1, address2);
	}

}
```

위 Value 의 경우 Set 메소드를 지원하지 않는다. 말 그대로 읽기 전용이기 때문이다 <br>
만약 필드를 수정하고 싶다면 Address 객체를 할당하는 메소드를 구현해서 작업해야 한다 <br>
```java

@Entity
public class Hotel {
	@Embedded
	private Address address;
}

@Embeddable
public class Address {
	private String zipcode;
	private String address1;
	private String address2;

}
```

- @Embedded 클래스는 매핑 대상이 @Embeddable 클래스의 인스턴스 라는 것을 설정한다.
  - 위 설정을 사용하면 address 필드의 매핑을 처리할 때 Address 에 설정된 매핑 정보를 이용한다.
- 위 설정을 하고 만약 트랜잭션 범위 안에서 address 필드가 변경이 되면 더티체킹이 실행된다.
- @Embeddable 접근 타입 -> 필드 접근 타입을 가짐.

### @Entity 와 @Embeddable 의 라이프 사이클
@Embedded 로 매핑한 객체는 엔티티와 동일한 라이프 사이클을 가진다 <br>
즉 엔티티 트랜잭션이 발생할 때 @Embeddable 객체도 함께 트랜잭션이 발생한다 <br>

### @AttributeOverrides 를 이용한 매핑 설정 재정의
만약에 Address 를 한국 주소, 영어 주소 2개로 관리를 하고 싶다 <br>
@Embeddable 객체는 1개인데 이럴 땐 어떻게 관리를 해야 할까? <br>
만약 필드에 같은 @Embedded 가 2개라면 MappingException 이 발생한다<br>

이럴때 @AttributeOverrides 어노테이션을 사용하면 매핑 값 타입을 재정의하여 해결할 수 있다.<br>
```java
@Entity
public class Hotel {
	@Id
	private Long id;
	private String name;

	@Enumerated(EnumType.STRING)
	private Grade grade;

	@Embedded
	private Address korAddress;
	
	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="zipcode", column = @Column(name="eng_zipcode")),
		@AttributeOverride(name="address1", column = @Column(name="eng_addr1")),
		@AttributeOverride(name="address2", column = @Column(name="eng_addr2"))
	})
	private Address engAddress;
}
```

위 처럼 설정하면 korAddress 는 기본 매핑되로 컬럼이 생성이되고 아래 engAddress 컬럼은 위에 설정한 대로 매핑이 된다 <br>

### @Embeddable 중첩
@Embeddable 객체에 또 다른 @Embeddable 객체를 중첩해서 매핑할 수 있다 <br>
엔티티 -> @Embeddable -> @Embeddable 의 하위 @Embeddable

```java
// 상위 임베더블 Value
@Embeddable
public class Address {
	private String zipcode;
	private String address1;
	private String address2;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name = "name", column = @Column(name = "city_name")),
		@AttributeOverride(name = "location", column = @Column(name = "city_location"))
	})
	private City city;

}

// 하위 임베더블 Value
@Embeddable
public class City {
  private String name;
  private String location;
}

// 엔티티 설정 
@Embedded
@AttributeOverrides({
        @AttributeOverride(name="address.zipcode", column = @Column(name="eng_zipcode")),
        @AttributeOverride(name="address.address1", column = @Column(name="eng_addr1")),
        @AttributeOverride(name="address.address2", column = @Column(name="eng_addr2"))
})
private Address engAddress;
```

### 다른 테이블에 Value 저장하기
@SecondaryTable 은 데이터의 일부를 다른 테이블로 매핑할 때 사용한다 <br>

### @Embeddable 과 복합키
- Serializable 인터페이스을 구현받는다.
- @Embeddable 객체에 id 를 생성하고 복합키로 사용한다.
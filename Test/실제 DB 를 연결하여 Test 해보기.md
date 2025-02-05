## 실제 DB 를 연결하여 Test 를 진행해보자
DB 를 연결하여 Test 를 해보자 <br>

필자의 개인적인 생각으로는 C,U,D 를 진행할 때는 실제 DB 에 영향을 주지않게 Embedded DB 인 H2 를 사용하여 테스트를 하는게 적합하다고 생각한다. <br>
조회를 하는 경우 또한 내장 DB 를 사용하여 진행할 수 있지만, 실제 값을 보기 위해서는 MySQL, Postgres 같은 DB 연결을 하여 사용하는게 좋다고 생각한다 <br>

실제 운영 DB 를 사용할 수 없다면 H2 DB를 사용하여 아래와 같은 코드를 작성한 후에 실제 조회 테스트를 진행할 수도 있습니다.
```java
@BeforeEach
public void setUp() {
    for(int i=0; i<1000; i++) {
		// insert 로직
	}
}
```

기본적으로 내장 DB 및 실제 DB 를 접근하기 위한 방법이 있다.
```java
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class Test {
	// 
}
```

위 어노테이션은 내장 DB 를 사용하지 않겠다는 어노테이션이다 <br>

반대로 아래 코드는 내장 DB 를 사용하겠다는 걸 명시하는 어노테이션이다.
```java

```






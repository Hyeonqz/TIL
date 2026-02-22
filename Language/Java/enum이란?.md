# Enum 이란?
#### Enumerate 의 약자로, 열거형 이라고 불린다.

- Enum 은 상수의 집합이며, 상수와 관련된 로직을 담을 수 있는 공간이다.
    - 상태와 행위를 한 곳에서 관리할 수 있는 추상화된 객체
- 특정 도메인 개념에 대해 그 종류와 기능을 명시적으로 표현해줄 수 있다.
- 만약 변경이 정말 잦은 개념은, Enum 보다 DB 로 관리하는 것이 나을 수 있다.

✅ 즉 Enum 은 코드에 박혀있는 값이기 때문에 Ex) 월화수목금, 봄여름가을겨울 처럼 절대 변하지 않는 값들을 저장한다. <br><br>

**[Enum 예시 코드]**
```java
public enum UserAction {
	OPEN("Cell Open"),
	FLAG("Flag put in."),
	UNKNOWN("I don t know")
	;
	
	private String description;

	UserAction (String description) {
		this.description = description;
	}
}
```

Enum 의 기본구조이고, 보통 실무에서는 description 을 통해 복잡한 비즈니스를 간단하게 설명을 할 수 있게 만들어 둔다. <br>

**[Enum 메소드에서 활용]**
```java
	public UserAction getUserActionFromUser () {
		try {
			String userInput = br.readLine();
			
			if("1".equals(userInput)) {
				return UserAction.OPEN;
			}
			if("2".equals(userInput)) {
				return UserAction.FLAG;
			} 
			return UserAction.UNKNOWN;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
```

Enum 클래스에 필드들은 Static 키워드가 붙은 필드 또는 메소드 처럼 객체를 생성하지 않고 바로 사용할 수 있다.

<br><br>

> REF: https://techblog.woowahan.com/2527/
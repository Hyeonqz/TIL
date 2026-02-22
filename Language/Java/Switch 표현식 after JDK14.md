# Switch 표현식 after JDK14 

> 전체적인 코드 및 내용은 https://openjdk.org/jeps/361 문서를 참고하여 작성하였습니다
> > 정리한 글은 https://hyeonq.tistory.com/213 포스팅 되어있습니다.

Switch ~ case 구조를 잘 안사용할 것이라는 착각을 했지만 가끔 사용을 하기도 했고 JDK14 이후 변환이 있다길래 공부한 내용을 포스팅 해보려고 한다 <br>

실무 관점에서 이런 생각도 한다. 새로 바뀐 문법을 적용하는게 무조건 가독성 향상 및 유지보수에 편할까? 라는 고민도 했다 <br>
지금 회사에서는 내가 뭔가 새로운 것을 쓰면 다 설명을 해줘야 한다. 사람들한테... 왜 먼저 찾아보지 않고 질문을 할까라는 생각도 하지만 일단 그런 이야기는 pass~ <br>


### JDK 14 이전 vs JDK 14 이후
#### JDK 14 이전
- 불필요한 반복코드가 사용됨
- 다수의 case, break 를 써야함
- break 안쓰면 case 끝까지 요청이감.

#### JDK 14 이후
- 람다식으로 표현도 가능
- 단일 수행 또는 블록 수행이 가능하다.
- switch 블록 내에서 계산된 값을 반환하는 yield 라는 키워드가 생김
- 여러 조건을 쉼표로 구분하여 한 라인으로 처리하기 가능


한번 예시를 보자
```java
enum Days { 
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY;
}
```


switch 내부에 input 을 정의한 Enum 클래스이다 

아래 코드는 반환값이 없을 때 코드 예시이다.
```java
	@Test
	void beforeJDK14 () {

		String day = Days.MONDAY.name();

		String result = switch (day) {
			case "MONDAY":
				System.out.println(Days.MONDAY.getDescription());
				break;
			case "TUESDAY":
				System.out.println(Days.TUESDAY.getDescription());
				break;
			case "WEDNESDAY":
				System.out.println(Days.WEDNESDAY.getDescription());
				break;
			case "THURSDAY":
				System.out.println(Days.THURSDAY.getDescription());
				break;
			case "FRIDAY":
				System.out.println(Days.FRIDAY.getDescription());
				break;
			case "SATURDAY":
				System.out.println(Days.SATURDAY.getDescription());
				break;
			case "SUNDAY":
				System.out.println(Days.SUNDAY.getDescription());
				break;
			default:
				System.out.println("그만 탈출한다 switch ~ case");
		};

	}
```

위 코드는 JDK 12 이전에 사용하던, switch ~ case 방식으로 저거 치는대 조금 손가락이 귀찮았다 <br>
개발자라면 코드를 최대한 덜 치면서 기능을 만들고 싶어할테니 그래서 나온게 JDK 12 이후에 람다 방식 switch ~ case 이다

```java
	@Test
	void usedLambdaSwitchCase() {
		String day = Days.MONDAY.name();

		// break 가 포함되어 있음 안써도 댐 이젠
		switch (day) {
			case "MONDAY" -> System.out.println(Days.MONDAY.getDescription());
			case "TUESDAY" -> System.out.println(Days.TUESDAY.getDescription());
			case "WEDNESDAY" -> System.out.println(Days.WEDNESDAY.getDescription());
			case "THURSDAY" -> System.out.println(Days.THURSDAY.getDescription());
			case "FRIDAY" -> System.out.println(Days.FRIDAY.getDescription());
			case "SATURDAY" -> System.out.println(Days.SATURDAY.getDescription());
			case "SUNDAY" -> System.out.println(Days.SUNDAY.getDescription());
			default -> System.out.println("탈출 ㅋ");
		}

	}
```

그래도 break 도 사라지고 나름 아까보다는 깔끔해졌다. <br>
하지만 실무에서는 위 같은 방식보다는 어떠한 값을 분기처리해서 가져올 떄 사용할 것 이다 <br>
그리고 System.out 보다는 log 방식을 채용할 수 밖에 없을 것이고 그래서 아래와 같은 코드 형태가 되야한다.

```java
@Test
void usedLambdaSwitchCase() {
	String day = Days.MONDAY.name();

	// 변수가 생김
	String today = switch (day) {
		case "MONDAY" -> Days.MONDAY.getDescription();
		case "TUESDAY" -> Days.TUESDAY.getDescription();
		case "WEDNESDAY" -> Days.WEDNESDAY.getDescription();
		case "THURSDAY" -> Days.THURSDAY.getDescription();
		case "FRIDAY" -> Days.FRIDAY.getDescription();
		case "SATURDAY" -> Days.SATURDAY.getDescription();
		case "SUNDAY" -> Days.SUNDAY.getDescription();
		default -> "그만 탈출한다 switch ~ case";
	};
}
```

즉 저 리턴되는 값이 today 라는 변수에저장이 되는 것이고 위 로직을 통해 분기처리를 할 수 있다.<br>
간단한 값을 리턴하는 단일 수행일 경우 위와 같은 코드를 작성해야 하지만 만약 단일 수행이 아니라 다른 처리가 더 필요하다면? <br>
그럴 떄는 JDK 14 에 나온 **'yield'** 라는 키워드를 사용할 수도 있다. <br>

그럼 이전에는 어떻게 여러개의 case 안에서 사용했을까?<br>
바로 맨 처음 봤던 코드처럼 사용을 했다
```java
String description;

switch(day){
    case Monday:
        description = "월요일";
		// 필요하다면 분기 로직 더 처리
		break;
}
```

대충 위와 같은 코드였지만, 그걸 좀더 깔끔하게 정리를 한게 아래와 같은 코드이다.

이제는 아래와 같이 조금더 좋게 사용을 해보자!
```java
@Test
void usedYieldSwitchCase() {
    String day = Days.MONDAY.name();

    String description = switch (day) {
        case "MONDAY" -> {
			log.info("아 시간 겁나 안가네");
			yield Days.MONDAY.getDescription();
			// System.out.println("시간이 안가.."); -> yield 이후로는 로직 작성 불가
		}
        case "TUESDAY" -> Days.TUESDAY.getDescription();
        case "WEDNESDAY" -> Days.WEDNESDAY.getDescription();
        case "THURSDAY" -> Days.THURSDAY.getDescription();
        case "FRIDAY" -> Days.FRIDAY.getDescription();
        case "SATURDAY" -> Days.SATURDAY.getDescription();
        case "SUNDAY" -> Days.SUNDAY.getDescription();
        default -> {
            yield "탈출 ㅋ"; // 복잡한 로직을 처리하는 경우 유용
        }
    };
	
}
```


참고로 yield 는 항상 switch 블록 내부에서만 사용할 수 있다. 아래 예시를 보자
```java
String today = switch (day) {
	case MONDAY -> "월요일!";
	case TUESDAY -> yield "화요일"; // yield는 block {} 안에서만 사용가능 -> 즉 에러남 
	case WEDNESDAY -> { yield "수요일";} // 이런 방식으로 사용해야함
    // ~~~
	default -> "탈출";
};
```

<br>
즉 단순 반환일 경우 '람다(->)' 사용, 추가 로직 필요시 'yield' 사용 <br>
그리고 자동 break 처리 또한 되기 때문에 편하다 <br>

둘을 차이를 정리해보자면 <br> 
- JDK 14 이전
  - 외부 변수 선언 후 값 할당
  - break 필요
  - 가독성 상대적으로 복잡
  - 각 케이스에서 외부 변수 조작
- JDK 14 이후
  - 외부 변수 없이 yield 를 통한 값 반환
  - break 불 필요
  - 가독성 좋음 -> 코드가 간결하기 때문
  - 각 케이스별로 yield 를 통한 반환값 직접 처리 


JDK 14 이후로 yield 를 통해 값을 반환하면서 흐름 제어를 자동으로 처리하므로 더 간결하고, switch 표현식을 효율적으로 사용할 수 있게 되었다. <br>
마지막 고민은 새로운 기능을 사용한 코드를 나중에 코드 리뷰 받으면 우리 어르신 형님들이 어떻게 생각할지 고민임.. 이상 <br>
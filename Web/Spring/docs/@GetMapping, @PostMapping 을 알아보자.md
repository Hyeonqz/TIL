## @GetMapping, @PostMapping 차이.md

SpringBoot 를 사용한다면 보통 MVC 패턴을 사용하여 개발을 할 것이다 <br>
그리고 MVC 패턴에 C 에 해당하는 Controller 에서 직접적으로 web 과 통신을 한다 <br>

그리고 스프링에서 웹과 통신하기 위해서는 @Controller, @RestController 를 선언함으로써 Http 통신을 할 수가 있다 <br>
```java
@RestController
public class PaymentController {
    
    @Getmapping("/api/payment")
    public String getPayment() {
        
        // 서비스 메소드
        
        return new PaymentDto();
    }
    
    @PostMapping("/api/create")
    public ResponseEntity<?> createPayment(PaymentCreateReq paymentCreateReq) {
        
        PaymentCreateRes paymentCreateRes = new PaymentCretaeRes();
        // 서비스 메소드
        
        return ResponseEntity.ok(paymentCreateRes);
    }
}
```

대표적으로 사람들이 알고 있기에 'CRUD' 작업 중에서 'R' 작업을 하면 @GetMapping 사용한다 <br>
'CUD' 작업을 하고 있다면 @PostMapping 사용으로 사용하고는 한다 <br>

좀 더 디테일하게 설명하자면 Update -> @PutMapping, @PatchMapping , Delete -> @DeleteMapping 통상적으로 이렇게 사용된다 <br>
(사실 @PostMapping 으로 모든게 해결이 되기는 한다) <br>

자세한 설명 https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html <br>

그리고 통상적으로 제일 많이 사용되는 @GetMapping, @PostMapping 두 가지 차이점을 완벽하게 알아보려고 한다 <br>

> 두 Mapping 은 사실 역할은 같지만 입력한 값이 어떻게 url 에 붙어서 전송이 되느냐에 따라 차이가 난다.

<br>

### @GetMapping
- 위 방식은 HttpHeaders 에 내가 입력한 값을 url 로 전송을 한다 <br>

그리고 기본적인 동작 방식은 '쿼리 스트링' 으로 전송이 된다 <br>
> localhost:8080/api/payment?name=팝콘&amount=10000

즉 요청 데이터를 Http Body 에 담지않고 Http Header 에 담아서 요청을 보내는 것이다 <br>

위 '?' 를 기점으로 값이 서버에 전달이 된다. 그리고 서버는 위 값을 가져와서 조회 작업 을 진행한다 <br>

그리고 Get 요청은 불필요한 요청을 줄이기 위해 요청이 자동으로 캐싱이 됩니다 <br>
즉 동일한 요청을 보낼 때 이미 1번 요청했던 것들을 기억해두고 있다가 서버로 재요청하지 않고 캐시된 데이터를 사용한다는 뜻이다 <br>

가끔 사람들이 인터넷 느리면 '캐시 및 쿠키 삭제' 를 하라고 하는 이유 또한 위 이유다 <br>

위 방법을 보면 알겠지만 내가 요청한 정보들이 모두 Header 에 나온다. 즉 보안상에 위험이 많다. 이점을 잘 인지하자. <br>

자세한 내용이 궁금하다면 개발자 도구를 켜두고 Network 탭에서 페이지 하나를 이동후에 api 하나 잡아서 Headers 부터 쭈욱 보면 더 자세히 알수있다 <br>

위 데이터를 서버로 안정적으로 가져오기 위해서 스프링에서는 @RequestParam 어노테이션을 붙여서 사용한다 <br>
```java
@GetMapping("/get")
public void get(@ReqeustParam String name) {
    // name 가져와서 처리
    return "";
}
```

localhost:8080/api/payment?name=팝콘 위 어노테이션을 통해 url 에 붙은 name 값을 가져와서 서버에서 처리를 할 수 있다 <br>


만약 위 쿼리 스트링 방식이 맘에 들지 않는다면 PathVariable 방식으로도 가능하다<br>
보통 위 방식은 검색 과 같은 조건이 아닌 다른 페이지를 보여줄 때 많이 사용이 된다 <br>
ex) https://section.cafe.naver.com/ca-fe/home <br>

위 처럼 '/' 를 섹션으로 나누어서 경로가 난잡하지 않게 보여준다 <br>


정리를 하자면
- 조회를 할 때 사용함
- 캐싱 가능
    - 브라우저 히스토리에 남음
- 보안상 이슈가 있으므로 중요한 정보 넣는건 피하자

<br>

### @PostMapping
POST 의 기본동작은 리소스를 생성/변경 하기 위해 설계 되었기 떄문에 데이터를 HTTP 메시지 Body 에 담아서 전송 한다 <br>
그리고 길이 제한이 따로 없기 때문에 대용량 데이터를 전송할 떄 많이 쓰인다. <br>

```java
@PostMapping("/create")
public ResponseEntity<?> create(@RequestBody RequestDto requestDto) {
    // 로직 처리
    return ResponseEntity.ok(new ResponseDto());
}
```

추가적으로 스프링에서는 요청 파라미터에 @RequestBody 어노테이션을 사용하여 Http Body 값 가져와 자바 객체로 변환(Serializable) 사용을 한다 <br>
스프링에서 내부적으로 HttpMessageConverter 를 사용해 JSON 데이터를 파싱하여 자바 객체로 변환을 해준다는 뜻 이다 <br>

```java
public record RequestDto(String name, Integer age) {
}

```

만약 @RequestBody 를 붙이지 않으면
```java
POST /create?name=jin&age=12 HTTP/1.1
Content-Type: application/x-www-form-urlencoded
```

위처럼 응답이 나올 것이고 <br>

즉 요청 Http body 에 내용을 처리하지 않고 HttpHeader 을 내용을 처리하게 된다 <br>


@RequestBody 가 붙어 있으면 <br>
```java
POST /approval HTTP/1.1
Content-Type: application/json

{
    "name": "jin",
    "age": 12
}
```

이런식으로 처리를 하게 될 것이다 <br>
즉 한줄 요약을 하자면 JSON 데이터를 다룬다면 그냥 @RequestBoyd 어노테이션은 필수로 사용해야 합니다 <br>

<br>

### Get, Post 차이 정리
- 사용 목적 이 다름
- 요청 데이터 위치가 다름 **(Get -> Header, Post -> Body)**
- 캐싱
- 멱등성
    - Get -> 같은 값 여러번 요청해도 값 변동 없음
    - Post -> 같은 값 여러번 요청시 값 변동 있을 수 있음

### 결론
- @GetMapping: 데이터를 조회하거나, URL 기반의 간단한 요청 처리에 적합.
- @PostMapping: 데이터를 생성하거나, 민감한 정보 또는 대용량 데이터를 안전하게 전송할 때 적합.
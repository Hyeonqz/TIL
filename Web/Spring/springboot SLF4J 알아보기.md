# @Slf4j 알아보기

### 개발 환경
> springboot3.5, jdk21, mysql8.0, Docker

<br>

## 1. 들어가며
<hr>
보통 springboot를 사용하여 개발을 하는 백엔드 개발자는 서비스 운영을 위해서 필수적으로 어플리케이션에 메시지(기록)을 남겨야 한다 <br>
그 메시지는 개발 용어에서는 로그 라고 부른다 <br>

개발을 하다보면 알겠지만, 로그를 안찍고 개발을 할 수는 없다 <br>
아무리 천재적인 개발자도 로그를 찍을 것이다.. <br>

로그를 찍는 이유는 간단하다고 생각한다 <br>
-> 운영의 편의성을 위해서 로그를 찍는다고 생각한다 <br>

어떠한 장애가 발생할시, 로그를 적절하게 남겨두었으면, 장애를 추적하고 해결하는데 도움이 많이 될 것이다 <br>
그리고 적절한 로그를 남겨놔야, 내가 아닌 다른 사람들이 서비스를 운영할 때도 더 빠르게 파악을 할 수 있을 것이라고 생각한다 <br>

그리고 위 로그 시스템을 사용하기 위해서는 보통 Lombok 을 `@Slf4j` 를 사용할 것이라고 생각한다 <br>  
위 어노테이션을 클래스 상단에 선언하면 그 클래스에서는 로그를 자유롭게 사용할 수 있다 <br>

Lombok 을 사용하지 않는다면 아래와 같은 방법으로 사용할 수 있다.
```java
private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
```

보통은 위처럼 매번 선언하는 것이 귀찮으므로, 특정 상황이 아니라면 @Slf4j 를 사용하는것이 일반적이다 <br>

위처럼 직접 선언하는 방식은 같은 어플리케이션이지만, 로그를 각각 다른 파일에 쌓아야 할 때 주로 사용이 된다 <br>
ex)
- producer.log
- consumer.log


이제 더 자세하게 springboot 로그 시스템 및 `@Slf4j` 에 대해서 알아보자 <br>


## 2. 본론
<hr>

위에도 말했듯이 @Slf4j 를 사용하는 대표적인 이유는 꽤 긴 코드를 매번 클래스 상단에 작성하기 귀찮아서가 제일 크다 <br>

```java
@Slf4j
public class PaymentService {
    public void pay() {
        log.error("결제 실패: {}", paymentId);     // 시스템 에러
        log.warn("결제 지연 발생: {}", paymentId);   // 경고
        log.info("결제 처리 완료: {}", paymentId);   // 일반 정보
        log.debug("결제 상세 정보: {}", paymentDetail); // 디버깅용
    }
}
```

위 어노테이션을 통해서 로그를 자유롭게 사용할 수 있다 <br>

그리고 위 코드를 컴파일시 어노테이션에 의해 아래처럼 클래스가 변환된다 <br>
```java
public class PaymentService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PaymentService.class);

    public void pay() {
        // 로그 레벨별 사용 시나리오 예시
        log.error("결제 실패: {}", paymentId);     // 시스템 에러
        log.warn("결제 지연 발생: {}", paymentId);   // 경고
        log.info("결제 처리 완료: {}", paymentId);   // 일반 정보
        log.debug("결제 상세 정보: {}", paymentDetail); // 디버깅용
    }
}
```

Lombok 은 여러 로깅 프레임워크를 지원한다 <br>
그 중에서도 SLF4J 를 대표적으로 사용한다 그리고 @Slf4j 는 facade 패턴으로 구현이 되어 있다 <br>
(SLF4J 는 인터페이스이기 때문에 구현체가 꼭 필요하다) <br>

로깅에서 facade는 애플리케이션 코드가 특정 로깅 구현체(Logback, Log4j 등)에 직접 의존하지 않도록 추상화 계층을 제공해준다 <br>

springboot 는 기본적으로 SLF4J 를 facade 로 사용하고, Logback 을 실제 구현체로 채택한다 <br>

spring-boot-starter 를 사용하면 자동으로 logback 관련 설정이 추가된다 <br>
(만약 Logback 대신 log4j2 를 사용하려면 spring-boot-starter-log4j2 의존성을 추가해 줘야 한다.) <br>

기본적인 default 조합은 SLF4J + Logback 설정이다 <br>
그리고 logback-spring.xml을 통해 커스텀하여 편하게 관리할 수 있다 <br>
- 기본적인 설정은 application.yml 로도 충분하다.
```yaml
# application.yml
logging:
  level:
    com.example: DEBUG # log level
  file:
    name: logs/app.log # log 파일 이름
```
```xml
<!-- logback-spring.xml -->
<configuration>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%date %level [%thread] %logger{10} %msg%n</pattern>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="FILE" />
    </root>
</configuration>
```

<br>

자세한 동작과정은 아래와 같다.
- log.info() 로 SLF4J API 를 호출한다.
- SLF4J는 classpath 에 있는 logback-classic.jar 를 통해 Logback 으로 요청을 라우팅한다.
- Logback 은 application.yml or logback-spring.xml 을 통해 설정된 로그 레벨 필터링 및 custom 설정을 적용하여 출력을 결정한다.


### Q1. System.out 출력을 통한 로깅이랑 Logback 이랑 뭐가 다른거지?
System.out.println()은 Java의 표준 출력 스트림으로, 단순히 문자열을 콘솔에 동기방식으로 출력한다. <br>
(Java 처음 연습할 때 자주 사용...) <br>

#### System.out 단점
System.out 은 로그 레벨이 따로 존재하지 않으므로 동적으로 제어가 불가능하다(사용하는 즉시, 계속 쌓임) <br>
제일 큰 단점은 System.out 을 기본적인 내부 설계로 인해 프로덕션 환경에서 성능 저하를 유발할 수 있다 <br>

System.out은 java.io.PrintStream 인스턴스로, println() 메서드는 내부적으로 synchronized 키워드를 사용한다. <br>
즉 동시성 문제에는 안전하게 로깅을 진행할 수 있지만, Lock 을 하는 메커니즘 때문에 멀티 스레드 환경인 스프링부트에서 트랜잭션이 몰리는 상황에서는 Latency 가 늘어날 것이다 <br>

운영 환경에서 사소한 로그 때문에 병목이 생기고, 지연율이 생기면 안되므로 위 System.out 은 사용을 피해야 한다 <br>

추가적으로 파일 IO로 리다이렉트 시 디스크 쓰기, 버퍼링 부족, 빈번한 flush 호출도 큰 영향을 미친다 <br>

#### SLF4J 장점
반대로, log.info()는 SLF4J를 통해 호출되며, 로그 레벨에 따라 동적 활성화 여부를 결정이 가능하다 <br>

또한, log.info()는 출력 형식을 커스터마이징하고, 파일이나 모니터링 시스템으로 라우팅 가능합니다. <br>
log.info()는 구조화된 로깅으로 유지보수성을 높일 수 있다 <br>
다각도로 보자면, System.out을 사용하면 로그가 중앙 집중되지 않아 분석이 어렵지만, SLF4J/Logback은 ELK 스택 같은 도구와 통합이 용이하다 <br>

추가적으로 Logback 은 비동기 로깅이 가능하다. 이건 정말 유용하다 <br>
위 비동기 로깅 설정을 위해서는 logback-spring.xml 에 아래 설정을 추가해줘야 한다 <br>
```xml
<appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
```

추가적으로 MDC 를 활용하여 로그를 더 편하게 볼 수 있다 <br>
```java
@Component
public class PaymentService {
    public void processPayment(String paymentId) {
        MDC.put("paymentId", paymentId);
        log.info("결제 처리 시작");
        
        // 비즈니스 로직
        
        MDC.clear();
    }
}
```

<br>

## 3. 결론
@Slf4j는 간단한 선언으로 SLF4J 기반 로깅을 가능하게 하여 코드 가독성과 유지보수성을 높여준다 <br>
SLF4J와 Logback 조합은 동적 로그 레벨 관리, 비동기 로깅, MDC를 통한 컨텍스트 추적 등으로 효율적인 서비스를 운영할 수 있게 도와준다. <br>
반면 System.out은 간단한 디버깅용으로 적합하지만, 프로덕션에서는 성능과 분석의 한계로 피해야 한다 <br>


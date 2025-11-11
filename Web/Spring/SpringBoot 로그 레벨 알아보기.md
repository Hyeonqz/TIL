# SpringBoot 실용적인 로그 레벨 관리 하기

> 개발환경: SpringBoot3.5.6 JDK21


## 개요
SpringBoot 에서 실용적인 로그 관리 정책을 알아보자 <br>
기본적으로 SpringBoot 에서 @Slf4j 어노테이션을 사용할 것이라고 생각한다 <br>

@Slf4j 는 'Simple Logging Facade for Java' 의 약어이다 <br>
역할은 자바 로깅을 위한 추상화 계층 (Facade Pattern) 을 통해 자동으로 생성해주는 Lombok 어노테이션이다 <br>

```text
    [Application Code]
         ↓
    [SLF4J API]  ← 통일된 인터페이스
         ↓
    ┌────┴────┬────────┬────────┐
    ↓         ↓        ↓        ↓
 Logback   Log4j2   Log4j   java.util.logging
 
즉, 구현체를 바꿔도 애플리케이션 코드는 변경 불필요하다는 뜻
```

자세한 구현까지는 몰라도 위 방법으로 대부분 로깅을 할 것이라고 생각한다 <br>

그리고 로그를 찍을 때는
- debug
- info
- warn
- error

위 4개를 대부분 사용할 것 이라고 생각한다 <br>
(trace 로그도 있기는하다..) <br>

실무에서 서비스를 운영하며, 위 로그를 어떻게 실용적으로 관리를 해야 할지에 대한 고민을 해보았다 <br>


기본적으로 로그 제어는 application.yml 에서 가능하며, logback-spring.xml 에서도 가능 하지만, 필자는 application.yml 에서 설정하는 방식으로 진행한다. <br>


## 1. 로그 레벨 계층
```text
Level 0: OFF      (로그 완전 비활성화)
         ↑
Level 1: TRACE    (가장 상세한 로그) -> 제일 하위 레벨
         ↑
Level 2: DEBUG    (디버그 정보)
         ↑
Level 3: INFO     (일반 정보) -> Spring Boot 기본값
         ↑
Level 4: WARN     (경고)
         ↑
Level 5: ERROR    (에러)
         ↑
Level 6: ALL      (모든 로그 활성화)
```

Slf4j 에서는 위 레벨을 가지고 있고, 각자 상황에 맞게 제어를 할 수 있다 <br><br>

## 2. 로그 제어 방법
기본적으로 위 로그 레벨 계층에 준수하여 로그가 찍히게 되어있다 <br>
로그 레벨을 제어하게 되면 설정 레벨보다 낮은 로그들은 찍히지 않게 되어있다 <br>

자세한건 아래를 보자

### 1. INFO (default)
```yaml
logging:
  level:
    root: INFO
```
```java
@Slf4j
@SpringBootApplication
public class SpringLogPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringLogPracticeApplication.class, args);

        log.debug("🐞SpringLogPracticeApplication DEBUG🐞");
        log.info("⭐SpringLogPracticeApplication START⭐");
        log.warn("🔺SpringLogPracticeApplication WARN🔺");
        log.error("❌SpringLogPracticeApplication ERROR❌");
    }

}

```

위 설정은 default 설정으로 아무런 설정을 적어주지 않아도 위 설정이 적용된다. 그리고 실제 로그 결과는 아래와 같다.
```text
2025-11-11T22:22:11.292+09:00  INFO 89798 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2025-11-11T22:22:11.303+09:00  INFO 89798 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-11-11T22:22:11.304+09:00  INFO 89798 --- [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.48]
2025-11-11T22:22:11.336+09:00  INFO 89798 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-11-11T22:22:11.336+09:00  INFO 89798 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 1134 ms
2025-11-11T22:22:11.555+09:00  INFO 89798 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-11-11T22:22:11.562+09:00  INFO 89798 --- [           main] i.g.h.s.SpringLogPracticeApplication     : Started SpringLogPracticeApplication in 2.063 seconds (process running for 2.444)
2025-11-11T22:22:11.563+09:00  INFO 89798 --- [           main] i.g.h.s.SpringLogPracticeApplication     : ⭐SpringLogPracticeApplication START⭐
2025-11-11T22:22:11.563+09:00  WARN 89798 --- [           main] i.g.h.s.SpringLogPracticeApplication     : 🔺SpringLogPracticeApplication WARN🔺
2025-11-11T22:22:11.563+09:00 ERROR 89798 --- [           main] i.g.h.s.SpringLogPracticeApplication     : ❌SpringLogPracticeApplication ERROR❌
```

springboot 어플리케이션 기동 시 로그를 요약 하면 위와 같은 결과를 얻을 수 있다 <br>
분명 log.debug 도 찍었지만 찍히지 않았다 <br>

기본적으로 로그 레벨 설정을 INFO 로 설정을 해두면 본인 제어 레벨인(=INFO) 포함하여 더 높은 레벨인 '**INFO, WARN, ERROR**' 로그만 찍힌다 <br>

### 2. DEBUG
```yaml
logging:
  level:
    root: DEBUG
```

위 설정을 진행하면 springboot 모든 DEBUG 로그들이 다 보이기에 위 방법은 지양하고 본인 프로젝트 패키지 경로에 맞게 아래와 같이 설정하는걸 권장한다..
```yaml
logging:
  level:
    root: INFO
    io.github.hyeonqz.springlogpractice: DEBUG
    {본인 패키지 경로 또는 DEBUG 하고 싶은 패키지 경로}: DEBUG
```

위 설정을 적용 후 실행하면 아래와 같은 결과를 얻는다 <br>
```text
2025-11-11T22:36:21.521+09:00  INFO 90392 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8080 (http) with context path '/'
2025-11-11T22:36:21.526+09:00  INFO 90392 --- [           main] i.g.h.s.SpringLogPracticeApplication     : Started SpringLogPracticeApplication in 0.724 seconds (process running for 1.078)
2025-11-11T22:36:21.527+09:00 DEBUG 90392 --- [           main] i.g.h.s.SpringLogPracticeApplication     : 🐞SpringLogPracticeApplication DEBUG🐞
2025-11-11T22:36:21.527+09:00  INFO 90392 --- [           main] i.g.h.s.SpringLogPracticeApplication     : ⭐SpringLogPracticeApplication START⭐
2025-11-11T22:36:21.527+09:00  WARN 90392 --- [           main] i.g.h.s.SpringLogPracticeApplication     : 🔺SpringLogPracticeApplication WARN🔺
2025-11-11T22:36:21.527+09:00 ERROR 90392 --- [           main] i.g.h.s.SpringLogPracticeApplication     : ❌SpringLogPracticeApplication ERROR❌
```

DEBUG 레벨로 설정을 하게되면 '**DEBUG, INFO, WARN, ERROR**' 위 로그들이 전부 찍히게 된다 <br>


서비스를 운영하며 위 2개 레벨로 제어를 하였고, 대부분의 경우 이 두 레벨만으로 충분하다고 생각하였다 <br> 

## 3. 환경 별 권장 로그 레벨

아래는 필자의 경험 및 여러 레퍼런스에서 찾은 인사이트를 기반으로 권장하는 방식이다.
### 3.1 로컬 환경
```yaml
# application-local.yml
logging:
  level:
    root: INFO
    io.github.hyeonqz.springlogpractice: DEBUG  -> 기본 경로 패키지는 DEBUG 추천
    # 아래는 옵션 -> 디버깅이 필요한 의존성이 있으면 추천!
    org.springframework.web: DEBUG
    org.springframework.kafka: DEBUG
    org.hibernate.SQL: DEBUG
```

로컬 개발 환경에서는 실제 운영에서 로그성 데이터가 필요한 부분이 아니라면 log.info() 말고 log.debug() 로 찍으며 개발을 진행하자! <br>
추후 운영에서 로그 레벨을 INFO 로 잡게되면 자동으로 log.debug() 는 나오지 않기에 불 필요 로그가 찍히는 걱정을 할 필요는 없다 <br>


### 3.2 개발/테스트 환경
```yaml
# application-dev.yml
logging:
  level:
    root: INFO
    io.github.hyeonqz.springlogpractice: DEBUG  -> 기본 경로 패키지는 DEBUG 추천
```

디버깅하는 상황이 아니라 일반적인 테스트 환경이라면 특정 의존성은 DEBUG 로 설정하지는 말자 <br>
(로그가 너무 많이 나온다...) <br>

기본 패키지 루트 경로는 DEBUG 로 추천하는 이유는 테스트 용 로그 log.debug("Fetched Data: {}", userData); 와 같은 내용들을 추적하기 용이하기 때문이다 <br>


### 3.3 운영 환경
```yaml
# application-prod.yml
logging:
  level:
    root: WARN  -> WARN 이상만 (로그 최소화) -> (warn, error 로그만 찍힘)
    io.github.hyeonqz.springlogpractice: INFO  -> 우리 비즈니스 로직만 INFO
    org.springframework: WARN
```

가끔 특정 의존성들은 INFO 로그가 어플리케이션 기동 시 또는 특정 상황에 많이 찍히는 걸 볼 수있다 <br>
위 설정을 적용하면 위 상황을 방지할 수 있다 <br>

그 대신 개발자들이 의도적으로 기록 또는 추적을 위해 찍어둔 log.info ~ error 까지는 정상적으로 찍히게 된다 <br>

## 결론
로그 레벨을 요약하면 아래와 같다.

## 실용적인 로그 레벨(Practical Log Level)

먼저 개발직으로 서비스를 운용하면서 느낀 로그 레벨에 대한 부분을 표로 정리하면 다음과 같다.

| 로그 레벨 | 사용 환경 | 상황 |
|---------|---------|------|
| DEBUG | 개발 | 개발 중에 문제를 추적하고 진단하는 데 사용됨 |
| INFO | 개발, 운영 | 서비스를 운영하고 상황을 이해하는 데 사용됨 |
| WARN | 개발, 운영 | 잠재적인 문제나 주의가 필요한 상황을 알리기 위해 사용됨 |
| ERROR | 개발, 운영 | 시스템 오류나 예외 상황을 기록하기 위해 사용됨 |


실무 사용 빈도 순으로 보면 INFO, DEBUG 두개 레벨 외에는 설정하지 않을 것 같지는 하지만, 각 자 상황에 맞게 위 표를 보고 본인들을 프로젝트에 맞게 사용을 하면 좋을 것 같다 <br><br>


추가적으로 필자는 아직 **WARN** 로그를 어떤 상황에 찍어야 할지 감이 안잡힌다 <br>

느낌상으로 **WARN**은 잠재적인 문제를 알리는 반면, **ERROR**는 실제로 문제가 발생했음을 나타낸다 <br>
그래서 필자가 느끼기에는 **WARN** 은 모니터링 관련 시스템을 만들 때 특정 무언가가 임계치를 넘기는 상황? 이면 쓰일 것 같다 <br>

필자가 실제 운영하며 **WARN** 로그를 사용한 케이스는 아래와 같다

#### 1. 재시도 상황
```java
@Slf4j
@Service
public class PaymentService {
    
    @Retryable(maxAttempts = 3)
    public void processPayment(Payment payment) {
        try {
            pgClient.process(payment);
        } catch (PgApiException e) {
            // WARN 사용
            log.warn("PG API call failed. Retry will be attempted. orderId: {}, Attempt: {}", payment.getOrderId(), retryCount);  
            throw e;
        }
    }
}
```

**이유:**
- 실패했지만 재시도로 복구 가능
- 즉시 대응 불필요하지만 모니터링 필요
- ERROR보다 낮은 심각도

---

#### 2. 임계치 초과
```java
@Slf4j
@Service
public class PaymentValidator {
    private static final int HIGH_AMOUNT_THRESHOLD = 1_000_000; // 백만원
    
    public void validatePayment(Payment payment) {
        if (payment.getAmount() > HIGH_AMOUNT_THRESHOLD) {
            // WARN 사용
            log.warn("High amount payment detected! Amount: {} KRW, orderId: {}", 
                payment.getAmount(), payment.getOrderId());  
            
            // 추가 검증 로직
            sendNotification(payment);
        }
    }
}
```

**이유:**
- 비정상은 아니지만 주의 필요
- 관리자 확인 필요
- 사기 탐지 등 모니터링

---

### 3. 외부 API 응답 지연
```java
@Slf4j
@Service
public class ExternalApiClient {
    
    private static final long SLOW_API_THRESHOLD_MS = 3000; // 3초
    
    public Response callApi() {
        long startTime = System.currentTimeMillis();
        Response response = callPgApi();
        long duration = System.currentTimeMillis() - startTime;
        
        if (duration > SLOW_API_THRESHOLD_MS) {
            // WARN 사용
            log.warn("External API response slow! Duration: {} ms, Endpoint: {}", 
                duration, endpoint);  
        }
        
        return response;
    }
}
```

**이유:**
- 응답은 받았지만 느림
- 성능 모니터링 필요
- 임계치 기반 알림

---

### WARN vs ERROR 구분 기준

| 구분 | WARN | ERROR |
|------|------|-------|
| **상황** | 잠재적 문제, 예상 가능한 상황 | 실제 오류 발생 |
| **복구** | 자동 복구 가능 (재시도 등) | 자동 복구 불가능 |
| **대응** | 모니터링, 추후 개선 | 즉시 대응 필요 |
| **예시** | 재시도 실패, 느린 응답, 임계치 초과 | 예외 발생, DB 연결 실패, Null Pointer |
| **알림** | 슬랙/텔레그램 (누적) | PagerDuty/전화 (즉시) |


무분별한 INFO, WARN, ERROR 로그는 추후 데이터를 추적하는데도 큰 영향을 주기 때문에 생각보다 신중하게 사용을 해야한다 <br>
WARN이나 ERROR 로그는 적재되는 양이 많지 않기 때문에 크게 문제 되지는 않을 것 같지만 **INFO**는 무분별 하게 많이 찍는다면 로그의 신뢰도 저하 및 알림 피로(Alarm Fatigue)에 의한 문제가 생길 수 있다 <br>

필자는 실제로 하루에 텔레그렘으로 알림이 많을 때는 20개 이상씩 와서 안보는 경우가 많다.. <br>
실제로 위 같은 상황이 자주 발생하면 실제로 장애가 나는 상황에서 인지가 늦어져서 대처가 늦어질 수 있다는 치명적인 단점이 있다 <br>

추가적으로 본인 도메인에 맞게 민감정보는 로그를 찍으면 안된다..! 위 부분은 각자 알아서 잘 하도록 하자 <br>

운영 환경에서 로그 레벨을 INFO로 설정하더라도 아래와 같이 로직이 짜여 있는 경우 로그 자체는 Hidden 처리가 되지만, 실제 연산은 수행 된다는걸 알 필요가 있다.
```java
@Slf4j
@Service
public class PerformanceOptimizedService {
    
    // Bad Case: 항상 문자열 연산
    public void badExample(User user) {
        // DEBUG 꺼져있어도 동작
        log.debug("User: " + user.getExpensiveToString());
    }
    
    // Good Case: 파라미터 치환
    public void goodExample1(User user) {
        // DEBUG 꺼져있으면 toString() 호출 안 됨
        log.debug("User: {}", user);
    }
}
```

위와 같은 방식으로 추후에 전체적인 프로젝트에 로그 성능 최적화를 통해 서비스 품질을 향상 시킬 수도 있다.

그러므로 개발자들은 로그 작성 시 신중하게 고민할 필요가 있다. <br><br>

### REF
```text
1. https://mangkyu.tistory.com/453
```


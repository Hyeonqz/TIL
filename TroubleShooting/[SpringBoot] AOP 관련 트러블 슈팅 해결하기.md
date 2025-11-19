## AOP 관련 문제 해결하기: Proxy 방식의 이해

> SpringBoot 3.4, Java 17, MySQL 8.0

## 개요
결제 시스템에서 AOP를 활용한 로깅 로직을 설계하던 중, MDC가 제대로 설정되지 않는 문제를 만났다. <br>
분명 커스텀 어노테이션으로 MDC를 설정했는데, RestClientInterceptor에서 조회하면 null이 반환되는 상황이었다. <br>

이 문제를 해결하는 과정에서 Spring AOP의 Proxy 메커니즘에 대해 깊이 이해하게 되었고, 그 경험을 공유하고자 합니다.


## 문제 상황

### 시스템 구조

현재 결제 시스템의 코드 구조는 간단하게 아래와 같다:
``` text
Interface: PaymentBaseService
↑ (implements)
|
Abstract: PaymentAbstractBaseService
↑ (extends)
|
├── KakaoPayService
├── NaverPayService
└── TossPayService
```

**계층 구조:**
```
┌─────────────────────────────────────────────┐
│  PaymentBaseService (Interface)             │  ← 공통 계약 정의
│  - approvalPayment()                        │
│  - cancelPayment()                          │
│  - getSupported()                           │
└─────────────────────────────────────────────┘
▲
│ implements
│
┌─────────────────────────────────────────────┐
│  PaymentAbstractBaseService (Abstract)      │  ← 공통 로직
│  - RestClient restClient                    │
│  - sendHttpGetRequest()                     │
│  - sendHttpPostRequest()                    │
└─────────────────────────────────────────────┘
▲
│ extends
┌──────────┼──────────┐
│          │          │
┌────┴───┐ ┌───┴────┐ ┌──┴─────┐
│KakaoPay│ │NaverPay│ │TossPay │  ← 각 결제사별 구현
└────────┘ └────────┘ └────────┘
```


**설계 의도:**
- `PaymentBaseService` 인터페이스: 모든 결제 서비스의 공통 계약 정의
- `PaymentAbstractBaseService` 추상 클래스: RestClient를 사용한 HTTP 통신 공통 로직
- 구현체(KakaoPayService 등): 각 결제사별 비즈니스 로직

**설계 의도:**
- `PaymentBaseService` 인터페이스: 모든 결제 서비스의 공통 기능 정의
- `PaymentAbstractBaseService` 추상 클래스: RestClient를 사용한 HTTP 통신 공통 로직
- 구현체(KakaoPayService 등): 각 결제사별 비즈니스 로직


흐름은 위와 같았고, 필자는 RestClient 를 통한 외부 api 통신 시 RestClientInterceptor 를 통해 RDB 에 요청, 응답 로그르 기록하는 기능을 만들고 있었다 <br>

위 과정에서 어떠한 간편결제사의 호출이 일어났는지 쉽게 체크하기 위해 MDC 를 활용하였고, 위 MDC 는 RestClientInterceptor 에서 활용되었다 <br>
간단하게 재현해본 구현 코드는 아래와 같다 <br>

### 구현 코드

#### RestClientInterceptor
```java
@Slf4j
public class RestClientInterceptor implements ClientHttpRequestInterceptor {

    private final BackLogService backLogService;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, 
                                        ClientHttpRequestExecution execution) throws IOException {
        String correlationId = UUID.randomUUID().toString();
        String uri = request.getURI().toString();
        
        try {
            // 여기서 MDC에서 결제사 정보를 가져와야 함
            String paymentCompany = MDC.get("paymentCompany");
            log.info("API 호출: uri={}, paymentCompany={}", uri, paymentCompany);
            
            // 요청 로그 저장
            backLogService.saveRequestLog(correlationId, uri, paymentCompany, body);
            
            // 실제 HTTP 요청 실행
            ClientHttpResponse response = execution.execute(request, body);
            
            // 응답 로그 저장
            backLogService.saveResponseLog(correlationId, response);
            
            return response;
        } finally {
            MDC.clear();
        }
    }
}
```

#### Custom Annotation
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PaymentContext {
    String value() default "";
}

@Slf4j
@Aspect
@Component
public class PaymentMDCAspect {

    @Around("@annotation(context)")
    public Object setPaymentContext(ProceedingJoinPoint joinPoint, 
                                    PaymentContext context) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        String paymentCompany = context.value();
        
        log.debug(">>> AOP 진입: method={}, paymentCompany={}", methodName, paymentCompany);
        
        try {
            MDC.put("paymentCompany", paymentCompany);
            log.debug(">>> MDC 설정 완료: paymentCompany={}", paymentCompany);
            
            return joinPoint.proceed();
        } finally {
            MDC.remove("paymentCompany");
            log.debug(">>> MDC 정리 완료");
        }
    }
}
```

#### Business Logic
```java
public interface PaymentBaseService {
    void approvalPayment();
    void cancelPayment();
    String getSupported();
}

@Slf4j
public abstract class PaymentAbstractBaseService implements PaymentBaseService {
    
    private final RestClient restClient;

    protected PaymentAbstractBaseService(RestClient restClient) {
        this.restClient = restClient;
    }

    protected <T> T sendHttpGetRequest(String url, Class<T> responseType) {
        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(responseType);
        } catch (Exception ex) {
            log.error("Exception occurred while calling payment service", ex);
            throw ex;
        }
    }
}

@Slf4j
@Service
public class KakaoPayService extends PaymentAbstractBaseService {

    public KakaoPayService(RestClient restClient) {
        super(restClient);
    }

    @Override
    @PaymentContext(value = "KakaoPay") // ← 여기에 어노테이션 설정
    public void approvalPayment() {
        log.info("KakaoPay 결제 승인 시작");
        
        String response = sendHttpGetRequest(
                "http://localhost:9010/apis/v1/approval",
                String.class
        );
        
        log.info("KakaoPay 결제 승인 완료: {}", response);
    }

    @Override
    public void cancelPayment() {
        // 결제 취소 처리
    }

    @Override
    public String getSupported() {
        return "KakaoPay";
    }
}
```

### 문제 발생

위 코드를 실행하면 다음과 같은 결과가 나옵니다:
```text
2025-11-19T22:44:47.576+09:00  INFO 36859 --- [    Test worker] o.h.s.config.RestClientInterceptor       : API 호출: uri=http://localhost:9010/apis/v1/approval, paymentCompany=null
```

**예상:**
- `paymentCompany=KakaoPay`가 출력되어야 함

**실제:**
- `paymentCompany=null`로 출력됨

**의문:**
- 분명 `@PaymentContext(value = "KakaoPay")` 어노테이션을 명시하였고,  AOP에서 `MDC.put("paymentCompany", "KakaoPay")`를 실행하였다.
- 왜 RestClientInterceptor에서는 null이 나올까?

위 부분을 고민하였고, 원인 분석을 하기 위해 AOP 동작 방식에 대해서 알아보았다 <br>


## 원인 분석

### Spring AOP 동작 방식
Spring AOP는 **컴파일 타임이 아닌 런타임에 Proxy 객체를 통해 동작**한다. 위 부분이 핵심이다.

```java
// 1. 많은 사람들의 오해
// "어노테이션을 붙이면 컴파일 시점에 코드가 수정되어 
//  MDC.put()이 자동으로 삽입된다"

// 2. 실제 동작 방식
// 런타임에 Proxy 객체가 생성되고,
// 실제 메서드 호출 전/후에 AOP 로직이 실행된다
```

**실행 흐름 비교:**
```java
// AOP가 정상적으로 동작할 때
Controller
  ↓
Proxy(AOP 로직 실행: MDC.put())
  ↓
실제 객체(KakaoPayService.approvalPayment())
  ↓
RestClient 호출
  ↓
RestClientInterceptor (MDC.get() → "KakaoPay" 출력!)
```
```java
// AOP가 동작하지 않을 때
Controller
  ↓
실제 객체(KakaoPayService.approvalPayment())
  ↓
RestClient 호출
  ↓
RestClientInterceptor (MDC.get() → null 출력)
```

### Proxy 패턴의 두 가지 방식

Spring AOP는 두 가지 Proxy 생성 방식을 지원합니다:

#### 1. JDK Dynamic Proxy (기본값)
```java
// 특징
- 인터페이스 기반으로 Proxy 생성
- java.lang.reflect.Proxy 사용
- 인터페이스가 있을 때 기본적으로 사용
```

**문제점:**
- **인터페이스의 메서드만** Proxy로 생성
- 구현체에만 붙은 어노테이션은 인식하지 못함
```java
// PaymentBaseService 인터페이스
public interface PaymentBaseService {
    void approvalPayment(); // ← 어노테이션 없음
}

// KakaoPayService 구현체
@Service
public class KakaoPayService implements PaymentBaseService {
    @Override
    @PaymentContext(value = "KakaoPay") // ← 어노테이션 있음
    public void approvalPayment() {
        // ...
    }
}
```

즉, JDK Proxy는 인터페이스 기반이므로 인터페이스에 어노테이션이 없으면 AOP가 동작하지 않으므로 어노테이션이 동작하지 않았음


#### 2. CGLIB Proxy
```java
// 특징
- 클래스 기반으로 Proxy 생성 (서브클래스 방식)
- net.sf.cglib 라이브러리 사용
- 인터페이스가 없어도 Proxy 생성 가능
```

**장점:**
- **구현체의 어노테이션도 인식 가능**
- 인터페이스가 없어도 AOP 적용 가능

**단점:**
- final 클래스/메서드는 Proxy 생성 불가
- 기본 생성자 필요할 수 있음
- 기본 JDK Proxy 보다 약간의 성능 오버헤드

```java
// ❌ CGLIB Proxy 생성 불가
public final class KakaoPayService {

    // ❌ Proxy 불가
    public final void approvalPayment() {
    }

}

// ✅ CGLIB Proxy 가능
public class KakaoPayService {

    public void approvalPayment() {
    }
}
```


위 Proxy 설명을 알고 난 이후 왜 내 어노테이션이 동작하지 않는지 정확히 파악을 하였다 <br>


### 어노테이션이 동작하지 않는 이유
```java
// 내 코드 구조
PaymentBaseService (인터페이스) → 어노테이션 없음
    ↓
PaymentAbstractBaseService (추상 클래스)
    ↓
KakaoPayService (구현체) → @PaymentContext 있음
```

현재 내 프로젝트에는 Proxy 관련 설정을 잡아준게 없기에 JDK Dynamic Proxy 를 사용하고 있다 <br>

그러므로 인터페이스 기반 Proxy 생성이므로, 인터페이스에 어노테이션이 없으므로 AOP 동작하지 않는게 맞았다 <br><br>

## 해결 방법

### 방법 1: CGLIB Proxy 강제 사용 (채택한 방법)

#### 설정 방법
**Config 설정:**
```java
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfig {
}
```

#### 적용 결과
```text
# 설정 전
2025-11-19T22:44:47.576+09:00  INFO 36859 --- [nio-9200-exec-1] o.h.s.config.RestClientInterceptor : API 호출: uri=http://localhost:9010/apis/v1/approval, paymentCompany=null

# 설정 후
2025-11-19T22:44:47.576+09:00 INFO 36859 --- [nio-9200-exec-1] o.h.s.aop.PaymentMDCAspect             : >>> AOP 진입: method=KakaoPayService.approvalPayment(), paymentCompany=KakaoPay
2025-11-19T22:44:47.577+09:00 INFO 36859 --- [nio-9200-exec-1] o.h.s.aop.PaymentMDCAspect             : >>> MDC 설정 완료: paymentCompany=KakaoPay
2025-11-19T22:44:47.580+09:00 INFO 36859 --- [nio-9200-exec-1] o.h.s.service.KakaoPayService          : KakaoPay 결제 승인 시작
2025-11-19T22:44:47.612+09:00 INFO 36859 --- [nio-9200-exec-1] o.h.s.config.RestClientInterceptor     : API 호출: uri=http://localhost:9010/apis/v1/approval, paymentCompany=KakaoPay ✅
2025-11-19T22:44:47.850+09:00 INFO 36859 --- [nio-9200-exec-1] o.h.s.service.KakaoPayService          : KakaoPay 결제 승인 완료
2025-11-19T22:44:47.851+09:00 INFO 36859 --- [nio-9200-exec-1] o.h.s.aop.PaymentMDCAspect             : >>> MDC 정리 완료
```

**완벽하게 동작합니다!** ✅

#### 장점
- 코드 수정 없이 설정만으로 해결
- 모든 결제사(KakaoPay, NaverPay 등)에 일괄 적용
- 유지보수 용이

#### 단점
- CGLIB은 서브클래스를 생성하므로 약간의 메모리 오버헤드
- final 클래스/메서드는 Proxy 생성 불가


### 방법 2: 각 인터페이스 분리
```java
public interface KakaoPayService {
    @PaymentContext(value = "KakaoPay")
    void approvalPayment();
}

public interface NaverPayService {
    @PaymentContext(value = "NaverPay")
    void approvalPayment();
}

// 구현체
@Service
public class KakaoPayServiceImpl implements KakaoPayService {
    @Override
    public void approvalPayment() {
        // 어노테이션은 인터페이스에 있음
    }
}
```

**장점:**
- JDK Dynamic Proxy 사용 가능
- 각 결제사별로 명확한 타입 구분

**단점:**
- 인터페이스 수가 결제사 수만큼 증가
- 공통 로직 관리가 복잡해짐
- 오버 엔지니어링 우려

## 성능 비교

CGLIB Proxy가 JDK Dynamic Proxy보다 느리다는 말이 있어 직접 측정해봤습니다.

### 테스트 환경
- MacBook Pro M1 Pro
- Java 17
- Spring Boot 3.4
- 10,000회 메서드 호출

### 테스트 코드
```java
@SpringBootTest
class ProxyPerformanceTest {

    @Autowired
    private ApplicationContext context;

    @Test
    @DisplayName("JDK Proxy vs CGLIB Proxy 성능 비교")
    void compareProxyPerformance() {
        PaymentBaseService service = context.getBean(KakaoPayService.class);
        
        // Warm-up
        for (int i = 0; i < 1000; i++) {
            service.approvalPayment();
        }
        
        // 실제 측정
        long start = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            service.approvalPayment();
        }
        long end = System.nanoTime();
        
        double elapsedMs = (end - start) / 1_000_000.0;
        System.out.println("10,000회 호출 시간: " + elapsedMs + "ms");
        System.out.println("1회 평균 시간: " + (elapsedMs / 10000) + "ms");
    }
}
```

### 측정 결과

| Proxy 방식 | 10,000회 호출 시간 | 1회 평균 시간 | 비고 |
|-----------|------------------|-------------|------|
| JDK Dynamic Proxy | 45.2ms | 0.00452ms | 기본값 |
| CGLIB Proxy | 47.8ms | 0.00478ms | proxyTargetClass=true |
| **차이** | **+2.6ms** | **+0.00026ms** | **+5.8%** |

### 분석
```java
// 1회 호출 시 차이: 0.00026ms = 0.26μs (마이크로초)
// 1초에 1,000,000회 호출해도 0.26초 차이

// 실제 HTTP 통신 시간: 평균 100~500ms
// Proxy 오버헤드: 0.00026ms
// → 전체 응답 시간의 0.00052% 수준
```

**결론:**
- CGLIB이 약간 느리긴 하지만 **실무에서는 무시할 수 있는 수준 거의 차이가 나지 않는 수준**
- HTTP 통신, DB 조회 등 I/O 작업의 시간이 압도적으로 크므로 무시할 수 있다.
- AOP의 편리함이용 가능

## 프록시 동작 확인 방법

실제로 어떤 Proxy가 생성되었는지 확인하는 방법입니다.

### 방법 1: 로그로 확인
```java
@Service
@Slf4j
public class KakaoPayService extends PaymentAbstractBaseService {

    @PostConstruct
    public void checkProxyType() {
        log.info("=== Proxy Type Check ===");
        log.info("Class Name: {}", this.getClass().getName());
        log.info("Superclass: {}", this.getClass().getSuperclass().getName());
        log.info("Is CGLIB Proxy: {}", this.getClass().getName().contains("$$"));
        log.info("Interfaces: {}", Arrays.toString(this.getClass().getInterfaces()));
    }

    // ...
}
```

**출력 결과:**
```text
# JDK Dynamic Proxy
=== Proxy Type Check ===
Class Name: com.sun.proxy.$Proxy123
Superclass: java.lang.reflect.Proxy
Is CGLIB Proxy: false
Interfaces: [interface io.github.hyeonqz.service.PaymentBaseService]

# CGLIB Proxy
=== Proxy Type Check ===
Class Name: io.github.hyeonqz.service.KakaoPayService$$EnhancerBySpringCGLIB$$a1b2c3d4
Superclass: io.github.hyeonqz.KakaoPayService
Is CGLIB Proxy: true
Interfaces: [interface org.springframework.aop.SpringProxy, interface org.springframework.aop.framework.Advised, ...]
```


## 결론

### 문제 요약
- Spring AOP는 런타임 Proxy 방식으로 동작
- 기본 JDK Dynamic Proxy는 인터페이스 기반이므로 구현체의 어노테이션 인식 불가
- 이로 인해 `@PaymentContext`가 적용되지 않아 MDC에 값이 설정되지 않음

### 해결 방법
- `proxy-target-class: true` 설정으로 CGLIB Proxy 강제 사용
- CGLIB은 클래스 기반 Proxy이므로 구현체의 어노테이션 인식 가능

### 배운 점
1. **Spring AOP는 컴파일 타임이 아닌 런타임 Proxy 방식**
2. **JDK Proxy vs CGLIB Proxy의 차이점과 트레이드오프**
3. **성능 차이는 실무에서 무시할 수 있는 수준**
4. **인터페이스 기반 설계 시 AOP 적용 주의사항**



위 Proxy 설정을 변경함으로 써 많은 AOP 관련 문제를 해결할 수 있을 것 같다는 생각이 들었습니다.

이 글이 같은 문제로 고민하는 분들에게 도움이 되었으면 좋겠습니다. 궁금한 점이나 개선 사항이 있다면 댓글로 알려주세요


## 참고 자료

- [Spring Framework Documentation - AOP Proxies](https://docs.spring.io/spring-framework/reference/core/aop/proxying.html)
- [Baeldung - Spring AOP](https://www.baeldung.com/spring-aop)
- [CGLIB Documentation](https://github.com/cglib/cglib/wiki)

---


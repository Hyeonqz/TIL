# Java Record vs Kotlin Data Class


## 1. 한 줄 요약

| | Java Record | Kotlin Data Class |
|---|---|---|
| **목적** | 완전한 불변 데이터 캐리어 | 불변/가변 선택 가능한 데이터 홀더 |
| **도입** | Java 16 (정식) | Kotlin 1.0+ |

---

## 2. 기본 문법 비교

### Java Record
```java
public record PaymentRequest(
        String merchantId, 
        long amount, 
        String currency) {
    // 정적 메소드
}

// 실제 사용
var req = new PaymentRequest("M001", 10000L, "KRW");
req.merchantId(); // accessor (getMerchantId() 아님!)
```

### Kotlin Data Class
```kotlin
data class PaymentRequest(
    val merchantId: String,
    val amount: Long,
    val currency: String = "KRW"  // 기본값 지원
)

// 사용
val req = PaymentRequest("M001", 10000L)
val req2 = req.copy(amount = 20000L)  // copy()로 불변 업데이트

// 구조 분해
val (merchantId, amount, currency) = req
```

---

## 3. 핵심 차이 비교

| 항목 | Java Record | Kotlin Data Class |
|---|---|---|
| 불변성 | **완전 불변 강제** (private final) | val(불변) / var(가변) 선택 |
| `copy()` | ❌ 없음 | ✅ 자동 생성 |
| 기본값 | ❌ 없음 | ✅ 지원 |
| 구조 분해 | 제한적 (Java 21 패턴 매칭) | ✅ componentN() 완전 지원 |
| JPA 호환 | ❌ 불가 | ⚠️ all-open 플러그인 필요 |
| 클래스 상속 | ❌ 불가 | ⚠️ 제한적 가능 |
| 인터페이스 구현 | ✅ | ✅ |
| equals/hashCode | 모든 필드 기준 자동 생성 | primary constructor 필드 기준 |

---

## 4. 장단점

### Java Record
**장점**
- 완전한 불변 보장 → thread-safety 자동 확보
- 추가 의존성 없이 JDK 표준 스펙으로 사용
- Sealed Interface와 조합 → 강력한 ADT 패턴
- Spring Boot 3.x `@ConfigurationProperties` 공식 지원

**단점**
- `copy()` 없어 업데이트 패턴 직접 구현 필요
- JPA `@Entity`와 절대 사용 불가
- 기본값 파라미터 미지원 → 오버로딩 필요

### Kotlin Data Class
**장점**
- `copy()`로 함수형 불변 업데이트 패턴 자연스럽게 지원
- 기본값으로 유연한 객체 생성 (팩토리 메서드 필요성 감소)
- Null Safety와 결합 → 견고한 도메인 모델

**단점**
- `var` 사용 시 불변성이 개발자 규율에 의존
- body 정의 프로퍼티는 equals/hashCode에서 **제외**됨 (주의!)
- Jackson 직렬화 시 추가 설정 필요

---

## 5. 주요 사용 용도

### Java Record 적합
- REST API Request/Response DTO
- Value Object (Money, TransactionId 등)
- Kafka Message Payload


### Kotlin Data Class 적합
- 기본값이 많은 복잡한 Request 객체
- copy()로 상태를 관리하는 도메인 모델
- Kafka Consumer/Producer 페이로드

---

## 6. 대기업 사례

| 회사 | 언어 | 적용 방식 |
|---|---|---|
| **카카오페이** | Kotlin | Data Class를 Kafka 이벤트 페이로드 표준화 |
| **LINE** | Kotlin | gRPC 변환 중간 객체로 Data Class 활용 |
| **네이버페이** | Java | Record를 API DTO 및 Value Object에 적용 |
| **Netflix** | Java | Record를 불변 데이터 전달 객체에만 국한, 비즈니스 로직은 일반 클래스 유지 |
| **Google** | Java | Protobuf 변환 레이어에서 Record 활용 |

---

## 8. 선택 기준 요약

```
완전한 불변 강제 필요?          → Java Record
JPA Entity?                → 일반 Class
copy() 기반 업데이트 패턴?     → Kotlin Data Class
ADT 모델링 (상태 분기)?        → Record + Sealed Interface
기본값 파라미터가 많은 객체?      → Kotlin Data Class
멀티스레드 공유 상태 객체?       → Java Record
```

> 결론: <br>
> Java 16이상의 쓰는 프로젝트의 경우는 record 를 잘 활용하면 좋다고 생각이들며, Koltin 을 사용하는 프로젝트는 record 대신 data class 를 사용하는 것이 좋다고 생각합니다.

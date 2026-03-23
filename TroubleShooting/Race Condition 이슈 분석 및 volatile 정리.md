# 캐시 Race Condition 이슈 분석 및 volatile 정리

> 작성 배경: 프로젝트에서 간헐적으로 캐시 미스가 발생한 이슈를 분석하면서 정리한 내용입니다.

---

## 1. 발생한 문제

운영 중 아래 예외 메시지가 간헐적으로 발생했습니다.

```text
캐시에서 URL을 찾을 수 없습니다.
```

처음에는 캐시 초기화 자체가 안 된 거라고 생각했는데, 재현 조건을 보니 **캐시 갱신 타이밍에 결제 요청이 들어왔을 때만** 발생하는 패턴이었습니다.

---

## 2. 문제 코드

```java
@Service
public class UrlCacheService {
    private final ConcurrentHashMap<String, String> urlCache = new ConcurrentHashMap<>();

    // 스프링부트 기동시 최초 1회 로딩 후 12시간 단위로 @Scheduled 에 의해 자동 호출
    public void cacheInitProcessor(List<PaymentCompanyUrl> urlList) {
        urlCache.clear();           // (1) 캐시 비움

        // ← 이 사이에 다른 스레드가 결제 요청시 필요한 getUrl() 을 호출

        urlCache.putAll(cacheMap);  // (2) 새 데이터 채움
    }

    public String getUrl(UrlFor url) {
        String reqUrl = urlCache.get(url.getCode());
        if (reqUrl == null)
            throw new RuntimeException("캐시에서 URL을 찾을 수 없습니다.");

        return reqUrl;
    }
}
```

`clear()`와 `putAll()` 동작 사이에 순간적으로 빈 Map(Cache)가 노출되는 구간이 생기는걸 확인했습니다.

`ConcurrentHashMap`은 각 **단일 연산**의 thread-safety만 보장합니다. <br>
`clear()`도 안전하고 `putAll()`도 안전하지만, **두 연산 사이 구간**은 보호되지 않습니다.

아주 짧은 사이에, **CPU 캐시와 가시성 문제**로 인해, 다른 스레드가 `getUrl()`을 호출할 때, `clear()`와 `putAll()` 사이에 Map이 빈 상태로 노출됩니다.

---

## 3. 원인 — CPU 캐시와 가시성 문제

위 문제를 결론적으로는 `volatile`을 사용하여 해결했고, 위 내용을 이해하기 위해서는 volatile 내용에 대한 이해가 필요합니다.

volatile을 이해하려면 CPU가 메모리를 어떻게 다루는지 먼저 알아야 합니다.

### 3-1. CPU는 메인 메모리를 직접 읽지 않는다

현대 CPU는 성능을 위해 각 코어마다 L1/L2 캐시를 가지고 있습니다. 변수를 읽을 때 메인 메모리(RAM) 대신 이 로컬 캐시(L1, L2)에서 읽습니다.

```
Core 1 (초기화 Thread)     Core 2 (결제 Thread)
┌─────────────────┐        ┌─────────────────┐
│  L1 캐시        │        │  L1 캐시        │
│  urlCache → A   │        │  urlCache → A   │ ← 아직 갱신 전
└────────┬────────┘        └────────┬────────┘
         │                          │
         └──────────┬───────────────┘
                    ▼
             ┌─────────────┐
             │  Main Memory │
             │  urlCache → B│ ← Core 1이 새 Map으로 교체
             └─────────────┘
```

Core 1이 `urlCache`를 새 Map으로 교체했지만, Core 2는 여전히 자신의 L1 캐시에서 이전 참조(A)를 바라볼 수 있습니다. 이걸 **가시성(Visibility) 문제**라고 합니다.

### 3-2. volatile이 하는 일

`volatile`을 선언하면 해당 변수의 **읽기/쓰기를 항상 메인 메모리(RAM)에서** 처리하도록 강제합니다.

- 쓰기: Core 1이 값을 바꾸면 즉시 메인 메모리에 반영
- 읽기: Core 2가 값을 읽을 때 L1 캐시를 무시하고 메인 메모리에서 직접 읽음

```
Core 1이 volatile 변수에 쓰기
    → 메인 메모리 즉시 반영
    → 다른 코어의 해당 캐시 라인 무효화

Core 2가 volatile 변수를 읽기
    → 메인 메모리에서 직접 읽음 (캐시 바이패스)
```

### 3-3. volatile이 해결하지 못하는 것

volatile은 **참조 자체의 가시성**만 보장합니다. 복합 연산(read-modify-write)의 원자성은 보장하지 않습니다.

```java
// 이건 여전히 thread-unsafe
volatile int count = 0;
count++;  // read → modify → write 세 단계, 원자적이지 않음
```

count++ 같은 연산은 `AtomicInteger`, `synchronized` 또는 `Lock`이 필요합니다.

제일 좋은 건 스프링부트 멀티 스레드 환경에서는 공유 자원 사용하는건 지양해야 합니다.

---

## 4. 해결 방법 — 참조 교체 패턴

`clear()` + `putAll()`의 문제는 두 연산 사이 빈 Map이 노출된다는 점이 가장 큰 문제이므로 <br>
새 Map을 만들어서 참조를 한 번에 교체하면 빈 Map이 노출될 구간이 없어집니다.

```java
@Service
public class QrUrlCacheService {

    // volatile: 참조 교체 시 다른 스레드에 즉시 반영
    private volatile ConcurrentHashMap<String, String> urlCache = new ConcurrentHashMap<>();

    public void cacheInitProcessor(List<PaymentCompanyUrl> urlList) {
        Map<String, String> cacheMap = urlList.stream()
            .filter(...)
            .collect(Collectors.toMap(
                    Key::getCode,
                    Key::getUrl)
        );

        // 새 Map으로 참조 교체 — 원자적으로 전환됨
        this.urlCache = new ConcurrentHashMap<>(cacheMap);
    }

    public String getUrl(UrlFor urlFor) {
        String url = urlCache.get(urlFor.getCode());
        if (url == null) {
            log.error("[QrUrlCache] 캐시 미스 — urlFor={}, 캐시 크기={}", urlFor.getCode(), urlCache.size());
            throw new QRBankException(URL_CACHE_NOT_FOUND);
        }
        return url;
    }
}
```

```
기존 방식
  urlCache(A) → clear() → 빈 Map → putAll() → urlCache(B)
                            ↑
                      이 구간에서 getUrl() 호출 시 null

개선 방식
  urlCache(A) ──────────────────────────── urlCache(B)
                                                ↑
                                    new ConcurrentHashMap(cacheMap)
                                    참조 교체 — 빈 Map 노출 없음
```

`volatile`을 통해서, 이제는 L1/L2 캐시가 아닌 RAM을 다이렉트로 바라보게 됩니다.

---

## 5. 트레이드오프

`volatile`을 사용하면 해당 변수를 읽을 때 L1/L2 캐시를 무시하고 RAM에서 직접 읽습니다. RAM은 L1 캐시보다 약 100배 느립니다.

단, `volatile`이 보호하는 범위는 **`urlCache` 변수가 가리키는 주소값** 뿐입니다. <br>
`ConcurrentHashMap` 내부의 key-value 탐색은 여전히 L1/L2 캐시를 활용합니다.

말 그대로 캐시 참조 주소 호출만 RAM이 하는 것이고, 내부적인 Map 탐색 동작은 L1/L2 캐시를 활용합니다.

```text
urlCache.get(key) 호출 시 비용 구성

주소값 읽기 (volatile) : ~100ns  ← RAM 접근 (매번)
Map 내부 탐색 (일반)   : ~1ns    ← L1 캐시 활용
────────────────────────────────
외부 API 호출          : ~500ms  = 500,000,000ns

volatile 비용 비율     : 0.00002%
```

성능 병목을 체감하려면 해당 변수를 초당 수백만 번 읽는 핫패스 수준이어야 합니다. <br>
현재 프로젝트에서 `urlCache`는 결제 요청당 1~2회 읽는 구조라 `volatile` 비용은 무시할 수 있는 수준이었습니다.

---

## 6. synchronized vs volatile

#### volatile
- 보장: 가시성 (읽기/쓰기의 RAM 직접 반영)
- 미보장: 복합 연산의 원자성
- Lock: 없음
- 성능: 빠름

#### synchronized
- 보장: 가시성 + 원자성 + 순서 보장
- Lock: 있음 (한 스레드만 진입)
- 성능: Lock 획득/해제 비용 있음

`synchronized`를 쓰면 초기화 구간을 완전히 보호할 수 있지만, `getUrl()` 캐시 호출 메소드에도 Lock을 걸어야 완전히 안전합니다. <br>
결제 요청마다 Lock 경합이 발생해 읽기 전용 메서드가 불필요하게 직렬화됩니다.

```java
// synchronized 적용 시
public synchronized void cacheInitProcessor(List<PaymentCompanyUrl> urlList) {
    this.urlCache = new ConcurrentHashMap<>(cacheMap);
}

public String getUrl(UrlFor urlFor) {
    // getUrl도 synchronized 걸어야 완전히 안전
    // → 결제 요청마다 Lock 경합 발생
    synchronized (this) {
        String url = urlCache.get(urlFor.getCode());
    }
}
```

즉 `synchronized`를 걸면 결제 요청이 동시에 100개 들어와도 하나씩 순차 처리됩니다. <br>
읽기 전용 메서드에 Lock을 거는 건 불필요한 병목이 생길 수 있어서 지양해야 합니다.

---

## 7. 다중화 환경에서 volatile의 한계

`volatile`은 **단일 JVM 프로세스 내** 스레드 간 가시성만 보장합니다.

서버가 2대라면 JVM도 2개고, 힙 메모리도 완전히 분리되어 있습니다. <br>
Server A에서 `volatile` 쓰기가 발생해도 Server B의 메모리에는 전혀 영향을 주지 않습니다.

```
  Server A (JVM 1)              Server B (JVM 2)
┌──────────────────┐          ┌──────────────────┐
│  volatile        │          │  volatile        │
│  urlCache → B    │          │  urlCache → A    │ ← 구버전 캐시
└──────────────────┘          └──────────────────┘
         ↑                              ↑
    캐시 갱신 완료                      갱신 안 됨
```

현재 프로젝트 구조(ApplicationRunner로 기동 시 DB에서 로드)는 두 서버가 동일한 DB를 바라보기 때문에 기동 후 데이터는 일치합니다.

런타임 중 URL이 바뀔 경우에는 두 서버 모두 갱신되도록 스케줄러 주기 갱신 등의 추가 작업을 진행했습니다.

---

## 8. 정리

| 항목 | 내용 |
|------|------|
| 문제 | `clear()` + `putAll()` 사이 빈 Map 노출 |
| 원인 | 두 연산 사이 구간은 `ConcurrentHashMap`도 보호 못 함 |
| 해결 | 새 Map 생성 후 `volatile` 참조 교체 |
| volatile 한계 | 단일 JVM 내 가시성만 보장, 이중화 환경에서는 별도 동기화 필요 |

---

## Q & A

> Q1) volatile이 최초 1회만 RAM을 보고 이후에는 L1/L2 캐시를 쓰는 건지?

```text
A1)
volatile 변수는 접근할 때마다 매번 RAM을 봅니다. "1회만 RAM 보고 이후 캐시 사용"이 아닙니다.
그러므로 성능적으로 조금은 떨어질 수는 있습니다. 하지만 대부분 volatile의 매번 RAM 접근 비용은
무시할 수 있는 수준이라고 생각하며, 만약 병목이 발생한다면 그때는 위 부분보다는
다른 부분부터 점검하는게 좋다고 생각합니다.
```

---

## 참고

- [Java Memory Model — JSR-133](https://www.cs.umd.edu/~pugh/java/memoryModel/)
- [ConcurrentHashMap Javadoc](https://docs.oracle.com/en/java/docs/api/java.base/java/util/concurrent/ConcurrentHashMap.html)
## Redis Eviction이란?
Redis는 In-Memory DB이기 때문에 메모리 한계(maxmemory)에 도달하면 새로운 데이터를 쓰기 위해 기존 키를 자동으로 삭제하는 메커니즘이 Eviction이다.

maxmemory를 설정하지 않으면 메모리가 꽉 찰 때 OOM(Out of Memory) 또는 쓰기 거부가 발생한다.

### redis.conf
```shell
maxmemory 2gb
maxmemory-policy allkeys-lru
```

## 2. Eviction 정책 종류 (8가지)

크게 3가지 그룹으로 분류된다.

### 그룹 1 : noeviction (기본값)

메모리 한계 초과 시 **쓰기 명령 자체를 에러로 거부**한다. <br>
데이터 손실은 없지만 애플리케이션이 에러를 받는다. 캐시보다 **영속 데이터 저장소로 Redis를 쓸 때** 선택하는 방어적 정책이다.

---

### 그룹 2 : allkeys-* (전체 키 대상)

| 정책 | 삭제 대상 선정 방식 |
|---|---|
| `allkeys-lru` | 전체 키 중 **가장 오래 사용되지 않은** 키 삭제 |
| `allkeys-lfu` | 전체 키 중 **사용 빈도가 가장 낮은** 키 삭제 |
| `allkeys-random` | 전체 키 중 **무작위** 삭제 |

---

### 그룹 3 : volatile-* (TTL 설정된 키만 대상)

| 정책 | 삭제 대상 선정 방식 |
|---|---|
| `volatile-lru` | TTL 있는 키 중 **가장 오래 사용되지 않은** 키 삭제 |
| `volatile-lfu` | TTL 있는 키 중 **사용 빈도가 가장 낮은** 키 삭제 |
| `volatile-random` | TTL 있는 키 중 **무작위** 삭제 |
| `volatile-ttl` | TTL 있는 키 중 **TTL이 가장 짧게 남은** 키 삭제 |

---

### LRU vs LFU 핵심 차이
```
LRU (Least Recently Used)
→ "언제 마지막으로 사용됐냐"
→ 최근에 한 번 쓰인 키는 살아남음
→ 갑작스러운 접근 패턴 변화(scan성 조회)에 취약

LFU (Least Frequently Used)  ← Redis 4.0+
→ "얼마나 자주 사용됐냐"
→ 반복적으로 쓰이는 인기 키가 살아남음
→ 장기적으로 인기 없는 키를 더 정확하게 제거
```

---

## 3. 언제 어떤 정책을 쓰는가

### Case 1 : 순수 캐시 레이어 (가장 일반적)
```
정책: allkeys-lru 또는 allkeys-lfu
이유: 모든 키가 캐시 데이터이므로 TTL 여부와 무관하게 메모리 최적화 가능
예시: 상품 정보 캐싱, API 응답 캐싱, 세션 캐싱
```

### Case 2 : 중요 데이터 + 캐시 혼용
```
정책: volatile-lru
이유: TTL 없는 키(중요 데이터)는 절대 삭제 안 되고, TTL 있는 캐시만 삭제 대상
예시: 설정값(TTL 없음)과 일반 캐시(TTL 있음)를 같은 인스턴스에서 운영할 때
```

### Case 3 : 세션 / 토큰 관리
```
정책: volatile-ttl
이유: 만료 임박한 세션부터 제거 → 유효한 세션 보호
예시: JWT Refresh Token, OAuth Access Token 임시 저장
```

### Case 4 : Payment Gateway 트랜잭션 상태 관리
```
정책: volatile-lru (권장) 또는 noeviction (강력 권장)
이유: 결제 진행 중 트랜잭션 상태가 Eviction되면 데이터 정합성 문제 발생
→ 결제 관련 Redis는 maxmemory를 충분히 확보하고 noeviction + 알람 설정
→ 캐시 목적과 결제 상태 목적의 Redis 인스턴스를 반드시 분리
```

### Case 5 : Leaderboard / 실시간 집계
```
정책: allkeys-lfu
이유: 자주 조회되는 인기 랭킹 데이터 보존, 오래된 집계는 자연스럽게 제거
예시: 실시간 거래량 집계, 가맹점 랭킹
```

---

## 4. 대기업 적용 사례

### 카카오페이
카카오페이는 결제 캐시와 세션 스토어를 **Redis 클러스터 인스턴스 분리** 운영한다. 결제 트랜잭션 상태를 담는 인스턴스는 `noeviction` + 충분한 메모리 확보로 운영하고, 메모리 사용률 70% 초과 시 PagerDuty 알림이 발생하도록 설정. 캐시 전용 인스턴스는 `allkeys-lfu`를 표준 정책으로 사용한다.

### 네이버 (NAVER)
네이버 쇼핑 검색 캐시에 `allkeys-lru`를 적용하되, Redis 6.0의 **LRU approximation sample size**를 기본값 5에서 10으로 올려 정확도를 높였다. 메모리 Eviction 발생 자체를 **비정상 신호**로 간주하고, Eviction 발생 건수를 Prometheus 메트릭으로 수집해 SLO 지표로 관리한다.

### 쿠팡
쿠팡은 상품 재고 캐시에 `volatile-lru`를 사용하고, 재고가 0인 상품 키에는 TTL을 짧게 설정해 자연스럽게 우선 Eviction되도록 유도한다. 또한 Redis keyspace notification과 연동하여 Evicted 키 이벤트를 Kafka로 흘려 DB 동기화 트리거로 활용한다.

### Netflix
넷플릭스는 EVCache(자체 Redis 래퍼)에서 `allkeys-lru` 기반이지만, 전 세계 리전별로 메모리 사용률이 65%를 초과하면 **자동으로 신규 노드를 추가**하는 Auto-scaling 연동으로 Eviction 자체가 발생하지 않도록 시스템을 설계한다. Eviction은 "버그가 아닌 설계 실패 신호"로 정의.

### Line
Line은 메시지 전송 상태 캐싱에 `volatile-ttl` 정책을 사용하고, TTL을 메시지 중요도에 따라 차등 부여한다. 중요 메시지(결제 알림)는 TTL을 길게 주고 일반 메시지는 짧게 설정해 Eviction 우선순위를 자연스럽게 제어한다.

---

## 5. 장단점 정리

### allkeys-lru
| | 내용 |
|---|---|
| **장점** | 구현 단순, 대부분의 캐시 시나리오에 잘 맞음, TTL 관리 불필요 |
| **단점** | Scan성 대량 조회 시 중요 키가 밀려날 수 있음 (cache pollution) |
| **적합** | 일반 캐시, 접근 패턴이 비교적 균일한 서비스 |

### allkeys-lfu
| | 내용 |
|---|---|
| **장점** | 실제 인기도 기반 보존 → hot key 보호 효과적, LRU보다 cache hit율 높음 |
| **단점** | 빈도 카운터 관리로 메모리 오버헤드 소폭 증가, 새로 추가된 키가 초반에 불리 |
| **적합** | 접근 패턴이 불균일한 서비스 (파레토 법칙이 뚜렷한 데이터) |

### volatile-lru
| | 내용 |
|---|---|
| **장점** | TTL 없는 중요 데이터는 절대 삭제 안 됨, 캐시/영속 데이터 혼용 가능 |
| **단점** | TTL 있는 키가 적으면 OOM 발생 가능 (삭제할 대상이 없어서) |
| **적합** | 설정 데이터 + 캐시 혼용 구조 |

### noeviction
| | 내용 |
|---|---|
| **장점** | 데이터 손실 절대 없음, 예측 가능한 에러 응답 |
| **단점** | 메모리 초과 시 쓰기 자체 실패 → 애플리케이션 레벨 에러 핸들링 필수 |
| **적합** | **결제 트랜잭션 상태, 금융 데이터** 등 손실이 치명적인 경우 |

---

## 6. Redis 실무 권장 아키텍처
### Redis Instance 분리 원칙
대기업은 용도별 인스턴스 분리를 기본으로 하고, 각 인스턴스 내부에서 Master/Replica + Sentinel/Cluster 이중화를 적용한다.

### 6.1) Master/Slave(Replica) 구조
Redis의 HA(High Availability)를 위한 **동일 인스턴스 내부의 이중화** 방식이다.

```
[Master] ──write──▶ [Replica 1]
                └──▶ [Replica 2]

- Master : 읽기/쓰기 모두 처리
- Replica : 읽기 전용 (Read Scaling) + Failover 대기
- Replica는 Master 데이터를 비동기 복제
```

### 6.2) Redis 고가용성 3가지 모드

| 모드 | 구성 | 특징 | 사용 시점 |
|---|---|---|---|
| **Standalone** | Master 1대 | 단순, SPOF 존재 | 개발/테스트 |
| **Sentinel** | Master + Replica + Sentinel 3대 | 자동 Failover, 소규모 | 스타트업~중견 |
| **Cluster** | Master 3+ Shard, 각 Shard에 Replica | 수평 확장, 자동 샤딩 | 대용량/대기업 |

```
[Sentinel 구성]
Sentinel 1, 2, 3 → Master 감시 → Master 다운 시 자동 Replica 승격

[Cluster 구성]  
Shard1: M1 + R1   16384 hash slot을 샤드별로 분배
Shard2: M2 + R2   → 데이터 + 트래픽 모두 수평 분산
Shard3: M3 + R3
```

### 6.3) 용도별 인스턴스 분리

**각 도메인/목적마다 별도 Redis 프로세스(or 클러스터)를 운영**하는 방식이다.

```
1. redis-cache     : 일반 캐시 (상품 정보, API 응답 등)
- maxmemory: 적절히 제한
- maxmemory-policy: allkeys-lfu
- TTL: 데이터 특성에 맞게 설정

2. redis-session   : 세션/토큰 관리
- maxmemory-policy: volatile-ttl
- 모든 키에 TTL 필수

3. redis-payment   : 결제 트랜잭션 상태
- maxmemory: 충분히 크게 (모니터링 필수)
- maxmemory-policy: noeviction
- 알람: 메모리 70% 초과 시 즉시 PagerDuty

4. redis-ratelimit : Rate Limiting 카운터
```

### 6.4) 왜 같이 쓰면 안 되는가?
### 문제 1 : Eviction 정책 충돌

```
캐시       → allkeys-lru   (메모리 부족 시 자동 삭제 허용)
결제 상태  → noeviction    (절대 삭제 불가)

→ 하나의 인스턴스에서 두 정책을 동시에 쓰는 건 불가능
```

### 문제 2 : 장애 격리 실패

```
캐시 인스턴스에 OOM 발생
→ 같은 인스턴스의 결제 트랜잭션 상태도 함께 유실
→ 결제 정합성 붕괴
```

### 문제 3 : Persistence 설정 충돌

```
캐시       → RDB/AOF 불필요 (어차피 DB에서 재생성)
세션/결제  → AOF 필수 (데이터 유실 불허)

→ 같이 쓰면 캐시 데이터까지 디스크에 쓰는 비용 낭비
```

### 문제 4 : 성능 간섭 (Noisy Neighbor)

```
대량 캐시 조회로 Redis CPU/Memory 점유
→ 결제 트랜잭션 응답 지연 발생
→ SLA 위반
```


### 6.5) 인스턴스 분리 기준
대기업이 Redis 인스턴스를 나누는 기준은 아래 4가지다.

### 기준 1 : 데이터 손실 허용 여부

```
손실 허용 (캐시)   → 하나의 인스턴스, allkeys-lru/lfu
손실 불허 (결제)   → 별도 인스턴스, noeviction + AOF
```

### 기준 2 : Persistence 필요 여부

```
Persistence 불필요 → RDB/AOF OFF, 성능 최대화
Persistence 필요   → AOF everysec or always
```

### 기준 3 : 트래픽 특성

```
읽기 폭발적 (캐시)     → Replica 다수로 Read Scaling
쓰기 집중 (트랜잭션)   → Master 성능 최대화, Replica 최소화
```

### 기준 4 : SLA 요구 수준

```
결제/세션 : 99.99% → Sentinel 또는 Cluster 필수
일반 캐시 : 99.9%  → Sentinel 정도면 충분
개발 환경 : -      → Standalone
```


## 7. 장단점 비교

<br>

### 용도별 인스턴스 분리

| | 내용 |
|---|---|
| **장점** | Eviction 정책 독립 적용, 장애 격리, Persistence 개별 최적화, 서비스별 독립 스케일링 |
| **단점** | 운영 인스턴스 수 증가, 인프라 비용 상승, 모니터링 복잡도 증가 |
| **적합** | 결제, 금융, 의료 등 데이터 정합성이 중요한 서비스 |

<br>

### 단일 인스턴스 + Logical DB 분리 (DB 0, 1, 2...)

| | 내용 |
|---|---|
| **장점** | 운영 단순, 비용 절감, 작은 팀에서 관리 용이 |
| **단점** | Eviction 정책 공유 (치명적), 장애 격리 불가, Cluster 모드에서 DB 0만 사용 가능 |
| **적합** | 개발/스테이징 환경, 소규모 단일 서비스 |
| **⚠️ 주의** | Redis Cluster는 단일 DB(DB 0)만 지원 → 스케일아웃 시 마이그레이션 필요 |

### Sentinel vs Cluster

| | Sentinel | Cluster |
|---|---|---|
| **구성 최소** | Master 1 + Replica 1 + Sentinel 3 | Master 3 + Replica 3 (최소 6대) |
| **수평 확장** | ❌ (단일 Master) | ✅ (샤딩 자동) |
| **Multi-key 명령** | ✅ 모두 지원 | ⚠️ 같은 슬롯의 키만 가능 |
| **운영 복잡도** | 낮음 | 높음 |
| **적합 규모** | ~수십 GB | ~수백 GB, 수만 TPS |


## 8. 최종 요약
> Q1. Master/Slave 이중화 vs 용도별 인스턴스 분리 <br>
> -> 둘 다 진행하는게 좋다. 용도별로 인스턴스를 분리하고,
각 인스턴스 내부에서 Master/Replica + Sentinel/Cluster로 이중화한다.


결제 시스템에서 절대 원칙:
1. 결제 트랜잭션 Redis ≠ 캐시 Redis (혼용 금지)
2. noeviction + AOF는 결제 Redis의 기본
3. Eviction 발생 = 설계 실패 신호
4. 메모리 70% 알람 → 85% PagerDuty

<br>

### REF
- https://chagokx2.tistory.com/102

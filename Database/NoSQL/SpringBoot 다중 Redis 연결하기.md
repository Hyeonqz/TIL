# TIL: 다중 Redis 사용 방법 - Multi-Instance vs Master-Slave 완벽 가이드

> **작성일**: 2026-02-11
> **키워드**: Redis, Multi-Instance, Master-Slave, Replication, Sentinel, Spring Boot

---

## 목차
1. [개요](#개요)
2. [Multi-Instance 구조](#multi-instance-구조)
3. [Master-Slave 구조](#master-slave-구조)
4. [비교 분석](#비교-분석)
5. [선택 가이드](#선택-가이드)
6. [실전 구현](#실전-구현)
7. [트러블슈팅](#트러블슈팅)
8. [참고 자료](#참고-자료)

---

## 개요

### 다중 Redis가 필요한 이유

대규모 시스템에서는 Redis를 **용도별로 분리**하여 사용하는 것이 일반적입니다 <br>
기본적으로 시스템 고가용성을 위해선 이중화, 다중화 작업은 필수입니다.

Examples:
- **Queue**: 대기열, 메시지 큐 (영구성 중요, AOF 사용)
- **Cache**: 캐싱 (휘발성 OK, LRU/LFU 정책)
- **Session**: 사용자 세션 (TTL 필수)
- **Lock**: 분산 락 (고가용성 중요)

### 두 가지 접근 방식
| 방식 | 설명 | 대표적인 사용 사례 |
|------|------|------------------|
| **Multi-Instance** | 물리적으로 완전히 분리된 여러 개의 Redis 인스턴스 | Queue + Cache 완전 분리 |
| **Master-Slave** | 하나의 Redis 클러스터를 복제하여 고가용성 확보 | 고가용성 + 읽기 부하 분산 |


Multi-Instance 아키텍쳐를 가져가면서도, Multi-Instances 에 대한, 이중화 작업또한 필수라는 생각을 합니다.

---

## Multi-Instance 구조

### 개념

```
┌────────────────────────────┐
│  Redis Instance 1 (6379)   │ ← Queue 전용
│  - AOF 활성화              │
│  - allkeys-lru             │
└────────────────────────────┘

┌────────────────────────────┐
│  Redis Instance 2 (6380)   │ ← Cache 전용
│  - AOF 비활성화            │
│  - allkeys-lfu             │
└────────────────────────────┘
```

### 장점

1. ✅ **완전한 격리**: Queue 문제가 Cache에 영향 없음
2. ✅ **독립적 튜닝**: 각 용도에 맞게 최적화 가능
3. ✅ **장애 격리**: 한쪽 장애가 다른 쪽에 영향 없음

### 단점

1. ⚠️ **리소스 2배**: 메모리, CPU 등 리소스 2배 필요
2. ⚠️ **관리 포인트 증가**: 2개의 Redis 인스턴스 관리
3. ⚠️ **복잡한 설정**: Spring Boot에서 수동 설정 필요 (`exclude`)

---

### Docker Compose 설정

```yaml
services:
  # Queue용 Redis
  redis-queue:
    image: redis:latest
    ports:
      - "6379:6379"
    command: >
      redis-server
      --maxmemory 2gb
      --maxmemory-policy allkeys-lru
      --save 60 1000
      --appendonly yes

  # Cache용 Redis
  redis-cache:
    image: redis:latest
    ports:
      - "6380:6379"
    command: >
      redis-server
      --maxmemory 2gb
      --maxmemory-policy allkeys-lfu
      --save ""
      --appendonly no
```

---

### Spring Boot 설정

#### application.yml

```yaml
spring:
  data:
    redis:
      queue:
        host: localhost
        port: 6379
        lettuce:
          pool:
            max-active: 50
            max-idle: 20

      cache:
        host: localhost
        port: 6380
        lettuce:
          pool:
            max-active: 100
            max-idle: 30
```

#### RedisConfig.kt (Multi-Instance)

```kotlin
@Configuration
class RedisConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.data.redis.queue")
    fun redisQueueProperties(): RedisProperties = RedisProperties()

    @Bean
    @ConfigurationProperties(prefix = "spring.data.redis.cache")
    fun redisCacheProperties(): RedisProperties = RedisProperties()

    @Primary
    @Bean(name = ["redisQueueConnectionFactory"])
    fun redisQueueConnectionFactory(
        @Qualifier("redisQueueProperties") properties: RedisProperties
    ): RedisConnectionFactory {
        val config = RedisStandaloneConfiguration().apply {
            hostName = properties.host
            port = properties.port
            database = properties.database
        }
        return LettuceConnectionFactory(config)
    }

    @Bean(name = ["redisCacheConnectionFactory"])
    fun redisCacheConnectionFactory(
        @Qualifier("redisCacheProperties") properties: RedisProperties
    ): RedisConnectionFactory {
        val config = RedisStandaloneConfiguration().apply {
            hostName = properties.host
            port = properties.port
            database = properties.database
        }
        return LettuceConnectionFactory(config)
    }

    @Primary
    @Bean(name = ["redisQueueTemplate"])
    fun redisQueueTemplate(
        @Qualifier("redisQueueConnectionFactory") factory: RedisConnectionFactory
    ): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            connectionFactory = factory
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
        }
    }

    @Bean(name = ["redisCacheTemplate"])
    fun redisCacheTemplate(
        @Qualifier("redisCacheConnectionFactory") factory: RedisConnectionFactory
    ): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            connectionFactory = factory
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
        }
    }
}
```

#### CacheConfig.kt

```kotlin
@EnableCaching
@Configuration
class CacheConfig {

    @Bean
    fun cacheManager(
        @Qualifier("redisCacheConnectionFactory") factory: RedisConnectionFactory
    ): CacheManager {
        val cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .prefixCacheNameWith("cache:")

        return RedisCacheManager.builder(factory)
            .cacheDefaults(cacheConfig)
            .build()
    }
}
```

#### Application.kt (중요!)

```kotlin
@SpringBootApplication(
    exclude = [
        RedisAutoConfiguration::class,
        RedisRepositoriesAutoConfiguration::class
    ]
)
class TicketingApplication
```

**⚠️ 주의**: Multi-Instance 사용 시 반드시 Redis Auto-configuration을 exclude 해야 합니다!

---

### 사용 예시

#### Queue 사용

```kotlin
@Service
class QueueService(
    @Qualifier("redisQueueTemplate")
    private val redisTemplate: RedisTemplate<String, Any>
) {

    fun addToQueue(userId: String) {
        redisTemplate.opsForList().rightPush("waiting", userId)
    }

    fun pollFromQueue(): String? {
        return redisTemplate.opsForList().leftPop("waiting") as? String
    }
}
```

#### Cache 사용

```kotlin
@Service
class TicketService {

    // @Cacheable은 CacheConfig의 redisCacheConnectionFactory 사용
    @Cacheable(value = ["tickets"], key = "#id")
    fun getTicket(id: Long): Ticket {
        return ticketRepository.findById(id)
            .orElseThrow { TicketNotFoundException(id) }
    }
}
```

---

## Master-Slave 구조

### 개념

```
┌────────────────────────────┐
│  Redis Master (6379)       │ ← 읽기/쓰기 (Queue + Cache)
│  - Write 담당              │
└────────────┬───────────────┘
             │ 복제
             ↓
┌────────────────────────────┐
│  Redis Slave (6380)        │ ← 읽기 전용
│  - Read 담당 (부하 분산)   │
└────────────▲───────────────┘
             │ 모니터링
┌────────────────────────────┐
│  Redis Sentinel (26379)    │ ← 자동 Failover
│  - Master 장애 감지        │
│  - Slave → Master 승격     │
└────────────────────────────┘
```

### 장점

1. ✅ **고가용성**: Sentinel 기반 자동 Failover
2. ✅ **읽기 부하 분산**: Slave에서 읽기 처리
3. ✅ **설정 간소화**: Spring Boot Auto-configuration 활용
4. ✅ **리소스 절약**: 단일 클러스터로 통합
5. ✅ **MySQL과 일관성**: Master-Replica 패턴 동일

### 단점

1. ⚠️ **물리적 분리 불가**: Queue와 Cache가 같은 Redis
2. ⚠️ **Key 네이밍 규칙**: `queue:*`, `cache:*` 접두사 필수
3. ⚠️ **장애 영향 범위**: 전체 클러스터 영향

---

### Docker Compose 설정

```yaml
services:
  # Master (읽기/쓰기)
  redis-master:
    image: redis:latest
    ports:
      - "6379:6379"
    command: >
      redis-server
      --maxmemory 4gb
      --maxmemory-policy allkeys-lru
      --save 60 1000
      --appendonly yes

  # Slave (읽기 전용)
  redis-slave:
    image: redis:latest
    ports:
      - "6380:6379"
    command: >
      redis-server
      --replicaof redis-master 6379
      --replica-read-only yes
      --maxmemory 4gb
    depends_on:
      - redis-master

  # Sentinel (자동 Failover)
  redis-sentinel:
    image: redis:latest
    ports:
      - "26379:26379"
    command: redis-sentinel /etc/redis/sentinel.conf
    volumes:
      - ./redis-sentinel.conf:/etc/redis/sentinel.conf
    depends_on:
      - redis-master
      - redis-slave
```

#### redis-sentinel.conf

```conf
port 26379
sentinel monitor mymaster redis-master 6379 1
sentinel down-after-milliseconds mymaster 5000
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster 10000
```

---

### Spring Boot 설정

#### application.yml

```yaml
spring:
  data:
    redis:
      sentinel:
        master: mymaster
        nodes:
          - localhost:26379
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
        shutdown-timeout: 200ms
```

#### RedisConfig.kt (Master-Slave) ✨

```kotlin
@Configuration
class RedisConfig {

    /**
     * Lettuce 읽기 전략 설정
     * - REPLICA_PREFERRED: Slave 우선, 없으면 Master
     */
    @Bean
    fun lettuceClientConfigurationBuilderCustomizer(): LettuceClientConfigurationBuilderCustomizer {
        return LettuceClientConfigurationBuilderCustomizer { builder ->
            builder.readFrom(ReadFrom.REPLICA_PREFERRED)
        }
    }

    /**
     * RedisTemplate
     * - Spring Boot Auto-configuration의 ConnectionFactory 사용
     */
    @Bean
    fun redisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            connectionFactory = factory
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer()
        }
    }
}
```

**🎯 핵심**: Multi-Instance보다 **훨씬 간결**합니다! (125줄 → 67줄)

#### CacheConfig.kt

```kotlin
@EnableCaching
@Configuration
class CacheConfig {

    @Bean
    fun cacheManager(factory: RedisConnectionFactory): CacheManager {
        val cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            .prefixCacheNameWith("cache:")  // Key Prefix!
            .entryTtl(Duration.ofHours(1))

        return RedisCacheManager.builder(factory)
            .cacheDefaults(cacheConfig)
            .build()
    }
}
```

#### Application.kt

```kotlin
@SpringBootApplication  // ✅ exclude 불필요!
class TicketingApplication
```

---

### 사용 예시

#### Queue 사용 (Key Prefix 주의!)

```kotlin
@Service
class QueueService(
    private val redisTemplate: RedisTemplate<String, Any>
) {

    fun addToQueue(userId: String) {
        // Key: queue:waiting
        redisTemplate.opsForList().rightPush("queue:waiting", userId)
    }

    fun pollFromQueue(): String? {
        return redisTemplate.opsForList().leftPop("queue:waiting") as? String
    }
}
```

#### Cache 사용 (자동으로 cache: prefix 추가됨)

```kotlin
@Service
class TicketService {

    // Key: cache:tickets::1
    @Cacheable(value = ["tickets"], key = "#id")
    fun getTicket(id: Long): Ticket {
        return ticketRepository.findById(id)
            .orElseThrow { TicketNotFoundException(id) }
    }
}
```

---

## 비교 분석

### 코드 복잡도 비교

| 항목 | Multi-Instance | Master-Slave | 차이 |
|------|---------------|--------------|------|
| **RedisConfig** | 125줄 | 67줄 | **-58줄** |
| **CacheConfig** | 49줄 | 65줄 | +16줄 |
| **Application** | exclude 필요 | exclude 불필요 | ✅ |
| **총계** | **174줄** | **132줄** | **-42줄** |

**결론**: Master-Slave가 **24% 더 간결**합니다.

---

### 기능 비교

| 기능 | Multi-Instance | Master-Slave |
|------|---------------|--------------|
| **장애 격리** | ✅ 완전 분리 | ⚠️ Key로만 구분 |
| **독립적 튜닝** | ✅ 가능 | ❌ 불가능 |
| **자동 Failover** | ❌ 수동 | ✅ Sentinel 자동 |
| **읽기 부하 분산** | ❌ 없음 | ✅ Slave 활용 |
| **설정 복잡도** | ⚠️ 복잡 | ✅ 간단 |
| **리소스 사용** | ⚠️ 2배 | ✅ 절약 |
| **Spring Boot 호환** | ⚠️ exclude 필요 | ✅ Auto-config 활용 |

---

### 성능 비교

| 측정 항목 | Multi-Instance | Master-Slave |
|----------|---------------|--------------|
| **쓰기 성능** | ✅ 분산 가능 | ⚠️ Master만 |
| **읽기 성능** | ✅ 분산 가능 | ✅ Slave 분산 |
| **메모리 효율** | ⚠️ 2배 사용 | ✅ 절약 |
| **장애 복구 시간** | ⚠️ 수동 | ✅ 자동 (5초) |

---

### 비용 비교

| 환경 | Multi-Instance | Master-Slave | 절감 효과 |
|------|---------------|--------------|---------|
| **개발 환경** | 2개 컨테이너 | 3개 컨테이너 (Master+Slave+Sentinel) | ⚠️ +50% |
| **운영 환경** | 2개 인스턴스 × 4GB = 8GB | 2개 인스턴스 × 4GB + Sentinel = 8.5GB | ✅ 비슷 |
| **AWS ElastiCache** | 2개 × cache.m5.large | 1개 Cluster (2 nodes) | ✅ 30% 절감 |

---

## 선택 가이드

### Multi-Instance를 선택해야 하는 경우

1. ✅ **장애 격리가 최우선**: Queue 장애가 Cache에 영향 주면 안 됨
2. ✅ **독립적 튜닝 필요**: Queue는 AOF, Cache는 LFU 등 완전히 다른 설정
3. ✅ **리소스 충분**: 메모리, CPU가 충분히 여유로움
4. ✅ **서비스 규모가 큼**: 각 Redis가 독립적으로 스케일 필요

**예시**: 금융 시스템, 대규모 이커머스

---

### Master-Slave를 선택해야 하는 경우

1. ✅ **고가용성 필수**: 자동 Failover가 필요
2. ✅ **읽기 부하 분산 필요**: Slave로 읽기 부하 분산
3. ✅ **리소스 절약**: 단일 클러스터로 통합
4. ✅ **설정 간소화**: Spring Boot Auto-configuration 활용
5. ✅ **MySQL과 패턴 일치**: 아키텍처 일관성

**예시**: 스타트업, 중소규모 서비스, 학습 목적

---

### 의사결정 플로우차트

```
장애 격리가 절대적으로 중요한가?
    ├─ Yes → Multi-Instance
    └─ No
        ↓
    자동 Failover가 필요한가?
        ├─ Yes → Master-Slave
        └─ No
            ↓
        리소스가 충분한가?
            ├─ Yes → Multi-Instance
            └─ No → Master-Slave
```

---

## 실전 구현

### 패턴 1: Multi-Instance → Master-Slave 마이그레이션

#### Step 1: 데이터 백업

```bash
# Queue Redis 백업
docker exec redis-queue redis-cli BGSAVE

# Cache Redis 백업 (선택)
docker exec redis-cache redis-cli BGSAVE
```

#### Step 2: Master-Slave 클러스터 구축

```bash
# 기존 Multi-Instance 중단
docker-compose -f docker-compose-redis.yml down

# Master-Slave 시작
docker-compose -f docker-compose-redis.yml up -d
```

#### Step 3: 데이터 복원

```bash
# Master에 데이터 복원
docker cp /path/to/dump.rdb redis-master:/data/
docker restart redis-master
```

#### Step 4: Spring Boot 설정 변경

- RedisConfig 간소화
- application.yml Sentinel 설정 추가
- Application.kt에서 exclude 제거

#### Step 5: 검증

```bash
# Replication 상태 확인
docker exec redis-master redis-cli INFO replication

# 애플리케이션 시작
./gradlew :case-ticketing:bootRun
```

---

### 패턴 2: Hybrid 구조

Queue는 Multi-Instance, Session/Cache는 Master-Slave로 혼합 사용:

```yaml
services:
  # Queue 전용 (독립)
  redis-queue:
    image: redis:latest
    ports:
      - "6379:6379"

  # Session/Cache Master-Slave
  redis-master:
    image: redis:latest
    ports:
      - "6380:6379"

  redis-slave:
    image: redis:latest
    ports:
      - "6381:6379"
    command: --replicaof redis-master 6379

  redis-sentinel:
    image: redis:latest
    ports:
      - "26379:26379"
```

---

## 트러블슈팅

### 문제 1: Sentinel 연결 실패

**증상**:
```
RedisConnectionFailureException: Unable to connect to Redis Sentinel
```

**원인**: Sentinel이 Master를 찾지 못함

**해결**:
```bash
# Sentinel 로그 확인
docker logs redis-sentinel

# Sentinel 재시작
docker-compose restart redis-sentinel

# Sentinel 상태 확인
docker exec -it redis-sentinel redis-cli -p 26379 SENTINEL masters
```

---

### 문제 2: Slave 복제 지연

**증상**:
```
# Slave 상태
master_link_status: down
```

**원인**: Master와 Slave 네트워크 연결 문제

**해결**:
```bash
# Slave에서 Master PING 확인
docker exec -it redis-slave redis-cli -h redis-master PING

# Slave 재시작
docker-compose restart redis-slave

# Replication 상태 확인
docker exec -it redis-slave redis-cli INFO replication
```

---

### 문제 3: Key Prefix 충돌

**증상**: Queue 데이터와 Cache 데이터가 섞임

**원인**: Key Prefix를 제대로 사용하지 않음

**해결**:
```kotlin
// ❌ 잘못된 사용
redisTemplate.opsForList().rightPush("waiting", userId)

// ✅ 올바른 사용
redisTemplate.opsForList().rightPush("queue:waiting", userId)
```

**또는 RedisTemplate에 Prefix 자동 추가**:
```kotlin
@Bean
fun queueRedisTemplate(factory: RedisConnectionFactory): RedisTemplate<String, Any> {
    return RedisTemplate<String, Any>().apply {
        connectionFactory = factory
        keySerializer = object : StringRedisSerializer() {
            override fun serialize(key: String?): ByteArray? {
                return super.serialize("queue:$key")
            }
        }
    }
}
```

---

### 문제 4: Failover 후 데이터 유실

**증상**: Master 장애 후 Slave가 승격되었지만 일부 데이터 유실

**원인**: Slave 복제 지연 (Replication Lag)

**해결**:
```conf
# sentinel.conf 수정
sentinel down-after-milliseconds mymaster 5000
sentinel parallel-syncs mymaster 1  # 동시 복제 수를 1로 제한

# Master에서 AOF 활성화
--appendonly yes
--appendfsync everysec
```

---

## 참고 자료

### 공식 문서

- [Redis Replication](https://redis.io/docs/management/replication/)
- [Redis Sentinel](https://redis.io/docs/management/sentinel/)
- [Spring Data Redis - Sentinel](https://docs.spring.io/spring-data/redis/reference/redis/sentinel.html)
- [Lettuce Read From Settings](https://lettuce.io/core/release/reference/index.html#readfrom.read-from-settings)

### 추천 글

- [Redis Cluster vs Sentinel](https://redis.io/docs/management/scaling/)
- [Spring Boot Redis Auto-configuration](https://docs.spring.io/spring-boot/reference/data/nosql.html#data.nosql.redis)

### 도구

- **Redis Commander**: GUI 기반 Redis 클라이언트 (http://localhost:8081)
- **redis-cli**: CLI 기반 Redis 클라이언트
- **RedisInsight**: Redis Labs 공식 GUI

---

## 마무리

### 핵심 정리

| 항목 | Multi-Instance | Master-Slave |
|------|---------------|--------------|
| **추천 상황** | 완전한 격리 필요 | 고가용성 + 간소화 |
| **코드 복잡도** | ⚠️ 높음 | ✅ 낮음 |
| **리소스 사용** | ⚠️ 2배 | ✅ 절약 |
| **장애 대응** | ⚠️ 수동 | ✅ 자동 |


---

## 참고 자료

### 공식 문서

- [Redis Replication](https://redis.io/docs/management/replication/)
- [Redis Sentinel](https://redis.io/docs/management/sentinel/)
- [Spring Data Redis - Sentinel](https://docs.spring.io/spring-data/redis/reference/redis/sentinel.html)

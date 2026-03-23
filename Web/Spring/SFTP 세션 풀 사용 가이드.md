# SFTP 세션 풀 구성 및 사용 가이드

## 목차

1. [배경 및 문제 정의](#1-배경-및-문제-정의)
2. [해결 방향](#2-해결-방향)
3. [기술 스택 및 의존성](#3-기술-스택-및-의존성)
4. [구성 요소 설명](#4-구성-요소-설명)
5. [구현 코드](#5-구현-코드)
6. [로컬 테스트 환경 구성](#6-로컬-테스트-환경-구성)
7. [테스트 코드](#7-테스트-코드)
8. [장애 대응 시나리오](#8-장애-대응-시나리오)
9. [운영 시 주의사항](#9-운영-시-주의사항)

---

## 1. 배경 및 문제 정의
실무에서 SFTP 동시 접속 세션 부족 문제를 겪고, 해결한 내용을 예시 코드로 작성해보았습니다. 

### 실무 구조의 문제

```
User 파일 업로드 요청 1건 → SFTP 세션 1개 생성 → 업로드 완료 → 세션 종료
```

| 항목 | 내용 |
|---|---|
| 세션 생성 방식 | 요청마다 신규 세션 생성 |
| 서버 최대 세션 수 | 10개 |
| 문제 발생 조건 | 동시 접수 10건 초과 시 세션 고갈 |
| 결과 | 업로드 실패 → 파일 유실 → CS 발생 |

### 문제 흐름

```
동시 파일 업로드 10건 초과
       ↓
세션 풀 고갈 (세션 10개 모두 점유)
       ↓
신규 세션 생성 실패
       ↓
파일 업로드 실패 → 파일 유실
       ↓
파일 유실 CS 발생
```

---

## 2. 해결 방향

### 3단계 안전망 구조

```
1단계: SFTP 세션 풀링        → 세션 고갈 원천 차단 (장애 예방)
2단계: Dead 세션 유효성 검증   → 파일 서버 재시작 또는 장애 시 자동 복구 (장애 대응)
3단계: 지수 백오프 재시도      → 일시적 장애 자동 복구 (장애 복구)
```

### AS-IS vs TO-BE 비교

| 항목 | AS-IS | TO-BE |
|---|---|---|
| 세션 생성 방식 | 요청마다 신규 생성 | 사전 생성 후 재사용 |
| 세션 고갈 | 동시 10건 초과 시 고갈 | 풀 크기 내 재사용, 초과 시 대기 |
| Dead 세션 처리 | 없음 | 유효성 검증 후 자동 재생성 |
| 장애 대응 | 없음 | 지수 백오프 최대 5회 재시도 |
| 파일 유실 | 발생 | 0% |

---

## 3. 기술 스택 및 의존성

### build.gradle

```gradle
dependencies {
    // SFTP 클라이언트
    implementation 'com.github.mwiede:jsch:0.2.17'

    // 세션 풀링
    implementation 'org.apache.commons:commons-pool2:2.12.0'
}
```

### application.yml

```yaml
sftp:
  host: localhost
  port: 22
  username: test
  password: test
  upload-dir: /upload
  pool:
    max-total: 5        # 최대 세션 수
    max-idle: 3         # 최대 유휴 세션 수
    min-idle: 1         # 최소 유휴 세션 수 (항상 유지)
    max-wait-millis: 3000  # 세션 대기 타임아웃 (ms)
```

> **풀 크기 설정 기준**  
> 동시 접수 최대치 분석 후 결정  
> 현재 서버 최대 세션(10개)의 50% 수준인 5개로 설정  
> 트래픽 증가 시 `max-total` 조정 필요

---

## 4. 구성 요소 설명

```
┌─────────────────────────────────────────────────┐
│                   업로드 요청                      │
└───────────────────────┬─────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────┐
│            SftpUploadService                    │
│  - 풀에서 세션 가져오기                              │
│  - 업로드 실행                                     │
│  - 실패 시 지수 백오프 재시도 (최대 5회)                │
│  - 세션 반납                                      │
└───────────────────────┬─────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────┐
│         GenericObjectPool<ChannelSftp>          │
│  - 세션 5개 사전 생성 및 관리                         │
│  - 대출/반납 처리                                  │
│  - 유휴 세션 주기적 유효성 검증                        │
└───────────────────────┬─────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────┐
│            SftpChannelFactory                   │
│  - 세션 생성 (create)                             │
│  - 유효성 검증 (validateObject)                    │
│    └ Dead 세션 감지 → 폐기 → 자동 재생성              │
│  - 세션 폐기 (destroyObject)                      │
└─────────────────────────────────────────────────┘
```

---

## 5. 구현 코드

### 5-1. SftpProperties

```java
@Getter
@Setter
@ConfigurationProperties(prefix = "sftp")
public class SftpProperties {

    private String host;
    private int port;
    private String username;
    private String password;
    private String uploadDir;
    private Pool pool = new Pool();

    @Getter
    @Setter
    public static class Pool {
        private int maxTotal = 5;
        private int maxIdle = 3;
        private int minIdle = 1;
        private long maxWaitMillis = 3000;
    }
}
```

---

### 5-2. SftpChannelFactory

세션 생성, 유효성 검증, 폐기 로직을 담당합니다.

```java
@Slf4j
@RequiredArgsConstructor
public class SftpChannelFactory
        extends BasePooledObjectFactory<ChannelSftp> {

    private final SftpProperties sftpProperties;

    /**
     * 세션 생성
     * 풀 초기화 시 및 Dead 세션 교체 시 호출됩니다.
     */
    @Override
    public ChannelSftp create() throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(
            sftpProperties.getUsername(),
            sftpProperties.getHost(),
            sftpProperties.getPort()
        );
        session.setPassword(sftpProperties.getPassword());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        log.info("[SFTP] 새 세션 생성 완료");
        return channel;
    }

    @Override
    public PooledObject<ChannelSftp> wrap(ChannelSftp channel) {
        return new DefaultPooledObject<>(channel);
    }

    /**
     * 세션 유효성 검증 (핵심)
     * - testOnBorrow: 풀에서 꺼낼 때 호출
     * - testOnReturn: 풀에 반납할 때 호출
     * - testWhileIdle: 유휴 상태 주기적 호출
     * false 반환 시 해당 세션 폐기 후 자동 재생성
     */
    @Override
    public boolean validateObject(PooledObject<ChannelSftp> pooledObject) {
        ChannelSftp channel = pooledObject.getObject();
        try {
            if (channel == null
                    || channel.isClosed()
                    || !channel.isConnected()) {
                log.warn("[SFTP] Dead 세션 감지 → 폐기 후 재생성");
                return false;
            }
            // 실제 연결 상태 확인 (pwd 호출로 검증)
            channel.pwd();
            return true;
        } catch (Exception e) {
            log.warn("[SFTP] 세션 유효성 검증 실패 → 폐기: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 세션 폐기
     * validateObject가 false를 반환하거나 풀 종료 시 호출됩니다.
     */
    @Override
    public void destroyObject(PooledObject<ChannelSftp> pooledObject) {
        ChannelSftp channel = pooledObject.getObject();
        try {
            if (channel != null && channel.isConnected()) {
                channel.getSession().disconnect();
                channel.disconnect();
                log.info("[SFTP] 세션 폐기 완료");
            }
        } catch (Exception e) {
            log.error("[SFTP] 세션 폐기 실패: {}", e.getMessage());
        }
    }
}
```

---

### 5-3. SftpPoolConfig

```java
@Configuration
@EnableConfigurationProperties(SftpProperties.class)
@RequiredArgsConstructor
public class SftpPoolConfig {

    private final SftpProperties sftpProperties;

    @Bean
    public GenericObjectPool<ChannelSftp> sftpChannelPool() {
        SftpChannelFactory factory = new SftpChannelFactory(sftpProperties);

        GenericObjectPoolConfig<ChannelSftp> config =
            new GenericObjectPoolConfig<>();

        config.setMaxTotal(sftpProperties.getPool().getMaxTotal());
        config.setMaxIdle(sftpProperties.getPool().getMaxIdle());
        config.setMinIdle(sftpProperties.getPool().getMinIdle());
        config.setMaxWait(
            Duration.ofMillis(sftpProperties.getPool().getMaxWaitMillis()));

        // Dead 세션 자동 검증 설정 (필수)
        config.setTestOnBorrow(true);   // 꺼낼 때 검증
        config.setTestOnReturn(true);   // 반납 시 검증
        config.setTestWhileIdle(true);  // 유휴 상태 주기적 검증

        return new GenericObjectPool<>(factory, config);
    }
}
```

---

### 5-4. SftpUploadService

지수 백오프 기반 재시도 로직을 포함합니다.

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class SftpUploadService {

    private final GenericObjectPool<ChannelSftp> sftpChannelPool;
    private final SftpProperties sftpProperties;

    private static final int MAX_RETRY = 5;

    /**
     * 파일 업로드
     * 실패 시 지수 백오프 기반 최대 5회 재시도
     * 1s → 2s → 4s → 8s → 16s
     *
     * @param fileName 업로드할 파일명
     * @param fileData 파일 데이터 (byte[])
     * @throws SftpUploadException 최대 재시도 초과 시
     */
    public void upload(String fileName, byte[] fileData) {
        int attempt = 0;
        long waitTime = 1000L;

        while (attempt < MAX_RETRY) {
            ChannelSftp channel = null;
            try {
                // 풀에서 세션 획득
                channel = sftpChannelPool.borrowObject();
                log.info("[SFTP] 세션 획득 완료 (활성: {}/{})",
                    sftpChannelPool.getNumActive(),
                    sftpChannelPool.getMaxTotal());

                // 파일 업로드 실행
                try (InputStream inputStream =
                        new ByteArrayInputStream(fileData)) {
                    channel.put(
                        inputStream,
                        sftpProperties.getUploadDir() + "/" + fileName
                    );
                }

                log.info("[SFTP] 업로드 성공: {}", fileName);
                return;

            } catch (Exception e) {
                attempt++;
                log.warn("[SFTP] 업로드 실패 ({}/{}회): {}",
                    attempt, MAX_RETRY, e.getMessage());

                if (attempt >= MAX_RETRY) {
                    log.error("[SFTP] 최대 재시도 초과: {}", fileName);
                    throw new SftpUploadException(
                        "SFTP 업로드 최종 실패: " + fileName, e);
                }

                // 지수 백오프 대기
                try {
                    log.info("[SFTP] {}ms 후 재시도...", waitTime);
                    Thread.sleep(waitTime);
                    waitTime *= 2; // 1s → 2s → 4s → 8s → 16s
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }

            } finally {
                // 세션 반납 (성공/실패 무관 항상 실행)
                if (channel != null) {
                    try {
                        sftpChannelPool.returnObject(channel);
                        log.info("[SFTP] 세션 반납 완료");
                    } catch (Exception e) {
                        log.error("[SFTP] 세션 반납 실패: {}", e.getMessage());
                    }
                }
            }
        }
    }
}
```

---

## 6. 로컬 테스트 환경 구성

Docker를 사용하여 로컬 SFTP 서버를 실행합니다.

### docker-compose.yml

```yaml
services:
  sftp:
    image: atmoz/sftp
    # mac m 시리즈일 경우 아래 설정 추가
    # platform: linux/amd64
    ports:
      - "22:22"
    command: test:test:::upload  # user:password:::directory
```

### 실행 방법

```bash
# SFTP 서버 실행
docker-compose up -d

# 접속 확인
sftp test@localhost

# 서버 상태 확인
docker ps

# 서버 중단 (Dead 세션 테스트 시 사용)
docker-compose stop sftp

# 서버 재시작
docker-compose start sftp
```

---

## 7. 테스트 코드

### 7-1. 단건 업로드 테스트

```java
@SpringBootTest
class SftpUploadServiceTest {

    @Autowired
    private SftpUploadService sftpUploadService;

    @Autowired
    private GenericObjectPool<ChannelSftp> sftpChannelPool;

    @Test
    @DisplayName("단건 업로드 성공")
    void uploadSingleFile() {
        byte[] content = "테스트 파일".getBytes();
        assertDoesNotThrow(() ->
            sftpUploadService.upload("test.txt", content));
    }
}
```

---

### 7-2. 동시 업로드 테스트 (세션 풀 동작 검증)

```java
@Test
@DisplayName("동시 10건 업로드 - 세션 풀 동작 검증")
void uploadConcurrent() throws InterruptedException {
    int threadCount = 10;
    CountDownLatch latch = new CountDownLatch(threadCount);
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

    for (int i = 0; i < threadCount; i++) {
        final int idx = i;
        executor.submit(() -> {
            try {
                sftpUploadService.upload(
                    "test_" + idx + ".txt",
                    ("파일 내용 " + idx).getBytes()
                );
                log.info("[테스트] 업로드 완료: {}", idx);
            } catch (Exception e) {
                errors.add(e);
                log.error("[테스트] 업로드 실패: {}", e.getMessage());
            } finally {
                latch.countDown();
            }
        });
    }

    latch.await(60, TimeUnit.SECONDS);

    log.info("[테스트] 풀 상태 - 활성: {}, 유휴: {}",
        sftpChannelPool.getNumActive(),
        sftpChannelPool.getNumIdle());

    assertTrue(errors.isEmpty(), "업로드 실패 건 존재: " + errors.size());
}
```

---

### 7-3. Dead 세션 자동 복구 테스트

```java
@Test
@DisplayName("Dead 세션 감지 및 자동 재생성 검증")
void deadSessionRecovery() throws Exception {
    // 1. 업로드 성공 확인
    sftpUploadService.upload("before_restart.txt", "test".getBytes());

    // 2. Docker SFTP 서버 중단 (수동 또는 Runtime.exec 활용)
    // docker-compose stop sftp
    log.info("[테스트] SFTP 서버를 중단하세요 (docker-compose stop sftp)");
    Thread.sleep(5000); // 서버 중단 대기

    // 3. 서버 재시작
    // docker-compose start sftp
    log.info("[테스트] SFTP 서버를 재시작하세요 (docker-compose start sftp)");
    Thread.sleep(3000); // 서버 재시작 대기

    // 4. Dead 세션 자동 복구 후 업로드 재시도
    assertDoesNotThrow(() ->
        sftpUploadService.upload("after_restart.txt", "test".getBytes()));

    log.info("[테스트] Dead 세션 자동 복구 성공");
}
```

---

## 8. 장애 대응 시나리오

### 시나리오 1. 파일 서버 재시작

```
상황: SFTP 서버 재시작으로 풀의 모든 세션이 Dead 상태

대응 흐름:
1. 업로드 요청 발생
2. 풀에서 세션 획득 (testOnBorrow = true)
3. validateObject() 호출 → channel.pwd() 실패 → false 반환
4. 해당 세션 폐기 (destroyObject 호출)
5. 신규 세션 자동 생성 (create 호출)
6. 업로드 재시도
```

### 시나리오 2. 네트워크 순단

```
상황: 일시적 네트워크 장애로 업로드 실패

대응 흐름:
1. 업로드 실패 (Exception 발생)
2. 지수 백오프 대기 (1s → 2s → 4s → 8s → 16s)
3. 최대 5회 재시도
4. 재시도 성공 시 정상 처리
5. 최종 실패 시 SftpUploadException → Outbox FAILED 처리
```

### 시나리오 3. 세션 풀 초과 요청

```
상황: 동시 요청이 풀 크기(5개)를 초과

대응 흐름:
1. 풀에서 세션 요청
2. 사용 가능한 세션 없음 → maxWaitMillis(3000ms) 대기
3. 3초 내 세션 반납 시 → 정상 처리
4. 3초 초과 시 → NoSuchElementException 발생
5. 재시도 로직으로 대응
```

---

## 9. 운영 시 주의사항

### 풀 크기 조정 기준

| 항목 | 기준 |
|---|---|
| max-total | 동시 접수 최대치 분석 후 SFTP 서버 최대 세션의 50% 수준 |
| max-idle | max-total의 60% 수준 |
| min-idle | 항상 1개 이상 유지 (Cold Start 방지) |
| max-wait-millis | 업로드 평균 소요 시간 + 버퍼 (3000ms 권장) |

### 모니터링 지표

```java
// 풀 상태 주기적 로깅 권장
log.info("[SFTP Pool] 활성: {}, 유휴: {}, 대기: {}",
    sftpChannelPool.getNumActive(),
    sftpChannelPool.getNumIdle(),
    sftpChannelPool.getNumWaiters()
);
```

### 멀티 인스턴스 환경 주의사항

> ⚠️ 현재 구조는 인스턴스별 로컬 임시저장소를 사용합니다.  
> 멀티 인스턴스 환경에서 인스턴스 A가 저장한 파일을 인스턴스 B가 접근할 수 없습니다.  
> 향후 S3, NAS 등 공유 저장소 도입을 검토가 필요합니다.

### 재시도 정책 조정

```yaml
# 파일 서버 SLA에 따라 재시도 횟수 및 대기 시간 조정
sftp:
  retry:
    max-attempts: 5      # 최대 재시도 횟수
    initial-wait: 1000   # 초기 대기 시간 (ms)
    multiplier: 2        # 대기 시간 배수
    # 최종 대기 시간: 1s → 2s → 4s → 8s → 16s
```

---

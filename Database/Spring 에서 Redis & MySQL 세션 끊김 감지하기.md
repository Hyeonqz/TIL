# Spring 에서 Redis, MySQL 연결 끊김 감지하기

## 1. 들어가며
회사에서 비용 절감 차원에서 가끔 테스트 쪽 서버 몇가지를 시간을 정해서 운영한 적이 있었다 <br>
19:00 ~ 09:00 까지는 테스트 서버 몇개는는 운영하지 않았고, 서버가 다운됨에 따라, <br>
다운된 서버에 의존하는 스프링 어플리케이션은 java.net.SocketException 을 30초 주기로 계속 발생했다 <br>
```java
org.springframework.data.redis.RedisConnectionFailureException:Unable to connect to Redis; nested exception is io.lettuce.core.RedisConnectionException: Unable to connect to localhost:6379
Caused by: java.net.ConnectException: Connection refused
```

여기서 궁금증이 생겼다. 스프링은 이미 linux 메모리에 프로세스로 올라가 있는데 어떻게 Redis/MySQL 이 끊긴걸 알아챘을까?? <br>
위 내용이 궁금해서 찾아 본 내용을 정리하였다<br> 


## 2. 본론
결론부터 말하자면, Spring 은 요청이 있기 전까지는 Redis/MySQL 이 끊긴지 알 수 없다 <br>
이유는 아래 글을 읽다보면 나올 것이다 <br>

맨처음에 생각해본 방법은 스프링 어플리케이션이 interval 로 설정된 무언가가 있어서 polling 을 주기적으로 주고받는걸까? 라는 생각을 하였다 <br>

그럼 몇초 주기로 polling 을 하는거지? timeout 몇초를 보고 죽었다고 판단하는거지? 라는 고민을 찾기 위해 `DataSource` 인터페이스를 열심히 찾아보았다 <br>

하지만 명확한 해결책을 보지못했고 검색해본 결과 답은 다른 것이였다 <br>
위 polling 방법은 상위 수준에서 요청 <-> 응답 구조를 가지고 있지만 <br>
생각해보니 더 저수준인 TCP 로 세션을 맺으면 한번 연결해두고 둘중 하나가 끊지 않는 이상 계속 연결되는 것 이였다 <br>

### 2-1) TCP 소켓 연결감지 메커니즘 원리
```java
// Spring이 Redis/MySQL과 연결하는 방식
┌─────────────────┐    TCP Socket     ┌─────────────────┐
│ Spring App      │ ←───────────────→ │ Redis/MySQL     │
│ (Client)        │      (양방향통신)    │ (Server)        │
└─────────────────┘                   └─────────────────┘

// 연결 상태
1. ESTABLISHED: 정상 연결
2. FIN_WAIT: 종료 대기
3. CLOSE: 연결 종료
```

Spring 이 Client 라고 생각을 하고 Redis/MySQL 은 서버의 입장이다 <br>
그리고 Spring 은 TCP 소켓 상태 변화를 통해 연결 끊김을 감지한다 <br>

그리고 내가 한가지 간과 하고 있었던건 건 내가 어플리케이션 헬스체크 대상에 MySQL, Redis 을 30초 주기로 체킹하게 설정을 해뒀기에 감지가 되었던 것이였다 <br>
기본적으로 스프링은 어떠한 요청이 들어오기 전까지 죽었는지 살았는지 알 수가 없다 <br>
```java
┌─────────────────┐    TCP Socket     ┌─────────────────┐
│ Spring App      │ ←───────────────→ │ Redis/MySQL     │
│ (Client)        │                   │ (Server)        │
└─────────────────┘                   └─────────────────┘

// 연결 감지 시점
1. 서버 다운 → TCP RST/FIN 패킷 전송 → 소켓 상태 변경
2. Health Check 클라이언트가 실제 요청 전송 시도
3. SocketException 발생 → Spring에서 감지
```

그럼 Spring Actuator 구축이 안되어있는 상황이라면 어떻게 감지를 해야할까? <br>

1. spring 설정을 변경 (yml, @Bean 등록) 을 통한 능동적 감지
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20          # connection pool
      minimum-idle: 5
      connection-timeout: 3000      # 30초
      idle-timeout: 60000           # 10분
      max-lifetime: 180000          # 30분
      validation-timeout: 3000       # 5초
      connection-test-query: "SELECT 1"
      test-while-idle: true
      test-on-borrow: true
```


위 처럼 간단한 방법 말고도 모니터링 로직을 따로 추가한다거나, 인프라 적으로 모니터링을 건다거나 다양한 방법이 있다 <br>

실제 운영상황에 서버가 다운되는건 최악의 문제이다 <br>
그러므로 모니터링 하는 것이 정말 중요하다 <br>


## 3. 결론
- DB 연결 상태 감지 -> 소켓 기반 (Polling 아님)
- Redis/MySQL 세션 종료 시	소켓 끊김 → 요청이 오는 시점에 감지

추가적으로 짚고 넘어가야 하는 점은 모니터링에 대한 중요성 이다 <br>
- 능동적 감지의 중요성
  - 단순 연결이 끝이 아닌, 주기적인 상태 확인은 필수다

즉 그러므로 spring actuator 를 적극적으로 활용했으면 하는 바램이 있다 <br>
자체적으로 구축하여 사용을 하여도 좋지만, 이미 잘 추상화되어 구현된 라이브러리가 존재하므로 적극 활용을 추천한다! <br>

spring actuator 에 대한 참조할만한 글들을 아래 정리해 두었다
- https://incheol-jung.gitbook.io/docs/study/srping-in-action-5th/chap-16.
- https://www.baeldung.com/spring-boot-actuators


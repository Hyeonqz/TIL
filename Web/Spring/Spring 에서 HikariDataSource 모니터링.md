# Spring 에서 HikariDataSource 모니터링 진행하기

## 개요
<hr>

실무에서 가끔 `java.io.IOException: Connection reset by peer` 메시지를 본적이 있다 <br>
위 메시지를 조사해보다가 결국 나온 답은 애플리케이션 커넥션 풀 점검 이였다 <br>
- 커넥션 풀에서 오래된 연결을 재사용하지 않도록 설정했는지
- HikariCP, Tomcat pool의 타임아웃 설정 확인

위 2가지 위주를 보기 위해서 내용들을 찾아보았고, 찾아보면서 공부해본 내용을 공유해보려고 한다 <br><br>


## 본론
<hr>

> 개발환경: SpringBoot3.3, Jdk17, MySQL, JPA 를 실무에서 사용하고 있다 <br>

### 0. 기본 세팅
첫번째로 찾아본 내용은 springboot 자체에서 DataSource 를 찾아서 모니터링을 하는 것이다 <br>

Springboot 는 내장 톰캣을 이용하고 커넥션 풀을 사용하여 데이터베이스 커넥션을 지원한다 <br>
SpringBoot 2.0부터 **HikariCP**가 기본 커넥션 풀이 되었다. <br>
(이전 SpringBoot1.x에서는 Tomcat JDBC Pool이 기본이였다) <br>

SpringBoot 는 `DataSource` 인터페이스를 제공한다 <br>
그리고 `DataSource` 구현체로 `HikariDataSource` 를 사용한다 <br>

위 내용은 springboot starter data jpa 의존성에서 자동으로 지원을 한다 <br>

그리고 의존성 추가시 mysql, postgresql 등을 추가했다면 그걸 기반으로 jdbc 드라이버를 자동으로 로드하여 DataSource Bean 을 구성한다 <br>
Jdbc Driver 는 각각 다르지만 DataSource 구현체는 `HikariDataSource` 로 동일하다 <br>

아래는 HikariDataSource 설정 예시이다
```yaml
spring:
  datasource:
    hikari:
      # 커넥션 풀 사이즈 설정
      maximum-pool-size: 8
      minimum-idle: 4
      
      # 커넥션 라이프사이클 관리
      max-lifetime: 1800000          # 30분 (ms)
      idle-timeout: 600000           # 10분 (ms)
      connection-timeout: 30000      # 30초 (ms)
      
      # 커넥션 유효성 검증
      connection-test-query: SELECT 1
      validation-timeout: 5000       # 5초 (ms)
      
      # 성능 최적화
      leak-detection-threshold: 60000 # 1분 (ms)
      
      # 추가 설정
      pool-name: TestPool
      auto-commit: true
      read-only: false
```

위 설정은 yml(properties) 에서 설정하거나 아니면 Spring Config 를 통해 Bean 을 생성해주면 된다 <br>
```java
@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driverClassName}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        // 데이터베이스 연결 정보 설정
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);

        // 커스텀 풀 설정
        config.setMaximumPoolSize(4);
        config.setMinimumIdle(2);
        config.setIdleTimeout(60000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);
        config.setConnectionTestQuery("SELECT 1");
        config.setValidationTimeout(5000);
        config.setLeakDetectionThreshold(60000);
        config.setPoolName("TestHikariPool");
        config.setAutoCommit(true);
        config.setReadOnly(false);

        return new HikariDataSource(config);
    }

}

```

위 처럼 설정을 할 수 있다 <br>
위 `HikariConfig` 가 아니라 `HikariDataSource` 를 사용하여 설정을 잡을 수 있지만, 다중 DataSource 환경을 고려한다면 `HikariConfig` 를 사용하는게 좋다 <br>

이제 위 설정을 기반으로 실제 서버가 뜰 때 어떻게 동작하고 있는지를 보려고 봐보자 <br>

### 1. Springboot 기능을 통한 모니터링
```java
@Slf4j
@RequiredArgsConstructor
@Component
public class HikariPoolMonitor {
    private final DataSource dataSource;

    @EventListener
    public void handleContextRefreshedEvent(final ContextRefreshedEvent event) {
        monitorHikariCp();
    }

    @Scheduled(fixedRate = 60000) // 60초 주기
    public void monitorHikariCp() {
        if(dataSource instanceof HikariDataSource hikariDataSource) {
            HikariPoolMXBean poolMXBean = hikariDataSource.getHikariPoolMXBean();

            log.info("===HikariCP Pool Status===");
            log.info("Active Connections: {}", poolMXBean.getActiveConnections());
            log.info("Idle Connections: {}", poolMXBean.getIdleConnections());
            log.info("Total Connections: {}", poolMXBean.getTotalConnections());
            log.info("Threads awaiting connection : {}", poolMXBean.getThreadsAwaitingConnection());
            log.info("========================================");

        } else {
            log.warn("DataSource is not HikariDataSource : {}", dataSource.getClass().getName());
        }
    }
}
```

```java
        2025-08-13T00:14:11.296+09:00  INFO 17079 --- [   scheduling-1] o.h.springlab.config.HikariPoolMonitor   : ===HikariCP Pool Status===
        2025-08-13T00:14:11.296+09:00  INFO 17079 --- [   scheduling-1] o.h.springlab.config.HikariPoolMonitor   : Active Connections: 0
        2025-08-13T00:14:11.296+09:00  INFO 17079 --- [   scheduling-1] o.h.springlab.config.HikariPoolMonitor   : Idle Connections: 2
        2025-08-13T00:14:11.296+09:00  INFO 17079 --- [   scheduling-1] o.h.springlab.config.HikariPoolMonitor   : Total Connections: 2
        2025-08-13T00:14:11.297+09:00  INFO 17079 --- [   scheduling-1] o.h.springlab.config.HikariPoolMonitor   : Threads awaiting connection : 0
        2025-08-13T00:14:11.297+09:00  INFO 17079 --- [   scheduling-1] o.h.springlab.config.HikariPoolMonitor   : ========================================
```

위 모니터링 결과가 콘솔에 로그로 찍히게 된다 <br>
실무에서는 위 데이터를 보고 안정적이지 않을 때 조치를 취하는 것은 정말 중요하다 <br>
ex) Connection Pool 부족할 것 같으면 알람을 보낸다 <br><br>
```java
@Component
public class ConnectionPoolAlertManager {

    @EventListener
    @Async
    public void handleHighConnectionUsage(ConnectionPoolEvent event) {
        HikariPoolMXBean pool = event.getPoolMXBean();

        // 80% 이상 사용 시 알림
        double usageRate = (double) pool.getActiveConnections() / pool.getTotalConnections();
        if (usageRate > 0.8) {
            // Slack, 이메일 등으로 알림 체계에 맞게 메시지 발송
            alertService.sendConnectionPoolAlert(usageRate);
        }

        // 대기 중인 스레드가 있을 경우 즉시 알림
        if (pool.getThreadsAwaitingConnection() > 0) {
            alertService.sendUrgentAlert("Connection pool exhausted!");
        }
    }
}
```


### 2. Spring Actuator 를 사용한 모니터링
```java
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    runtimeOnly ("io.micrometer:micrometer-registry-prometheus")
```

위 2개의 의존성이 기본적으로 필요하다 <br>

위 의존성을 추가하고 application.yml 에 설정을 잡아줘야 한다 <br>
```yaml
management:
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include: "*"
```

위 설정을 잡은 후에 'http://localhost:8080/actuator/prometheus' 에 접속하면은 아주 긴 text 가 나온다 <br> 
많은 내용중에서 Database Connection 관련 내용들만 보면 아래와 같다 <br>
```java
# HELP jdbc_connections_active Current number of active connections that have been allocated from the data source.
# TYPE jdbc_connections_active gauge
jdbc_connections_active{name="dataSource"} 0.0
        # HELP jdbc_connections_idle Number of established but idle connections.
        # TYPE jdbc_connections_idle gauge
jdbc_connections_idle{name="dataSource"} 2.0
        # HELP jdbc_connections_max Maximum number of active connections that can be allocated at the same time.
# TYPE jdbc_connections_max gauge
jdbc_connections_max{name="dataSource"} 4.0
        # HELP jdbc_connections_min Minimum number of idle connections in the pool.
        # TYPE jdbc_connections_min gauge
jdbc_connections_min{name="dataSource"} 2.0
```

위 정보를 분석하면 아래와 같다
- Active Connections: 0개 (사용 중인 커넥션 없음)
- Idle Connections: 2개 (유휴 상태 커넥션)
- Max Pool Size: 4개 (최대 허용 커넥션)
- Min Pool Size: 2개 (최소 유지 커넥션)


위 내용을 나중에는 Grafana 에 추가하여 시각화하여 보면 더 편하다! <br>
그 부분은 나중에 진행을 해보겠다 <br>


## 결론
<hr>
요즘들어 모니터링에 중요성을 자주 느낀다 <br>
서비스가 커져감에 따라 메트릭 분석하는 능력또한 중요하게 여겨진다 <br>
아직 모르는게 너무 많고 배울게 너무 많다.. 그래서 아직은 개발자가 재밌다 <br>

결국 Connection reset by peer 메시지는 위 설정을 잡아줌에 따라 많이 없어졌다 <br>


### Reference    
> 1. https://www.openmaru.io/connection-reset-by-peer/ <br>
> 2. https://junuuu.tistory.com/968


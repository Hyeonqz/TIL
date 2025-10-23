# Apparent connection leak detected 문제 해결하기

## 1. 개요
애플리케이션 배포 후 HikariCP에서 Connection Leak 경고 로그가 발생하여 문제를 해결한 과정을 기록한다. <br>
실제 운영 환경이 아닌 테스트 서버에서 발생했으며, 시스템 장애를 유발하지는 않았지만 잠재적인 리소스 누수를 방지하기 위해 즉시 조치하였다 <br>

> Tech Stack: Spring Boot 3.3, Java 17, MySQL 8.0, JPA, HikariCP

<br>

## 2. 문제 상황
애플리케이션 기동 시 DB와의 최초 연결을 감사 로그로 기록하는 요구사항이 있었다. <br>
이를 위해 ApplicationRunner를 구현하여 DB 연결 정보를 수집하고 이벤트를 발행하는 로직을 작성했다. 

```java
@Slf4j
@Component
public class DataSourceAuditHelper implements ApplicationRunner {
    private final ApplicationEventPublisher auditEventPublisher;
    private final Environment environment;
    private final DataSource dataSource;

    @Value("${spring.application.name}")
    private String applicationName;

    public DataSourceAuditHelper(ApplicationEventPublisher auditEventPublisher, Environment environment, 
                                 DataSource dataSource) {
        this.auditEventPublisher = auditEventPublisher;
        this.environment = environment;
        this.dataSource = dataSource;
    }

    /**
     * Spring Bean 이 초기화 될 때 ApplicationEvent 보다 늦게 초기화 되기 때문에 Spring Bean 및 Event 가 다 초기화 된 이후에 Event를 발생시킨다.
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        String activeProfiles = environment.getActiveProfiles()[0];

        publishDataSourceAuditEvent(activeProfile);
    }

    private void publishDataSourceAuditEvent(String profile) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            auditEventPublisher.publishEvent(new DbConnectionEvent(metaData));
        }
    }

}
```

최초 DB 연결 정보를 로깅해야 했기에 dataSource Connection 을 사용하였고, <br>
사용한 커넥션을 돌려주기 위해 try-with-resources 패턴을 사용하였다 <br>

고수분들은 위 로직을 보자마자, 왜 Connection leak 이 나오는지 알 수 있을 것이다 <br>
지금 글을 정리하면서 아차 하는 생각은 들지만 개발 중에는 미쳐 생각을 하지 못했다. <br>

아래 설정은 필자가 HikariDataSource 를 Custom 하여 사용한 설정이다 
```java
@Slf4j
@RequiredArgsConstructor
@Configuration
public class DataSourceConfig {
    private final Environment environment;

    @Bean
    public DataSource dataSource() {
        String profile = environment.getActiveProfiles()[0];
        /*
         * 로컬 dev, 테스트 test, 운영 prod
         * */
        DataSource dataSource = getDataSource(profile);

        if (isProfileDev(profile)) {
            return getDevDataSource(dataSource);
        } 
        if (isProfileTest(profile)) {
            return getTestHikariDataSource(dataSource);
        }
        if (isProfileProd(profile)) {
            return getProdHikariDataSource(dataSource);
        }
    }

    @NotNull
    private HikariDataSource getTestHikariDataSource(DataSource dataSource) {
        HikariDataSource hikariDataSource = new HikariDataSource();

        hikariDataSource.setDataSource(dataSource);
        hikariDataSource.setPoolName("XXService-Test-Hikari");
        hikariDataSource.setMaximumPoolSize(10); // 연결 풀 크기 증가 -> 기본은 10
        hikariDataSource.setMinimumIdle(5); // 유휴 연결수 -> 즉시 사용 가능한 연결
        hikariDataSource.setIdleTimeout(300000); // 유휴 상태 연결이 풀에 유지되는 최대 시간 10분
        hikariDataSource.setConnectionTimeout(30000); // 연결 요청 최대 대기 시간
        hikariDataSource.setMaxLifetime(600000); // 연결 최대 수명 10분 -> 10분 지나면 연결이 강제로 닫힘
        hikariDataSource.setLeakDetectionThreshold(60000); // 연결 반환되지 않고 60초 이상 유지되면 연결 누수로 간주하고 경고 로그 출력

        return hikariDataSource;
    }
}

```

그리고 위 로직을 테스트 서버에 배포한 후 나온 로그는 아래있다 <br>

### 2.1) 발생한 문제
```java
2025-10-23 16:20:04 | []  () |  | XX-ASYNC-1(3260087) | com.p6spy.engine.spy.appender.Slf4JLogger:60
insert into db_connection_audit_log ('컬럼 이름은 비밀') values ('')
2025-10-23 16:21:04 | []  () |  | XX-Service-Test-Hikari housekeeper(3260087) | com.zaxxer.hikari.pool.ProxyLeakTask:84
    Connection leak detection triggered for HikariProxyConnection@483093970 wrapping com.mysql.cj.jdbc.ConnectionImpl@7ff70cf1 on thread main, stack tra                                 ce follows
 java.lang.Exception: Apparent connection leak detected
        at com.zaxxer.hikari.HikariDataSource.getConnection(HikariDataSource.java:128)
        at com.p6spy.engine.spy.P6DataSource.getConnection(P6DataSource.java:300)
        at org.springframework.jdbc.datasource.DelegatingDataSource.getConnection(DelegatingDataSource.java:101)
        at kr.co.qrbank.common.helper.DataSourceAuditHelper.publishDataSourceAuditEvent(DataSourceAuditHelper.java:53)
        at kr.co.qrbank.common.helper.DataSourceAuditHelper.run(DataSourceAuditHelper.java:48)
        at org.springframework.boot.SpringApplication.lambda$callRunner$4(SpringApplication.java:786)
        at org.springframework.util.function.ThrowingConsumer$1.acceptWithException(ThrowingConsumer.java:83)
        at org.springframework.util.function.ThrowingConsumer.accept(ThrowingConsumer.java:60)
        at org.springframework.util.function.ThrowingConsumer$1.accept(ThrowingConsumer.java:88)
        at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:798)
        at org.springframework.boot.SpringApplication.callRunner(SpringApplication.java:786)
        ...
```

타임라인 분석: <br>
16:20:04 - DB 감사 로그 INSERT 완료 <br>
16:21:04 - Connection Leak 감지 (60초 후)

leakDetectionThreshold(60000) 설정에 의해, Connection이 Pool에서 체크아웃된 후 60초 이상 반환되지 않으면 경고 로그가 출력된다. <br>
이 설정 덕분에 개발 단계에서 잠재적 리소스 누수를 조기에 발견할 수 있었다.

즉 이벤트 발행을 통해 DB 감사로그를 찍을 때 Connection Leaked 이 탐지된다는 뜻이다 <br>

이제 문제를 확인했고, 해결한 과정을 적어보려고 한다 <br>

### 2.2) 원인 분석
문제가 되는 로직은 아래 부분이다.
```java
    private void publishDataSourceAuditEvent(String profile) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            auditEventPublisher.publishEvent(new DbConnectionEvent(metaData));
        }
    }
```

위 로직을 분석해보면 아래와 같다. 
```java
    private void publishDataSourceAuditEvent(String profile) throws SQLException {
        // [1번 커넥션 획득] Connection Pool에서 첫 번째 Connection 획득
        try(Connection connection = dataSource.getConnection()) {
            
            // [2번 커넥션 획득] 동일한 DataSource에서 두 번째 Connection 획득 -> Connection Leaked
            DatabaseMetaData metaData = dataSource.getConnection().getMetaData();
            auditEventPublisher.publishEvent(new DbConnectionEvent(metaData));

        }   // [1번 커넥션 반납] try-with-resources에 의해 1번 Connection만 자동 반환
        // → 2번 Connection은 반환되지 않고 Pool에 남아있음
    }
```

즉 2번 커넥션을 획득했고, 1번 커넥션은 자동으로 닫아주지만, 2번 커넥션은 획득하고, 반납을 하지않았다 <br>
두 번째 Connection은 DatabaseMetaData를 통해 간접 참조되지만, getMetaData() 메서드는 Connection을 자동으로 닫지 않았다 <br>

### 2.3) 해결 방법
결론적으로 문제는 간단하게 해결하였다, 2번 커넥션을 획득하지 않게 로직을 바꾸면 되는것이였다
```java
    private void publishDataSourceAuditEvent(String profile) throws SQLException {
        // 1번 커넥션 획득하여 단일 커넥션 사용
        try(Connection connection = dataSource.getConnection()) { 
            DatabaseMetaData metaData = connection.getMetaData(); 
            auditEventPublisher.publishEvent(new DbConnectionEvent(metaData));
        } // 1번 커넥션 반납 끝 커넥션 누수 없이 정상 처리 완료
    } 
```

## 결론
DataSource 를 직접 컨트롤하여 무언가 작업을 해야 한다면 무조건 dataSource 사용 후 close 해주는 로직이 필요하다 <br>
-> DataSource 인터페이스는 AutoClosable 을 구현하지 않으므로 명시적인 close() 가 필요하다 <br>

정말 당연한 이야기지만, 필자가 까먹지 않기 위해서 글로 남기며 되새김질을 해본다 <br>
DB 커넥션 관련 로직이 필요할 때는 코드 리뷰를 통해 꼭 한번 점검하고, 두번 점검을 해보길 권장한다. <br> 

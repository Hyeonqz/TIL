# SpringBoot 어플리케이션 생명주기 이벤트 EventListener 부분 알아보기

## 개요
<hr>
스프링부트 서버가 기동 될 때 Spring Context(Bean) 이 초기화 되는 순서 관련 문제가 발생해서 <br>
위 문제를 해결하기 위해서 해답을 찾던 중 스프링 생명주기 이벤트에 대한 내용을 알게되서 알게된 내용을 정리해보았다. <br>

## 본론
<hr>

#### 스프링 생명주기 이벤트란 무엇일까?
Springboot 서버가 기동되는 종료되는 과정에서 발생하는 이벤트를 스프링 생명주기 이벤트 라고 한다 <br>
스프링 생명주기 사이클 및 동작 순서를 알고 있으면 더욱 더 스프링 프레임워크를 잘 활용해서 사용할 수 있을 것이라고 생각한다 <br>

#### 왜 사용할까?
위 생명주기 이벤트를 잘 활용해서 이점을 보기 좋은 부분은 요구사항에 의해 특정 시점에 비즈니스 로직에 변동을 줘야할 때 때 라고 생각한다<br>
**Example**
- 초기 데이터 insert
- Cache
- 특정 생성자 초기화
- 특정 비즈니스 로직 특화된 부분

등등 여러 상황에서 유용하게 사용이 가능할 것으로 생각한다 <br>


### SpringBoot 생명주기 이벤트
아래 순서가 SpringBoot 서버가 시작하는 순서이다 <br>
```
1. ApplicationStartingEvent            (스프링 처음 기동시)
2. ApplicationEnvironmentPreparedEvent
3. ApplicationContextInitializedEvent
4. ApplicationPreparedEvent
5. ContextRefreshedEvent               (컨텍스트 완료)
6. ApplicationStartedEvent             (애플리케이션 시작)
7. ApplicationReadyEvent               (시작 준비 완료)
8. ApplicationFailedEvent              (실패 시)
```

위 중에서 내가 원했던 부분을 처리하기 위해서 알아야했던 이벤트는 3가지가 이다 


#### 1. ContextRefreshedEvent
```java
@EventListener(ContextRefreshedEvent.class)
public void onContextRefreshed(ContextRefreshedEvent event) {
    log.info("=== Spring Context Refreshed Success ===");
    // 모든 빈 생성, 의존성 주입, @PostConstruct 완료
    // 하지만 애플리케이션이 외부 요청을 받을 준비는 안 됨*
}
```

- **모든 빈 초기화 완료**
- **@PostConstruct 모두 실행 완료**
- **스프링 컨텍스트 완전 준비**
- **하지만 서버 포트 바인딩 미완료** (내장 톰캣 포트 오픈 등 서버는 아직 미시작)
- **웹 애플리케이션이 아닌 일반 스프링 앱에서도 발생**

#### 2. ApplicationStartedEvent

```java
@EventListener(ApplicationStartedEvent.class)
public void onApplicationStarted(ApplicationStartedEvent event) {
    log.info("=== SpringBoot Application 시작 완료 ===");
    // 내장 톰캣 서버 포트 오픈 및 시작됨, 하지만 CommandLineRunner/ApplicationRunner 미실행
}
```

- **서버 포트 바인딩 완료** (8080 포트 등 listen 시작)
- **웹 요청 수신 가능**
- **하지만 CommandLineRunner, ApplicationRunner 아직 미실행**
- **비즈니스 로직 초기화에는 이른 시점**

### 3. ApplicationReadyEvent

```java
@EventListener(ApplicationReadyEvent.class)
public void onApplicationReady(ApplicationReadyEvent event) {
    log.info("=== SpringBoot Server 완전 준비 완료 ===");
    // 모든 것이 완료된 최종 시점
}
```

- **CommandLineRunner, ApplicationRunner 모두 실행 완료**
- **애플리케이션이 완전히 준비된 상태**
- **외부 요청 처리 가능**
- **비즈니스 초기화 로직에 가장 안전한 시점**


실제 코드를 보고 Console 에 찍힌 부분을 체크해 보자 <br>
```java
@Slf4j
@Component
public class SpringLifeCycleEvent {

    /**
     * 1. ContextRefreshedEvent - 가장 빠른 시점
     * - 모든 빈이 생성되고 초기화된 직후
     * - 웹 서버 시작 전
     * - 데이터베이스 연결, 캐시 초기화 등에 적합
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed(ContextRefreshedEvent event) {
        log.info("1. ContextRefreshed - 빈 초기화 완료, 서버 시작 전");
        log.info("- 애플리케이션 컨텍스트: {}", event.getApplicationContext().getClass().getSimpleName());
    }

    /**
     * 2. ApplicationStartedEvent - 중간 시점
     * - 웹 서버 시작 완료
     * - CommandLineRunner 실행 전
     * - 외부 시스템 연결, API 클라이언트 초기화에 적합
     */
    @EventListener(ApplicationStartedEvent.class)
    public void onApplicationStarted(ApplicationStartedEvent event) {
        log.info("2. ApplicationStarted - 서버 시작 완료, CommandLineRunner 실행 전");
        log.info("- 애플리케이션: {}", event.getSpringApplication().getClass().getSimpleName());
    }

    /**
     * 3. ApplicationReadyEvent - 가장 늦은 시점
     * - 모든 준비 완료, 요청 처리 가능
     * - 워밍업, 헬스체크, 알림 발송에 적합
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        log.info("3. ApplicationReady - 모든 준비 완료!");
        log.info("- 애플리케이션 완전 준비됨");
    }
}
```

아래는 실제 이벤트 로그를 찍어놓고, 서버를 실행시킨 결과이다.
```java
2025-08-16T13:22:10.087+09:00  INFO 80626 --- [cTaskExecutor-9] o.h.springlab.SpringLifeCycleEvent       : 1️. ContextRefreshed - 빈 초기화 완료, 서버 시작 전
2025-08-16T13:22:10.087+09:00  INFO 80626 --- [cTaskExecutor-9] o.h.springlab.SpringLifeCycleEvent       :    - 현재 시각: 2025-08-16T13:22:10.087758
2025-08-16T13:22:10.088+09:00  INFO 80626 --- [TaskExecutor-14] o.h.springlab.SpringLifeCycleEvent       : 2. ApplicationStarted - 서버 시작 완료, CommandLineRunner 실행 전
2025-08-16T13:22:10.088+09:00  INFO 80626 --- [TaskExecutor-14] o.h.springlab.SpringLifeCycleEvent       :    - 애플리케이션: SpringApplication
2025-08-16T13:22:10.089+09:00  INFO 80626 --- [TaskExecutor-19] o.h.springlab.SpringLifeCycleEvent       : 3. ApplicationReady - 모든 준비 완료!
2025-08-16T13:22:10.088+09:00  INFO 80626 --- [cTaskExecutor-9] o.h.springlab.SpringLifeCycleEvent       :    - 애플리케이션 컨텍스트: AnnotationConfigServletWebServerApplicationContext
2025-08-16T13:22:10.088+09:00  INFO 80626 --- [TaskExecutor-14] o.h.springlab.SpringLifeCycleEvent       :    - 현재 시각: 2025-08-16T13:22:10.088315
2025-08-16T13:22:10.089+09:00  INFO 80626 --- [TaskExecutor-19] o.h.springlab.SpringLifeCycleEvent       :    - 현재 시각: 2025-08-16T13:22:10.089786
2025-08-16T13:22:10.089+09:00  INFO 80626 --- [TaskExecutor-19] o.h.springlab.SpringLifeCycleEvent       :    - 애플리케이션 완전 준비됨
```

위 이벤트들은 기본적으로 **동기 방식 실행**이지만, 스프링의 기본 TaskExecutor 설정에 따라 다른 스레드에서 실행될 수 있습니다. <br>
로그에서 보이는 다른 스레드명은 이벤트 처리 방식 때문이다 <br> 

위 전체적인 사이클 순서를 보고, 이벤트 이름을 보면 뭔가 순서가 앞뒤가 바뀐것 같은 생각이 들었다 <br>

초기화 순서를 보면
```
1. ApplicationStartingEvent            
2. ApplicationEnvironmentPreparedEvent
3. ApplicationContextInitializedEvent
4. ApplicationPreparedEvent
5. ContextRefreshedEvent               
6. ApplicationStartedEvent     --> ???
7. ApplicationReadyEvent       --> ???
8. ApplicationFailedEvent           
```

논리적으로 보면 Ready 가 된 후에 Started 가 되어야 한다고 생각하지만, 스프링 팀에서는 위 부분을 반대로 진행하고 있다는 생각이 들었다. <br>

springboot 프로젝트에 비슷한 issue 를 남겨보려고 했지만, 이미 같은 생각을 했던 케이스가 있었지만, 반영은 되지 않은듯 하다. <br>

그러므로 네이밍으로 구분하기에는 약간 헷갈리는 부분이 있긴하다..
```java
@Component
public class PaymentSystemLifecycle {
    
    @EventListener(ApplicationStartedEvent.class)
    public void onServerStarted() {
        // "서버 포트는 열렸지만 아직 모든 준비 안 됨"
        log.info("서버 시작됨 - 하지만 아직 모든 컴포넌트가 준비되지 않음");
        
        // 기본적인 헬스체크 준비 정도만
        healthCheckService.enableBasicCheck();
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void onFullyReady() {
        // "이제 진짜 모든 게 준비 완료"
        log.info("애플리케이션 완전 준비 - 모든 비즈니스 로직 사용 가능");
        
        // 실제 비즈니스 초기화
        paymentServiceInitializer.initialize();
        healthCheckService.enableFullCheck();
    }
}
```

진짜 사용자에게 서비스를 제공할 준비가 된것이 ApplicationReadyEvent 이고 <br>
톰캣 포트가 열리면서 서버 기동 준비가 된 것이 ApplicationStartedEvent 라고 이해하면 된다. <br>

이벤트 사용 관련해서 **best practice** 는 아래와 같다.
- ContextRefreshedEvent: 기본 시스템 검증
- ApplicationStartedEvent: 외부 연결 초기화
- ApplicationReadyEvent: 비즈니스 로직 초기화

<br>

### [번외편] Spring 은 왜 @PostConstruct 를 통한 초기화를 안티패턴으로 보는걸까?
스프링에서 @PostConstruct 은 빈(bean)이 초기화된 후 특정 메서드를 실행하도록 한다 <br>
유용한 기능이긴 하지만, 스프링을 사용하는 사람들 사이에서는 , 이를 안티패턴으로 간주하는 경우가 있다. <br>

그 이유는 아래와 같다.
1. 유연성 부족:
- @PostConstruct는 특정 빈의 초기화 로직을 해당 빈 클래스 내부에 tightly-coupled(강하게 결합) 시킨다. 
- 따라서, 빈의 초기화 로직을 변경하거나 다른 빈에 재사용하기 어려워진다 -> 초기화 로직을 변경하려면 해당 빈 클래스를 수정해야 하므로, 유지보수가 어려워질 수 있다. -> **변경에 취약함**

2. 테스트 어려움:
- @PostConstruct 메서드는 빈이 생성되고 초기화되는 시점에 자동으로 실행되므로, 테스트 환경에서 원하는 시점에 실행하거나 특정 상황을 Mocking 하기 어렵다. 
- 의존성 주입(DI)을 통해 초기화 로직을 제어할 수 없기 때문에, 테스트를 위한 격리가 어렵고 복잡해질 수 있습니다.

3. 단일 책임 원칙(SRP) 위배:
- 빈의 초기화 로직이 너무 복잡해지면, 빈의 본래 목적에서 벗어나 초기화 로직까지 담당하게 되어 단일 책임 원칙(Single Responsibility Principle)을 위배할 수 있다.

4. 의존성 초기화 순서 문제:
- @PostConstruct는 해당 빈만 초기화된 후 실행되므로, 다른 빈들이 완전히 준비되지 않을 수 있다
- 특히 데이터베이스나 외부 시스템 연결이 필요한 초기화 로직에서 문제가 발생할 수 있다.

5. 순환 의존성 위험:
- @PostConstruct에서 다른 빈을 호출할 때 순환 의존성이나 초기화 순서 문제가 발생할 수 있다.
  - 필자도 위 부분 때문에 고생을 했다..


#### 대안은 뭐가 있을까?
1. 의존성 주입 사용:
- 가능하다면, 초기화 로직을 의존성 주입을 통해 처리하는 것이 좋다. 
   
2. 팩토리 메서드 사용:
- 빈 생성과 초기화를 분리하여 팩토리 메서드를 사용하면, 초기화 로직을 보다 유연하게 관리할 수 있다.
  - 팩토리 메서드는 빈의 생성과 초기화를 책임지므로, 빈 클래스는 순수한 비즈니스 로직에 집중할 수 있습니다.


3. ApplicationContextAware 인터페이스 사용:
- ApplicationContextAware 인터페이스를 구현하여 직접 스프링 컨테이너(컨텍스트)에 접근하고, 필요한 초기화 작업을 수행할 수 있다.
- 단점은 메소드에서 한번만 호출 가능하다. 재사용이 힘들다.
  

4. Spring ApplicationEvent 인터페이스 사용
- ApplicationEvent 인터페이스 구현체 중에서 위 설명에 맞는 것을 사용하면 초기화에 유용하게 사용이 될것으로 생각한다. 

@PostConstruct는 편리하지만, 유연성 저하와 테스트 어려움 등의 문제로 인해 안티패턴으로 간주될 수 있다. <br>
그러므로 최대한 지양하여 다른 대안을 활용하여 초기화 로직을 관리하여 더 좋은 설계로 나아갈 수 있게 해야겠다 <br>


## 결론
<hr>

필자는 @PostConstruct 를 사용하여 가끔 생성자 초기화를 담당하였고, 이번 기회에 많은 @PostConstruct 로직을 걷어내고 더 안전한 방식을 도입하게 되었다 <br>

추가적으로 스프링부트 생명주기 사이클에 대한 내용을 자세하게 알게되었고, 이제는 위 부분을 잘 활용할 수 있을 것 같다 <br>

참고로 위 EventListener 말고 `CommandLineRunner`, `ApplicationRunner` 등 여러가지 방법이 더 존재한다 <br>
위 부분은 추후 시간이 될 때 공부해보고 정리한 내용을 공유하려고 한다


### Reference
> https://mangkyu.tistory.com/233 <br>

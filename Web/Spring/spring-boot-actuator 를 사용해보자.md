# spring-boot-actuator 를 사용해보자.

> https://www.baeldung.com/spring-boot-actuators
>> https://toss.tech/article/how-to-work-health-check-in-spring-boot-actuator

제가 작성한 내용은 위 글들을 참고하여 작성 하였습니다.<br>


시작에 앞서 한가지 질문을 던져보자. 왜 Spring Actuator 를 사용해야 하고 어떠한 상황에서 사용해야 할까? <br>
위 질문을 본인한테 해보고 나름 기술적 도입에 대한 이유 및 타당한 근거가 있다면 위 기술을 도입해야 할것이다 <br>
그냥 이런거 써보니 좋더라~ 다른 사람들도 다 이거쓰더라~ 라는 이유로 기술을 도입하게 된다면 나중에 분명 문제가 생길 수 있다 <br>

어떠한 기술이든 잘 알고 쓰는게 참 중요한 것 같다는 생각이 든다 <br>
그냥 동작만 한다고해서 좋은 소프트웨어가 아니듯이, 그냥 동작만 하는 소프트웨어는 문제가 발생하기 전까지는 문제를 방치하기 마련이다 <br>

사용하는 기술이나 구조에 대하여 끊임없이 의심하고 질문하고 탐구를 할 필요가있다. 그래야 좋은 소프트웨어가 탄생한다고 생각한다 <br>

나는 실무에 위 라이브러리를 도입하게 된 계기는 Spring 어플리케이션에 대한 모니터링이 필요했다 <br>
너무 딥하게 JVM 모니터링 까지는 필요하지 않아 찾아보다가 Spring 에서 지원하는 위 라이브러리를 찾아보았고, 어플리케이션 단에서만 모니터링을 할 수 있다해서 위 기술을 채택하게 되었다 <br>

서비스의 고가용성, 고성능을 위한 부하 분산 등의 이유로 우리는 서버의 이중화를 하고, 그 앞에서 어떤 서버로 요청을 보낼지 라우티 역할을 하는 로드 밸런서를 둔다 <br>
로드 밸런서에서는 각 서버의 헬스 체크 API 를 호출해서 해당 서버가 현재 서비스 가능한 상태인지 아닌지 주기적으로 점검을 해야 한다 <br>

헬스 체크 API 경로는 커스텀하게 설정 가능하다 -> /health <br>
헬스 체크에서 서버에 문제가 발견되면 로드 밸런서는 해당 서버로 요청을 보내지 않게 된다 <br>

헬스 체크는 정상적으로 서비스가 가능한 서버에만 트래픽을 보내서 서비스의 고가용성을 확보하는데 도움을 준다 <br>

## Spring Boot Actuator 헬스 체크
> implementation 'org.springframework.boot:spring-boot-starter-actuator'

위 의존성을 추가하면 기본적으로 헬스 체크 엔드포인트가 활성화가 된다 <br>
> GET http://127.0.0.1:8081/actuator/health

Spring Boot Actuator 는 어떤 기준으로 서버의 헬스 체크를 할까요? <br>
확인하기 위해서는 <a href="https://docs.spring.io/spring-boot/docs/3.0.5/reference/html/actuator.html#actuator.endpoints.health">링크</a> 에서 확인할 수 있습니다 <br>

모든 정보를 체크하기 위해서는 아래 와 같은 설정이 필요합니다
```java
management:
  # 모든 정보 항상 체크
  endpoint:
    health:
      show-details: always
  
  # 모든 엔드포인트를 웹에 노출
  endpoints:
    web:
      exposure:
        include: "*"
```

만약 특정 엔드포인트를 활성화 하기 위해서는 management.endpoint.{엔드포인트명}.enabled=true 를 적용하면 된다 <br>

위 설정을 추가하게 되면 의존성에 추가된 내용을 바탕으로 정보를 확인할 수 있습니다 <br>


### metrics
metrics 엔드포인트를 사용하면 기본적으로 제공되는 메트릭을 확인할 수 있고 수집된 matrics 는 프로메테우스,그라프나 를 통하여 모니터링을 할 수 있다 <br>

현재까지는 spring actuator 가 제공하는 기능들은 우리 애플리케이션 내부 정보를 자세하게 다 보여주기 때문에 위험하다 <br>

## 헬스 체크시 조심해야 하는 점
Spring Boot Actuator 헬스 체크의 동작원리를 잘 모르고 사용하면 아래와 같은 문제가 발생할 수 있다 <br>

### 1. 의도치 않은 장애 발생

### 2. 트러블 슈팅의 지연


TODO 추후 Metrics 에 대한 내용 좀더 추가하자 아직 미완성임.
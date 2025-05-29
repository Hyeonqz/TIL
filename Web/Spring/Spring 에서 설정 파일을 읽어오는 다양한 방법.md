# Spring 에서 Properties 파일을 읽어오는 다양한 방법

현재 실무에서 application.yml 에 환경 변수 또는 어떠한 static 한 값들을 정의해두고 사용하고 있다 <br>
그리고 보통 profile 별로 분리해서 사용하고, 각 profile(=환경) 마다 사용해야 하는 값들이 다를 것이다. <br>

예시를 보면 아래와 같다.
```yaml

#Profile: dev
---
spring:
  config:
    activate:
      on-profile: dev

hkjin:
  email: "abcd-test@naver.com"
  
#Profile: prod
---
spring:
  config:
    activate:
      on-profile: prod

hkjin:
  email: "abcd@naver.com"
```


dev 환경에서 이메일을 호출할 때는 abcd-test@naver.com 가 필요하고, prod 환경에서는 abcd@naver.com 가 필요하다 <br>
즉 같은 hkjin.email 구조를 가진 yml 파일이지만, 환경에 따라 다른 값들이 세팅될 필요가 있다는 뜻이다 <br>

그 방법을 하나씩 알아보자 <br>

### Spring 은 어떻게 yml 파일을 읽어 올까?
Spring이 기동될 때 ApplicationContext가 Environment 추상화를 통해 yml (또는 properties) 파일들을 읽어와서 설정 값을 로드 한다 <br>
그러므로 yml, properties 설정 값들은 Environment 인터페이스를 통해 접근할 수 있게 된다. <br>

주 구현체로는 StandardEnvironment, StandardServletEnvironment, ConfigurableEnvironment 가 존재한다 <br>

그 중에서도 property 들은 ConfigurableEnvironment 인터페이스를 통해 주입이 되며, map 에 key,value 형식으로 저장이 되어있다 <br>
```java
	/**
	 * Return the value of {@link System#getProperties()}.
	 * <p>Note that most {@code Environment} implementations will include this system
	 * properties map as a default {@link PropertySource} to be searched. Therefore, it is
	 * recommended that this method not be used directly unless bypassing other property
	 * sources is expressly intended.
	 */
	Map<String, Object> getSystemProperties();
        
```

<br>

### 1. @Value
보편적으로 많이 사용되는 방법중 하나이다. 대상 클래스가 Spring Bean 에 등록되어 있어야지만 주입이 가능하다.

```java

@Service
public class KakaoPaymentService {
    
    @Value("${hkjin.email}")
    private String email;
}
```

### 2. Environment Abstraction
실제 비즈니스 로직을 관리하는 클래스에서는 자주 사용되지 않고, 환경변수 값을 여러 곳에서 호출해야 할 때 사용하였다 
```java
@RequiredArgsConstructor
@Component
public class TestEnvironment {
    private final Environment environment;
    
    public String getProperty(String key) {
        return environment.getProperty(key);
    }

}

@RequiredArgsConstructor
@Service
public class TestA {
    private final TestEnvironment testEnvironment;

    public void test(String message) {
        String property = testEnvironment.getProperty(message);
    }
}


```

실제 비즈니스 로직을 관리하는 클래스에서는 자주 사용되지 않고, Spring의 Environment 인터페이스를 직접 활용하여 애플리케이션 전반의 환경 변수나 설정 값에 접근해야 할 때 유용하다. <br>
대부분 동적으로 다양한 프로퍼티를 조회할 필요가 있을 때 활용한다 <br>


### 3. @ConfigurationProperties 
특정 prefix 를 가진 설정 값들이 많을 때 유용하게 사용한다 <br>
참고로 위 설정은 Getter, Setter 는 필수적으로 만들어야 한다 <br>

```java
@Component
@ConfigurationProperties(prefix = "hkjin")
@Getter @Setter
public class AppConfig {
    private String email;
}
```


<br>

### Spring Bean 이 아닌 곳에서 application.yml 값이 필요할 때는?
Spring 을 사용한다면 Bean 을 활용한 인스턴스 생성 및 라이프 사이클을 관리하면 좋지만, Bean 에 등록되지 않는 곳에서 설정 값이 필요한 상황이 있을 수도 있다 <br>
위 상황에서 나는 아래와 같이 대응하였다 <br>

```java

@Component
@ConfigurationProperties(prefix = "hkjin")
@Getter @Setter
public class AppConfig {
    private String email;

    @PostConstruct
    public void init() {
        ZeroPayService.setZeroPayService(this.email);
    }
}

public class ZeroPayService {
    private String email;
    
    public static void setZeroPayService(String email) {
        email = email;
    }
}
```

Spring Bean 이 업로드가 되고 ZeroPayService 인스턴스가 생성이 되는 초기화시점에 정적 메소드를 사용하여 주입을 하였다 <br>
하지만 위 방법은 스프링 DI 를 포기하는 것이기 때문에 권장하지는 않지만, 어쩔 수 없는 상황에서는 쓸 필요가 있다 <br>

Spring 을 사용한다면 인스턴스가 생성이되야하는 객체들은 최대한 Bean 으로 등록하고 사용하는게 좋다고 생각한다.. <br>

<br><br>

최종적으로는 필자는 @Value, @ConfigurationProperties 방식을 선호한다 <br>


> REF
> > https://www.baeldung.com/spring-boot-yaml-vs-properties

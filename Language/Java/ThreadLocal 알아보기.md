# ThreadLocal 알아보기

실무에서 가끔 ThreadLocal 자료구조를 사용하였고, 위 자료구조에 대해서 자세하게 공부를 해보았다 <br> 

## 동작 구조
ThreadLocal은 각 스레드마다 독립적인 변수 복사본을 제공하는 자료구조 이다 <br>

각 Thread 객체는 ThreadLocalMap 내부 Map<K,V> 를 가지고 있다. <br>
즉 ThreadLocal 객체가 키(key)가 되고, 저장하려는 값이 값(value)이 된다 <br> 

실제 사용은 Thread.currentThread().threadId()  통해 현재 스레드의 맵에 접근한다 <br>
```java
// ThreadLocal.get() 메서드의 동작
public T get() {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null) {
        ThreadLocalMap.Entry e = map.getEntry(this);
        if (e != null) {
            return (T)e.value;
        }
    }
    return setInitialValue();
}
```

각 스레드가 ThreadLocal 변수에 접근할 때, 자신만의 독립적인 저장 공간에서 값을 가져오거나 설정한다. <br>

위 자료구조를 사용하는 주된 이유는 캐싱을 목적으로 많이 사용할 것이라고 생각한다 <br>
멀티 스레드 환경에서 각 스레드당 독립적인 값을 보장해주니 편하게 사용하기 좋다고 생각한다. <br>

## 사용방법
```java
public class ThreadLocalStorage {
    public static final ThreadLocal<String> sessionId = new ThreadLocal<>();
    public static final ThreadLocal<String> userId = new ThreadLocal<>();
}
```

필자는 위 ThreadLocalStorage 클래스에 ThreadLocal 변수를 static 으로 선언해두고 사용하는 편이다 <br>

그리고 실제로 특정 로직에서 ThreadLocal 에 set 을 한다면 set 을 요청한 스레드 간에 공유가 가능하다. <br>
set, get 방법은 아래와 같다.
```java
    void test () {
        ThreadLocalStorage.sessionId.set("123456789");
        ThreadLocalStorage.userId.set("123456789-102313");

        log.info("ThreadLocalSessionId : {}", ThreadLocalStorage.sessionId.get());
        log.info("ThreadLocalUserId : {}", ThreadLocalStorage.userId.get());
    }
```

```java
2025-07-29T19:43:18.119+09:00  INFO 4111 --- [Thread-1] o.h.s.a.threads.ThreadLocalStorage: ThreadLocalSessionId : 123456789
2025-07-29T19:43:18.119+09:00  INFO 4111 --- [Thread-1] o.h.s.a.threads.ThreadLocalStorage: ThreadLocalUserId : 123456789-102313
```

요청 스레드간에 이제 자유롭게 사용이 가능하다 <br>
그리고 스레드 사용이 다 끝났다면 ThreadLocal 을 remove 해줘야 한다 <br>
remove 를 해주지 않으면 그 스레드에 대한 ThreadLocal 객체가 살아있어 GC 대상이 잡히지 않아 추후 메모리 누수로 이어질 수 있다.
```java
        ThreadLocalStorage.userId.remove();
        ThreadLocalStorage.sessionId.remove();
```

위 처럼 간단하게 사용을 할 수 있다 <br>


그리고 메모리 누수를 방지하기 위해서 항상 try-finally 블록에서 실행하는 것이 좋다 <br>
```java
public void start() {
    
    try {
        ThreadLocalStorage.sessionId.set("123456789");
        ThreadLocalStorage.userId.set("123456789-102313");
        
        // 비즈니스 로직
        
    } finally {
        ThreadLocalStorage.userId.remove();
        ThreadLocalStorage.sessionId.remove();
    }
}
```

## 장점
#### 1. 스레드 안전성 (Thread Safety)
- 각 스레드가 독립적인 변수 복사본을 가지므로 동기화 없어도 안전
- synchronized, lock 없이도 스레드 간 데이터 충돌 방지

#### 2. 성능 향상
- 동기화 오버헤드가 없어 멀티스레드 환경에서 효율이 좋음
- os 레벨 스레드 간 컨텍스트 스위칭 비용 감소 


#### 3. 코드 간소화
- 메서드 파라미터로 값 전달이 필요 없고, 전역에서 편하게 접근 가능
  - 깊은 호출 스택에서도 쉽게 데이터에 접근 가능 
  - -> 이 부분이 생각보다 엄청 유용합니다. 너무 많은 파라미터는 좋지 않으므로 ThreadLocal 사용을 통한 클린 코드를 만들 수 있음

#### 4. 격리성 보장
- 각 스레드의 데이터가 완전히 격리되어 예측 가능한 동작을 보장합니다


## 한계
#### 1. 비동기 환경에서의 동작 불가
- 비동기 처리 필요 시 ThreadLocal 사용 불가
  - CompletableFuture, @Async 사용 -> 새로운 작업 스레드 생성
  - 원본 스레드와 비동기 작업 스레드가 다르므로 ThreadLocal 값에 접근 시 -> null 반환함

#### 2. 메모리 누수 위험
- 명시적으로 remove() 를 해주지 않으면 메모리 누수 위험
- `ThreadLocalMap` 의 Entry 는 ThreadLocal 인스턴스의 `WeakReference`를 키로 사용하고 실제 저장될 값은 강한 참조를 가지고 있다'
  - WeakReference 덕분에 ThreadLocal 객체 자체가 외부에서 더 이상 참조되지 않으면 GC 대상이 될 수 있지만 ThreadLocalMap 내의 값(value)은 스레드가 살아있는 한 강한 참조로 유지된다 <br>
  - `ThreadLocal` 객체가 GC 대상이 되더라도, 해당 스레드의 ThreadLocalMap 에는 여전히 null 키와 함께 값(value)이 남아있게 되어 메모리 누수가 발생하게 된다.
  - 그러므로 값을 지워주는 `remove()`를 꼭 호출해야 한다 그래야 메모리 누수를 방지할 수 있다.
  - 특히 SpringBoot 에서 사용하는 내장 Tomcat 은 스레드 풀을 사용하여 스레드를 재사용 하기 때문에 메모리 누수에 취약할 수 있다.

    
## 결론
`ThreadLocal`은 요청별 변하지 않는 특정 값 ex) 사용자 세션 정보(예: `sessionId`, `userId`)를 저장해 메서드 파라미터 전달을 줄이는 데 유용하다 <br>
하지만 비즈니스 프로세스상 비동기 처리 또는 애플리케이션 전역 캐싱이 필요한 경우에는 ThreadLocal 이 아닌 다른 대안을 찾아야 한다 <br>
ex) API 응답 캐싱(전역캐싱) ->  Caffeine Cache,  분산 환경 -> Redis

<br>

## REF
• [Oracle Java Documentation - ThreadLocal](https://docs.oracle.com/javase/8/docs/api/java/lang/ThreadLocal.html)
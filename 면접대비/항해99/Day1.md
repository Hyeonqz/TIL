# Day1

## 동시성 문제 해결
#### Q. 동시성 문제란 무엇이며, 이를 해결하기 위한 기본적인 전략을 설명해주세요. 실제 운영 환경에 적용한 사례가 있다면 함께 설명해 주어도 좋습니다.
동시성이라 함은 작게 보면 어떠한 기능에 User 가 동시에 접근하는 경우를 의미한다

ex) 여러 User 가 동시에 주문 요청을 한다

나는 SpringBoot/Java 를 사용하는 멀티스레드 환경에 있다

멀티스레드 환경인 만큼 동시성에 그나마 안전하다

Java 진영에서 위 상황을 막기 위해서 여러가지 방법이 존재한다

기본적으로 Java 에서는 synchronized 키워드를 제공한다
```java
public synchronized void createOrder() {
    // 주문 비즈니스 로직 작성
}
```

위 키워드를 메소드에 붙으면 하나의 스레드만 접근하게 끔, 임계 영역으로 지정되어 동시성에 안전해진다.

또는 다른 방법으로 Lock 인터페이스를 사용하는 것이다 <br>
```java
    private final Lock lock = new ReentrantLock();

    public void add(int value) {
        lock.lock();
        try {
            this.count += value;
        } finally {
            lock.unlock();
        }
    }
```

참고로 jdk5 이후 concurrent 가 나오면서 동시성 지원에 간편해졌다 <br>

추가적으로 SpringBoot 환경에서는 다양한 방법으로 동시서 제어를 할 수 있다 <br>
- 동시성 제어를 위한 트랜잭션 격리 수준
- Lock 메커니즘 활용
    - Redis 사용
    - rdb lock 사용
    - orm lock 사용 ex) jpa

일반적으로는 LOCK 메커니즘을 사용할 때 낙관락, 비관락 2가지를 주로 사용하고는 한다 <br>

실제 나는 분산 환경에서 Batch 작업이 동시간대에 동작하면서 같은 파일을 2번 생성하는 것을 막기 위해 <br>
Redission 을 활용하여 Lock 선점을 통한 동시성 방지를 처리하였다.
# @Transactional 전파와 JPA 1차 캐시로 인한 데이터 불일치 트러블슈팅


## 1. 개요
실무에서 결제 취소 관련 비즈니스 로직을 운영 중에 이번에 새로 배포된 결제 진행 중 강제 취소 기능이 있었다 <br>
회사에서는 따로 QA 가 존재하지 않아 필자가 직접 테스트 시나리오를 짜서 간단한 단위 테스트 및 직접 QA 를 진행하였다 <br>
분명 위 기능을 테스트 할 때 까지는 정상 동작했지만, 운영 배포 후 결제 진행 중 강제 취소가 되지 않는 케이스가 발생하였다 <br>

이번 글에서는 JPA 1차 캐시와 트랜잭션 격리 수준으로 인한 데이터 불일치 문제와 해결 과정을 기록해보았다 <br>

> Skill: SpringBoot3.3 JPA(Hibernate), MySQL

## 2. 문제 상황
### 2.1) 비즈니스 로직 개요
결제 진행 중 강제 취소 요청이 들어오면, 다음과 같은 흐름으로 처리된다
```text
1. 강제 취소 요청 수신
2. 원거래가 승인될 때까지 대기 (최대 5회 재시도, 각 2.5초 간격)
3. 승인 확인 후 취소 처리 실행
```
초기 코드는 다음과 같았다 <br>

```java
    @Async("abortAsyncExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processAbort(EndpointInputs.Refund refund) {
        try {
            int maxAttempts = 5; // 최대 5회 재시도
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                Payment abortTargetPayment = paymentRepository.findByOrderId(endpointInput.getOrderId())
                    .orElseThrow(() -> new OrderIdNotFoundException(GlobalErrorCode.ORDER_ID_NOT_FOUND));

                if (isAlreadyAbort(abortTargetPayment)) {
                    log.info("이미 취소 처리가 완료된 건 입니다.);
                    return;
                }
                
                if (isReadyAbort(abortTargetPayment)) {
                    log.info("취소 준비 완료 -> 취소 처리 진행");
                    refundService.executeAbortProcess(abortTargetPayment, refund);
                    return;
                }
                
                if (attempt == maxAttempts) {
                    abortTargetPayment.updateErrorMessage("[취소 실패] 승인되지 않은 거래입니다.");
                    paymentRepository.save(abortTargetPayment);
                }
            }
        } catch (AbortException e) {
            log.error("취소 실패 : {} - {}", GlobalErrorCode.SERVER_ERROR, e.getMessage());
        }
    }
```

로직을 좀 더 설명하자면, abortTargetPayment가 결제 진행 중에 승인이 나게 되면 승인 처리를 위해 데이터가 업데이트 된다. <br>
강제 취소 요청은 해당 거래가 승인 완료되기를 기다렸다가 취소를 진행해야 했다 <br>


### 2.2) 발생한 문제
강제 취소 요청 이후 해당 OrderId의 거래는 실제로 승인이 완료되었지만, 강제 취소 로직에서 조회 시 여전히 승인되지 않은 상태로 보이는 문제가 발생했다. <br>

예상 시나리오 (Happy Case)
```text
결제 요청 → 승인 진행 중 → 강제 취소 요청 
→ 승인 완료 (Payment 상태: APPROVED로 업데이트) 
→ 강제 취소 처리 (승인 상태 확인 후 진행) 
→ 취소 완료
```

실제 상황 (Bad Case)
```text
결제 요청 → 승인 진행 중 → 강제 취소 요청 
→ 승인 완료 (DB에는 APPROVED로 업데이트됨) 
→ 강제 취소 처리 (여전히 PROGRESS 상태로 조회됨!) 
→ 5회 재시도 후 "승인되지 않은 거래" 에러 발생
```

현재 필자는 Bad Case 에 해당하는 상황을 겪고 있었다 <br>

분명 실제 DB 데이터는 업데이트가 되어 승인 상태로 남아있기에, 강제 취소 처리가 된다고 생각했지만, <br>
실제로는 강제 취소 처리 로직에서 조회시 해당 데이터가 업데이트가 되어있지 않아 데이터 불일치로 인한 실패 처리가 되고 있었다 <br>

문제를 곰곰히 생각하던 중, 설마라고 생각한 부분에서 해답을 찾았다 <br>

### 2.3 원인 분석
핵심은 비동기 처리를 위해서 상위 메소드에 걸어둔 `@Transactional(propagation = Propagation.REQUIRES_NEW)` 이 문제였고 엄밀히 말하면 트랜잭션 경계와 1차 캐시가 문제였다  <br>

최상단에 걸려있는 `@Transactional` 있는 상태로 Payment 엔티티를 조회하면 아래과 같은 결과가 보여진다 <br>
> state = "PROGRESS", 

재시도 5회를 진행해도 상태는 계속 'PROGRESS' 로 동일 했다. <br>
실제로는 2~3회차에 거래 승인이 완료되어 다른 트랜잭션에서 state = "APPROVED"로 업데이트했지만, 현재 로직에서는 이를 감지하지 못했다. <br>

분명 해당 엔티티 상태가 변경되었는데 현재 로직에서는 변경되지 않은 상태로 조회가 되는지 이유를 찾아야했다 <br> 

기본적으로 JPA 를 사용하기에 1차 캐쉬 부분을 바로 생각하였다 <br>

#### @Transactional이 있을 때 실행 흐름
```java
@Transactional // 메소드 호출 시 트랜잭션 생성
public void processAbort() {
    // 전체 메서드가 하나의 트랜잭션 내에서 실행
    for (int attempt = 1; attempt <= 5; attempt++) {
        // 하나의 트랜잭션으로 묶여 같은 계속 같은 영속성 컨텍스트 내에서 조회 중
        Payment payment = paymentRepository.findByOrderId(...) 
            .orElseThrow(...);
        // 최상단 트랜잭션(1차 캐싱) 상태 모든 쿼리 및 비즈니스 로직 동작
    }
} // 메소드 종료 시 트랜잭션 커밋 & 1차 캐시 소멸
```

자세한 실행 흐름은 아래와 같다
```text
[트랜잭션 시작] - 1차 캐시 생성

[1회차]
- SELECT 쿼리 1개 (Payment)
- Payment 엔티티가 1차 캐시에 저장됨
  ex: Payment(id=123, state="PROGRESS")

[2회차]
- findByOrderId(같은 번호)
- JPA가 1차 캐시에서 Payment(id=123) 발견
- **DB 쿼리 없이 캐시에서 반환**

[3~5회차]
- 동일하게 캐시에서 반환
- 여전히 state="PROGRESS" 반환
- 외부 변경사항 감지 불가

[트랜잭션 커밋] - 1차 캐시 소멸
```

현재는 위와 같은 흐름으로 동작 하는 중이였다.<br>
뭐가 문제인지 정확하게 꺠닫게 되었다 <br>

그래서 이번에는 @Transactional 을 제거하고 하위 메소드에 트랜잭션을 걸고 사용을 한 결과다 
```java
public void processAbort(...) {
    for (int attempt = 1; attempt <= 5; attempt++) {
        // 매 호출마다 새로운 트랜잭션 생성 (Spring Data JPA 기본 동작)
        // 트랜잭션 시작 → 쿼리 실행 → 트랜잭션 종료
        Payment payment = paymentRepository.findByOrderId(...)  
            .orElseThrow(...);
        // 1차 캐시가 트랜잭션 종료와 함께 소멸
    }
}
```

```text
[1회차]
- 새 트랜잭션 시작
- SELECT 쿼리 1개 (Payment)
- Payment 엔티티가 1차 캐시에 저장 
  - ex: Payment(id=123, state="PROGRESS")
- Commit -> 1차 캐시 초기화
  
[2회차]
- 새 트랜잭션 시작
- SELECT 쿼리 1개 (Payment)
- Payment 엔티티가 1차 캐시에 저장 후 Commit 되어 사라짐
  - ex: Payment(id=123, state="PROGRESS")
- Commit -> 1차 캐시 초기화

[3회차]
- 새 트랜잭션 시작
- SELECT 쿼리 1개 (Payment)
- Payment 엔티티가 1차 캐시에 저장 후 Commit 되어 사라짐
  - ex: Payment(id=123, state="APPROVED")
-> 상태 변화 감지 완료 -> 비즈니스 로직 처리
- Commit -> 1차 캐시 초기화
```

문제를 정확하게 깨닫게 되었다 <br>

최상단에 트랜잭션을 선언하고 전파방식 또한 일반적으로 상위 트랜잭션을 전파받아서 사용을 하니 1개의 로직에서 트랜잭션을 길게 물고있고 <br>
그 트랜잭션은 상태가 업데이트가 되기전 트랜잭션이고 JPA 1차 캐쉬에 의하여 변경상황을 감지하지 못하고, 계속 같은 값을 리턴하고 있던 것이였다 <br>


### 2.2) 1차 캐시 동작 방식
```java
// EntityManager 내부 구조
class EntityManager {
    private Map<EntityKey, Object> firstLevelCache = new HashMap<>();

    public Payment find(Class<Payment> clazz, String id) {
        EntityKey key = new EntityKey(clazz, id);

        // 1. 캐시 확인: firstLevelCache.get(Key(Payment, "TXN001")) → null
        // 2. DB 쿼리 실행: SELECT * FROM payment WHERE transaction_no = 'TXN001'
        // 3. 캐시 저장: firstLevelCache.put(Key(Payment, "TXN001"), payment1)
        // 4. 반환: payment1
        if (firstLevelCache.containsKey(key)) {
            return (Payment) firstLevelCache.get(key); // DB 조회 없이 반환
        }

        // 2. 캐시에 없으면 DB 조회
        Payment payment = selectFromDatabase(id);

        // 두 번째 조회 (같은 트랜잭션 내)
        // 1. 캐시 확인: firstLevelCache.get(Key(Payment, "TXN001")) → payment1 발견!
        // 2. DB 쿼리 없이 캐시에서 반환
        // 3. payment1 == payment2 (동일 인스턴스)

        // 3. 캐시에 저장
        firstLevelCache.put(key, payment);

        return payment;
    }
}
```

결과적으로 캐싱된 데이터를 return 을 하니 다른 트랜잭션에 의해 변경된 상태를 보지 못하고 있는 점이다 <br>

JPA 1차 캐시뿐만 아니라, MySQL의 기본 격리 수준인 REPEATABLE READ도 문제에 기여했다 <br>

#### 트랜잭션 격리 수준 (MySQL REPEATABLE READ)
```text
트랜잭션 A (T0 시작):
- SELECT * FROM payment WHERE id = 123;  (T1)
  → status = 'PENDING' (스냅샷 생성)

트랜잭션 B (T2 시작):
- UPDATE payment SET status = 'APPROVED' WHERE id = 123;
- COMMIT (T2)

트랜잭션 A 계속:
- SELECT * FROM payment WHERE id = 123;  (T3)
  → status = 'PENDING' (T0 시점 스냅샷 사용)
  → REPEATABLE READ 보장을 위해 트랜잭션 시작 시점 데이터 반환
```

JPA 1차 캐시로 인해 DB 쿼리 자체가 발생하지 않았고, 쿼리가 발생해도 **REPEATABLE READ** 격리 수준에서는 같은 트랜잭션 내에서 같은 행을 여러 번 읽어도 동일한 값을 보장하기 떄문이였다 <br>


### 2.3) 해결 방법
#### 1. @Transactional 제거
간단하게 @Transactional 을 상위 메소드에서 제거하고 하위 메소드에서 사용하는 것이다.
```java
    @Async("abortAsyncExecutor")
    public void processAbort(EndpointInputs.Refund refund) {
        try {
            int maxAttempts = 5; // 최대 5회 재시도
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                // JPA 에 의해 트랜잭션 자체 생성 및 commit
                Payment abortTargetPayment = paymentRepository.findByOrderId(endpointInput.getOrderId())
                    .orElseThrow(() -> new OrderIdNotFoundException(GlobalErrorCode.ORDER_ID_NOT_FOUND));

                if (isAlreadyAbort(abortTargetPayment)) {
                    log.info("이미 취소 처리가 완료된 건 입니다.);
                    return;
                }
                
                if (isReadyAbort(abortTargetPayment)) {
                    log.info("취소 준비 완료 -> 취소 처리 진행");
                    // 하위 메소드에서 트랜잭션 생성 
                    refundService.executeAbortProcess(abortTargetPayment, refund);
                    return;
                }
                
                if (attempt == maxAttempts) {
                    abortTargetPayment.updateErrorMessage("[취소 실패] 승인되지 않은 거래입니다.");
                    paymentRepository.save(abortTargetPayment);
                }
            }
        } catch (AbortException e) {
            log.error("취소 실패 : {} - {}", GlobalErrorCode.SERVER_ERROR, e.getMessage());
        }
    }
```
로직을 바꾸면서 간단하게 문제를 해결할 수 있었고, 필자는 위 방법을 선택하여 해결을 하였다 <br>

```text
[1회차]
- 새 트랜잭션 시작
- SELECT: Payment(state="PROGRESS")
- 트랜잭션 커밋 → 1차 캐시 소멸

[2회차]
- 새 트랜잭션 시작
- SELECT: Payment(state="PROGRESS")
- 트랜잭션 커밋 → 1차 캐시 소멸

[외부 승인 완료: state="APPROVED" 업데이트]

[3회차]
- 새 트랜잭션 시작
- SELECT: Payment(state="APPROVED") **변경 감지**
- isReadyAbort() → true
- executeAbortProcess() 호출 → 취소 성공
```

#### 2. entityManager.refresh()
```java
    @Transactional
    @Async("abortAsyncExecutor")
    public void processAbort(EndpointInputs.Refund refund) {
        try {
            int maxAttempts = 5; // 최대 5회 재시도
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {

                // JPA 에 의해 트랜잭션 자체 생성 및 commit
                Payment abortTargetPayment = paymentRepository.findByOrderId(endpointInput.getOrderId())
                    .orElseThrow(() -> new OrderIdNotFoundException(GlobalErrorCode.ORDER_ID_NOT_FOUND));

                // 강제로 DB에서 최신 데이터 가져오기
                entityManager.refresh(payment);
                
                if (isReadyAbort(abortTargetPayment)) {
                    log.info("취소 준비 완료 -> 취소 처리 진행");
                    // 하위 메소드에서 트랜잭션 생성 
                    refundService.executeAbortProcess(abortTargetPayment, refund);
                    return;
                }
                
                if (attempt == maxAttempts) {
                    abortTargetPayment.updateErrorMessage("[취소 실패] 승인되지 않은 거래입니다.");
                    paymentRepository.save(abortTargetPayment);
                }
            }
        } catch (AbortException e) {
            log.error("취소 실패 : {} - {}", GlobalErrorCode.SERVER_ERROR, e.getMessage());
        }
    }
```


entityManager.refresh()를 사용하면 1차 캐시를 우회하여 DB 쿼리를 강제로 발생시켜도, MySQL의 **REPEATABLE READ** 격리 수준 때문에 여전히 트랜잭션 시작 시점의 스냅샷을 볼 가능성이 있다 <br>


## 3. 결론
트랜잭션 전파 방법 및 JPA 사용 방법에 대하여 더 깊게 생각하게 되는 트러블 슈팅 해결이였다 <br>

> 1.JPA 1차 캐시의 양면성
- 같은 트랜잭션 내에서 동일 엔티티 조회 시 DB 쿼리를 줄여 성능을 향상시킴
- 하지만 외부 트랜잭션의 변경사항을 감지하지 못하는 문제 발생

> 2.트랜잭션 격리 수준의 영향

- REPEATABLE READ는 일관성을 보장하지만, 같은 트랜잭션 내에서 다른 트랜잭션의 커밋을 볼 수 없음

> 3.트랜잭션 경계 설계의 중요성

- 비즈니스 로직의 특성에 맞게 트랜잭션 경계를 신중하게 설정할 필요를 확실히 느낌
- 외부 변경사항을 감지해야 하는 폴링 로직에서는 트랜잭션을 길게 유지하면 안 됨

즉 위 공부한 내용을 기반으로 실무에서 로직을 작성해 나갈 때 한번 더 생각을 하게 되었다 <br>

- 트랜잭션을 상위 메서드에 걸어야 하는 Case
  - 여러 DB 작업이 원자성을 보장해야 할 때
  - 롤백이 함께 이루어져야 하는 작업들

- 트랜잭션을 하위 메서드에 분리해야 하는 Case
  - 외부 시스템의 변경사항을 감지해야 할 때
  - 폴링(Polling) 방식으로 상태 변화를 확인해야 할 때
  - 긴 대기 시간이 포함된 로직 (Thread.sleep 등)



## REF
> https://goddaehee.tistory.com/167 <br>
> https://docs.spring.io/spring-framework/reference/data-access/transaction.html
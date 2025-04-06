# Redis 조회랑 MySQL 조회 시간 직접 알아보자

문득 궁금한게 생겼다. Redis 가 얼마나 빠르길래 사람들이 Redis 를 자주 사용하는걸까? <br>

그래서 직접 RDB 와 InMemory DB 의 퍼포먼스를 비교해보려고 한다 <br>

단순한 궁금증에서 출발한 실험이지만, 실제로도 Redis 캐시를 도입할 가치가 있는지 가늠할 수 있는 지표가 될 수 도 있다. <br>

### 실험 조건
- rdb 는 mysql, inMemory 는 redis 를 사용한다
- Sample 은 40만건 기준이다.
- 비교 정보는 단건 조회 및 전체 조회 이다.
- rdb 및 redis 는 조회할 컬럼에 인덱스가 걸려있다.
- 동시성은 고려하지 않는다.
- Redis 는 Hash 자료구조를 사용한다.
<br>

  
### 단건 조회
```java
// MySQL 단건 조회
public UUID getUuid(Long id) {
	Pos pos = posRepository.findById(id)
		.orElseThrow(() -> new IllegalArgumentException("No such pos: " + id));

	return pos.getUuid();
}

// Redis 단건 조회
public PosTerminal getPos(String posId) {
	Object object = redisTemplate.opsForHash().get("posTerminals", posId);

	if(object==null)
		throw new IllegalArgumentException(posId + " 는 유효하지 않습니다.");

	return objectMapper.convertValue(object, PosTerminal.class);
}

```

### 단건 조회 결과
위 로직을 실행하는 테스트 코드는 생략하였고 api 로 만들어서 호출해본 결과는 아래와 같다 <br>
```java
// MySQL
2025-04-06T16:25:50.357+09:00  INFO 70916 --- [nio-9400-exec-1] o.h.r.rdb.ui.PosTerminalController       : 총 조회 걸린 시간: 101ms

// Redis
2025-04-06T16:26:02.842+09:00  INFO 81562 --- [nio-9400-exec-1] o.h.redislab.nosql.ui.PosController      : 총 조회 걸린 시간 : 25ms
```

사람이 체감하기엔 큰 차이는 없지만, 내부적으로는 약 4배 정도 빠른 성능을 보였다. <br>

<br>

### 전체 조회
```java
// MySQL 전체 조회
public int getAllPos() {
	List<Pos> all = posRepository.findAll();
	return all.size();
}

// Redis 전체 조회
public int getAllPos() {
	Map<Object, Object> entries = redisTemplate.opsForHash().entries("posTerminals");

	return entries.size();
}

```

### 전체 조회 결과
```java
// MySQL
2025-04-06T16:26:07.142+09:00  INFO 70916 --- [nio-9400-exec-2] o.h.r.rdb.ui.PosTerminalController       : 총 조회 걸린 시간: 1947ms

// Redis
2025-04-06T16:26:51.406+09:00  INFO 81562 --- [nio-9400-exec-3] o.h.redislab.nosql.ui.PosController      : 총 조회 걸린 시간 : 1306ms
```

<br>

전체 조회도 Redis가 더 빠르긴 했지만, 드라마틱한 차이는 없었다. <br>
Redis 자료구조에서 Hash 방식을 선택했기에 Hash 전체를 한 번에 와야하므로 Redis 측도 내부적으로 처리 비용이 발생하기 때문이라고 생각한다 <br>


### 전체 결과
| Sample  | **content** | **rdb 조회 시간** | **redis 조회 시간** | **개선 폭**        |
|---------|-------|---------------|-----------------|-----------------|
| 400,000 | 단건 조회 | 101ms         | 25ms            | 4배 이상 빠른 성능     |
| 400,000 | 전체 조회 | 1947ms        | 1306ms          | 대략 30% 이상 빠른 성능 |


<br>

### 요약 
이번 실험은 단순한 호기심에서 출발한 테스트였지만, 실제 Redis 도입 시 얻을 수 있는 성능 개선 폭을 가늠해볼 수 있었다. <br>
다만 이 실험 결과는 절대적인 기준이 아니라 참고용으로 받아들여야 한다. <br>

Redis가 항상 빠르다는 보장은 없고, 어떤 구조로 데이터를 넣고, 어떤 방식으로 조회하는지에 따라 결과가 달라진다. <br>
추가적으로 각자의 상황 및 비즈니스 로직에 따라 언제든 다른 결과가 나올 수 있다 <br> 

현재 실무에서 In-Memory DB에 대한 도입을 두고 고민 중이라면 <br>
실제 운영 환경과 서비스 요구사항을 기준으로 poc 과정을 거친 후 도입할 필요가 있다 <br>

무조건적으로 좋은 기술은 없다. 본인 상황에 맞춰서 최적을 기술을 찾아서 사용하는게 실력 좋은 개발자라고 생각한다 <br><br>

Git: https://github.com/Hyeonqz/Hyeonq-Lab/tree/master/redis-lab/src/main/java/org/hyeonqz/redislab
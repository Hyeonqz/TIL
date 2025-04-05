# Redis 조회랑 MySQL 조회 시간 직접 알아보자

문득 궁금한게 생겼다. Redis 가 얼마나 빠르길래 사람들이 Redis 를 자주 사용하는걸까 <br>
궁금했기 직접 차이를 봐보려고 한다 <br>


### 조건
- Sample 은 30만건 기준이다.
- 조회할 컬럼은 인덱스가 걸려있다.
- 동시성은 고려하지 않는다.

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
	return posTerminalRepository.findByPosId(posId)
		.orElseThrow(() -> new IllegalArgumentException(posId + "는 유효하지 않습니다."));
}
```




### 전체 조회
```java
// MySQL 전체 조회
public int getAllPos() {
	List<Pos> all = posRepository.findAll();
	return all.size();
}

// Redis 전체 조회
public int getAllPosSize() {
	Iterable<PosTerminal> all = posTerminalRepository.findAll();

	List<PosTerminal> list = StreamSupport.stream(all.spliterator(), false)
		.toList();

	return list.size();
}
```


위 로직을 실행하는 테스트 코드는 생략하였고 api 로 만들어서 호출해본 결과는 아래와 같다 <br>

### 단건 조회 결과


### 다건 조회 결과





<br>

as ~ is - to ~ be 구조로 보여주자.





실제로 RDB와 In-Memory DB에 대한 도입을 두고 고민 중이라면 <br>
실제 운영 환경과 서비스 요구사항을 기준으로 poc 과정을 거친 후 도입할 필요가 있다 <br>

무조건적으로 좋은 기술은 없다. 본인 상황에 맞춰서 최적을 기술을 잘하는게 실력 좋은 개발자라고 생각한다 <br>
# Docker 를 활용한 kafka 환경 구성하기.

## 목차
1. 멀티 브로커 구축
2. Kafka-UI 구축
3. Kafka 4.0과 KRaft 전환
4. 참고 자료


## multi broker 구축
기본적으로 카프카 클러스터를 구축할 떄는 1개의 zookeeper 및 3개의 broker 를 사용 한다 <br>
그 이유는 안정적인 서비스 제공에 있다 <br>

Zookeeper 는 Kafka-broker 메타데이터를 관리한다 -> 브로커 상태, 토픽 정보, 브로커 리더 선출 등등.. <br>

3개의 브로커가 있다고 가정하였을 때 기본적으로 브로커 3개중 1개는 리더, 2개는 팔로어로 구성을 한다 <br>
한대의 브로커에 장애가 발생하더라도 다른 브로커로 옮겨가며 서비스를 제공할 수 있다 <br>

메시지 복제는 broker 간의 복제 메커니즘인 replication.factor 를 통해 이루어진다 <br>

아래는 kafka 구성을 위한 docker compose 파일 이다 <br>
참고로 필자는 Spring 환경에서 kafka3.6 버전을 사용중에 있다 <br>
```yml
version: '3.8'
services:
  zookeeper-1:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_SERVER_ID: 1
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
      ZOOKEEPER_INIT_LIMIT: 5
      ZOOKEEPER_SYNC_LIMIT: 2
    container_name: zookeeper-1
    restart: on-failure
    networks:
      - kafka_network
    ports:
      - "22181:2181"
    volumes:
      - ./zookeeper-1/data:/data
      - ./zookeeper-1/logs:/datalog

  kafka-1:
    image: confluentinc/cp-kafka:latest
    container_name: kafka-1
    hostname: kafka-1
    restart: on-failure
    networks:
      - kafka_network
    ports:
      - "9092:9092"
      - "29092:29092"
    depends_on:
      - zookeeper-1
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_DEFAULT_REPLICATION_FACTOR: 3
      KAFKA_NUM_PARTITIONS: 3
      KAFKA_ADVERTISED_HOST_NAME: 127.0.0.1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_INTER_BROKER_LISTENER_NAME: "INTERNAL"
      KAFKA_LISTENERS: INTERNAL://:19092,EXTERNAL://:9092,DOCKER://:29092
      KAFKA_ADVERTISED_LISTENERS: "INTERNAL://kafka-1:19092,EXTERNAL://127.0.0.1:9092,DOCKER://host.docker.internal:29092"
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: "INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT,DOCKER:PLAINTEXT"
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 3000
      KAFKA_GROUP_MIN_SESSION_TIMEOUT_MS: 6000
      KAFKA_GROUP_MAX_SESSION_TIMEOUT_MS: 60000
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 3
      CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1
      CONFLUENT_METRICS_ENABLE: 'false'
    volumes:
      - ./kafka-1/data:/var/lib/kafka/data

  kafka-2:
    image: confluentinc/cp-kafka:latest
    container_name: kafka-2
    hostname: kafka-2
    restart: on-failure
    networks:
      - kafka_network
    ports:
      - "9093:9093"
      - "29093:29093"
    depends_on:
      - zookeeper-1
    environment:
      KAFKA_BROKER_ID: 2
      KAFKA_DEFAULT_REPLICATION_FACTOR: 3
      KAFKA_NUM_PARTITIONS: 3
      KAFKA_ADVERTISED_HOST_NAME: 127.0.0.1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_INTER_BROKER_LISTENER_NAME: "INTERNAL"
      KAFKA_LISTENERS: INTERNAL://:19093,EXTERNAL://:9093,DOCKER://:29093
      KAFKA_ADVERTISED_LISTENERS: "INTERNAL://kafka-2:19093,EXTERNAL://127.0.0.1:9093,DOCKER://host.docker.internal:29093"
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: "INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT,DOCKER:PLAINTEXT"
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 3000
      KAFKA_GROUP_MIN_SESSION_TIMEOUT_MS: 6000
      KAFKA_GROUP_MAX_SESSION_TIMEOUT_MS: 60000
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 3
      CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1
      CONFLUENT_METRICS_ENABLE: 'false'
    volumes:
      - ./kafka-2/data:/var/lib/kafka/data

  kafka-3:
    image: confluentinc/cp-kafka:latest
    container_name: kafka-3
    hostname: kafka-3
    restart: on-failure
    networks:
      - kafka_network
    ports:
      - "9094:9094"
      - "29094:29094"
    depends_on:
      - zookeeper-1
    environment:
      KAFKA_BROKER_ID: 3
      KAFKA_DEFAULT_REPLICATION_FACTOR: 3
      KAFKA_NUM_PARTITIONS: 3
      KAFKA_ADVERTISED_HOST_NAME: 127.0.0.1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_INTER_BROKER_LISTENER_NAME: "INTERNAL"
      KAFKA_LISTENERS: INTERNAL://:19094,EXTERNAL://:9094,DOCKER://:29094
      KAFKA_ADVERTISED_LISTENERS: "INTERNAL://kafka-3:19094,EXTERNAL://127.0.0.1:9094,DOCKER://host.docker.internal:29094"
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: "INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT,DOCKER:PLAINTEXT"
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 3000
      KAFKA_GROUP_MIN_SESSION_TIMEOUT_MS: 6000
      KAFKA_GROUP_MAX_SESSION_TIMEOUT_MS: 60000
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 3
      CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1
      CONFLUENT_METRICS_ENABLE: 'false'
    volumes:
      - ./kafka-3/data:/var/lib/kafka/data

networks:
  kafka_network:
    name: kafka_docker_net
```

위 설정은 kafka-broker 3개와 zookeeper 1개로 구성된 kafka cluster 이다 <br>

위 compose 파일에서 kafka 관련 구성 설명은 아래와 같다 <br>
#### Zookeeper
- ZOOKEEPER_SERVER_ID: Zookeeper 클러스터에서 이 서버의 고유 ID 값 설정 -> 클러스터가 1개이기 때문에 1 로 설정
- ZOOKEEPER_CLIENT_PORT: Zookeeper 클라이언트가 외부에서 접속할 포트 지정
- ZOOKEEPER_TICK_TIME: Zookeeper 기본 단위로, 하트비트와 타임아웃 계산에 사용 됨
- ZOOKEEPER_INIT_LIMIT: Zookeeper 클러스터 초기화 시 리더와 팔로워 간 동기화 기다리는 시간
- ZOOKEEPER_SYNC_LIMIT: 브로커 리더와 팔로워 간 동기화 제한 시간

#### Kafka
- KAFKA_BROKER_ID: 브로커의 고유 ID -> 브로커마다 다른 값 지정
- KAFKA_DEFAULT_REPLICATION_FACTOR: 토픽의 기본 복제본 수
- KAFKA_NUM_PARTITIONS: 토픽의 기본 파티션 수
- KAFKA_ADVERTISED_HOST_NAME: kafka 가 떠있는 ip 를 적는다
- KAFKA_ZOOKEEPER_CONNECT: 클러스터 연결을 위한 zookeeper 정보를 적는다
- KAFKA_LISTENERS: Kafka 브로커가 메시지 수신 대기할 리스너 설정.
  - INTERNAL://:19092: 브로커 간 통신용 내부 리스너.
  - EXTERNAL://:9092: 외부 클라이언트용 리스너.
  - DOCKER://:29092: Docker 환경에서의 접근용 리스너.
- KAFKA_ADVERTISED_LISTENERS: listener 설정
  - INTERNAL://kafka-1:19092: 내부 리스너.
  - EXTERNAL://127.0.0.1:9092: 외부 리스너.
  - DOCKER://host.docker.internal:29092: Docker 호스트 접근용.
- KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 3000: 소비자 그룹 리밸런싱 초기 지연 시간(밀리초).
- KAFKA_GROUP_MIN_SESSION_TIMEOUT_MS: 6000: 소비자 그룹 세션 타임아웃 최소값.
- KAFKA_GROUP_MAX_SESSION_TIMEOUT_MS: 60000: 소비자 그룹 세션 타임아웃 최대값.
- KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3: 오프셋 토픽의 복제본 수.
- KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 3: 트랜잭션 로그 복제본 수.
- CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1: Confluent 메트릭 리포터 토픽 복제본 수.
- CONFLUENT_METRICS_ENABLE: 'false': Confluent 메트릭 비활성화.



## kafka-ui 구축
많은 Kafka 어드민 및 브로커 관리 UI tool 이 있지만, 나는 UI for Apache Kafka 툴이 제일 무난하게 좋았다 <br>
다양한 kafka-ui 는 <a href="https://towardsdatascience.com/overview-of-ui-tools-for-monitoring-and-management-of-apache-kafka-clusters-8c383f897e80/">링크</a> 에서 알 수 있다 <br>

kafka-ui 구성 docker-compose 는 아래와 같다
```yml
version: '3.8'
services:
  kafka-ui:
    image: provectuslabs/kafka-ui
    container_name: kafka-ui
    ports:
      - "8989:8080"
    restart: always
    environment:
      - KAFKA_CLUSTERS_0_NAME=local
      - KAFKA_CLUSTERS_0_BOOTSTRAPSERVERS=kafka-1:19092,kafka-2:19093,kafka-3:19094
      - KAFKA_CLUSTERS_0_ZOOKEEPER=zookeeper:2181
    networks:
      - kafka_docker_net
networks:
  kafka_docker_net:
    external: true

```

networks 설정에서 docker-compose-kafka.yml 에서 마지막에 설정한 kafka_network 이름을 적용해야지 kafka-ui 에서 카프카 브로커 및 토픽들이 나온다 <br>

위 설정을 적용하기 위해서 docker compose 를 실행한다
> docker-compose -f docker-compose-kafka-ui.yml up -d

참고로 kafka4.0 이후부터는 클러스터 관리하는 Zookeeper 가 deprecated 가 되었다<br>
kafka3.9 까지만 apache zookeeper 모드를 지원한다 <br>

물론 완전히 제거되지는 않았지만, 점차 사라질 예정인 것으로 판단된다 <br>

kafka4.0 부터는 KRaft 만 지원을 한다(zookeeper 도 지원하기는 한다..) <br>
이제는 kafka-broker 메타데이터 관리를 위해 이제 zookeeper 대신 Kraft 를 사용해야 한다 <br>

이제는 4.0부터는 KRaft 가 기본모드로 권장되므로 다시 익숙해져야 한다..이제 zookeeper 를 이해했지만 새로운 KRaft 를 공부해야 한다.... <br>

필자의 경우 아직 kafka3.6 버전을 사용중에 있으며 브로커 관리를 위해 zookeeper 를 사용중이다 <br>
추후 마이그레이션이 필요하다면 아래와 같은 루트로 마이그레이션을 해야할 것 같다 <br>
- Kafka 4.0 이상으로 업그레이드
- ZooKeeper에서 KRaft로 마이그레이션


다음에 시간이 될 때 kafka zookeeper 를 걷어내고 kraft 를 적용한 방법 및 후기에 대하여 글을 작성해봐야 겠다
<br>

### REF
1. https://devocean.sk.com/blog/techBoardDetail.do?ID=163980&boardType=techBlog&searchData=&searchDataMain=&page=&subIndex=&searchText=kafka+ui&techType=&searchDataSub=&comment=
2. https://www.confluent.io/blog/introducing-apache-kafka-3-9/
3. https://docs.confluent.io/platform/current/installation/docker/config-reference.html#zookeeper-configuration
4. https://docs.confluent.io/platform/current/installation/docker/config-reference.html#kafka-broker-configuration


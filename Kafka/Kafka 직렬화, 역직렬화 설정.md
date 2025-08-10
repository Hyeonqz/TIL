# Kafka 직렬화, 역직렬화 설정 알아보기.

## 1. 개요
#### 왜 Kafka 사용시 직렬화, 역직렬화가 필요할까? <br>
spring 에서는 쉽게 카프카를 사용하기 위해서 kafka 인터페이스를 제공한다 <br>
그리고 우리는 위 kafka 를 사용하기 위해서 producer, consumer 설정을 잡아줘야 한다 <br>

위 설정에서 가장 중요한 message 발행 및 소비시에 필요한 key,value 설정에 대하여 알아보려고 한다 <br>

일반적인 카프카 사용시 데이터 흐름을 서버 아키텍쳐로 보면 아래와 같다(간단하게만 그려본 것이다)
```text
User --> LB --> WAS --> Kafka --> Consumer
```

User 가 데이터를 보낼 때 보통은 Http Body 에 값을 넣어서 보내고는 한다 <br>
kafka 가 아니더라도 일반적인 Http 프로토콜을 사용하여 통신을 할 때 또한 Http Body 에 값을 넣고 통신을 한다 <br>

위 통신하는 과정에서 위 Http Body(Json) 을 통째로 서버에 보낼수가 없다 <br>
#### 왜 Http Body(Json)를 통째로 보낼 수 없을까?
HTTP Body에 담긴 JSON은 문자열 형태지만, 네트워크 전송 시 바이트 단위로 처리된다. <br>
JSON 객체 자체는 메모리 내에 존재하므로, 이를 그대로 전송하면 시스템 간 호환성 문제나 데이터 손실 위험이 있다.
- **호환성**: 서로 다른 언어나 플랫폼(JAVA, Python 등)은 객체 구조를 다르게 해석할 수 있습니다.
- **효율성**: JSON 문자열은 크기가 클 수 있어, 직렬화로 바이트 스트림으로 변환하면 전송 효율이 높아집니다.
- **무결성**: 직렬화는 데이터 구조를 명확히 정의해 손실 없이 전송하고, 역직렬화로 원래 형태를 복원합니다.
  
따라서 Kafka나 HTTP 통신에서는 직렬화를 통해 데이터를 바이트로 변환해 전송해야 한다 <br>

그리고 위 객체(Json)를 byte 형태로 변형하여 데이터 전송을 진행 하는 과정을 **'직렬화(Serialization)'** 라고 한다. <br>
반대로 객체를 받는쪽 입장에서 byte 로 된 데이터를 알아 볼수 있게 다시 만드는 과정을 '**역직렬화(Deserialization)**' 라고 한다 <br>

Kafka 도 똑같다 <br>
메시지를 발행하여 kafka broker 에 있는 topic 에 메시지를 보낸다 -> **직렬화 필요** <br>
topic 메시지 를 소비하기 위해 가져온다 -> **역직렬화 필요** <br>

kafka 뿐만 아니라 대부분 server - server 가 데이터를 주고 받을 때는 직렬화 & 역직렬화가 필요하다 <br>


## 2. 본론
기본적으로 producer 할 때와, consumer 할 때 직렬화, 역직렬화 방식을 같게 하는게 좋다 <br>
아래 코드를 보자
```java
// producer
@Configuration
public class KafkaProducerConfig {
    private String bootstrapAddress = "127.0.0.1:11000";

    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        // key -> String
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // value -> String
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }
}

```

위 설정을 보면 메시지 전송시 **key 는 String, value 또한 String** 으로 되어있다 <br>

아래는 이제 consumer 설정이다
```java
@Configuration
public class KafkaConsumerConfig {
    
    private String bootstrapAddress = "127.0.0.1:11000";

    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        
        // key -> String
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // value -> json
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        return new DefaultKafkaConsumerFactory<>(props);
    }
}
```

위 설정을 보면 **key 는 String, value 는 Json** 이다 <br>


현재 설정을 보면 producer 할 때 value 가 String 이고 consumer 할 때 value 는 json 이다 <br>

위 설정으로 kafka 를 사용하면 producer 는 잘되지만, consumer 시 에러가 발생한다 <br>

> 왜 에러가 발생할까?

Kafka Consumer가 메시지를 역직렬화할 때, Producer가 사용한 직렬화 방식과 정확히 동일한 방식으로 역직렬화를 시도하기 때문이다 <br>
- Producer는 메시지를 문자열 그대로 바이트 배열로 변환하여 Kafka에 보낸다
- Consumer는 받은 바이트 배열이 JSON 형식의 문자열일 것이라고 가정하고 JSON 객체로 변환을 시도합니다.


그러므로 위 설정은 잘못된 설정이다. 정상적인 통신을 위해서는 Producer와 Consumer의 직렬화/역직렬화 방식이 일치해야 한다 <br>

아래 설정은 일반적으로 많이 사용하는 case 의 config 이다 <br>

### Good Case
#### 1. <String,String>
```java
@Configuration
public class KafkaProducerConfig {
    private String bootstrapAddress = "127.0.0.1:11000";

    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        
        return new DefaultKafkaProducerFactory<>(props);
    }
}

@Configuration
public class KafkaConsumerConfig {

    private String bootstrapAddress = "127.0.0.1:11000";

    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }
}
```


#### 2. <String, Object>
```java
@Configuration
public class KafkaProducerConfig {
    private String bootstrapAddress = "127.0.0.1:11000";

    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        return new DefaultKafkaProducerFactory<>(props);
    }
}

@Configuration
public class KafkaConsumerConfig {

    private String bootstrapAddress = "127.0.0.1:11000";

    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }
}
```


추가적인 설정으로 consumer 에서 역직렬화 중 에러가 발생하면 메세지를 처리하지 못하고 consumer 가 중단될 수 있다 <br>
이를 방지하기 위해서 `ErrorHandlingDeserializer` 를 사용해 에러를 감지하고 실패한 메시지를 DLT 로 보낼 수 있다 <br>

이 내용은 권장사항 이므로 참고 하길 바란다
```java
@Configuration
public class KafkaConsumerConfig {

    private String bootstrapAddress = "127.0.0.1:11000";

    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        
        return new DefaultKafkaConsumerFactory<>(props);
    }

    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            KafkaTemplate<String, Object> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(2); // server spec 보고 설정하기
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.getContainerProperties().setShutdownTimeout(10000L);
        factory.setCommonErrorHandler(new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaTemplate),
                new FixedBackOff(1000L, 3L)));
        return factory;
    }
}
```

참고로 위 설정시 key,value 설정을 위가 아닌 아래 케이스 처럼 진행하면 Exception 이 일어나니 위 설정을 사용하길 바란다 <br>
```java
props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);

```
```java
// Exception
java.lang.IllegalStateException: No type information in headers and no default type provided
org.apache.kafka.common.errors.RecordDeserializationException: Error deserializing key/value for partition ...
```

원인은 `ErrorHandlingDeserializer` 가 실제 Deserializer를 래핑해야 하는데, 순서가 뒤바뀌었기 때문이다 <br>
`ErrorHandlingDeserializer` 에러를 처리하지만, 실제 역직렬화는 내부 Deserializer에 위임하므로 <br>
ConsumerConfig에서 `KEY_DESERIALIZER_CLASS_CONFIG` 와 `VALUE_DESERIALIZER_CLASS_CONFIG` 를 `ErrorHandlingDeserializer`로 먼저 설정해야 한다 <br>



## 3. 결론
Kafka에서 직렬화와 역직렬화는 서버 간 데이터 통신의 핵심이다. <br>
Producer와 Consumer의 설정이 일치하지 않으면 에러가 발생하므로, 항상 동일한 Serializer/Deserializer를 사용해야 한다 <br>
또한 데이터 무결성이 중요하므로 `ErrorHandlingDeserializer`와 DLT를 활용해 안정성을 높이는걸 추천한다 <br>

**실무 팁**:
- **단위 테스트**: Producer와 Consumer를 별도로 테스트해 직렬화/역직렬화 호환성을 체크
- **모니터링**: DLT에 쌓이는 메시지를 모니터링해 에러 패턴 분석


올바른 설정은 기술 부채를 줄이므로 초반에 잡아두는게 좋다!

### 4. References
- [Spring Kafka 공식 문서](https://docs.spring.io/spring-kafka/reference/kafka/serdes.html)
- [Apache Kafka 공식 문서](https://kafka.apache.org/documentation/)
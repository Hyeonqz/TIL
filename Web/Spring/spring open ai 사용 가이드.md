# Spring AI(open ai) 사용 가이드

#### 요약
> SpringBoot 에서 Open AI 를 연결하여 채팅을 진행한다.


### 1. 개요
처음 내가 ChatGPT 를 접한건 23년도 9월? 그쯤이였다 <br>
그 당시 ChatGPT 는 혁명이긴 하였지만, 뭔가 개발할 때 사용하기에는 아직 어설프고 많이 부족해보였다 <br>
참고 정도는 했지만 신뢰도가 많이 떨어졌었다 <br>

하지만 현재는 많이 발전하여 생성형 AI 에 대한 신뢰도가 많이 높아졌고, 개발자들 또한 적극적으로 AI 를 활용한다. <br>
현재는 AI 를 적극적으로 활용하고 잘 사용하는 개발자들이 미래에 살아남을 것이라고 생각한다. <br>

Spring AI 가 나온지도 거의 1년이 되었다 <br>
이번 기회에 Spring Ai 를 좀 사용해보고 실무에 도움이 된다면 더 공부해서 도입을 해보려고 한다 <br>

### 2. Spring AI?
기존에는 생성형 AI 를 스프링에서 사용하기 위해서는 RestTemplate, RestClient 등 http 연결을 지정해야 했다 <br>
하지만 Spring AI 를 사용하면, 스프링이 추상화 해둔 컴포넌트를 사용하여 쉽게 요청,응답을 받을 수 있다 <br>

참고로 현재 스프링 AI 는 따끈따근 1.0.0 버전이 배포되어 있다. <br>

### 3. setting
> 필자는 SpringBoot3.4 , JDK21, Gradle 을 사용함

#### build.gradle
```gradle
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.4.3'
	id 'io.spring.dependency-management' version '1.1.7'
}

group = 'org.hyeonqz'
version = '1.0.0'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
	maven { url 'https://repo.spring.io/milestone' }
	maven { url 'https://repo.spring.io/snapshot' }
}

dependencies {
	// Spring 기본
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'

	// Lombok
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'

	// DB
	runtimeOnly 'com.mysql:mysql-connector-j'

	// Spring AI 
	implementation platform("org.springframework.ai:spring-ai-bom:1.0.0-SNAPSHOT")
	implementation 'org.springframework.ai:spring-ai-openai' // spring ai 추상화 컴포넌트 사용
	implementation 'org.springframework.ai:spring-ai-starter-model-openai' // 실제 open ai 연동

	// Test
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}

tasks.named('test') {
	useJUnitPlatform()
}

```

#### application.yml
```yaml
spring:
  ai:
    openai:
      api-key: asdlkgdgsaljke7809
      base-url: https://api.openai.com/v1
      chat:
        options:
          model: gpt-3.5-turbo # gpt 유료를 사용중이라면 gpt-4
```


### 4. 채팅 AI
LLM 을 호출하는 데 사용되는 컴포넌트는 ChatClient, ChatModel 2가지가 있다 <br> 

둘다 같은 역할을 하지만, 세부적으로 다른 것이 있다 <br>
범용적으로는 아직 ChatClient 가 많이 사용되는 것으로 보이지만, ChatModel 이 조금 더 빠르게 사용할 수 있다 <br>

ChatClient 는 어떤 Client 를 사용할지에 대한 것을 Bean 으로 등록해줘야 한다 <br>
ChatModel 은 Bean 에 따로 등록하지 않고 바로 사용이 가능하다 <br>

```java
@Configuration
public class ChatClientConfig {

	@Bean
	public ChatClient chatClient(OpenAiChatModel openAiChatModel) {
		return ChatClient.builder(openAiChatModel).build();
	}

}


@RestController
public class ChatOpenAIController {
	private final ChatClient chatClient;
	private final ChatModel chatModel;

	public ChatOpenAIController (ChatClient chatClient, OpenAiChatModel chatModel) {
		this.chatClient = chatClient;
		this.chatModel = chatModel;
	}

	@PostMapping("/api//call")
	public String call() {
		return chatModel.call("경기도 수원 날씨 알려줘");
	}

	@PostMapping("/api/v2/call")
	public String callV2 () {
		return chatClient.prompt()
			.user("경기도 수원 날씨 알려줘")
			.call()
			.content();
	}

}
```

> POST 127.0.0.1:9300/api/v1/call



<br>

> POST 127.0.0.1:9300/api/v2/call











### REF
```txt
1. https://devocean.sk.com/blog/techBoardDetail.do?ID=166152
2. https://spring.io/projects/spring-ai#overview
3. https://docs.spring.io/spring-ai/reference/
```
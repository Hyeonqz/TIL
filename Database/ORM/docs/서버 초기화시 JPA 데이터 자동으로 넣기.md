# 서버 초기화시 JPA 데이터 자동으로 넣기
 <br>
Springboot, Jpa 환경에서 서버가 재시작 즉 초기화시 테이블에 데이터를 자동으로 넣는 방법을 알아보자<br>

[application.yml]
```java
spring:
  config:
    activate:
      on-profile: local

  jpa:
    show-sql: true
    generate-ddl: true
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        format_sql: true
```

보통 local 환경에서 jpa 설정 중에 ddl-auto 를 create 로 설정을 하고 개발을 해왔다 <br>
그래서 서버를 재시작 할 때 마다 테이블을 삭제하고 재생성을 하였다 <br>

이 과정에서 조회 테스트를 진행하기 위해선 테이블 내에 데이터가 있어야 했는데, 매번 insert 를 하기 귀찮아서 구글링을 해보니 아래와 같은 방법이 있었다 <br>

> defer-datasource-initialization: true 

이 옵션을 추가해주고 resources/data.sql 파일을 만들어두면 서버가 재시작 할 때 마다 Spring 이 data.sql 내부를 듸져 안에 쿼리들을 자동으로 실행시켜준다. <br>
위 기능은 boot 2.5 부터 실행이 되었다.<br>

[data.sql]
```java
insert into product(product_number, type, selling_type, name, price)
values ('001', 'HANDMADE', 'SELLING', '아메리카노', 4000),
       ('002', 'HANDMADE', 'HOLD', '카페라떼', 5000),
       ('003', 'BAKERY', 'STOP_SELLING', '크룽지', 4500);
```

위 같은 insert 쿼리를 미리 만들어두면, 서버가 재시작 될 때 product 테이블에 자동으로 3개의 데이터를 insert 가 되므로 테스트시 용이하다 <br>

[전체 코드]
```java
spring:
  config:
    activate:
      on-profile: local

  jpa:
    show-sql: true
    generate-ddl: true
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
        format_sql: true
    defer-datasource-initialization: true
```
# DDL, DML, DCL 및 @AutoCommit

로컬에서 테이블 데이터를 비우기 위해서 **truncate** 를 계속 사용하고 있다가 테스트 DB 에서 테스트를 한 후
'truncate' 로 테이블 데이터를 지우는 것을 사수님이 보고 깜짝 놀라셨다 <br>

잠깐 당황했지만, 이유를 여쭤보았다 <br>
위 명령어는 최대한 지양 하라고 하고, 이유는 꼭 찾아보라고 말씀하셔서 제대로 공부를 해본 내용을 정리해보겠다 <br>

> **RDBMS** 는 **MySQL8.0** 을 기준으로 작성하였습니다 

<br>

## DDL (데이터 정의어)
보통 DBA 나 DB 를 관리하는 사람들이 자주 사용을 한다 <br>
작업단위는 '테이블(=객체)' 단위로 변화가 일어난다 <br>
exO schema, table, view, index <br>

#### 1) CREATE (테이블 생성, 스키마 생성)
```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);
```

#### 2) ALTER (테이블 정보 수정)
```sql
ALTER TABLE users ADD email VARCHAR(100);
```

#### 3) DROP (테이블 완전 삭제)
```sql
DROP TABLE users;
```

#### 4) TRUNCATE (테이블 데이터 초기화)
```sql
TRUNCATE TABLE users;
```

> ⭐️ 중요한건 위 DDL 들은 AutoCommit 이 내포되어 있어, 실행하면 바로 DB에 적용이 된다 ⭐️
> > MySQL Autocommit 과 관계없이 위 설정이 해제되어 있어도 자동으로 커밋이 된다.

즉 Rollback 이 되지않아 신중하게 사용해야 한다는 뜻이다. 특히 '**DROP**, **TRUNCATE**'

<br>

## DML (데이터 조작어)
DML 은 **'CRUD'** 작업을 하기위한 대표적인 쿼리이다 <br>
보통 서버 개발자분들이 위 쿼리를 셀수 없이 사용하고 있을 것이라고 생각한다^~^ <br>

위 명령어들을 작업단위는 '**row**' 행 별로 일어나게 된다 <br>
테이블 단위로 변화가 일어나는 DDL 과 차이가 있다 <br>

#### 1) INSERT (행 삽입)
```sql
INSERT INTO users (name, email) VALUES ('jin', 'jin@google.com');
```

#### 2) UPDATE (행 수정)
```sql
UPDATE users SET name = 'hkjin' WHERE id = 1;
```

#### 3) DELETE (행 삭제)
```sql
DELETE FROM users WHERE id = 1;
```

> AutoCommit 이 되지 않아, rollback 이 가능하다.

하지만 보통 스프링부트에 JPA 를 사용하면 트랜잭션 관리가 가능하기 떄문에 트랜잭션이 성공적으로 일어나면 <br>
commit 을 하는 로직이 포함되어 있어 자동으로 commit 이 되는 편이다 <br>

<br>

## DCL (데이터 정의어)
위 쿼리 또한 보통 DBA 나 DB 를 관리하는 사람들이 자주 사용을 한다 <br>

#### 1) grant (권한부여)
```sql
GRANT insert ON payment TO 'user1'@'localhost';
```

user1@localhost 에게 payment 테이블에 insert 권한을 준다는 뜻이다 <br>

#### 2) revoke (권한회수)
```sql
REVOKE insert ON payment FROM 'user1'@'localhost';
```

반대로 user1@localhost 가 payment 테이블에 가지고 있는 insert 권한을 회수한다는 뜻이다 <br>

만약 다른 권한을 부여하고 싶다면 grant(revoke) [ ] on 이 중간에 명령어를 바꿔주면 된다 <br>

위 쿼리들은 보다시피 관리자가 DB 에 접속할 수 있는 계정마다 권한을 부여하고 회수할 때 사용한다 <br>
보통 관리자는 백엔드 개발자한테는 DML 만 사용할 수있는 권한을 부여하고, DDL,DCL 권한은 부여하지 않는다 <br>
리눅스 서버에서 sudo 권한을 주지 않는 것이랑 비슷한 느낌이다 <br>

> ⭐️ 중요한건 AutoCommit 이 내포되어 있어, 실행하면 바로 DB에 적용이 된다 ⭐️
> > MySQL Autocommit 과 관계없이 위 설정이 해제되어 있어도 자동으로 커밋이 된다.

rollback 이 되지않아 신중하게 사용할 필요가 있는 쿼리들이라고 생각한다

<br>

**[DDL,DCL 과 트랜잭션 autocommit 의 관계]**
[DCL 기준 예시]
```sql
SET autocommit = 0;  -- 트랜잭션 자동 커밋 해제
START TRANSACTION;  -- 트랜잭션 시작
GRANT SELECT ON mysql TO 'user2'@'localhost';
ROLLBACK; -- 권한 변경은 롤백되지 않음
```

위 쿼리를 한번 직접 날려보시면 알 수 있다. <br>
Autocommit 이 해제되어 있어도 'Grant' 는 위와 무관하게 commit 이 되어있음을 알 수 있다 <br>

#### 🖐🏻왜 DCL 명령어는 autocommit 과 무관할까? 
DML, DCL 의 특성 상 보통 관리자가 작업을 할 때 사용한다 <br>
즉 위 명령어들은 User 에게 직접적인 영향을 미치므로 트랜잭션의 롤백 대상이 되지 않도록 설계되어 있다 <br>
즉 DB 상태에 직접적인 영향을 끼치므로, 명령어 실행 후 즉시 commit 되게 설계가 되어있다

<br>

## TCL (트랜잭션 제어언어)
#### 1) commit
- 트랜잭션의 결과를 영구적으로 DB 에 반영한다.

#### 2) rollback
- DB 를 마지막 commit 된 시점의 상태로 복원한다.

#### 3) savepoint
- Rollback 시 트랜잭션에 포함된 전체 복원이 아닌 savepoint 까지 트랜잭션의 일부만 롤백 가능

위 명령어 들은 보다 시피 '**행**' 에 적용되는 것이 아닌 **'테이블(=객체)'** 전체에 적용이 된다. <br>
위 부분을 꼭 알고 있어야 한다.

<br>

## @AutoCommit 은 좋은건가?
결론만 말씀하자면 거의 대부분 사람들은 사용하고 있을 것이라고 생각한다 <br>

MySQL 같은 경우는 자동으로 AutoCommit 설정이 되어있다 <br>
AutoCommit 이 설정 되어있고 안되어있고 차이는 트랜잭션이 일어나는 것을 보면 알 수 있다 <br>

DB 에서 'Create' 'Update' 'Delete' 가 일어나는 것을 '**트랜잭션**' 이라고 부른 다는 건 다들 알고 있을 것이다 <br>
트랜잭션은즉 DB 의 상태에 변경을 주는 것들을 지칭한다 <br>

그리고 트랜잭션이 발생한 이후에 현재는 자동으로 Commit 을 통하여 DB 에 작업이 반영이 된다. <br>

즉 우리가 모르고 있었지만, 우리의 트랜잭션은 sql 쿼리를 날리고 'commit(=영구저장)' 이 되어야 DB 에 변경이 일어나는 것이다 <br>
위 기능을 @AutoCommit 이 자동으로 commit 쿼리를 날려주고 있던 것 이다 <br>

AutoCommit 을 확인하기 위해선
```sql
SELECT @@AUTOCOMMIT;
```

명령어를 실행시키면 결과물이 1로 되있을 것 입니다. <br>
1 은 @AutoCommit 이 되어있는 상태이고 위 AutoCommit 을 해제하기 위해선 숫자를 '0' 으로 바꾸면 됩니다.
```sql
SET AUTOCOMMIT = 0; -- autocommit [해제]
SET AUTOCOMMIT = 1; -- autocommit [설정]
```

AutoCommit 을 해제한 후에는 이제 트랜잭션에 대한 롤백도 가능하다 <br>
하지만 트랜잭션이 일어난 이후 'commit' 을 매번 해줘야 하는 귀찮음 또한 있다 <br>

하지만 데이터가 중요한 작업을 하는 경우에는 autocommit 을 비활성화 후 트랜잭션을 수동으로 관리하는 것은 괜찮은 방법이라고 생각합니다 <br>

ORM 인 JPA 를 사용하고 있다면 자체적으로 영속성 컨텍스트 및 DB 트랜잭션을 관리할 수 있다 <br>
그리고 스프링을 사용중이라면 @Transactional 을 통하여 트랜잭션 실패시 롤백 되는 기능을 사용할 수도 있다 <br>

Spring Framework와 같은 애플리케이션에서는 트랜잭션을 코드 레벨에서 처리하기 때문에, <br>
MySQL의 autocommit 설정은 보통 애플리케이션 레이어에서 관리됩니다. <br>

즉 MySQL workbench 나 datagrip 같은 툴에서 직접적인 트랜잭션이 발생할 일 은 크게 많지는 않을 것이다 <br>
그러므로 트랜잭션에 Commit 같은 경우는 Persistence Layer 쪽을 다뤄야 하는 경우가 많을 것이다 <br>

즉 위 내용을 자세하게 알기 위해서는 '트랜잭션 격리 수준' 에 대한 학습이 필요할 것이라고 생각합니다 <br>

그러므로 Service Layer 에서 트랜잭션을 관리하되, DB tool 에서는 최대한 직접적인 제어는 지양해야 하겠지만, <br>
그럼에도 불구하고 트랜잭션이 발생할 일이 있을 것 이기 때문에 조심해서 사용하기 위해 @Autocommit 을 해제 후 여러번 확인하고 직접 commit 을 하는 방법을 추천한다.
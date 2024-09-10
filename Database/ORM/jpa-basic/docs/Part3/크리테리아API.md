# 크리테리아 API 를 이용한 쿼리
JPQL 이 문자열을 이용하여 작성하는 쿼리라면 <br>
크리테리아 API 는 자바 코드를 이용해서 작성하는 쿼리이다 <br>
```java
EntityManager em = EMFUtils.currentEntityManager();
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);
cq.select(root);
cq.where(cb.equal(root.get("name"),"고길동"));
	
TypedQuery<User> query = em.createQuery(cq);
List<User> users = query.getResultList();
```

위 코드가 크리테리아 API 를 이용한 간단한 코드이다 <br>

JPQL 보다 더 복잡해 보인다 <br>

복잡함에도 불구하고 사용하는 이유는 다양한 조건을 조합하기 쉽다 <br>
문자열과 달리 자바 코드를 사용하기 때문에 타입에 안전한 쿼리를 만들 수 있다 <br>

### fetch 조인
JPQL 에서 연관된 대상을 하나의 쿼리로 조회하기 위한 fetch 조인이 있다고 했다 <br>
여기서는 .fetch() 메소드를 사용해서 fetch 조인을 구현할 수 있다
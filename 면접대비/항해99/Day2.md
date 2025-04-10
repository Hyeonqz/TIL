## Restful API
#### Q. Restful API 에 대해서 설명해라
restful api 라 함은 rest 아키텍처 스타일을 따르는  api 로 http 프로토콜을 활용하여 통신을 한다 <br>
통신은 URI 로 식별을 하며 데이터를 주고 받는다 <br>

restful api 에서 사용하는 대표적인 http 프르토콜은 아래와 같다.
- GET(조회)
- POST(생성)
- PUT(전체 수정)
- DELETE(삭제)
- PATCH(일부 수정)

일반적으로 restful 하다의 의미는, rest api 원칙을 잘 준수한 것을 의미한다 <br>

기본적으로 restful api 특징으로는 Stateless 해야 하며 Cache 를 사용할 수 있어야하고 클라이언트-서버 구조, 계층화가 있다 <br>
일반적으로 json 형식을 사용해 요청 및 응답을 주고 받는다 <br>

restful api 를 설계할 때는 명확하고 직관적인 URI 네이밍과 HTTP 상태 코드를 활용해야 한다 <br>
이를 통해 확장성과 유지보수성을 높일 수 있다 <br> 

추가적으로 스프링부트 환경에서는 api 를 정의하고 반환 값은 ResponseEntity 를 사용한다 <br>
ResponseEntity 는 HTTP Response 메시지를 감싸는 클래스로 http 상태코드, 응답 헤더, 응답 본문 데이터를 포함하여 보여줄 수 있다 <br>
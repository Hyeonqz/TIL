package org.token.jwt;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtApplicationTests {

	@Autowired
	private JwtService jwtService;

	@Test
	void contextLoads () {
	}

	@Test
	@DisplayName("토큰 생성 확인 테스트")
	void tokenCreate () {
		// given
		Map<String,Object> claims =  new HashMap<String, Object>();
		claims.put("user_id",923); // 데이터 담음
		LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(10);

		// when
		String token = jwtService.createToken(claims, expiredAt);

		// then
		System.out.println(token);
	}

	@Test
	@DisplayName("토큰 검증 확인 테스트")
	void tokenValidation () {

		// given
		String token = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo5MjMsImV4cCI6MTcxMzkzNjQxNH0.F-1CfdR5q6wL23QhxXApPF6cYjIUUaO5SFtYvUZT_IE";

		// then
		jwtService.validToken(token);
	}

}

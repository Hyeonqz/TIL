package org.token.jwt;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtService {

	private static final String SECRET_KEY = "java17springbootJWTTokenIssuedExample"; // 256bit 이상의 Key를 넣어야함

	// Token 생성
	public String createToken (Map<String, Object> claims, LocalDateTime expireAt) {
		// 외부에서 claim 을 받아온다
		// 만료일자 또한 외부에서 받아온다.

		// custom key 를 생성한다.
		SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
		// 날짜 형식 맞추기
		Date _expiredAt = Date.from(expireAt.atZone(ZoneId.systemDefault()).toInstant());

		return Jwts.builder()
			.signWith(secretKey, SignatureAlgorithm.HS256) // 어떤 알고리즘을 사용하여 키를 암호화 할 것인지
			.setClaims(claims) // 어떠한 내용을 body 에 넣을지
			.setExpiration(_expiredAt) // 만료시간 지정
			.compact();
	}

	// Token 검증 -> 검증은 서버에서 한다.
	public void validToken (String token) { // 클라이언트에서 token 이 넘어오면 그것을 검증한다.

		SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

		// Token 파싱하기 위한 기초 작업.
		JwtParser parser = Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build();

		// 파싱 로직 + 유효성 예외 처리
		try {
			var result = parser.parseClaimsJws(token);

			result.getBody().entrySet().forEach(value -> {
				log.info("key : {}, Value : {}", value.getKey(), value.getValue());
			});
		} catch (Exception e) {
			if (e instanceof SignatureException) {
				throw new RuntimeException("JWT Token Not Valid Exception");
			}
			else if (e instanceof ExpiredJwtException) {
				throw new RuntimeException("JWT Token Expired Exception");
			} else {
				throw new RuntimeException("JWT Token Validation Exception");
			}
		}

	}

}
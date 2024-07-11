package org.token.jwt;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/jwt")
@RequiredArgsConstructor
@RestController
public class JwtController {
	private final JwtService jwtService;

	@PostMapping("/create")
	public String create(@RequestParam String token) {
		return null;
	}

	@GetMapping("/valid")
	public void valid(@RequestParam String token) {

	}

}

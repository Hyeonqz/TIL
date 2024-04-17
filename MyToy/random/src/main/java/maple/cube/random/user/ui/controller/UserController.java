package maple.cube.random.user.ui.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import maple.cube.random.user.domain.User;
import maple.cube.random.user.application.dto.request.UserRequestDTO;
import maple.cube.random.user.application.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
	private final UserService userService;

	@PostMapping("/create")
	public User createUser(@RequestBody UserRequestDTO userRequestDTO) {
		return userService.save(userRequestDTO);
	}

	@GetMapping("/all")
	public ResponseEntity<List<User>> findAllUser() {
		return ResponseEntity.ok(userService.findAllUser());
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> findOne(@PathVariable Long id) {
		log.info("id : {}  ",id);
		return ResponseEntity.ok(userService.findOne(id));
	}
}

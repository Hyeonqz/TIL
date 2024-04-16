package maple.cube.random.user.ui.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import maple.cube.random.user.service.UserService;

@RequiredArgsConstructor
@RestController
public class UserController {
	private final UserService userService;
}

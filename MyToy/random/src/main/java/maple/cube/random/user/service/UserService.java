package maple.cube.random.user.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import maple.cube.random.user.domain.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {
	private final UserRepository userRepository;

}

package maple.cube.random.user.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import maple.cube.random.user.application.dto.response.UserResponse;
import maple.cube.random.user.domain.User;
import maple.cube.random.user.domain.UserRepository;
import maple.cube.random.user.application.dto.request.UserRequestDTO;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public User save(UserRequestDTO userRequestDTO) {
		return userRepository.save(userRequestDTO.toEntity());
	}

	public List<User> findAllUser() {
		return userRepository.findAll();
	}

	public User findOne(Long id) {
		return userRepository.findById(id).orElseThrow();
	}

}

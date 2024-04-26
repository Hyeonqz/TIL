package maple.cube.random.user.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import maple.cube.random.cube.domain.CubeType;
import maple.cube.random.user.domain.User;
import maple.cube.random.user.domain.UserRepository;
import maple.cube.random.user.application.dto.request.UserRequestDTO;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public User save (UserRequestDTO userRequestDTO) {
		return userRepository.save(userRequestDTO.toEntity());
	}

	public List<User> findAllUser () {
		return userRepository.findAll();
	}

	public User findOne (Long id) {
		return userRepository.findById(id).orElseThrow();
	}

	public void useCube () {
		final var BLACK = CubeType.BLACK_CUBE;
		final var RED = CubeType.RED_CUBE;
		final var ODD = CubeType.Odd_CUBE;
		final var EDITONAL = CubeType.EDITIONAL_CUBE;

		// 큐브 사용



		// 큐브 사용시 정해진 확률 적용해서 업그레이드 시키기



		// 사용자 큐브 차감



	}

}

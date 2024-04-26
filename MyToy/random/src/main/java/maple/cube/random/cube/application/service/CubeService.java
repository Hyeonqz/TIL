package maple.cube.random.cube.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import maple.cube.random.cube.application.dto.request.CubeRequestDTO;
import maple.cube.random.cube.domain.CashCube;
import maple.cube.random.cube.domain.CashCubeRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CubeService {
	private final CashCubeRepository cashCubeRepository;

	@Transactional
	public CashCube create(CubeRequestDTO cubeRequestDTO) {
		return cashCubeRepository.save(cubeRequestDTO.toEntity());
	}
}

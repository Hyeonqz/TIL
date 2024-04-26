package maple.cube.random.cube.ui.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import maple.cube.random.cube.application.dto.request.CubeRequestDTO;
import maple.cube.random.cube.application.service.CubeService;
import maple.cube.random.cube.domain.CashCube;

@RequiredArgsConstructor
@RequestMapping("/api/cube")
@RestController
public class CubeController {
	private final CubeService cubeService;

	@PostMapping("/create")
	public ResponseEntity<CashCube> create (@RequestBody CubeRequestDTO cubeRequestDTO) {
		return ResponseEntity.ok(cubeService.create(cubeRequestDTO));
	}

}

package maple.cube.random.cube.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import maple.cube.random.cube.domain.CashCube;
import maple.cube.random.cube.domain.CubeType;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class CubeRequestDTO {

	private Long cubeId;
	private CubeType cubeType;
	private Long count;

	public CashCube toEntity() {
		return CashCube.builder()
			.id(cubeId)
			.cubeType(CubeType.BLACK_CUBE)
			.count(count)
			.build();
	}
}

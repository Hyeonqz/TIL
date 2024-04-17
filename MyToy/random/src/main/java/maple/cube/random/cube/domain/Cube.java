package maple.cube.random.cube.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cube {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="cube_id")
	private Long id;

	@Enumerated(value = EnumType.STRING)
	private CubeType cubeType;
}

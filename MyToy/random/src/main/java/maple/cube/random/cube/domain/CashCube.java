package maple.cube.random.cube.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import maple.cube.random.common.BaseEntity;
import maple.cube.random.user.domain.User;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="cubes")
@Entity
public class CashCube extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="cube_id")
	private Long id;

	private Long count;

	@Enumerated(value = EnumType.STRING)
	private CubeType cubeType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id")
	private User user;


}

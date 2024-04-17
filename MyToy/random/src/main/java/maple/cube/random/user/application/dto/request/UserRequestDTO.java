package maple.cube.random.user.application.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import maple.cube.random.user.domain.User;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserRequestDTO {

	private String name;
	private LocalDateTime createAt;
	private LocalDateTime updatedAt;

	public User toEntity() {
		return User.builder()
			.name(name)
			.createAt(LocalDateTime.now())
			.updatedAt(updatedAt)
			.build();
	}
}

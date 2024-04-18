package maple.cube.random.user.application.dto.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import maple.cube.random.cube.domain.CashCube;
import maple.cube.random.item.domain.Item;
import maple.cube.random.user.domain.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserResponse {

	private Long id;
	private String name;
	private List<Item> items = new ArrayList<>();
	private List<CashCube> cashCubes = new ArrayList<>();
	private LocalDateTime createAt;
	private LocalDateTime updatedAt;

	public static UserResponse fromEntity(User user) {
		return UserResponse.builder()
			.id(user.getId())
			.name(user.getName())
			.items(user.getItems())
			.cashCubes(user.getCashCubes())
			.createAt(user.getCreateAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}

}

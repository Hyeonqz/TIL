package maple.cube.random.item.application.dto.request;

import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import maple.cube.random.item.domain.Item;
import maple.cube.random.item.domain.enums.CubeRank;
import maple.cube.random.item.domain.enums.EditionalRank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequestDTO {

	private Long itemId;
	private String name;
	private CubeRank ordinaryRank;
	private EditionalRank editionalRank;
	private LocalDateTime createAt;

	public Item toEntity() {
		return Item.builder()
			.id(itemId)
			.name(name)
			.ordinaryRank(CubeRank.RARE)
			.editionalRank(EditionalRank.RARE)
			.createAt(LocalDateTime.now())
			.build();
	}


}

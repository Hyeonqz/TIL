package maple.cube.random.item.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.util.Lazy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import maple.cube.random.item.domain.enums.CubeRank;
import maple.cube.random.item.domain.enums.EditionalRank;
import maple.cube.random.user.domain.User;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="item")
public class Item {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="item_id")
	private Long id;

	private String name;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="user_id")
	@JsonIgnore
	private User user;

	@Enumerated(EnumType.STRING)
	private CubeRank ordinaryRank;

	@Enumerated(EnumType.STRING)
	private EditionalRank editionalRank;

	@CreatedDate
	private LocalDateTime createAt;

}

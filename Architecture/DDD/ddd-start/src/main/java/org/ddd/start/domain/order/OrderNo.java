package org.ddd.start.domain.order;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor @AllArgsConstructor
@Embeddable
public abstract class OrderNo {
	private Long id;
}

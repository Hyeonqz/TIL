package org.spring.lotto.ui.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor @AllArgsConstructor
@Getter
@Builder
public class LottoDTO {
	private String round;
	private String count;

	@JsonIgnore
	private String firstLotto;
	@JsonIgnore
	private String secondLotto;
	@JsonIgnore
	private BigDecimal amount;
	@JsonIgnore
	private LocalDateTime createAt;
	@JsonIgnore
	private LocalDateTime announcementDay;
}

package org.spring.lotto.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.Comment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@Entity
public class User {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	@Comment("로또 회차")
	private String round;

	private String firstLotto;

	private String secondLotto;

	@Comment("구매일")
	private LocalDateTime createAt;

	@Comment("당첨 발표날")
	private LocalDateTime announcementDay;

	@Comment("당첨 결과")
	private String result;

	@Comment("총 구매 비용")
	private BigDecimal amount;

	public User (Long id, String name, String round, String firstLotto, String secondLotto, LocalDateTime createAt,
		LocalDateTime announcementDay, String result, BigDecimal amount) {
		this.id = id;
		this.name = name;
		this.round = round;
		this.firstLotto = firstLotto;
		this.secondLotto = secondLotto;
		this.createAt = createAt;
		this.announcementDay = announcementDay;
		this.result = result;
		this.amount = amount;
	}

	public User () {
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User)o;
		return Objects.equals(id, user.id) && Objects.equals(name, user.name)
			&& Objects.equals(round, user.round) && Objects.equals(firstLotto, user.firstLotto)
			&& Objects.equals(secondLotto, user.secondLotto) && Objects.equals(createAt, user.createAt)
			&& Objects.equals(announcementDay, user.announcementDay) && Objects.equals(result,
			user.result) && Objects.equals(amount, user.amount);
	}

	@Override
	public int hashCode () {
		return Objects.hash(id, name, round, firstLotto, secondLotto, createAt, announcementDay, result, amount);
	}

	@Override
	public String toString () {
		return "User{" +
			"id=" + id +
			", name='" + name + '\'' +
			", round='" + round + '\'' +
			", firstLotto='" + firstLotto + '\'' +
			", secondLotto='" + secondLotto + '\'' +
			", createAt=" + createAt +
			", announcementDay=" + announcementDay +
			", result='" + result + '\'' +
			", amount=" + amount +
			'}';
	}

}

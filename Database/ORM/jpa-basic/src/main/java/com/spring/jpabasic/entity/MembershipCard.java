package com.spring.jpabasic.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class MembershipCard {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_email")
	private User owner;

	@Temporal(TemporalType.DATE)
	@Column(name = "expiry_date")
	private Date expiryDate;

	private boolean enabled;

	public MembershipCard () {
	}

	public MembershipCard (Long id, User owner, Date expiryDate, boolean enabled) {
		this.id = id;
		this.owner = owner;
		this.expiryDate = expiryDate;
		this.enabled = enabled;
	}

	/*
	 * 위 로직을 통해 이 객체의 필드를 변경한다 -> 나중에 Commit 할 때 자동으로 업데이트를 함 -> 더티 체킹
	 * */
	public void assignTo (User owner) {

		if (this.owner != null)
			throw new RuntimeException("에러");

		this.owner = owner;
	}

	public void cancelAssign() {
		 this.owner = null;
	}

	public void disable() {
		this.enabled = false;
	}

}

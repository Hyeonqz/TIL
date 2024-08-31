package com.spring.jpabasic.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
public class RealUserLog {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne @JoinColumn(name ="review_id")
	private Review review; // 참조키를 이용한 1:1 단방향 연관

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="user_date")
	private Date userDate;

	public RealUserLog (Review review, Date userDate) {
		this.review = review;
		this.userDate = userDate;
	}

}


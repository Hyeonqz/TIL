package com.spring.jpabasic.entity;

import java.sql.Date;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Table(name = "hotel_review")
@Entity
public class Review {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name="hotel_id")
	private Hotel hotel;

	private int rate;
	private String comment;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	protected Review () {
	}

	public Review (Hotel hotel, int rate, String comment, Date createTime) {
		this.hotel = hotel;
		this.rate = rate;
		this.comment = comment;
		this.createTime = createTime;
	}

	public Long getId () {
		return id;
	}

	public Hotel getHotel () {
		return hotel;
	}

	public int getRate () {
		return rate;
	}

	public String getComment () {
		return comment;
	}

	public Date getCreateTime () {
		return createTime;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Review review = (Review)o;
		return rate == review.rate && Objects.equals(id, review.id) && Objects.equals(hotel,
			review.hotel) && Objects.equals(comment, review.comment) && Objects.equals(createTime,
			review.createTime);
	}

	@Override
	public int hashCode () {
		return Objects.hash(id, hotel, rate, comment, createTime);
	}

	@Override
	public String toString () {
		return "Review{" +
			"id=" + id +
			", hotel=" + hotel +
			", rate=" + rate +
			", comment='" + comment + '\'' +
			", createTime=" + createTime +
			'}';
	}

}

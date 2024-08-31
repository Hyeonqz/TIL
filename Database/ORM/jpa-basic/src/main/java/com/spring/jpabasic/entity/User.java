package com.spring.jpabasic.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Table(name="user")
@Entity
public class User {
	@Id
	String email;

	@OneToOne(mappedBy = "owner")
	private MembershipCard membershipCard;

	private String name;

	@Temporal(TemporalType.TIMESTAMP) // java.sql.timestamp 사용한다는 뜻
	@Column(name="create_date")
	private Date createDate;

	public User () {
	}

	public User (String email, String name, Date createDate) {
		this.email = email;
		this.name = name;
		this.createDate = createDate;
	}

	public String getEmail () {
		return email;
	}

	public String getName () {
		return name;
	}

	public Date getCreateDate () {
		return createDate;
	}

	public void changeName(String name) {
		this.name = name;
	}

}

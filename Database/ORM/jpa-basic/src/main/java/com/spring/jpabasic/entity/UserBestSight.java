package com.spring.jpabasic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
public class UserBestSight {

	@Id
	private String email;

	@OneToOne(mappedBy = "user")
	private User user;

	private String name;
	private String description;



	public UserBestSight (User user, String name, String description) {
		this.email = user.getEmail(); // user Parameter 로 받은 이메일 값을 받는다.
		this.user = user;
		this.name = name;
		this.description = description;
	}

	public UserBestSight (String email, User user, String name, String description) {
		this.email = email;
		this.user = user;
		this.name = name;
		this.description = description;
	}

	public UserBestSight () {

	}

}

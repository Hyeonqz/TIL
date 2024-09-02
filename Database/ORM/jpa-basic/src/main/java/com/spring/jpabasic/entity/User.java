package com.spring.jpabasic.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Basic;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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

	@Basic @Temporal(TemporalType.TIMESTAMP) // java.sql.timestamp 사용한다는 뜻
	@Column(name="create_date")
	private Date createDate;

	@ElementCollection
	@CollectionTable(
		name = "user_keyword",
		joinColumns = @JoinColumn(name="user_email")
	)
	@Column(name="keyword")
	private Set<String> keywords = new HashSet<String>();

	protected User () {
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

	public MembershipCard getMembershipCard () {
		return membershipCard;
	}

	public Set<String> getKeywords () {
		return keywords;
	}

	public void changeName(String name) {
		this.name = name;
	}

}

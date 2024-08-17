package com.spring.jpabasic.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity
public class Hotel {
	@Id
	private Long id;
	private String name;

	@Enumerated(EnumType.STRING)
	private Grade grade;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="address.zipcode", column = @Column(name="eng_zipcode")),
		@AttributeOverride(name="address.address1", column = @Column(name="eng_addr1")),
		@AttributeOverride(name="address.address2", column = @Column(name="eng_addr2"))
	})
	private Address engAddress;
}

package com.spring.jpabasic.entity;

import java.util.Objects;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;

@Embeddable
public class Address {
	private String zipcode;
	private String address1;
	private String address2;

	@Embedded
	@AttributeOverrides({
		@AttributeOverride(name="name", column = @Column(name="city_name")),
		@AttributeOverride(name="location", column = @Column(name="city_location"))
	})
	private City city;

	public Address (String zipcode, String address1, String address2) {
		this.zipcode = zipcode;
		this.address1 = address1;
		this.address2 = address2;
	}

	public Address () {

	}

	public String getZipcode () {
		return zipcode;
	}

	public String getAddress1 () {
		return address1;
	}

	public String getAddress2 () {
		return address2;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Address address = (Address)o;
		return Objects.equals(zipcode, address.zipcode) && Objects.equals(address1, address.address1)
			&& Objects.equals(address2, address.address2);
	}

	@Override
	public int hashCode () {
		return Objects.hash(zipcode, address1, address2);
	}

}

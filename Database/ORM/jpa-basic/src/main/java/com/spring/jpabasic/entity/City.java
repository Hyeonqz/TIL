package com.spring.jpabasic.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class City {
	private String name;
	private String location;
}

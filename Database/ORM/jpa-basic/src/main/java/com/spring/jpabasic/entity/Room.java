package com.spring.jpabasic.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Room {

	@Id @SequenceGenerator(name="room_seq_gen", sequenceName = "room_seq", allocationSize = 1)
	private Long id;
}

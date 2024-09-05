package com.spring.jpabasic.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Player {
	@Id
	@Column(name="player_id")
	private String id;
	private String name;

	@ManyToOne
	@JoinColumn(name="team_id")
	private Team team;

	public Player (String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Player () {
	}

	public String getId () {
		return id;
	}

	public String getName () {
		return name;
	}

	@Override
	public boolean equals (Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Player player = (Player)o;
		return Objects.equals(id, player.id) && Objects.equals(name, player.name);
	}

	@Override
	public int hashCode () {
		return Objects.hash(id, name);
	}

}

package com.spring.jpabasic.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Team {
	@Id
	private String id;
	private String name;

	@OneToMany(mappedBy = "team")
	private Set<Player> players = new HashSet<>();

	public void addPlayer(Player player) {
		this.players.add(player);
	}

	public void removePlayer(Player player) {
		this.players.remove(player);
	}

	public Team (String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Team () {
	}

	public String getId () {
		return id;
	}

	public String getName () {
		return name;
	}

	public Set<Player> getPlayers () {
		return players;
	}

}

package com.spring.jpabasic.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;

@Entity
public class Itinerary {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String description;

	@ElementCollection
	@CollectionTable(
		name = "itinerary_site", joinColumns = @JoinColumn(name = "itinerary_id"))
	@OrderColumn(name = "list_idx")
	private List<SiteInfo> sites;

	public Itinerary () {
	}

	public Itinerary (String name, String description, List<SiteInfo> sites) {
		this.name = name;
		this.description = description;
		this.sites = sites;
	}


	public Long getId () {
		return id;
	}

	public String getName () {
		return name;
	}

	public String getDescription () {
		return description;
	}

	public List<SiteInfo> getSites () {
		return sites;
	}

}
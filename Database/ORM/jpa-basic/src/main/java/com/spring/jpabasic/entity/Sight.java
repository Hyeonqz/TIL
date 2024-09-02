package com.spring.jpabasic.entity;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.annotations.SortNatural;
import org.hibernate.sql.ast.Clause;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.OrderBy;

public class Sight {

	@ElementCollection
	@CollectionTable()
	@OrderBy("name asc")
	private Set<RecItem> recItems = new LinkedHashSet<>();

}

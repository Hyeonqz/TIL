package com.spring.jpabasic.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class SiteInfo {
	private String site;
	private int time;

	public SiteInfo () {
	}

	public SiteInfo (String site, int time) {
		this.site = site;
		this.time = time;
	}

	public String getSite () {
		return site;
	}

	public int getTime () {
		return time;
	}

}

package com.hkjin.unittest.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record PokemonPageableResponse(
	List<PokemonDto> content,
	int pageNo,
	int pageSize,
	long totalElements,
	int totalPages,
	boolean last) {
}

package com.hkjin.unittest.dto;

import java.util.List;

public record PokemonPageableResponse(
	List<PokemonDto> content,
	int pageNo,
	int pageSize,
	long totalElements,
	int totalPages,
	boolean last) {
}

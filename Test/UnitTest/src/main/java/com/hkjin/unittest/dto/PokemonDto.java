package com.hkjin.unittest.dto;

import lombok.Builder;

@Builder
public record PokemonDto(Long id, String name, String type) {
}

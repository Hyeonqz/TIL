package com.hkjin.unittest.dto;

import lombok.Builder;

@Builder
public record ReviewDto(Long id, String title, String content, Integer stars) {
}

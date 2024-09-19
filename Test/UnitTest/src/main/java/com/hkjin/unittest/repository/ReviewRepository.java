package com.hkjin.unittest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hkjin.unittest.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}

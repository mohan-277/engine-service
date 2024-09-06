package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.Ball;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BallRepository extends JpaRepository<Ball, Integer> {
}

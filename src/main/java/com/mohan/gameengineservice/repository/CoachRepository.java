package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, Integer> {
}

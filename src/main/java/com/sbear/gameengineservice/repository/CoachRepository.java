package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.Coach;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoachRepository extends JpaRepository<Coach, Integer> {
}

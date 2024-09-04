package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.PlayerScore;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlayerScoreRepository  extends JpaRepository<PlayerScore, Long> {
}

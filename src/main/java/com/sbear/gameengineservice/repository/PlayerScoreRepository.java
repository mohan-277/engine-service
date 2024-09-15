package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.PlayerScore;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlayerScoreRepository  extends JpaRepository<PlayerScore, Long> {
}

package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}

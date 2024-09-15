package com.sbear.gameengineservice.repository;

import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.PlayerObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerObjectRepository extends JpaRepository<PlayerObject, Integer> {

    PlayerObject findByPlayer(Player player);
}

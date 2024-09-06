package com.mohan.gameengineservice.repository;

import com.mohan.gameengineservice.entity.Player;
import com.mohan.gameengineservice.entity.PlayerObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerObjectRepository extends JpaRepository<PlayerObject, Integer> {

    PlayerObject findByPlayer(Player player);
}

package com.sbear.gameengineservice.service;

import com.sbear.gameengineservice.dto.PlayerScoreCardDTO;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.stats.PlayerScoreCard;

public interface PlayerService {

    PlayerScoreCardDTO getPlayerScoreCard(String playerName);



}

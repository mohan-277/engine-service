package com.sbear.gameengineservice.service;

import com.sbear.gameengineservice.dto.PlayerDTO;
import com.sbear.gameengineservice.dto.PlayerScoreCardDTO;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.exceptions.PlayerNotFoundException;

import java.util.List;

public interface PlayerService {

    PlayerScoreCardDTO getPlayerScoreCard(String playerName);

    Player registerPlayer(PlayerDTO playerDTO);

    PlayerDTO getPlayerById(Long id) throws PlayerNotFoundException;

    List<PlayerDTO> getAllPlayersByCountry(String country);

     List<PlayerDTO> getAllPlayers();


    List<String> getAllPlayersCountry();

}

package com.sbear.gameengineservice.service;


import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.CricketMatch;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.Team;

import java.util.List;

public interface HomeService {
    // endPoint it shows all the available match mix of all
     List<CricketMatch> getAllMatches();
     List<Team> teams();
     List<Player> players();

     List<String> getAllPlayersCountry();
    MatchDetailsDTO matchDetailsGetByMatchId(Long matchId);


}

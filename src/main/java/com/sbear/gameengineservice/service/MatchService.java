package com.sbear.gameengineservice.service;

import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.CricketMatch;

import java.util.List;

public interface MatchService {

    List<MatchDetailsDTO> getAllMatches();

    MatchDetailsDTO createMatch(MatchDetailsDTO matchDetailsDTO);

    CricketMatch addMatch(CricketMatch cricketMatch) throws Exception;

    MatchDetailsDTO matchDetailsGetByMatchId(Long matchId);


}

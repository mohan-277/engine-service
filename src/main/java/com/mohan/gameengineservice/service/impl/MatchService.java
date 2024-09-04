package com.mohan.gameengineservice.service.impl;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.mohan.gameengineservice.dto.MatchDetailsDTO;
import com.mohan.gameengineservice.entity.CricketMatch;
import com.mohan.gameengineservice.entity.Location;
import com.mohan.gameengineservice.repository.CricketMatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchService {

    @Autowired
    CricketMatchRepository cricketMatchRepository;

    public List<MatchDetailsDTO> getMatchesByTournamentId(Long tournamentId) {
        List<CricketMatch> matches = cricketMatchRepository.findCricketMatchesByTournamentId(tournamentId);
        return matches.stream().map(this::convertToMatchDetailsDTO).collect(Collectors.toList());
    }

    private MatchDetailsDTO convertToMatchDetailsDTO(CricketMatch match) {
        MatchDetailsDTO matchDetail = new MatchDetailsDTO();
        matchDetail.setMatchId(match.getId());
        matchDetail.setTeamA(match.getTeamA().getName());
        matchDetail.setTeamB(match.getTeamB().getName());
        matchDetail.setMatchDateTime(match.getMatchDateTime());
        matchDetail.setLocation(match.getLocation().getCountry() + " - " + match.getLocation().getGround());
        matchDetail.setMatchStage(match.getMatchStage()); // Set match type  play-off , semifinal, finals
        return matchDetail;
    }


}

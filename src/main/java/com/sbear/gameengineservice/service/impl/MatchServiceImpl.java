package com.sbear.gameengineservice.service.impl;

import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.*;
import com.sbear.gameengineservice.repository.*;
import com.sbear.gameengineservice.service.MatchService;
import com.sbear.gameengineservice.mappers.MatchMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class MatchServiceImpl implements MatchService {


    private final CricketMatchRepository cricketMatchRepository;
    private final MatchMapper matchMapper;
    private final TeamRepository teamRepository;

    public MatchServiceImpl(CricketMatchRepository cricketMatchRepository, MatchMapper matchMapper, TeamRepository teamRepository) {
        this.cricketMatchRepository = cricketMatchRepository;
        this.matchMapper = matchMapper;
        this.teamRepository = teamRepository;
    }

    public List<MatchDetailsDTO> getAllMatches() {
         List<CricketMatch> cricketMatches = cricketMatchRepository.findAll();
        return cricketMatches.stream()
                .map(matchMapper::toDTO)
                .collect(Collectors.toList());
    }


    public MatchDetailsDTO createMatch(MatchDetailsDTO matchDetailsDTO){
        Team teamA = teamRepository.findTeamByName(matchDetailsDTO.getTeamA());
        if (teamA == null) {
            throw new RuntimeException("Team A not found");
        }

        Team teamB = teamRepository.findTeamByName(matchDetailsDTO.getTeamB());
        if (teamB == null) {
            throw new RuntimeException("Team B not found");
        }

        CricketMatch match = matchMapper.toEntity(matchDetailsDTO, teamA, teamB);

        CricketMatch savedMatch = cricketMatchRepository.save(match);

        return matchMapper.toDTO(savedMatch);
    }


    public  CricketMatch addMatch(CricketMatch cricketMatch) throws Exception{
        try {
            if (cricketMatch == null) {
                throw new IllegalArgumentException("Cricket match details cannot be null");
            }
            return cricketMatchRepository.save(cricketMatch);
        } catch (Exception e) {
            throw new Exception("An error occurred: " + e.getMessage());
        }
    }

    public MatchDetailsDTO matchDetailsGetByMatchId(Long matchId) {
        CricketMatch cricketMatch = cricketMatchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match not found"));
        return matchMapper.toDTO(cricketMatch);
    }

}


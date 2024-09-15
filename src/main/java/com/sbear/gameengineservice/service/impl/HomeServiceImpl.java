package com.sbear.gameengineservice.service.impl;

import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.CricketMatch;
import com.sbear.gameengineservice.entity.Player;
import com.sbear.gameengineservice.entity.Team;
import com.sbear.gameengineservice.repository.CricketMatchRepository;
import com.sbear.gameengineservice.repository.PlayerRepository;
import com.sbear.gameengineservice.repository.TeamRepository;
import com.sbear.gameengineservice.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    CricketMatchRepository cricketMatchRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private PlayerRepository playerRepository;



   public List<CricketMatch> getAllMatches(){
        return cricketMatchRepository.findAll();
    }


    public List<Team> teams() {
        return List.of();
    }


    public List<Player> players() {
        return List.of();
    }
    public List<String> getAllPlayersCountry(){
       return playerRepository.findDistinctCountries();
    }


    public MatchDetailsDTO matchDetailsGetByMatchId(Long matchId) {
        CricketMatch cricketMatch = cricketMatchRepository.findById(matchId).orElseThrow(() -> new RuntimeException("Match not found"));

        return matchToMatchDetailDTOConverter(cricketMatch);
    }

//    @Override
//    public TeamDTO teamGetByTeamId(Integer teamId) {
//        Optional<Team> optionalTeam = teamRepository.findById(teamId);
//        if (optionalTeam.isPresent()) {
//            Team team = optionalTeam.get();
//            return convertToTeamDto(team);
//        } else {
//            return null; // or throw new EntityNotFoundException("Team not found with id: " + teamId);
//        }
//    }



    public  MatchDetailsDTO matchToMatchDetailDTOConverter(CricketMatch cricketMatch){
        return MatchDetailsDTO.builder()
                .matchId(cricketMatch.getId())
                .matchGroup(cricketMatch.getMatchGroup())
                .matchType(cricketMatch.getMatchType())
                .matchStage(cricketMatch.getMatchStage())
                .teamA(cricketMatch.getTeamA().getName())
                .teamB(cricketMatch.getTeamB().getName())
                .location(cricketMatch.getLocation().getCountry())
                .matchDateTime(cricketMatch.getMatchDateTime())
                .live(cricketMatch.isLive())
                .matchStatus(cricketMatch.getMatchStatus())
                .build();
    }


}

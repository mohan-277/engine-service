package com.sbear.gameengineservice.mappers;

import com.sbear.gameengineservice.dto.MatchDetailsDTO;
import com.sbear.gameengineservice.entity.CricketMatch;
import com.sbear.gameengineservice.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {

    public   CricketMatch toEntity(MatchDetailsDTO matchDto, Team teamA, Team teamB) {
        if (matchDto == null || teamA == null || teamB == null) {
            return null;
        }

        return CricketMatch.builder()
                .teamA(teamA)
                .teamB(teamB)
                .matchGroup(matchDto.getMatchGroup())
                .matchType(matchDto.getMatchType())
                .matchStage(matchDto.getMatchStage())
                .matchDateTime(matchDto.getMatchDateTime())
                .isLive(matchDto.isLive())
                .matchStatus(matchDto.getMatchStatus())
                .build();
    }

    public   MatchDetailsDTO toDTO(CricketMatch cricketMatch) {
        if (cricketMatch == null) {
            return null;
        }

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

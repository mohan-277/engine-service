package com.mohan.gameengineservice.dto;

import com.mohan.gameengineservice.entity.Team;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class MatchRequest {

//        private Team team1;
//        private Team team2;


    // this is the way to request the team i think and easy way instead  of send the large json with id i need to query the team
    private String stadiumName;
    private String matchType; // e.g., "T20", "ODI", "Test"
    private LocalDateTime matchStartTime;
    private int teamAId;
    private int teamBId;
    private int battingTeamId; // The team that will bat first
    private int bowlingTeamId;


}

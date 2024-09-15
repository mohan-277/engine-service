package com.sbear.gameengineservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MatchRequest {

    private String stadiumName;
    private String matchType; // e.g., "T20", "ODI", "Test"
    private LocalDateTime matchStartTime;
    private int teamAId;
    private int teamBId;
    private int battingTeamId; // The team that will bat first
    private int bowlingTeamId;


}

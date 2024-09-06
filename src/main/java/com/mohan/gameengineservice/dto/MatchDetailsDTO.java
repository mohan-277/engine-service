package com.mohan.gameengineservice.dto;

import com.mohan.gameengineservice.entity.constants.MatchStage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class MatchDetailsDTO {


    private Long matchId;
    private String teamA;
    private String teamB;
    private LocalDateTime matchDateTime;
    private String location;// Include the stage of the match in the DTO
    private String matchType; // Match type (e.g., IPL, T20, ODI, Test)
    private MatchStage matchStage; // Match stage (e.g., Playoffs, Semifinals, Finals)
    private String matchGroup; // Group A or Group B
    private boolean live;

}

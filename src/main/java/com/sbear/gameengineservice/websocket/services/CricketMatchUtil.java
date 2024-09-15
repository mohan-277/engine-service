package com.sbear.gameengineservice.websocket.services;

import com.sbear.gameengineservice.utilities.TeamUtil;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;



public class CricketMatchUtil {

    @Getter
    @Setter
    private  Long matchId;
    private TeamUtil teamUtilA;
    private TeamUtil teamUtilB;
    @Setter
    @Getter
    private String matchType;
    @Getter
    private final LocalDateTime matchDateTime;
    @Setter
    @Getter
    private String result;
    @Getter
    private final int totalOvers;



    public CricketMatchUtil(TeamUtil teamUtilA, TeamUtil teamUtilB, String matchType, Long matchId, LocalDateTime matchDateTime) {
        this.teamUtilA = teamUtilA;
        this.teamUtilB = teamUtilB;
        this.matchType = matchType;
        this.matchId = matchId;
        this.totalOvers = determineOvers(matchType);
        this.matchDateTime = matchDateTime;
    }

    public TeamUtil getTeamA() { return teamUtilA; }
    public void setTeamA(TeamUtil teamUtilA) { this.teamUtilA = teamUtilA; }
    public TeamUtil getTeamB() { return teamUtilB; }
    public void setTeamB(TeamUtil teamUtilB) { this.teamUtilB = teamUtilB; }

    private int determineOvers(String matchType) {
        return switch (matchType) {
            case "T20" -> 5;
            case "ODI" -> 60;
            case "Test" -> 100;
            default -> 10;
        };
    }

}

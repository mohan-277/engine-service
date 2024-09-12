package com.mohan.gameengineservice.websocket.services;

import com.mohan.gameengineservice.utilities.TeamUtil;

import java.time.LocalDateTime;

public class CricketMatchUtil {

    private  Long matchId;
    private TeamUtil teamUtilA;
    private TeamUtil teamUtilB;
    private String matchType;
    private LocalDateTime matchDateTime;
    private String result;
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
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    public LocalDateTime getMatchDateTime() { return matchDateTime; }
    public void setMatchDateTime(LocalDateTime matchDateTime) { this.matchDateTime = matchDateTime; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }

    public int getTotalOvers() {
        return totalOvers;
    }

    private int determineOvers(String matchType) {
        switch (matchType) {
            case "T20":
                return 2;
            case "ODI":
                return 60;
            case "Test":
                return 100; // Example for Test match, 90 overs per day, adjust as needed
            default:
                throw new IllegalArgumentException("Invalid match type: " + matchType);
        }
    }

}

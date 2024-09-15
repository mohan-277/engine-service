package com.sbear.gameengineservice.utilities;

import com.sbear.gameengineservice.websocket.services.CricketMatchUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class InningsUtil {
    @Setter
    @Getter
    private Long inningsId;
    @Setter
    @Getter
    private CricketMatchUtil match;
    private TeamUtil battingTeamUtil;
    private TeamUtil bowlingTeamUtil;
    private Integer runs;
    private Integer wickets;
    private List<OverUtil> overUtils;
    private boolean isCompleted;
    private final Integer totalOvers;
    private Integer currentOverBalls;

    public InningsUtil(CricketMatchUtil match, TeamUtil battingTeamUtil, TeamUtil bowlingTeamUtil ,Long inningsId) {
        this.match = match;
        this.battingTeamUtil = battingTeamUtil;
        this.bowlingTeamUtil = bowlingTeamUtil;
        this.runs = 0;
        this.wickets = 0;
        this.overUtils = new ArrayList<>();
        this.isCompleted = false;
        this.totalOvers = match.getTotalOvers();
        this.currentOverBalls = 0;
        this.inningsId = inningsId;
    }

    public TeamUtil getBattingTeam() { return battingTeamUtil; }
    public void setBattingTeam(TeamUtil battingTeamUtil) { this.battingTeamUtil = battingTeamUtil; }
    public TeamUtil getBowlingTeam() { return bowlingTeamUtil; }
    public void setBowlingTeam(TeamUtil bowlingTeamUtil) { this.bowlingTeamUtil = bowlingTeamUtil; }
    public int getRuns() { return runs; }
    public void setRuns(int runs) { this.runs = runs; }
    public int getWickets() { return wickets; }
    public void setWickets(int wickets) { this.wickets = wickets; }
    public List<OverUtil> getOvers() { return overUtils; }
    public void setOvers(List<OverUtil> overUtils) { this.overUtils = overUtils; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public int getTotalOvers() {
        return totalOvers;
    }
    public void incrementWickets() {
        wickets++;
    }
    public void addRuns(int runs) {
        this.runs += runs;
    }

    public void incrementBall() {
        currentOverBalls++;

        if (currentOverBalls >= 6) {
            // If 6 balls have been bowled, increment the over count and reset currentOverBalls
            overUtils.add(new OverUtil(currentOverBalls));
            currentOverBalls = 0;
        }

        // Check if innings is completed
        if (wickets >= 10) {
            isCompleted = true;
        }
    }
}

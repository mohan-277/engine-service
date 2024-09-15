package com.sbear.gameengineservice.dto;



import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class PlayerScoreCardDTO {
    private String playerName;
    private String teamName;
    private String playerType;
    private Long totalRuns;
    private Integer totalBallsFaced;
    private Integer totalFours;
    private Integer totalSixes;
    private Integer totalDotBalls;
    private Integer totalSingles;
    private Integer totalTwos;
    private Integer totalThrees;
    private Integer totalOversBowled;
    private Integer totalWicketsTaken;

    // Constructor
    public PlayerScoreCardDTO(String playerName, String teamName, String playerType,
                              Long totalRuns, Integer totalBallsFaced, Integer totalFours, Integer totalSixes,
                              Integer totalDotBalls, Integer totalSingles, Integer totalTwos, Integer totalThrees,
                              Integer totalOversBowled, Integer totalWicketsTaken) {
        this.playerName = playerName;
        this.teamName = teamName;
        this.playerType = playerType;
        this.totalRuns = totalRuns;
        this.totalBallsFaced = totalBallsFaced;
        this.totalFours = totalFours;
        this.totalSixes = totalSixes;
        this.totalDotBalls = totalDotBalls;
        this.totalSingles = totalSingles;
        this.totalTwos = totalTwos;
        this.totalThrees = totalThrees;
        this.totalOversBowled = totalOversBowled;
        this.totalWicketsTaken = totalWicketsTaken;
    }
}

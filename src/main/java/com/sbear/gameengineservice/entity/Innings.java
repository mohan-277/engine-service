package com.sbear.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;





@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Innings")
public class Innings {  // innings is just means a  batting not more than that

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "innings_number", nullable = false)
    private Long inningsNumber;
    @ManyToOne
    @JoinColumn(name = "match_id")
    private CricketMatch cricketMatch;

//    @ManyToOne
//    @JoinColumn(name = "match_id")
//    private CricketMatch cricketMatch;
//
//    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Player> players = new ArrayList<>();


    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "batting_team_id")
    private Team battingTeam;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "bowling_team_id")
    private Team bowlingTeam;

//    @Getter
//    private Integer totalRuns; // Runs scored in this inning
//    private Integer wickets; // Wickets fallen in this inning

//    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL)
//    private List<Over> overs = new ArrayList<>(); // over is for the complete innings


    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL)
    private List<Ball> balls = new ArrayList<>();

//    private Integer currentScore;  // Current score of the batting team
//    private Integer currentWickets; // Current wickets fallen
//    private Integer currentOvers; // Current overs completed

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "wicket_count")
    private Integer wicketCount;


    private Boolean isCompleted;

    private  Integer totalOvers;


//    @ManyToOne
//    @JoinColumn(name = "current_striker_id")
//    private PlayerObject currentStriker; // The player currently batting
//
//    @ManyToOne
//    @JoinColumn(name = "current_non_striker_id")
//    private PlayerObject currentNonStriker; // The player at the other end
//
//    @ManyToOne
//    @JoinColumn(name = "current_bowler_id")
//    private PlayerObject currentBowler; // The player currently bowling
//
//    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL)
//    private List<PlayerScore> playerScores = new ArrayList<>();



    public Innings(CricketMatch match, Team battingTeam, Team bowlingTeam) {
        this.cricketMatch = match;
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;

        this.isCompleted = false;
    }

    public CricketMatch getMatch() { return cricketMatch; }
    public void setMatch(CricketMatch match) { this.cricketMatch = match; }


//    public void incrementWickets() {
//        wickets++;
//    }
//    public void addRuns(int runs) {
//        this.runs += runs;
//    }


}

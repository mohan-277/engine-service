package com.mohan.gameengineservice.entity;

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
public class Innings {  // innings is just means a  batting not more than that
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Getter
    private int runs; // Runs scored in this inning
    private int wickets; // Wickets fallen in this inning

    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL)
    private List<Over> overs = new ArrayList<>(); // over is for the complete innings

    private String currentScore;  // Current score of the batting team
    private String currentWickets; // Current wickets fallen
    private String currentOvers; // Current overs completed

    private boolean isCompleted;

    private  int totalOvers;


    @ManyToOne
    @JoinColumn(name = "current_striker_id")
    private PlayerObject currentStriker; // The player currently batting

    @ManyToOne
    @JoinColumn(name = "current_non_striker_id")
    private PlayerObject currentNonStriker; // The player at the other end

    @ManyToOne
    @JoinColumn(name = "current_bowler_id")
    private PlayerObject currentBowler; // The player currently bowling

    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL)
    private List<PlayerScore> playerScores = new ArrayList<>();



    public Innings(CricketMatch match, Team battingTeam, Team bowlingTeam) {
        this.cricketMatch = match;
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;
        this.runs = 0;
        this.wickets = 0;
        this.overs = new ArrayList<>();
        this.isCompleted = false;
    }

    public CricketMatch getMatch() { return cricketMatch; }
    public void setMatch(CricketMatch match) { this.cricketMatch = match; }


    public void incrementWickets() {
        wickets++;
    }
    public void addRuns(int runs) {
        this.runs += runs;
    }
}

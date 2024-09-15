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
public class Innings {  // innings are just means a batting not more than that

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "innings_number", nullable = false)
    private Long inningsNumber;
    @ManyToOne
    @JoinColumn(name = "match_id")
    private CricketMatch cricketMatch;

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



    @OneToMany(mappedBy = "innings", cascade = CascadeType.ALL)
    private List<Ball> balls = new ArrayList<>();

    @Column(name = "total_score")
    private Integer totalScore;

    @Column(name = "wicket_count")
    private Integer wicketCount;


    private Boolean isCompleted;

    private  Integer totalOvers;



    public Innings(CricketMatch match, Team battingTeam, Team bowlingTeam) {
        this.cricketMatch = match;
        this.battingTeam = battingTeam;
        this.bowlingTeam = bowlingTeam;

        this.isCompleted = false;
    }

    public CricketMatch getMatch() { return cricketMatch; }
    public void setMatch(CricketMatch match) { this.cricketMatch = match; }



}

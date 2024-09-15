package com.sbear.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;



@Entity
@Table(name = "cricket_matches")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CricketMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stadiumName;

    private String matchName;

    private boolean toss; // this represents the toss result
    private String tossDecision; // e.g., "batting" or "bowling"
    @ManyToOne
    @Lazy
    @JoinColumn(name = "team_a_id", nullable = false) // Column to store Team A ID
    private Team teamA;

    @ManyToOne
    @Lazy
    @JoinColumn(name = "team_b_id", nullable = false) // Column to store Team B ID
    private Team teamB;

    private LocalDateTime matchDateTime;

    @ManyToOne
    @Lazy
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;


    private String matchType; // e.g., IPL, T20, ODI, Test

    private String matchStage; // e.g., Group Stage, Playoffs, Semifinals, Finals

    private String matchGroup; // Group A or Group B

    @OneToMany(mappedBy = "cricketMatch", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<Innings> innings = new ArrayList<>(); // List of innings in the match

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    private String matchStatus;

    private boolean isLive;

    private String result; // it is a string win by some runs or lost by some runs or tie like this

    public CricketMatch(Team teamA, Team teamB, String matchType) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.matchDateTime = LocalDateTime.now();
        this.matchType  = matchType;
    }


}

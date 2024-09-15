package com.sbear.gameengineservice.entity.stats;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "player-stats")
public class PlayerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id; // Unique identifier (auto-generated)

    @Column(name = "player_name")
    private String playerName;

    @Column(name = "team_name")
    private String teamName;
    @Column(name = "player_type")
    private String playerType; // E.g., "Batsman", "Bowler", "All-Rounder"
    private int runs;
    @Column(name = "balls_faced")
    private int ballsFaced;
    @Column(name = "fours")
    private int fours;
    @Column(name = "sixes")
    private int sixes;
    @Column(name = "dot_balls")
    private int dotBalls;
    @Column(name = "singles")
    private int singles;
    @Column(name = "twos")
    private int twos;
    @Column(name = "threes")
    private int threes;
    @Column(name = "overs_bowled")
    private int oversBowled;// Relevant for bowlers and all-rounders
    @Column(name = "wickets_taken")
    private int wicketsTaken; //

    @Column(name = "current_playing_match_id")
    private Long currentPlayingMatchId; // this is for the differentiating/ retrieving the individual scoring for the player

    public PlayerStats( String playerName, String teamName, String playerType, long id ,int runs, int ballsFaced, int fours, int sixes, int dotBalls, int singles, int twos, int threes, int oversBowled) {
        this.id = id;
        this.playerName = playerName;
        this.teamName = teamName;
        this.playerType = playerType;
        this.runs = runs;
        this.ballsFaced = ballsFaced;
        this.fours = fours;
        this.sixes = sixes;
        this.dotBalls = dotBalls;
        this.singles = singles;
        this.twos = twos;
        this.threes = threes;
        this.oversBowled = oversBowled;
    }


}

package com.sbear.gameengineservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class PlayerObject {
    @Id
    private long id;
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
    @ManyToMany
    private List<Over> overs = new ArrayList<>();
    @ManyToMany
    private List<Wicket> wickets = new ArrayList<>();

    private int score; // Runs scored by the player
    private int ballsFaced; // Balls faced by the player
    private int fours; // Fours hit by the player
    private int sixes; // Sixes hit by the player
    private int dotBalls;
    private int singles;
    private int twos;
    private int threes;
    private int oversBowled;
    private int wicketsTaken;

    // this is for the player details, and it is required to store it in the player stats table
    private String specialization;
    private String country;
    private LocalDate dateOfBirth;

    public PlayerObject(Player player) {
        this.player = player;
        this.specialization = player.getSpecialization();
        this.country = player.getCountry();
        this.dateOfBirth = player.getDateOfBirth();
        this.dotBalls = 0;
        this.singles = 0;
        this.twos = 0;
        this.threes = 0;
        this.oversBowled = 0;
        this.wicketsTaken = 0;
        this.score = 0;
        this.ballsFaced = 0;
        this.fours = 0;
        this.sixes = 0;
    }





}

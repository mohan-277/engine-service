package com.mohan.gameengineservice.entity;

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
    @ManyToMany
    private List<Ball> balls = new ArrayList<>();


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
    // this is for the player details and it is required to store it in the player stats table
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


    public void incrementBallsFaced() { this.ballsFaced++; }
    public void incrementFours() { this.fours++; }
    public void incrementSixes() { this.sixes++; }


    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public List<Over> getOvers() {
        return overs;
    }

    public void setOvers(List<Over> overs) {
        this.overs = overs;
    }

    public List<Wicket> getWickets() {
        return wickets;
    }

    public void setWickets(List<Wicket> wickets) {
        this.wickets = wickets;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public void setBalls(List<Ball> balls) {
        this.balls = balls;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBallsFaced() {
        return ballsFaced;
    }

    public void setBallsFaced(int ballsFaced) {
        this.ballsFaced = ballsFaced;
    }

    public int getFours() {
        return fours;
    }

    public void setFours(int fours) {
        this.fours = fours;
    }

    public int getSixes() {
        return sixes;
    }

    public void setSixes(int sixes) {
        this.sixes = sixes;
    }

    public int getDotBalls() {
        return dotBalls;
    }

    public void addDotBall() {
        this.dotBalls++;
    }

    public int getSingles() {
        return singles;
    }

    public void addSingle() {
        this.singles++;
    }

    public int getTwos() {
        return twos;
    }

    public void addTwo() {
        this.twos++;
    }

    public int getThrees() {
        return threes;
    }

    public void addThree() {
        this.threes++;
    }

    public int getOversBowled() {
        return oversBowled;
    }

    public void addOverBowled() {
        this.oversBowled++;
    }

    public int getWicketsTaken() {
        return wicketsTaken;
    }

    public void addWicket() {
        this.wicketsTaken++;
    }
}
